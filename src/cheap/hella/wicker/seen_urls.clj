(ns cheap.hella.wicker.seen-urls
  (require [clojure.java.jdbc :as db]))

(defn make-db!
  [path]
  (let [res {:classname "org.sqlite.JDBC"
             :subprotocol "sqlite"
             :subname path}
        conn (db/db-find-connection res)]
    (db/db-do-commands res
      "create table if not exists urls (url text unique)"
      "create index if not exists url_index on urls (url)")
    {:conn res
     :insert-statement (db/prepare-statement conn "insert into urls (url) values (?)")
     :remove-statement (db/prepare-statement conn "delete from urls where url = ?")
     :exists-statement (db/prepare-statement conn "select url from urls where url = ? limit 1")
     :count-statement (db/prepare-statement conn "select count(url) from urls")}))

(defn add-url!
  [conn url]
  (db/query (:conn conn) (:insert-statement conn) (.toString url)))

(defn remove-url!
  [conn url]
  (db/query (:conn conn) (:remove-statement conn) (.toString url)))

(defn url-exists?
  [conn url]
  (->
    (db/query (:conn conn) [(:exists-statement conn) (.toString url)])
    (empty?)
    (not)))

(defn seen-count
  [conn]
  (first (first (db/query (:conn conn) (:count-statement conn)))))
