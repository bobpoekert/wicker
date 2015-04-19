(ns cheap.hella.wicker.queue
  (import [java.util.concurrent LinkedBlockingQueue BlockingQueue]
          [java.net URI]
          [java.util.concurrent ConcurrentHashMap])
  (require [aleph.http :as http]
           [manifold.deferred :as d]
           [durable-queue :as q]))

(defrecord CrawlContext [
  url-queue ^BlockingQueue
  output-queue
  global-state
  crawl-policy
  failure-policy
  crawl-thread
  stop-condition])
(defrecord URIFetch [uri crawl-times])

(def default-queue-length 100)

(defn default-crawl-policy
  [context job]
  (let [current-time (System/currentTimeMillis)]
    {:crawl? (or (not (:not-before job)) (< (:not-before job) current-time))
     :job (->
              job
              (assoc :crawl-time current-time)
              (assoc :n-tries (inc (or (:n-tries job) 0))))}))

(defn default-failure-policy
  [context job result]
  {:crawl? (< (:n-tries job) 10)
   :job job})

(defn ->CrawlContext
  ([queue-path crawl-policy failure-policy]
    (CrawlContext.
      (q/queues queue-path {})
      (LinkedBlockingQueue. default-queue-length)
      (ConcurrentHashMap.)
      crawl-policy failure-policy
      nil
      (atom nil)))
  ([queue-path]
    (->CrawlContext queue-path default-crawl-policy default-failure-policy)))

(defprotocol CrawlOutputHandler
  (handle-output! [this crawl-context]))

(extend-protocol CrawlOutputHandler
  URI
  (handle-output! [this context]
    (if (.isAbsolute this)
      (q/put! (:url-queue context) :url {:uri (.toString this)})
      (throw (IllegalArgumentException. (format "URIs to crawl must be absolute (on %s)" (.toString this))))))
  URIFetch
  (handle-output! [this context]
    (q/put! (:url-queue context) :url this))
  Object
  (handle-output! [this context]
    (.put ^BlockingQueue (:output-queue context) this)))

(defprotocol ToURI
  (to-uri [this]))

(extend-protocol ToURI
  URIFetch
  (to-uri [this] (URI. (:uri this)))
  URI
  (to-uri [this] this)
  String
  (to-uri [this] (URI. this)))

(defprotocol HTTPRequest
  (http-request [this]))

(extend-protocol HTTPRequest
  String
  (http-request [this] (http/get this))
  java.util.Map
  (http-request [this] (http/request this)))

(defn queue-seq
  [^BlockingQueue q]  
  (lazy-seq
    (let [v (.take q)]
      (if (= ::stop q)
        (do
          (.put q ::stop)
          nil)
        (cons v (queue-seq q))))))

(defn handle-failed-job!
  [^CrawlContext context job result]
  ;; we're not using q/retry! because we want to add extra state to the job
  (try
    (let [failure-result ((:failure-policy context) context job result)]
      (if (:crawl? failure-result)
        (q/put! (:url-queue context) :url (:job failure-result))))
    (finally
      (q/complete! (:url-queue context) job))))

(defn crawler
  [^CrawlContext context]
  (let [{:keys [url-queue stop-condition crawl-policy failure-policy]} context]
    (loop []
      (if (realized? stop-condition)
        nil
        (let [next-job (q/take! url-queue :url)
              next-uri (to-uri @next-job)
              policy-result (crawl-policy context @next-job)]
          (if-not (:crawl? policy-result)
            (handle-output! policy-result context)
            (d/on-realized
              (http-request next-uri)
              (fn [result] ;; success
                (try
                  (do
                    (handle-output! context result)
                    (q/complete! url-queue next-job))
                  (catch Throwable e
                    (do
                      (handle-failed-job! context next-job (assoc result :handle-output-exception e))
                      (throw e)))))
              (fn [result] ;; failure
                (handle-failed-job! context next-job result))))
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
      (handle-output! context start)
      (crawl context))))
