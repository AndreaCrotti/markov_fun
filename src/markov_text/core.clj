(ns markov-text.core
  (:require [com.rpl.specter :as specter]
            [clojure.tools.cli :refer [parse-opts]]))

(defn add-suffix
  [word-map prefix suffix]
  (specter/setval
   [(specter/keypath prefix) specter/END] [suffix] word-map))

(defn cycle-pref-suf
  [words size]
  (partition size 1 words))

(defn analyze-text
  "Take prefixes and suffixes and return a data
  structure keyed by prefix and with a vector
  of possible suffixes as value
  "
  ([pref-sufs]
   (analyze-text pref-sufs {}))
  
  ([pref-sufs word-map]
   (if (empty? pref-sufs)
     word-map
     (let [[pref suf] (first pref-sufs)
           others (rest pref-sufs)]
       
       (recur
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

;; this should handle punctuation as well
(defn to-words
  [text]
  (re-seq #"\w+" text))

(defn file-to-strings
  [filename]
  (->> filename
       slurp
       to-words))

(defn split-case
  [words]
  (let [is-upper #(Character/isUpperCase (first %))
        is-lower (complement is-upper)]

    (mapv #(filterv % words) [is-lower is-upper])))


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
  (when (pos? size)
    (let [word-probs (get probs word)
          next-word (pick-random-weighted word-probs)]

      (print next-word " ")
      (gen-sentence probs next-word (dec size)))))

(defn gen-string
  [words size]
  (let [probs (gen-probs words)
        [lowers uppers] (split-case words)
        ;; this also favours the more probable since duplicates are
        ;; still in the uppers list
        first-word (rand-nth uppers)]

    (print first-word " ")
    (gen-sentence probs first-word size)))

#_(def bible-words
  (file-to-strings "resources/sample_texts/pgsmall.txt"))

#_(def divina
  (file-to-strings "resources/sample_texts/divinacommedia.txt"))

(def cli-options
  ;; An option with a required argument
  [["-f" "--file" "File to use to generate the sentences from"] 
   ["-h" "--help"]])

(defn -main
  [& args]
  (let [options (parse-opts args cli-options)]
    (prn options)))
