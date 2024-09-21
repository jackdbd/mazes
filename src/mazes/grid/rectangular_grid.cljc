(ns mazes.grid.rectangular-grid
  "Rectangluar maze grid."
  {:author "Giacomo Debidda"}
  (:require
   [clojure.spec.alpha :as s]
   [mazes.grid.common :refer [direction]]
   [mazes.grid.neighbors :refer [neighbors-2d]]
   [mazes.grid.protocols :refer [IGrid neighbors positions]]
   [mazes.spec]
   [orchestra.spec.test :as st]))

(comment
  (st/instrument `direction)
  (direction "" "")
  (direction [1 1] [0 0])
  
  (direction [1 1] [0 1]) ; N
  (direction [1 1] [1 2]) ; E
  (direction [1 1] [2 1]) ; S
  (direction [1 1] [1 0]) ; W
  (st/unstrument `direction))

;; We have to use unqualified keys to validate record attributes.
;; https://clojure.org/guides/spec#_entity_maps
(s/def :unq/grid
  (s/keys :req-un [:mazes.spec/rows :mazes.spec/columns]
          :opt-un [:mazes.spec/links]))

(s/fdef rect-positions
  ;; :args (s/keys :req-un [:mazes.spec/rows :mazes.spec/columns])
  ;; :args (s/map-of :mazes.spec/rows :mazes.spec/columns) 
  :ret (s/coll-of :mazes.spec/pos-2d))

(defn- rect-positions
  "Positions available on a rows x columns rectangular grid."
  [{:keys [rows columns]}]
  (for [row (range rows)
        col (range columns)]
    [row col]))

(comment
  (st/instrument `rect-positions)
  (rect-positions "")
  (rect-positions {})
  (rect-positions {:rows 3 :columns 4})
  (st/unstrument `rect-positions))

(s/fdef rect-within-boundaries?
  :args (s/cat :grid :unq/grid :pos :mazes.spec/pos-2d)
  :ret boolean?)

(defn- rect-within-boundaries?
  "Predicate that checks whether a position `[row col]` is within the boundaries
   of a rectangular grid or not."
  [{:keys [rows columns]} [row col]]
  (and (>= row 0) (< row rows)
       (>= col 0) (< col columns)))

(comment
  (st/instrument `rect-within-boundaries?)
  (rect-within-boundaries? "" "")
  (rect-within-boundaries? {:rows 3 :columns 4} "")
  (rect-within-boundaries? {:rows 3 :columns 4} [1 2]) ;; within the boundaries of the grid
  (rect-within-boundaries? {:rows 3 :columns 4} [1 999]) ;; outside of the grid
  (st/unstrument `rect-within-boundaries?))

(s/fdef rect-neighbors
  :args (s/cat :grid :unq/grid :pos :mazes.spec/pos-2d)
  :ret (s/coll-of :mazes.spec/pos-2d))

(defn rect-neighbors
  [grid pos]
  (let [pred (partial rect-within-boundaries? grid)]
    (->> (neighbors-2d pos)
         (filter pred)
         set)))

(comment
  (st/instrument)
  (def grid {:rows 3 :columns 4})
  (neighbors-2d [0 0])
  (rect-neighbors grid [0 0]) 
  (neighbors-2d [1 1])
  (rect-neighbors grid [0 1])
  (st/unstrument)
  )

(s/fdef rect-link
  :args (s/cat :grid :unq/grid :p0 :mazes.spec/pos-2d :p1 :mazes.spec/pos-2d)
  :ret :unq/grid)

(defn- rect-link
  "Returns a **new** grid that contains a **bidirectional** link connecting the
   points `p0` and `p1`."
  [grid p0 p1]
  (-> grid
      (update-in [:links p0] #(conj (or % #{}) (direction p0 p1)))
      (update-in [:links p1] #(conj (or % #{}) (direction p1 p0)))))

(comment
  (st/instrument)
  (def grid {:rows 3 :columns 4})
  (def p0 [0 0])
  (def p1 [0 1])
  (rect-link grid p0 p1)
  (st/unstrument `rect-link)
  )

;; A grid has no links at the beginning. Only after running a particular
;; algorithm links are created and the maze takes shape.
(defrecord RectangularGrid [^Integer rows ^Integer columns]
  IGrid
  (link [this p0 p1] (rect-link this p0 p1))
  (neighbors [this pos] (rect-neighbors this pos))
  (positions [this] (rect-positions this))
  (within-boundaries? [this pos] (rect-within-boundaries? this pos)))

(defn rect-grid
  "Instantiates a RectangularGrid."
  [{:keys [rows columns]}]
  (let [grid (->RectangularGrid rows columns)]
    (if-not (s/valid? :unq/grid grid)
      (throw (ex-info (s/explain-str :unq/grid grid)
                      (s/explain-data :unq/grid grid)))
      grid)))

(comment
  (st/instrument)
  (def grid (rect-grid {:rows 4 :columns 3}))

  (positions grid)

  (neighbors-2d [0 0])
  (neighbors grid [0 0])

  (neighbors-2d [1 2])
  (neighbors grid [1 2])

  (neighbors-2d [99 99])
  (neighbors grid [99 99])
  (st/unstrument)
  )

