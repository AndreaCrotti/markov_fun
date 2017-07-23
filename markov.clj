#!/usr/bin/env boot

;; add more dependencies if you need to

(def sample-string "I am not a number! I am a free man!")
(def sample-words (->> sample-string to-words))

(defn cycle-substrings
  [words size]
  (apply concat
         (for [n (range size)]
           (partition size (drop n words)))))

(defn gen-wordmap
  "Generate the wordmap of prefixes and suffixesi
  desired result is
  I #{[am not] [am a]}
  am #{[not a] [a free]}"

  [words size]
  (let [word-map (transient {})]
    (for [[pref s1 s2] (cycle-substrings words size)]
      (if (contains? (persistent! word-map) pref)
        (assoc! word-map pref (cons [s1 s2] (get word-map pref)))
        (assoc! word-map pref [[s1 s2]])))

    (persistent! word-map)))

(gen-wordmap sample-words 3)

;; (contains? (persistent!(transient {1 2})) 1)

(defn generate
  "Generate a paragraph of the given length"
  [word-map length])

(defn to-words
  [text]
  (re-seq #"\w+" text))

(defn file-to-strings
  [filename]
  (->> filename
       slurp
       to-words))

(defn -main [& args]
  (let [filename (nth args 1)
        length (Integer/parseInt (nth args 2))
        strings (file-to-strings filename)
        wordmap (gen-wordmap strings)]

    (for [n (range length)]
      (print (generate strings length)))))
