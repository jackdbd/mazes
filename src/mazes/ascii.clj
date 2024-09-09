(ns mazes.ascii
  "ASCII representation of a maze."
  (:require
   [clojure.string :as str]
   [clojure.spec.alpha :as s]
   [orchestra.core :refer [defn-spec]]
   [orchestra.spec.test :as st]))

(def ^:private corner "+")
(def ^:private horizontal-wall "---")
(def ^:private vertical-wall "|")
(def ^:private inside-cell "   ")
(def ^:private vertical-link " ")
(def ^:private horizontal-link "   ")

(declare bottom-maze-boundary)

(s/def ::maze (s/keys :req-un [:mazes.spec/rows :mazes.spec/columns]))

(s/def ::maze-cols (s/keys :req-un [:mazes.spec/columns]
                           :opt-un [:mazes.spec/rows]))

(defn-spec bottom-maze-boundary string?
  [columns pos-int?]
  (str/join (flatten (concat corner
                             (repeat columns (concat horizontal-wall
                                                     corner))))))

(comment
  (st/instrument `bottom-maze-boundary)
  (def cols 3)
  (bottom-maze-boundary "")
  (bottom-maze-boundary cols)
  (st/unstrument `bottom-maze-boundary))

(s/fdef row-top-level
  :args (s/cat :maze ::maze-cols :row nat-int?)
  :ret string?)

(defn row-top-level
  "Renders the top level of a given row of the maze."
  [{:keys [columns] :as maze} row]
  (let [f (fn [col]
            (let [links (get-in maze [:links [row col]])]
              [corner
               (if (contains? links :north)
                 horizontal-link
                 horizontal-wall)]))]
    (str/join (flatten (concat (map f (range columns))
                                  corner)))))

(comment
  (st/instrument `row-top-level)
  (row-top-level {:columns 3} "")
  (row-top-level {:columns 3} 2)
  (st/unstrument `row-top-level)
  )

(s/fdef row-middle-level
  :args (s/cat :maze ::maze-cols :row nat-int?)
  :ret string?)

(defn row-middle-level
  "Renders the middle level of a given row of the maze."
  [{:keys [columns] :as maze} row]
  (let [f (fn [col]
            (let [links (get-in maze [:links [row col]])
                  last-col (dec columns)]
              [(if (contains? links :west)
                 vertical-link
                 vertical-wall)
              ;;  (format "%d;%d" row col)
               inside-cell
               (when (= last-col col) vertical-wall)]))]
    (apply str (flatten (map f (range columns))))))

(comment
  (st/instrument `row-middle-level)
  (row-middle-level {:columns 3} "")
  (row-middle-level {:columns 3} 2)
  (st/unstrument `row-middle-level)
  )

(s/fdef row-level
  :args (s/cat :maze ::maze-cols :row nat-int?)
  :ret string?)

(defn row-level
  "Renders a given row in the maze."
  [maze row]
  (str (row-top-level maze row) "\n" (row-middle-level maze row)))

(comment
  (st/instrument `row-level)
  (row-level {:columns 3} "")
  (row-level {:columns 3} 2)
  (st/unstrument `row-level)
  )

(s/fdef render
  :args (s/cat :maze ::maze)
  :ret string?)

(defn render
  "Renders a maze with ASCII art."
  [{:keys [rows columns] :as maze}]
  (let [f (fn [row]
            (str (row-level maze row) "\n"))]
    (str (apply str (map f (range rows)))
         (bottom-maze-boundary columns))))

(comment
  (st/instrument `render)
  (render {:columns 0 :rows -1}) 
  (render {:columns 3 :rows 4})
  (st/unstrument `render)
  )
