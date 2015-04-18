(ns cheap.hella.wicker.queue
  (import [java.util.concurrent LinkedBlockingQueue BlockingQueue]
          [java.net URI]
          [java.util WeakHashMap])
  (require [aleph.http :as http]
           [manifold.deferred :as d]
           [durable-queue :as q]))

(defrecord CrawlContext [url-queue ^BlockingQueue output-queue crawl-policy crawl-thread stop-condition])

(def default-queue-length 100)

(defn default-crawl-policy
  ([context row]
    true)
  ([context retry-state row]
    true))

(defn ->CrawlContext
  ([queue-path crawl-policy]
    (CrawlContext.
      (q/queues queue-path {})
      (LinkedBlockingQueue. default-queue-length)
      crawl-policy
      nil
      (atom nil)))
  ([queue-path]
    (->CrawlContext queue-path default-crawl-policy)))

(defprotocol CrawlOutputHandler
  (handle-output! [this crawl-context]))

(extend-protocol CrawlOutputHandler
  URI
  (handle-output! [this context]
    (if (.isAbsolute this)
      (q/put! (:url-queue context) :url (.toString this))
      (throw (IllegalArgumentException. "URIs to crawl must be absolute (otherwise we don't know what host to hit)"))))
  Object
  (handle-output! [this context]
    (.put ^BlockingQueue (:output-queue context) this)))

(defn queue-seq
  [^BlockingQueue q]  
  (lazy-seq
    (let [v (.take q)]
      (if (= ::stop q)
        (do
          (.put q ::stop)
          nil)
        (cons v (queue-seq q))))))

(defn crawler
  [^CrawlContext context]
  (let [{:keys [url-queue stop-condition crawl-policy]} context
        url-fetch-state (WeakHashMap.)]
    (loop []
      (if (realized? stop-condition)
        nil
        (let [next-url (q/take! url-queue :url)
              url-no-fetch-before (.get url-times-to-wait @next-url)]
          (cond
            (< (System/getCurrentTimeMillis) url-no-fetch-before) (q/retry! url-queue next-url)
            (not (crawl-policy context @next-url)) (q/retry! url-queue next-url)
            :else (d/on-realized
                    (http/get @next-url)
                    (fn [result] ;; success
                      (handle-output! context result)
                      (q/complete! url-queue next-url))
                    (fn [result] ;; failure
                      (.put url-no-fetch-befores @next-url (+ (System/getCurrentTimeMillis) 500))
                      (q/retry! url-queue next-url)))
          (recur))))))

(defn start-crawl-thread!
  [^CrawlContext context]
  (let [old-crawl-thread (:crawl-thread context)]
    (locking old-crawl-thread
      (if (or (nil? old-crawl-thread) (not (.isAlive old-crawl-thread)))
        (let [t (Thread. (partial crawler context))]
          (.start t)
          t)
        old-crawl-thread))))

(defn crawl
  ([^CrawlContext context]
    (do
      (start-crawl-thread! context)
      (queue-seq (:output-queue context))))
  ([^CrawlContext context ^URI start]
    (do
      (handle-output context start)
      (crawl context))))
