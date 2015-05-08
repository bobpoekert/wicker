(defproject cheap.hella.wicker "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :java-source-paths ["src/java"]
  :dependencies [[org.clojure/clojure "1.7.0-beta1"]
                 [factual/durable-queue "0.1.3" :exclusions [manifold/manifold byte-streams/byte-streams]]
                 [aleph "0.4.0-beta3"]
                 [byte-streams "0.2.0-alpha8"]
                 [org.antlr/antlr4-runtime "4.5"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [org.xerial/sqlite-jdbc "3.8.7"]
                 [org.jsoup/jsoup "1.8.2"]])
