(ns user
  (:require [mazes.ascii :as ascii :refer [render]]
            [mazes.binary-tree :refer [maze]]))

(prn "hello from user.clj")

(comment
  (println (render (maze 3 4)))
  (println (render (maze 8 8)))
  )