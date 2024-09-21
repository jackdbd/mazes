(ns mazes.algorithm.sidewinder
  (:require
   [clojure.spec.alpha :as s]
   [mazes.grid.common :refer [columns-seq rows-seq next-pos-outside-grid?]]
   [mazes.grid.directions :refer [north east south west]]
   [mazes.grid.protocols :refer [link within-boundaries?]]
   [mazes.grid.rectangular-grid :refer [rect-grid]]
   [orchestra.spec.test :as st]
   [random-seed.core :refer [rand-int rand-nth set-random-seed!]])
  (:refer-clojure :exclude [rand-int rand-nth]))

(comment
  (def grid (rect-grid {:rows 4 :columns 6}))

  (def pos [0 0])
  (def northward-pos (north pos))
  (def eastward-pos (east pos))
  (def southward-pos (south pos))
  (def westward-pos (west pos))

  (def top-left [0 0])
  (within-boundaries? grid top-left)
  (next-pos-outside-grid? grid north top-left)
  (next-pos-outside-grid? grid east top-left)

  (def bottom-right [3 5])
  (within-boundaries? grid bottom-right)
  (next-pos-outside-grid? grid north bottom-right)
  (next-pos-outside-grid? grid east bottom-right)
  )

(s/fdef gen-sidewinder
  :args (s/cat :grid :unq/grid :row (s/coll-of :mazes.spec/pos-2d))
  :ret :unq/grid)

(defn gen-sidewinder
  "Reducing function that creates a maze using the Sidewinder algorithm.
   
   In the Sidewinder algorithm we have a *run* of cells. We keep running as long
   as we flip the coin and it's tails. As soon as we get heads or reach the
   eastern boundary of the grid, we stop and choose a cell at random from the
   ones we visited. This is called *closing out the run*."
  [grid row]
  (let [next-pos-outside-northern-boundary? (partial next-pos-outside-grid? grid north)
        next-pos-outside-eastern-boundary? (partial next-pos-outside-grid? grid east)
        visited (atom [])] ;; cells visited during this run
    (reduce (fn [grid pos]
              (swap! visited conj pos)
              (let [eastern-boundary? (next-pos-outside-eastern-boundary? pos)
                    norther-boundary? (next-pos-outside-northern-boundary? pos)
                    close-out?        (or eastern-boundary? (and (not norther-boundary?)
                                                                 (= 0 (rand-int 2))))]
                (if close-out?
                  (let [member (rand-nth @visited)]
                    (reset! visited [])
                    (if (within-boundaries? grid (north member))
                      (link grid member (north member))
                      grid))
                  (link grid pos (east pos)))))
            grid row)))
(comment
  (st/instrument)
  (def grid (rect-grid {:rows 3 :columns 4}))
  (gen-sidewinder grid [[0 1] [0 2] [0 3]])
  (st/unstrument)
  )

(defn grid->maze
  "Converts a grid (with no links) into a maze (with links)."
  [grid] {:pre [(s/valid? :unq/grid grid)]
         :post [(s/valid? :unq/grid %)]}
  (reduce gen-sidewinder grid (rows-seq grid)))

(comment
  (st/instrument)
  (def grid (rect-grid {:rows 3 :columns 4}))

  (rows-seq grid)
  (columns-seq grid)

  (set-random-seed! 123)
  (grid->maze grid)
  (st/unstrument)
  )