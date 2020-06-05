(ns mazes.ascii
  "ASCII representation of a maze."
  (:require
   [clojure.string :as string]))

(def ^:private corner "+")
(def ^:private horizontal-wall "---")
(def ^:private vertical-wall "|")
(def ^:private inside-cell "   ")
(def ^:private vertical-link " ")
(def ^:private horizontal-link "   ")

(defn bottom-maze-boundary
  "Render the boundary at the bottom of the maze."
  [columns]
  (string/join (flatten (concat corner
                                (repeat columns (concat horizontal-wall
                                                        corner))))))

(defn row-top-level
  "Render the top level of a given row."
  [{:keys [columns] :as maze} row]
  (let [f (fn [col]
            (let [links (get-in maze [:links [row col]])]
              [corner
               (if (contains? links :north)
                 horizontal-link
                 horizontal-wall)]))]
    (string/join (flatten (concat (map f (range columns))
                                  corner)))))

(defn row-middle-level
  "Render the middle level of a given row."
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

(defn row-level
  "Render a given row in the maze."
  [maze row]
  (str (row-top-level maze row) "\n" (row-middle-level maze row)))

(defn render
  "Render a maze with ASCII art."
  [{:keys [rows columns] :as maze}]
  (let [f (fn [row]
            (str (row-level maze row) "\n"))]
    (str (apply str (map f (range rows)))
         (bottom-maze-boundary columns))))
