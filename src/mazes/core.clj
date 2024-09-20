(ns mazes.core
  "Maze algorithms from Jamis Buck's book: 'Mazes for Programmers'."
  {:author "Giacomo Debidda"}
  (:gen-class)
  (:require
   [mazes.ascii :as ascii :refer [render]]
   [mazes.binary-tree :refer [maze]]))

(defn -main
  "The entry-point"
  [& args]
  (let [maybe-rows (nth args 0)
        rows (if maybe-rows (Integer/parseInt maybe-rows) 8)
        maybe-cols (nth args 1)
        cols (if maybe-cols (Integer/parseInt maybe-cols) 8)
        maze (maze rows cols)]
    (println "render maze of" rows "rows and" cols "columns")
    (println (render maze))))

(comment
  (-main)

  (def rows "16")
  (def cols "8")
  (-main rows cols))