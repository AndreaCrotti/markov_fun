(ns markov-text.core-test
  (:require [clojure.test :as t]
            [markov-text.core :as core]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]))

(t/deftest gen-probs-test
  (t/testing "generate probabilities map"
    (t/are [words probs]
        (= probs (core/gen-probs words))

      ["hello" "world"] {"hello" {"world" 1}}
      ["this" "is" "this" "are"] {"this" {"is" 1/2, "are" 1/2}
                                  "is" {"this" 1}})))

(t/deftest uppers-lowers-test
  (t/testing "Split in lowers and uppers"
    (t/are [probs uppers-lowers]
        (= (core/split-case probs) uppers-lowers)

      ["hello" "world" "hi"]
      [["hello" "world" "hi"] []])))


(t/deftest analyze-text-test
  (t/testing "First pass of analysis"
    (t/are [pref-sufs analyzed]
        (= (core/analyze-text pref-sufs) analyzed)
      
      [["hello" "world"] ["world" "hello"]] {"hello" ["world"]
                                             "world" ["hello"]})))
