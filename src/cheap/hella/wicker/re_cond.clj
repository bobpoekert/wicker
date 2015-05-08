(ns cheap.hella.wicker.re-cond)

(defn antlr-tree
  [types tree]
  {:text (.getText tree)
   :token (.getToken tree)
   :type (keyword (get types (.getType tree)))
   :children (map (partial antlr-tree types) (.getChildren tree))})

(defn parse-re
  [^String inp]
  (let [^PCREParser parser (PCREParser. (PCRELexer. inp))
        tree (.getTree (.expr parser))
        types (clojure.set/map-invert (.getTokenTypeMap parser))]
    (antlr-tree tree types)))
