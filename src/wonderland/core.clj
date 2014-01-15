(ns wonderland.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [medley.core :refer [map-vals]]))

(def resource-files
  {:alice "alice.txt"
   :sherlock "sherlock.txt"
   :50 "50.txt"})

(defn sample-text [id]
  (slurp (io/resource (get resource-files id))))

(defn cleanup-word [word]
  (let [ptns [#"^\""
              #"\"$"
              #"^[^A-z^0-9]$"]]
    ((apply comp (for [ptn ptns] (fn [w] (str/replace w ptn ""))))
     word)))

(defn text->words [text-str]
  (->> (str/split text-str #"\s+")
       (map str/lower-case)
       (map cleanup-word)
       (remove empty?)))

(defn prefix-probabilities [text]
  (map-vals (fn [vals]
              (let [suff (map second vals)
                    freq (frequencies suff)
                    total (apply + (for [[a c] freq] c))]
                (map-vals #(/ % total) freq)))
            (group-by first
                      (map (fn [[a b suffix]]
                             (let [prefix (str a " " b)]
                               [prefix suffix]))
                           (partition 3 1 (text->words text))))))

(defn choose-word [probabilities]
  (when probabilities
    (let [r (rand)]
      (loop [[current & remaining] (into [] probabilities)
             upper-bound (current 1)]
        (let [[subsequent & _] remaining
              suffix (current 0)]
          (if (< r upper-bound)
            suffix
            (recur remaining (+ upper-bound (subsequent 1)))))))))

(defn generate-words
  [input-prefix prefix-map & {:keys [words] :or {words 100}}]
  (let [max-n words]
    (loop [g (vec (str/split input-prefix #"\s+"))
           n 0]
      (let [last-2 (str (last (pop g)) " " (last g))
            next-word (choose-word (get prefix-map last-2))]
        (if (or (> n max-n) (nil? next-word))
          (str/join " " g)
          (recur (conj g next-word) (inc n)))))))

(comment
  ;; Get list of word pairs that have the most suffix options
  (let [prefix-map (prefix-probabilities (sample-text :sherlock))]
    (take 20 (map (fn [[a b]] [a (count b)])
                  (reverse (sort-by (comp count second) (vec prefix-map))))))

  ;; Generate words provided two input words
  (let [prefix-map (prefix-probabilities (sample-text :sherlock))]
    (repeatedly 5 (partial generate-words "the case" prefix-map
                            :words 10))))
