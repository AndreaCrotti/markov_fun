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

(t/deftest uppers-lowers
  (t/testing "Split in lowers and uppers"
    (t/are [probs uppers-lowers]
        (= (core/upper-lowers probs) uppers-lowers)

      {"Hello" {"world" 1} "world" {"hi" 1}}
      [{"Hello" {"world" 1}} {"world" {"hi" 1}}])))
