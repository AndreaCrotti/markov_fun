(ns markov-text.core
  (:require [com.rpl.specter :as specter]
            [clojure.tools.cli :refer [parse-opts]]))

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

       ;; TODO: use recursion more properly
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

(defn upper-lowers
  [probs]
  (let [prefixes (keys probs)
        is-upper #(Character/isUpperCase (first %))
        is-lower (complement is-upper)
        back-to-map #(into {} (for [k %] [k (get probs k)]))]

    (mapv back-to-map
          [(filterv is-upper prefixes)
           (filterv is-lower prefixes)])))


(defn pick-random-weighted
  "Given a map of probability, pick a random element respecting the
  given weights"
  [probs]
  (let [slices (vec (vals probs))
        total (reduce + slices)
        r (rand total)]

    (loop [i 0 sum 0]
      (if (< r (+ (slices i) sum))
        (nth (keys probs) i)
        (recur (inc i) (+ (slices i) sum))))))

(defn gen-sentence
  [probs word size]
  (when (> size 0)
    (let [word-probs (get probs word)
          ;;lower-letter-probs (second (upper-lowers word-probs))
          next-word (pick-random-weighted word-probs)]

      (print next-word " ")
      (gen-sentence probs next-word (dec size)))))

(defn gen-string
  [probs size]
  (let [capitals (first (upper-lowers probs))
        first-el (rand-nth (seq capitals))
        first-word (first first-el)]

    (print first-word " ")
    (gen-sentence probs first-word size)))

#_(def bible-probs (gen-probs
                    (file-to-strings "resources/sample_texts/pgsmall.txt")))

#_(gen-string bible-probs 10)

(def cli-options
  ;; An option with a required argument
  [["-f" "--file" "File to use to generate the sentences from"] 
   ["-h" "--help"]])

(defn -main
  [& args]
  (let [options (parse-opts args cli-options)]
    (prn options)))
