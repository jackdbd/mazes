(ns mazes.binary-tree
  "Create a maze with the binary tree algorithm.
  The binary tree algorithm creates a *perfect maze*, namely a maze where every
  cell can reach every other cell by exactly one path.
  We have to visit each cell once to know how the maze will turn out, so this
  algorithm has O(n) complexity.
  Starting from any given cell we either go northward or eastward. This causes
  both the northern row and the eastern column to be unbroken corridors."
  (:require
   [clojure.spec.alpha :as s]
   [mazes.grid :as g]))

;; TODO: how to use fdef?

;; (s/fdef maybe-p1
;;   :args (s/cat :grid :unq/grid :p0 ::g/pos-2d)
;;   :ret (s/nilable boolean?))
(defn maybe-p1
  "Start from position p0 and try to reach a new position p1, either by moving
  northward or eastward (this function picks randomly when both choices are
  available). Return p1 if succeeded. Return nil if either the starting position
  p0, or the ending position p1 is not on the board."
  [grid [row col :as pos]] {:pre [(s/and (s/valid? :unq/grid grid)
                                         (s/valid? ::g/pos-2d pos))]
                            :post [(s/valid? (s/nilable ::g/pos-2d) %)]}
  (let [last-col (dec (:columns grid))
        direction (rand-nth [:north :east])]
    (cond
      (not (g/has-pos? grid pos)) nil
      (and (= row 0) (= col last-col)) nil
      (= row 0) (g/east grid pos)
      (= col last-col) (g/north grid pos)
      (= :east direction) (g/east grid pos)
      (= :north direction) (g/north grid pos))))

(s/fdef reducer
  :args (s/cat :grid :unq/grid :p0 ::g/pos-2d)
  :ret :unq/grid)
(defn reducer
  "Reducing function to create bidirectional links between cells."
  [grid p0]
  (if-let [p1 (maybe-p1 grid p0)]
    (g/link! grid p0 p1)
    grid))

(defn grid->maze
  "Convert a grid (with no links) to a maze (with links)."
  [grid] {:pre [(s/valid? :unq/grid grid)]
          :post [(s/valid? :unq/grid %)]}
  (reduce reducer grid (g/positions grid)))

(defn maze
  "Factory function to create a maze with the binary tree algorithm."
  [rows columns]
  (grid->maze (g/rect-grid rows columns)))
