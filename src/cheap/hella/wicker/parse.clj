(ns cheap.hella.wicker.parse
  (require [byte-streams]
           [cheap.hella.wicker.util :as u]
           [clojure.core.match :refer [match]]
           [clojure.core.match.java :refer [bean-match]])
  (import [org.jsoup Jsoup]
          [org.jsoup.select Elements]
          [org.jsoup.nodes Element Document Attributes]))

(defn ^Elements parse-html
  [inp]
  (Jsoup/parse (byte-streams/convert inp String)))

(defn- tag-getter
  [node-sym k]
  (case k
    :val `(.val ~node-sym)
    :class-name `(.className ~node-sym)
    :data `(.data ~node-sym)
    :has-text? `(.hasText ~node-sym)
    :text `(.text ~node-sym)
    :own-text `(.ownText ~node-sym)
    :data-nodes `(.dataNodes ~node-sym)
    :text-nodes `(.textNodes ~node-sym)
    :children `(.children ~node-sym)
    :parent `(.parent ~node-sym)
    :id `(.id ~node-sym)
    :tag-name `(.tagName ~node-sym)
    :node-name `(.nodeName ~node-sym)
    :attributes `(.attributes ~node-sym)
    :base-uri `(.baseUri ~node-sym)
    :child-nodes `(.childNodes ~node-sym)
    :sibling-nodes `(.siblingNodes ~node-sym)))

;; map: check properties of a node (eg: attributes, tag name)
;; keyword: check that it equals the tag name
;; symbol: call the function referred to by the symbol on nodes to check for matches
;; list:
;;  if second element is a number, match the nth sibling that matches the first element
;;  if second element is :*, check for matches of the first element arbitrarily deep
;;  if second element is a symbol, capture that node and bind it to that variable
;;  if second elemnt is a map, keys are looked up in the matching element and results are bound to the given values (which should be symbols)
;; vector: matches paths in the tree where each pattern is a child of the previous one
;; :. : match one of anything

#_[(:. :*) {:id "nav-flyout-shopAll"} (:div 2) :span (:span {:text category-name})] 

(declare query-form)

(defn- vector-predicate-descend
  [match-expr node-sym callback var-syms rst]
  (let [v-sym (with-meta (gensym "v") {:tag 'org.jsoup.nodes.Node})]
    `(let [^Element v# ~match-expr]
      (if v#
        (let [^List children# (.childNodes v#)]
          (loop [idx# (dec (.size children#))]
            (if (>= idx# 0)
              (let [~v-sym (.get children# idx#)]
                (if (instance? Element ~v-sym)
                  ~(compile-vector-predicate node-sym callback var-syms rst))
                (recur (dec idx#))))))))))

(defn- expr-error!
  [v]
  (->>
    (prn v)
    (.trim)
    (str "Invalid expression: ")
    (IllegalArgumentException.)
    (throw)))

(declare compile-vector-predicate)

(defn- var-bind-predicate
  [pred sym node-sym callback rst]
  `(let [~sym ~node-sym]
    ~(compile-vector-predicate node-sym callback (into [pred] rst))))

(defn- map-var-bind-predicate
  [pred syms node-sym callback rst]
  `(let ~(->
          (fn [[k v]]
            [v (tag-getter node-sym k)])
          (mapcat syms)
          (vec))
    ~(compile-vector-predicate node-sym callback (into [pred] rst))))

(defn- count-descend-predicate
  [raw-pred cnt node-sym callback rst]
  (let [pred (compile-vector-predicate node-sym callback (into [raw-pred] rst))]
    `(if (= (.siblingIndex ~node-sym) ~cnt)
      ~pred)))

(defn- kleene-star-predicate
  [raw-pred node-sym callback rst]
  

(defn- compile-vector-predicate
  [node-sym callback v]
  (if (empty? v)
    (callback node-sym)
    (let [[pred & rst] v]
      (cond
        (vector? pred) (compile-vector-predicate node-sym (into pred rst)) ;; flatten
        (= pred :.) (vector-predicate-descend node-sym callback rst)
        (keyword? pred) (compile-vector-predicate node-sym (into [{:tag-name (name pred)}] rst))
        (symbol? pred) (vector-predicate-descend (list pred node-sym) callback rst)
        (map? pred) (->
                      `(if (and ~@(u/forcat [[k v] arg]
                                    (cond
                                      (string? v) `(= ~(tag-getter node-sym k) ~v)
                                      (instance? java.util.regex.Pattern v)
                                        `(re-matches ~v ~(tag-getter node-sym k))
                                      :else (expr-error! pred))))
                        ~node-sym)
                      (vector-predicate-descend callback rst))
        (list? pred) (let [[l r] pred]
                      (cond
                        (= r :*) (kleene-star-predicate l node-sym callback rst)
                        (number? r) (count-descend-prediate l r node-sym callback rst)
                        (symbol? r) (var-bind-predicate l r node-sym callback rst)
                        (map? r) (map-var-bind-predicate l r node-sym callback rst)
                        :else (expr-error! pred)))
        :else (expr-error! pred)))))

(defmacro defparser
  [nom & patterns]
  (let [uri-sym (with-meta (gensym "uri") {:tag 'java.net.URI})
        page-sym (gensym "parsed-page")]
    `(defn ~nom [~uri-sym page#]
      (let [~page-sym (parse-html page#)]
        (clojure.core.match/match (cheap.hella.wicker.util/uri-map ~uri-sym)
          ~@(->
              (fn [[match-expr & bindings]]
                [match-expr (gen-query-bindings page-sym bindings)])
              (mapcat patterns)))))))

#_(defparser amazon
  ({:path "/"}
   [(:. :*) {:id "nav-flyout-shopAll"} (:div 2) :span (:span {:text category-name})]
   category-name))
   
