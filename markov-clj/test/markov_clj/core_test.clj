(ns markov-clj.core-test
  (:require [clojure.test :as t]
            [markov-clj.core :as core]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]))
