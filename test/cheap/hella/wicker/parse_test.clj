(ns cheap.hella.wicker.parse-test
  (require [clojure.java.io :as io]
           [clojure.core.match.java :refer [bean-match]])
  (import [java.net URI])
  (:use clojure.test
        cheap.hella.wicker.parse))

(defn test-page
  [page-name] 
  (with-open [inp (io/reader (format "./test_resources/%s.html" page-name))]
    (slurp inp)))

;; (clojure.core.match/val-at* (URI. "http://www.amazon.com/") :host :x)

(defparser parse-amazon
  (#"https?://www.amazon.com/?"
   ([(:. :*) {:id "nav-flyout-shopAll"} (:div 2) :span (:span {:text category-name})]
     category-name)))

;; (parse-amazon "http://www.amazon.com/" (test-page "amazon_homepage"))

;; (.getHost (URI. "http://www.amazon.com"))

(deftest amazon-test
  (let [parse-result (parse-amazon (URI. "http://www.amazon.com/") (test-page "amazon_homepage"))]
    true))
