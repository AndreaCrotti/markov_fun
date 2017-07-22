#!/usr/bin/env boot

(def sample-string "I am not a number! I am a free man!")

(defn wordmap
  "Generate the wordmap of prefixes and suffixes"
  [text]
  )

(defn generate
  "Generate a paragraph of the given length"
  [text length])

(defn file-to-strings
  [filename]
  (->> filename
       slurp
       (re-seq #"\w+")
       (into [])))

(def bible (-> "pg10.txt" file-to-strings))

(defn -main [& args]
  (let [filename (nth args 1)
        text (-> filename slurp)])

  (print "Hello"))
