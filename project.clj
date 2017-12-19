(defproject markov-text "0.1.0-SNAPSHOT"
  :description "Generate fun texts using markov chains"
  :url "https://github.com/AndreaCrotti/markov_fun"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.rpl/specter "1.0.5"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [aysylu/loom "1.0.0"]]

  :resource-paths ["config" "resources"]
  :min-lein-version "2.0.0"
  :main markov-text.core
  :profiles {:dev {:aliases
                   {"cli" ["run" "-m" "markov-text.core/-main"]}}})
