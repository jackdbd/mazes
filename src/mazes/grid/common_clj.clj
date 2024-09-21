(ns mazes.grid.common-clj
  (:require [mazes.grid.common :refer [direction]]))

(set! *warn-on-reflection* true)

(comment
  ;; This catch syntax is only for Clojure
  ;; https://clojuredocs.org/clojure.core/catch
  (try (direction [1 1] [0 0])
       (catch clojure.lang.ExceptionInfo e
         (prn "Caught exception")
         (prn (ex-message e))
         (ex-data e)))

  (direction [1 1] [0 1]) ; N
  (direction [1 1] [1 2]) ; E
  (direction [1 1] [2 1]) ; S
  (direction [1 1] [1 0]) ; W
  )