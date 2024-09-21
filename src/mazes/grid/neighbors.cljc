(ns mazes.grid.neighbors
  (:require [clojure.spec.alpha :as s]
            [mazes.spec]
            [mazes.grid.directions :refer [north east south west]]
            [orchestra.spec.test :as st]))

(s/fdef neighbors-2d
  :args (s/cat :pos :mazes.spec/pos-2d)
  :ret (s/coll-of :mazes.spec/pos-2d))

(defn neighbors-2d
  [pos]
  #{(north pos) (east pos) (south pos) (west pos)})  

(comment
  (st/instrument)
  (neighbors-2d [0 0])
  (neighbors-2d [99 99])
  (st/unstrument)
  )