(ns mazes.grid.directions
  (:require [clojure.spec.alpha :as s]
            [mazes.spec]
            [orchestra.spec.test :as st]))

(s/fdef north
  :args (s/cat :pos :mazes.spec/pos-2d)
  :ret :mazes.spec/pos-2d)

(defn north 
  "Returns the coordinates north of the given cell. It does not perform any boundary checks."
  [[y x]] 
  [(dec y) x])

(s/fdef east
  :args (s/cat :pos :mazes.spec/pos-2d)
  :ret :mazes.spec/pos-2d)

(defn east
  "Returns the coordinates east of the given cell. It does not perform any boundary checks."
  [[y x]]
  [y (inc x)])

(s/fdef south
  :args (s/cat :pos :mazes.spec/pos-2d)
  :ret :mazes.spec/pos-2d)

(defn south
  "Returns the coordinates south of the given cell. It does not perform any boundary checks."
  [[y x]]
  [(inc y) x])

(s/fdef west
  :args (s/cat :pos :mazes.spec/pos-2d)
  :ret :mazes.spec/pos-2d)

(defn west
  "Returns the coordinates west of the given cell. It does not perform any boundary checks."
  [[y x]] 
  [y (dec x)])

(comment
  (st/instrument)
  (def point-2d [0 0])
  (north "foo")
  (north [1 2 3])
  (north point-2d)
  (east point-2d)
  (south point-2d)
  (west point-2d)
  (st/unstrument)
  )
