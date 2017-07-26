(ns markov-clj.core
  (:require [com.rpl.specter :as specter]))

(def sample-words ["God" "made" "the" "light"])

(defn add-suffix
  [word-map prefix suffix]
  (specter/setval
   [(specter/keypath prefix) specter/END] [suffix] word-map))

(defn cycle-pref-suf
  [words size]
  (partition size 1 words))

(defn analyze-text
  ([pref-sufs]
   (analyze-text pref-sufs {}))
  
  ([pref-sufs word-map]
   (if (empty? pref-sufs)
     word-map
     (let [[pref suf] (first pref-sufs)
           others (rest pref-sufs)]

       (analyze-text
        others
        (add-suffix word-map pref suf))))))

(defn compute-probabilities
  [words]
  (let [word-freq (seq (frequencies words))
        total (count words)]

    (into {}
          (for [[el freq] word-freq]
            [el (/ freq total)]))))

(defn gen-probs
  [words]
  (let [analyzed (analyze-text (cycle-pref-suf words 2))]
    (specter/transform
     [specter/MAP-VALS]
     compute-probabilities
     analyzed)))

(gen-probs sample-words)

(defn to-words
  [text]
  (re-seq #"\w+" text))

(defn file-to-strings
  [filename]
  (->> filename
       slurp
       to-words))

(file-to-strings "pgsmall.txt")

;; TODO: not working as expected
(defn split-capitalized
  [probs]
  ;; not really a transform in this case
  ;; could do a select instead
  (let [is-upper #(Character/isUpperCase (first %))
        filter-fns [is-upper (complement is-upper)]]

    (for [f-fn filter-fns]
      (specter/transform
       [specter/MAP-KEYS f-fn]
       identity
       probs))))

(defn get-capitals
  [probs]
  (into {}
        (filter
         (fn [[key val]] (Character/isUpperCase (first key)))
         probs)))

(def bible-probs (gen-probs
                  (file-to-strings "pgsmall.txt")))


(defn gen-sentence
  [probs word size]
  (when (> size 0)
    (let [word-probs (get probs word)
          next-el (rand-nth (seq word-probs))
          next-word (first next-el)]
      (print next-word " ")
      (gen-sentence probs next-word (dec size)))))

(defn gen-string
  [probs size]
  (let [capitals (get-capitals probs)
        first-el (rand-nth (seq (get-capitals probs)))
        first-word (first first-el)]

    (print first-word " ")
    (gen-sentence probs first-word size)))

(gen-string bible-probs 10)
