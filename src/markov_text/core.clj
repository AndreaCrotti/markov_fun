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

(defn gen-sentence-loop
  ;; what happens if the given word has no suffix??
  [probs word size]
  (loop [w word
         n 0
         words []]

    (if (= n (dec size))
      words
      ;; XXX: hacky way to finish the sentence early
      (let [word-probs (get probs w {"." 1})
            new-word (pick-random-weighted word-probs)]

        (recur new-word
               (inc n)
               (conj words new-word))))))

(defn gen-string
  [words size]
  (let [probs (gen-probs words)
        [lowers uppers] (split-case words)
        ;; this also favours the more probable since duplicates are
        ;; still in the uppers list
        first-word (rand-nth uppers)]

    (cons first-word (gen-sentence-loop probs first-word size))))

(def cli-options
  ;; An option with a required argument
  [["-f" "--file" "File to use to generate the sentences from"
    :required true]

   ["-s" "--size" "Size of the words to generate"
    :default 10
    :parse-fn #(Integer/parseInt %)
    :validate [#(pos? %) "Positive number of words please"]]

   ["-n" "--number" "Number of sentences to generate"
    :validate [#(pos? %) "Positive number of sentences"]
    :parse-fn #(Integer/parseInt %)
    :default 1]

   ["-h" "--help"]])


(defn -main
  [& args]
  (let [options (parse-opts args cli-options)
        file-name (nth args 1)
        num-sentences (-> options :options :number)
        size-sentence (-> options :options :size)
        words (file-to-strings file-name)]

    (doseq [n (range num-sentences)]
      (println (clojure.string/join
                " "
                (gen-string words size-sentence))))))
