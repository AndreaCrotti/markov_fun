(defproject markov-text "0.1.0-SNAPSHOT"
  :description "Generate fun texts using markov chains"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.rpl/specter "1.0.2"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [instaparse "1.4.7"]
                 [aysylu/loom "1.0.0"]
                 #_[im.chit/lucid.aether "1.3.13"]
                 #_[im.chit/lucid.package "1.3.13"]]

  :main markov-text.core)
