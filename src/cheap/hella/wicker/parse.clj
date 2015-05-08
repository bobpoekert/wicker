(ns cheap.hella.wicker.parse
  (require [byte-streams]
           [cheap.hella.wicker.util :as u]
           [clojure.core.match :refer [match]]
           [clojure.core.match.java :refer [bean-match]])
  (import [org.jsoup Jsoup]
          [org.jsoup.select Elements]
          [org.jsoup.nodes Element Document Attributes]))

(set! *warn-on-reflection* true)

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

(declare compile-vector-predicate)

(defn- vector-predicate-descend
  [match-expr node-sym callback rst]
  (let [v-sym (with-meta (gensym "v") {:tag 'org.jsoup.nodes.Node})]
    `(let [^Element v# ~match-expr]
      (if v#
        (let [^java.util.List children# (.childNodes v#)]
          (loop [idx# (dec (.size children#))]
            (if (>= idx# 0)
              (let [~v-sym (.get children# idx#)]
                (if (instance? Element ~v-sym)
                  (let [~node-sym ~v-sym]
                    ~(compile-vector-predicate node-sym callback rst)))
                (recur (dec idx#))))))))))

(defn- expr-error!
  [v]
  (->>
    (prn-str v)
    (.trim)
    (str "Invalid expression: ")
    (IllegalArgumentException.)
    (throw)))

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
  `(let [kleene-fn#
          (fn kleene-fn# [~node-sym]
            ~(compile-vector-predicate node-sym callback (into [raw-pred] rst))
            (if ~(compile-vector-predicate node-sym (fn [v] true) [raw-pred])
              (let [^java.util.List children# (.childNodes ~node-sym)]
                (loop [idx# (dec (.size children#))]
                  (when (>= idx# 0)
                    (kleene-fn# (.get children# idx#))
                    (recur (dec idx#)))))))]
    (kleene-fn# ~node-sym)))

(defn- compile-vector-predicate
  [node-sym callback v]
  (if (empty? v)
    (callback node-sym)
    (let [[pred & rst] v]
      (cond
        (vector? pred) (recur node-sym callback (into pred rst)) ;; flatten
        (= pred :.) (vector-predicate-descend node-sym node-sym callback rst)
        (keyword? pred) (recur node-sym callback (into [{:tag-name (name pred)}] rst))
        (symbol? pred) (vector-predicate-descend (list pred node-sym) node-sym callback rst)
        (map? pred) (->
                      `(if (and ~@(->
                                    (fn [[k v]]
                                      (cond
                                        (string? v) `(.equals ^String ~(tag-getter node-sym k) ~v)
                                        (instance? java.util.regex.Pattern v)
                                          `(re-matches ~v ~(tag-getter node-sym k))
                                        (symbol? v) `(~v ~(tag-getter node-sym k))
                                        (list? v) (apply list (concat v [(tag-getter node-sym k)]))
                                        :else (expr-error! pred)))
                                    (map pred)))
                        ~node-sym)
                      (vector-predicate-descend node-sym callback rst))
        (list? pred) (let [[l r] pred]
                      (cond
                        (= r :*) (kleene-star-predicate l node-sym callback rst)
                        (number? r) (count-descend-predicate l r node-sym callback rst)
                        (symbol? r) (var-bind-predicate l r node-sym callback rst)
                        (map? r) (map-var-bind-predicate l r node-sym callback rst)
                        :else (expr-error! pred)))
        :else (expr-error! pred)))))

(defn- gen-query-bindings
  [page-sym bindings]
  (let [result-sym (with-meta (gensym "results") {:tag 'java.util.ArrayList})]
    `(let [~result-sym (java.util.ArrayList.)]
      ~@(for [[pred & body] bindings]
        (compile-vector-predicate
          page-sym
          (fn [node-sym] `(.add ~result-sym (do ~@body)))
          pred))
      ~result-sym)))

(bean-match java.net.URI)

(defmacro re-cond
  [test-var & pairs]
  (->>
    (partition 2 pairs)
    (reverse)
    (reduce
      (fn [prev [pattern body]]
        (let [^java.util.regex.Pattern re (if (vector? pattern) (first pattern) pattern)
              ^java.util.regex.Matcher test-matcher (.matcher re "test")
              group-count (.groupCount test-matcher)
              vars (if (vector? pattern)
                    (rest pattern)
                    (map #(symbol (format "group-%d" %)) (range group-count)))
              matcher-sym (with-meta (gensym "matcher") {:tag 'java.util.regex.Matcher})]
          `(let [~matcher-sym (.matcher ~re ~test-var)]
            (if (.matches matcher#)
              (let ~(->>
                      (if (empty? vars) [])
                      (range group-count)
                      (map vector vars)
                      (mapcat (fn [[v i]] [v `(.group ~matcher-sym ~i)]))
                      (vec))
                ~body)
              ~prev))))
      nil)))

(map vector (range 1 10) (range 10 1))

(macroexpand
  '(re-cond x
    [#"(a)" start] foo
    #"b" bar))

(defmacro defparser
  [nom & patterns]
  (let [page-src-sym (with-meta (gensym "src") {:tag 'String})
        page-sym (with-meta (gensym "parsed-page") {:tag 'org.jsoup.nodes.Element})]
    `(defn ~nom [uri# ~page-src-sym]
      (re-cond
        ~@(->
            (fn [[match-expr & bindings]]
              [match-expr 
                `(let [~page-sym (parse-html ~page-src-sym)]
                  ~(gen-query-bindings page-sym bindings))])
            (mapcat patterns))))))
