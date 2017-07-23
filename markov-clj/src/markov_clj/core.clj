(ns markov-clj.core
  (:require [com.rpl.specter :as specter]))

;; add more dependencies if you need to

(defn to-words
  [text]
  (re-seq #"\w+" text))

(def sample-string "I am not a number! I am a free man!")
(def sample-words (->> sample-string to-words))

(def simp {"I" #{["am" "groodt"] ["am" "super"]}})

(defn- add-to-wordmap
  "Fully functional way to modify a structure like
  {prefix #{[s1 s2] [s3 s4]}}"
  [word-map prefix value]
  (specter/transform
   (specter/keypath prefix)
   #(into #{} (clojure.set/union % [value]))
   word-map))

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
  ;; this should really support different sizes
  ([words]
   (gen-wordmap (cycle-substrings words 3) {}))

  ([[[pref s1 s2] :as words] word-map]
   (if (empty? words)
     word-map
     (let [new-map (add-to-wordmap word-map pref [s1 s2])]
       (recur (rest words) new-map)))))

(defn generate
  "Generate a paragraph of the given length"
  [word-map length]
  (for [n (range length)
        m (range (+ 4 (rand-int 6)))]

    (let [pref (rand-nth (keys word-map))
          suffixes (into [] (get word-map pref))]

      (cons pref (rand-nth suffixes)))))

(generate (gen-wordmap sample-words) 10)

(defn file-to-strings
  [filename]
  (->> filename
       slurp
       to-words))

(def bible-words (file-to-strings "pg10.txt"))
(def bible-wordmap (gen-wordmap bible-words))

(defn -main [& args]
  (print "Hello boot")
  #_(let [filename (nth args 1)
        length (Integer/parseInt (nth args 2))
        strings (file-to-strings filename)
        wordmap (gen-wordmap strings)]

    (for [n (range length)]
      (print (generate strings length)))))
