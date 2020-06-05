(ns mazes.core
  "Maze algorithms from Jamis Buck's 'Mazes for Programmers' book."
  (:gen-class)
  (:require
   [mazes.ascii :as ascii :refer [render]]
   [mazes.binary-tree :refer [maze]]))

(defn -main
  "The entry-point for 'lein run'"
  []
  (let [maze (maze 8 8)]
    (println (render maze))))
