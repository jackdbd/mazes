(ns mazes.grid.common
  (:require [clojure.spec.alpha :as s]
            [mazes.grid.directions :refer [north east]]
            [mazes.grid.protocols :refer [positions within-boundaries?]]
            [random-seed.core :refer [rand-nth]])
  (:refer-clojure :exclude [rand-nth]))

(defn rows-seq
  [grid]
  (->> (positions grid)
       (group-by first)
       (vals)))

(defn columns-seq
  [grid]
  (->> (positions grid)
       (group-by second)
       (vals)))

(s/fdef maybe-p1
  :args (s/cat :grid :unq/grid :p0 :mazes.spec/pos-2d)
  :ret :mazes.spec/pos-2d)

(defn maybe-p1
  "Start from position p0 and try to reach a new position p1, either by moving
  northward or eastward (this function picks randomly when both choices are
  available). Return p1 if succeeded. Return nil if either the starting position
  p0, or the ending position p1 is not on the grid."
  [grid [row col :as p0]]
  (let [last-col (dec (:columns grid))
        direction (rand-nth [:north :east])]
    (cond
      (not (within-boundaries? grid p0)) nil
      (and (= row 0) (= col last-col)) nil
      (= row 0) (east p0)
      (= col last-col) (north p0)
      (= :east direction) (east p0)
      (= :north direction) (north p0))))

(defn next-pos-outside-grid?
  [grid direction pos]
  (not (within-boundaries? grid (direction pos))))

;; https://clojure.github.io/spec.alpha/clojure.spec.alpha-api.html#clojure.spec.alpha/fdef
(s/fdef direction
  :args (s/cat :p0 :mazes.spec/pos-2d :p1 :mazes.spec/pos-2d)
  :ret :mazes.spec/direction)

(defn direction
  "Computes the direction from a *source* cell `[r0 c0]` to a *destination* cell `[r1 c1]`.

   Cell `[0 0]` is in the top-left corner of the grid, so for example if we
   move to the right (i.e. west), we end up at cell `[0 1]`."
  [[r0 c0] [r1 c1]]
  (case [(- r0 r1) (- c0 c1)]
    [0 1] :west
    [0 -1] :east
    [1 0] :north
    [-1 0] :south
    (throw (ex-info "p0 (source) and p1 (destination) are not neighbors, so no direction can be computed"
                    {:p0 [r0 c0] :p1 [r1 c1] :cause :not-neighbors}))))