(defproject markov-text "0.1.0-SNAPSHOT"
  :description "Generate fun texts using markov chains"
  :url "https://github.com/AndreaCrotti/markov_fun"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.rpl/specter "1.0.2"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [instaparse "1.4.7"]
                 [aysylu/loom "1.0.0"]
                 ;; pedestal related code
                 [io.pedestal/pedestal.service "0.5.2"]
                 [io.pedestal/pedestal.jetty "0.5.2"]

                 [ch.qos.logback/logback-classic "1.1.8" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.22"]
                 [org.slf4j/jcl-over-slf4j "1.7.22"]
                 [org.slf4j/log4j-over-slf4j "1.7.22"]
                 #_[im.chit/lucid.aether "1.3.13"]
                 #_[im.chit/lucid.package "1.3.13"]]

  :resource-paths ["config" "resources"]
  :min-lein-version "2.0.0"
  :main markov-text.core
  ;; do we need this instead??
  ;; :main ^{:skip-aot true} sample-pedestal.server
  :profiles {:dev {:aliases
                   {"run-dev" ["trampoline" "run" "-m" "markov-text.server/run-dev"]
                    "run-prod" ["run" "-m" "markov-text.server/run"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.2"]]}
             :uberjar {:aot [sample-pedestal.server]}})
