(ns cheap.hella.wicker.queue
  (import [java.util.concurrent LinkedBlockingQueue BlockingQueue]
          [java.net URI]
          [java.util.concurrent ConcurrentHashMap])
  (require [aleph.http :as http]
           [manifold.deferred :as d]
           [durable-queue :as q]
           [cheap.hella.wicker.seen-urls :as seen]))

(defrecord CrawlContext [
  url-queue
  ^BlockingQueue output-queue
  seen-urls
  global-state
  result-processor
  crawl-policy
  failure-policy
  exception-handler
  crawl-thread
  stop-condition])

(def default-local-queue-length 100)
(def default-persistent-queue-length (* 10, 10 10 10, 10 10 10))

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

(defn default-exception-handler
  [exc]
  (throw exc))

(defn ->CrawlContext
  [& args]
  (let [argmap (if args (apply hash-map args) {})
        {:keys [queue-path db-path crawl-policy failure-policy result-processor]} argmap]
    (assert crawl-policy ":crawl-policy not specified")
    (assert failure-policy ":failure-policy not specified")
    (assert result-processor ":result-processor not specified")
    (assert (or (:url-queue argmap) (:queue-path argmap)) "Must specify :url-queue or :queue-path")
    (assert (or (:seen-urls argmap) (:seen-db-path argmap)) "Musp specify :seen-urls or :seen-db-path")
    (CrawlContext.
      (or (:url-queue argmap) (q/queues queue-path {:max-queue-size default-persistent-queue-length}))
      (or (:output-queue argmap) (LinkedBlockingQueue. default-local-queue-length))
      (or (:seen-urls argmap) (seen/make-db! db-path))
      (or (:global-state argmap) (ConcurrentHashMap.))
      result-processor
      crawl-policy
      failure-policy
      (or (:exception-handler argmap) default-exception-handler)
      (:crawl-thread argmap)
      (or (:stop-condition argmap) (atom nil)))))

(defprotocol CrawlOutputHandler
  (handle-output! [this crawl-context]))

(extend-protocol CrawlOutputHandler
  URI
  (handle-output! [this context]
    (if (.isAbsolute this)
      (q/put! (:url-queue context) :url {:uri (.toString this)})
      (throw (IllegalArgumentException. (format "URIs to crawl must be absolute (on %s)" (.toString this))))))
  Object
  (handle-output! [this context]
    (.put ^BlockingQueue (:output-queue context) this)))

(defprotocol ToURI
  (to-uri [this]))

(extend-protocol ToURI
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
  (let [{:keys [url-queue stop-condition crawl-policy failure-policy seen-urls result-processor]} context]
    (loop []
      (if (realized? stop-condition)
        nil
        (let [next-job (q/take! url-queue :url)
              next-uri (to-uri @next-job)
              policy-result (crawl-policy context @next-job)]
          (cond
            (not (:crawl? policy-result)) (handle-output! policy-result context)
            (seen/url-exists? seen-urls (:uri @next-job)) nil
            :else (d/on-realized
                    (http-request next-uri)
                    (fn [result] ;; success
                      (future
                        (try
                          (do
                            (doseq [v (result-processor context result)]
                              (handle-output! v context))
                            (seen/add-url! (:seen-urls context) (:uri @next-job))
                            (q/complete! url-queue next-job))
                          (catch Throwable e
                            (do
                              (handle-failed-job! context next-job (assoc result :handle-output-exception e))
                              (throw e))))))
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
