(ns cheap.hella.wicker.util
  (require [clojure.reflect :as reflect]))

(set! *warn-on-reflection* true)

(defn method-name-to-key
  [m]
  (let [s (name m)
        s (.substring s 3)
        parts (clojure.string/split s #"(?=\p{Upper})")
        parts (map #(.toLowerCase %) parts)]
    (keyword (clojure.string/join "-" parts))))

(defmacro defmapwrap
  "Defines a function that takes an object of type input-type and returns an Associative (ie map) that has keys for all of that classes zero arity getter methods"
  [nom input-type-name]
  (let [input-type (resolve input-type-name)
        inp-sym (with-meta (gensym "inp") {:tag input-type-name})
        reflection-data (reflect/reflect input-type)
        refmethods (filter #(= (type %) clojure.reflect.Method) (:members reflection-data))
        get-methods (filter #(and (= (:parameter-types %) []) ((:flags %) :public) (.startsWith (name (:name %)) "get")) refmethods)]
    `(let [to-map# (fn [~inp-sym] ~(into {} (for [m get-methods] [(method-name-to-key (:name m)) `(. ~inp-sym ~(:name m))])))]
      (defn ~nom [~inp-sym]
        (proxy [clojure.lang.APersistentMap] []
          (containsKey [k#]
            (case k#
              ~@(mapcat (fn [method] [(method-name-to-key (:name method)) true]) get-methods)
              false))
          (entryAt [k#]
            (case k#
              ~@(mapcat
                (fn [method]
                  (let [n (method-name-to-key (:name method))]
                    [n `(clojure.lang.MapEntry. ~n (. ~inp-sym ~(:name method)))]))
              nil)))
          (valAt [k#]
            (case k#
              ~@(mapcat
                (fn [method]
                  (let [n (method-name-to-key (:name method))]
                    [n `(. ~inp-sym ~(:name method))]))
                get-methods)
              nil))
          (seq []
            (seq ~(vec
              (for [method get-methods]
                `(clojure.lang.MapEntry. ~(method-name-to-key (:name method)) (. ~inp-sym ~(:name method)))))))
          (entrySet []
            ~(into #{}
              (for [method get-methods]
                `(clojure.lang.MapEntry. ~(method-name-to-key (:name method)) (. ~inp-sym ~(:name method))))))
          (keySet []
            ~(into #{} (for [method get-methods] (method-name-to-key (:name method)))))
          (values []
            ~(->>
              (for [method get-methods]
                `(. ~inp-sym ~(:name method)))
              (vec))))))))
              
(defmapwrap uri-map java.net.URI)
