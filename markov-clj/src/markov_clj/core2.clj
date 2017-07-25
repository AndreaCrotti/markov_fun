(ns markov-clj.core2
  (:require [com.rpl.specter :as specter]))

(def analyze-global (atom {}))

(def sample-words
  ["My" "name" "is" "Andrea" "and" "is" "not"])

;; (def desired-output
;;   {"My" ["name"]
;;    "name" ["is"]
;;    "is" []})

(defn add-suffix
  [atom-name prefix suffix]
  (swap! atom-name
         assoc prefix (into [] (cons suffix (get @atom-name prefix)))))

;; (add-suffix analyze-global "pref" "suf2")

;; (get @analyze-global "pref")

{"my" ["name" "suff1"]
 "other" ["hello"]}

{"my" {"name" 1/2
       "suff1" 1/2}}

(defn cycle-pref-suf
  [words size]
  (partition size 1 words))

(defn analyze-text
  [words]
  (reset! analyze-global {})
  (for [[pref suf] (cycle-pref-suf words 2)]
    (add-suffix analyze-global pref suf)))

(analyze-text sample-words)
@analyze-global
;; (partition 2 1 [1 2 3])

(defn compute-probabilities
  [words]
  (let [word-freq (seq (frequencies words))
        total (count words)]

    (into {}
          (for [[el freq] word-freq]
            [el (/ freq total)]))))

(defn gen-probs
  [words]
  (let [analyzed (last (analyze-text words))]
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

(defn get-capitals
  [probs]
  (into {}
        (filter
         (fn [[key val]] (Character/isUpperCase (first key)))
         probs)))

;; (defn get-capitals-sp
;;   [probs]
;;   (specter/select
;;    [specter/MAP-KEYS (specter/filterer #(Character/isUpperCase (first %)))]
;;    probs))


(def bible-probs (gen-probs
                  (file-to-strings "pgsmall.txt")))


(defn gen-sentence
  [probs word size]
  (when (> size 0)
    (println word size)
    (let [word-probs (get probs word)
          next-word (rand-nth (seq word-probs))]
      (print next-word)
      (gen-sentence probs next-word (dec size)))))

(defn gen-string
  [probs size]
  (let [capitals (get-capitals probs)
        first-el (rand-nth (seq (get-capitals probs)))
        first-word (first first-el)]

    (print first-word)
    (gen-sentence probs first-word size)))
