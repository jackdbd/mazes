(ns mazes.grid
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::rows nat-int?)
(s/def ::columns nat-int?)

(s/def ::pos-2d (s/tuple nat-int? nat-int?))

(defn- direction? [x]
  (or (= :north x) (= :east x) (= :south x) (= :west x)))

(s/def ::directions (s/coll-of #{:north :east :south :west}))
(s/def ::direction direction?)

(s/def ::links (s/map-of ::pos-2d ::directions))

;; We have to use unqualified keys to validate record attributes.
;; https://clojure.org/guides/spec#_entity_maps
(s/def :unq/grid
  (s/keys :req-un [::rows ::columns]
          :opt-un [::links]))

;; the value passed to :fn is {:args conformed-args :ret conformed-ret}
;; https://clojure.github.io/spec.alpha/clojure.spec.alpha-api.html#clojure.spec.alpha/fdef
(s/fdef direction
  :args (s/cat :p0 ::pos-2d :p1 ::pos-2d)
  :ret ::direction)
(defn- direction
  "Get the direction from p0 (row r0, col c0) to p1 (row r1, col c1)."
  [[r0 c0] [r1 c1]]
  (case [(- r0 r1) (- c0 c1)]
    [0 1] :west
    [0 -1] :east
    [1 0] :north
    [-1 0] :south
    (throw (ex-info "p0 and p1 are not neighbors, so no direction can be computed" {}))))

(defprotocol IGrid
  "An abstraction of a grid composed of positions."
  (has-pos? [this p] "Predicate to test whether an arbitrary position is within the boundaries of the grid or not.")
  (neighbors [this p] "Neighbors of a position on the grid.")
  (positions [this] "Positions available on the grid."))

(defprotocol ILinkable
  (link! [this p0 p1] "Create a bidirectional link between 2 cells."))

(defn- rect-positions
  [{:keys [rows columns]}]
  (for [row (range rows)
        col (range columns)]
    [row col]))

(defmacro maybe-new-pos
  "Return nil if either pos or new-pos falls outside of the grid.
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

(comment
  (defn north-west [grid [row col :as pos]]
    (maybe-new-pos grid pos [(dec row) (dec col)])))

(defn- rect-neighbors
  [grid pos]
  {::north (north grid pos)
   ::east (east grid pos)
   ::south (south grid pos)
   ::west (west grid pos)})

(s/fdef rect-has-pos?
  :args (s/cat :grid :unq/grid :pos ::pos-2d)
  :ret boolean?)
(defn- rect-has-pos?
  [{:keys [rows columns]} [row col]]
  (and (>= row 0) (< row rows)
       (>= col 0) (< col columns)))

(s/fdef rect-link!
  :args (s/cat :grid :unq/grid :p0 ::pos-2d :p1 ::pos-2d)
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
  "Factory function to create a RectangularGrid record instance."
  [rows columns]
  (let [grid (->RectangularGrid rows columns)]
    (if-not (s/valid? :unq/grid grid)
      (throw (ex-info (s/explain-str :unq/grid grid)
                      (s/explain-data :unq/grid grid)))
      grid)))
