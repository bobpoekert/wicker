(ns cheap.hella.wicker.re-cond
  (import [org.antlr.v4.runtime ANTLRInputStream CommonTokenStream Lexer Parser]
          [org.antlr.v4.runtime.tree ParseTree]))

(defn antlr-tree
  [types tree]
  {:text (.getText tree)
   :token (.getToken tree)
   :type (get types (.getType tree))
   :children (map (partial antlr-tree types) (.getChildren tree))})

(defn parse-re
  [^String inp]
  (let [^ParseTree tree (->
                          (ANTLRInputStream. inp)
                          (Lexer.)
                          (CommonTokenStream.)
                          (Parser.)
                          (.prog))
        types (into {} (for [[k v] (.getTokenTypeMap tree)] [(keyword v) k]))]
    (antlr-tree types tree)))

;; (parse-re ".*")
