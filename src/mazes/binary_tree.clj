(ns mazes.binary-tree
  "Create a maze with the binary tree algorithm.
  The binary tree algorithm creates a *perfect maze*, namely a maze where every
  cell can reach every other cell by exactly one path.
  We have to visit each cell once to know how the maze will turn out, so this
  algorithm has O(n) complexity.
  Starting from any given cell we either go northward or eastward. This causes
  both the northern row and the eastern column to be unbroken corridors."
  (:require [clojure.spec.alpha :as s]
            [orchestra.spec.test :as st]
            [mazes.grid :as g]
            [mazes.protocols :refer [has-pos? link! positions]]))

(s/fdef maybe-p1
  :args (s/cat :grid :unq/grid :p0 :mazes.spec/pos-2d)
  :ret :mazes.spec/pos-2d)

(defn maybe-p1
  "Start from position p0 and try to reach a new position p1, either by moving
  northward or eastward (this function picks randomly when both choices are
  available). Return p1 if succeeded. Return nil if either the starting position
  p0, or the ending position p1 is not on the board."
  [grid [row col :as pos]]
  (let [last-col (dec (:columns grid))
        direction (rand-nth [:north :east])]
    (cond
      (not (has-pos? grid pos)) nil
      (and (= row 0) (= col last-col)) nil
      (= row 0) (g/east grid pos)
      (= col last-col) (g/north grid pos)
      (= :east direction) (g/east grid pos)
      (= :north direction) (g/north grid pos))))

(comment
  (st/instrument `maybe-p1)
  (maybe-p1 1 2)
  (maybe-p1 {} [])
  (def gr (g/rect-grid 3 4))
  (maybe-p1 gr [1 1])
  (st/unstrument `maybe-p1)
  )

(s/fdef reducer
  :args (s/cat :grid :unq/grid :p0 :mazes.spec/pos-2d)
  :ret :unq/grid)

(defn reducer
  "Reducing function to create bidirectional links between cells."
  [grid p0]
  (if-let [p1 (maybe-p1 grid p0)]
    (link! grid p0 p1)
    grid))

(comment
  (st/instrument `reducer)
  (reducer {:rows 3 :columns 4} [1 1])
  (def gr (g/rect-grid 3 4))
  (reducer gr [1 1])
  (st/unstrument `reducer))

(s/fdef grid->maze
  :args (s/cat :grid :unq/grid)
  :ret :unq/grid)

(defn grid->maze
  "Converts a grid (with no links) into a maze (with links)."
  [grid] {:pre [(s/valid? :unq/grid grid)]
          :post [(s/valid? :unq/grid %)]}
  (reduce reducer grid (positions grid)))

(comment
  (st/instrument `grid->maze)
  (grid->maze {:rows 3 :columns 4})
  (def gr (g/rect-grid 3 4))
  (grid->maze gr)
  (st/unstrument `grid->maze))

(s/fdef maze
  :args (s/cat :rows :mazes.spec/rows :columns :mazes.spec/columns)
  :ret :mazes.ascii/maze)

(defn maze
  "Factory function to create a maze with the binary tree algorithm."
  [rows columns]
  (grid->maze (g/rect-grid rows columns)))

(comment
  (st/instrument `maze)
  (maze "" "")
  (maze 3 4)
  (st/unstrument `maze))