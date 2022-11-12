(ns mazes.core
  "Maze algorithms from Jamis Buck's book: 'Mazes for Programmers'."
  (:gen-class)
  (:require
   [mazes.ascii :as ascii :refer [render]]
   [mazes.binary-tree :refer [maze]]))

(defn -main
  "The entry-point"
  []
  (let [maze (maze 8 8)]
    (println (render maze))))

(comment
  (-main))