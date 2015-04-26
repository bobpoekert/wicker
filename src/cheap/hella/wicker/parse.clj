(ns cheap.hella.wicker.parse
  (require [byte-streams]
           [ring.mock.request :as mock]
           [clout.core :as clout])
  (import [org.jsoup Jsoup]
            [org.jsoup.select Elements]
            [org.jsoup.nodes Element Document]))

(defn ^Elements parse-html
  [inp]
  (Jsoup/parse (byte-streams/convert inp String)))

(defprotocol IParseSpec
  (generate-parser [this arg-sym]))

(extend-protocol IParseSpec
  String
  (generate-parser [this arg-sym]
    `(.select ~arg-sym ~this))
  java.util.regex.Pattern
  (generate-parser [this arg-sym]
    `(re-seq ~this (.html ~(with-meta arg-sym {:tag Elements}))))
  clojure.lang.Seqable
  (generate-parser [this arg-sym]
    (reduce #(generate-parser % arg-sym) (reverse this))))

(defmacro defparser
  [nom & handler-pairs]
  (let [compiled-route-syms (into {}
                              (for [[url-binding html-patterns & body] handler-pairs]
                                [url-binding (gensym "url-binding")]))
        context-sym (gensym "context")
        url-sym (gensym "url")
        mock-request-sym (gensym "mock-request")
        result-sym (gensym "result")
        route-sym (gensym "route")]
    `(let ~(->>
            (for [[url-binding binding-sym] compiled-route-syms]
              [binding-sym `(clout/route-compile ~url-binding)])
            (apply concat)
            (vec))
      (defn ~nom [~context-sym ~url-sym ~result-sym]
        (let [~mock-request-sym (mock/request :get ~url-sym)]
          ~(reduce
            (fn [prev [url-binding compiled-route html-pattern body]]
              `(if-let [~route-sym (clout/route-matches ~(get compiled-route-syms url-binding) ~mock-request-sym)]
                (let [~result-sym ~(generate-parser html-pattern result-sym)]
                  ~@body)
                ~prev))
            []
            (reverse handler-pairs)))))))

#_(defparser amazon
  ({:path "gp/site-directory/"}
   [["#content .categories li.category_label" category-label]]
   [(href category-label) (text category-label)]))
   
