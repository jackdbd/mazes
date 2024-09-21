(ns mazes.algorithm.binary-tree
  "Create a maze with the binary tree algorithm.
  The binary tree algorithm creates a *perfect maze*, namely a maze where every
  cell can reach every other cell by exactly one path.
  We have to visit each cell once to know how the maze will turn out, so this
  algorithm has O(n) complexity.
  Starting from any given cell we either go northward or eastward. This causes
  both the northern row and the eastern column to be unbroken corridors."
  (:require [clojure.spec.alpha :as s]
            [orchestra.spec.test :as st]
            [mazes.grid.common :refer [maybe-p1]]
            [mazes.grid.protocols :refer [link positions]]
            [mazes.grid.rectangular-grid :refer [rect-grid]]
            [random-seed.core :refer [set-random-seed!]])
  (:refer-clojure :exclude [rand-nth]))

(comment
  (st/instrument `maybe-p1)
  (maybe-p1 1 2)
  (maybe-p1 {} [])
  (def grid (rect-grid {:rows 3 :columns 4}))
  (maybe-p1 grid [1 1])
  (st/unstrument `maybe-p1)
  )

(s/fdef gen-binary-tree
  :args (s/cat :grid :unq/grid :p0 :mazes.spec/pos-2d)
  :ret :unq/grid)

(defn gen-binary-tree
  "Reducing function that creates a maze using the binary tree algorithm."
  [grid p0]
  (if-let [p1 (maybe-p1 grid p0)]
    (link grid p0 p1)
    grid))

(s/fdef grid->maze
  :args (s/cat :grid :unq/grid)
  :ret :unq/grid)

(defn grid->maze
  "Converts a grid (with no links) into a maze (with links)."
  [grid] {:pre [(s/valid? :unq/grid grid)]
                   :post [(s/valid? :unq/grid %)]}
  (reduce gen-binary-tree grid (positions grid)))

(comment
  (st/instrument)

  (set-random-seed! 123)
  (def grid (rect-grid {:rows 3 :columns 4}))
  (positions grid)
  (grid->maze grid)

  (st/unstrument)
  )
