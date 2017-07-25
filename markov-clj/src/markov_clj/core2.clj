(ns markov-clj.core2)

(def sample-words
  ["My" "name" "is" "Andrea"])

(def desired-output
  {"My" ["name"]
   "name" ["is"]
   "is" []})

(cons "hello" (get desired-output "My"))

(def desired-output-atom
  (atom {"My" ["name"]
         "name" ["is"]
         "is" []}))

(defn add-suffix
  [atom-name prefix suffix]
  (swap! atom-name
         assoc prefix (into [] (cons suffix (get @atom-name prefix)))))

(defn cycle-pref-suf
  [words size]
  (map vec
       (apply
        concat
        (for [n (range size)]
          (partition size (drop n words))))))


(defn analyze-text
  [words]
  (reset! analyze-global {})
  (for [[pref suf] (cycle-pref-suf words 2)]
    (add-suffix analyze-global pref suf)))

(analyze-text sample-words)

@analyze-global

(defn compute-probabilities
  [words]
  (let [word-freq (seq (frequencies words))
        total (count words)]

    (into {}
          (for [[el freq] word-freq]
            [el (/ freq total)]))))

