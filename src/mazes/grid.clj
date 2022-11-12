(ns mazes.grid
  (:require
   [clojure.spec.alpha :as s]
   [mazes.spec]
   [orchestra.spec.test :as st]))

(set! *warn-on-reflection* true)

;; the value passed to :fn is {:args conformed-args :ret conformed-ret}
;; https://clojure.github.io/spec.alpha/clojure.spec.alpha-api.html#clojure.spec.alpha/fdef
(s/fdef direction
  :args (s/cat :p0 :mazes.spec/pos-2d :p1 :mazes.spec/pos-2d)
  :ret any?)

(defn- direction
  "Get the direction from p0 (row r0, col c0) to p1 (row r1, col c1)."
  [[r0 c0] [r1 c1]]
  (case [(- r0 r1) (- c0 c1)]
    [0 1] :west
    [0 -1] :east
    [1 0] :north
    [-1 0] :south
    (throw (ex-info "p0 (origin) and p1 (destination) are not neighbors, so no direction can be computed"
                    {:p0 [r0 c0] :p1 [r1 c1] :cause :not-neighbors}))))

(comment
  (st/instrument `direction)

  (direction "" "")

  (direction [1 1] [0 0])

  (try (direction [1 1] [0 0])
       (catch clojure.lang.ExceptionInfo e
         (prn "Caught exception")
         (prn (ex-message e))
         (ex-data e)))

  (direction [1 1] [0 1]) ; N
  (direction [1 1] [1 2]) ; E
  (direction [1 1] [2 1]) ; S
  (direction [1 1] [1 0]) ; W

  (st/unstrument `direction))

(defprotocol IGrid
  "An abstraction of a grid composed of positions."
  (has-pos? [this p] "Checks whether an arbitrary position is within the boundaries of the grid or not.")
  (neighbors [this p] "Neighbors of a position on the grid.")
  (positions [this] "Positions available on the grid."))

(defprotocol ILinkable
  (link! [this p0 p1] "Creates a bidirectional link between 2 cells."))

;; We have to use unqualified keys to validate record attributes.
;; https://clojure.org/guides/spec#_entity_maps
(s/def :unq/grid
  (s/keys :req-un [:mazes.spec/rows :mazes.spec/columns]
          :opt-un [:mazes.spec/links]))

(s/fdef rect-positions
  :args (s/cat :grid ::grid)
  :ret (s/coll-of :mazes.spec/pos-2d))

(defn- rect-positions
  [{:keys [rows columns]}]
  (for [row (range rows)
        col (range columns)]
    [row col]))

(comment
  (st/instrument `rect-positions)
  (rect-positions "")
  (rect-positions {:rows 3 :columns 4})
  (st/unstrument `rect-positions))

(s/fdef rect-has-pos?
  :args (s/cat :grid ::grid :pos :mazes.spec/pos-2d)
  :ret boolean?)

(defn- rect-has-pos?
  [{:keys [rows columns]} [row col]]
  (and (>= row 0) (< row rows)
       (>= col 0) (< col columns)))

(comment
  (st/instrument `rect-has-pos?)
  (rect-has-pos? "" "")
  (rect-has-pos? {:rows 3 :columns 4} "")
  (rect-has-pos? {:rows 3 :columns 4} [1 2])
  (rect-has-pos? {:rows 3 :columns 4} [1 999])
  (st/unstrument `rect-has-pos?))

(defmacro maybe-new-pos
  "Returns nil if either pos or new-pos falls outside of the grid.
  Otherwise return new-pos."
  [grid pos new-pos]
  `(if (has-pos? ~grid ~pos)
     (when (has-pos? ~grid ~new-pos) ~new-pos)
     nil))

(defn north [grid [row col :as pos]]
  (maybe-new-pos grid pos [(dec row) col]))

(defn east [grid [row col :as pos]]
  (maybe-new-pos grid pos [row (inc col)]))

(defn south [grid [row col :as pos]]
  (maybe-new-pos grid pos [(inc row) col]))

(defn west [grid [row col :as pos]]
  (maybe-new-pos grid pos [row (dec col)]))

(s/fdef rect-neighbors
  :args (s/cat :grid :unq/grid :pos :mazes.spec/pos-2d)
  :ret string?)

(defn- rect-neighbors
  [grid pos]
  {::north (north grid pos)
   ::east (east grid pos)
   ::south (south grid pos)
   ::west (west grid pos)})

(s/fdef rect-link!
  :args (s/cat :grid :unq/grid :p0 :mazes.spec/pos-2d :p1 :mazes.spec/pos-2d)
  :ret :unq/grid)

(defn- rect-link!
  "Return a new grid that contains a bidirectional link connecting p0 and p1."
  [grid p0 p1]
  (-> grid
      (update-in [:links p0] #(conj (or % #{}) (direction p0 p1)))
      (update-in [:links p1] #(conj (or % #{}) (direction p1 p0)))))

;; A grid has no links at the beginning. Only when running a particular
;; algorithm links are created and the maze takes shape.
(defrecord RectangularGrid [^Integer rows ^Integer columns]
  IGrid
  (has-pos? [this pos] (rect-has-pos? this pos))
  (neighbors [this pos] (rect-neighbors this pos))
  (positions [this] (rect-positions this))

  ILinkable
  (link! [this p0 p1] (rect-link! this p0 p1)))

(defn rect-grid
  "Factory function that returns a RectangularGrid record instance."
  [rows columns]
  (let [grid (->RectangularGrid rows columns)]
    (if-not (s/valid? :unq/grid grid)
      (throw (ex-info (s/explain-str :unq/grid grid)
                      (s/explain-data :unq/grid grid)))
      grid)))

(comment
  (st/instrument `rect-neighbors)
  (def rows 4)
  (def columns 3)
  (rect-neighbors "" "")
  (rect-neighbors {:rows rows :columns columns} "")
  (rect-neighbors {:rows rows :columns columns} [1 2])

  (def gr (rect-grid rows columns))
  (s/explain :unq/grid gr) 

  (rect-neighbors gr "")
  (rect-neighbors gr [1 2])

  (st/unstrument `rect-neighbors))
