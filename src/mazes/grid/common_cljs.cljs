(ns mazes.grid.common-cljs
  (:require [mazes.grid.common :refer [direction]]))

(comment
  ;; This catch syntax is only for ClojureScript
  ;; https://clojuredocs.org/clojure.core/catch
  (try (direction [1 1] [0 0])
       (catch :default e
         (prn "Caught exception")
         (prn (ex-message e))
         (ex-data e)))

  (direction [1 1] [0 1]) ; N
  (direction [1 1] [1 2]) ; E
  (direction [1 1] [2 1]) ; S
  (direction [1 1] [1 0]) ; W
  )