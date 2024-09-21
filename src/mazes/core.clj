(ns mazes.core
  "Mazes from Jamis Buck's book: 'Mazes for Programmers'."
  {:author "Giacomo Debidda"}
  (:gen-class)
  (:require
   [mazes.algorithm.binary-tree :as btree]
   [mazes.algorithm.sidewinder :as sw]
   [mazes.grid.rectangular-grid :refer [rect-grid]]
   [mazes.renderer.ascii :refer [render]]))

(defn -main
  "The entry-point"
  [& args]
  (let [maybe-rows (nth args 0)
        maybe-cols (nth args 1)
        rows (if maybe-rows (Integer/parseInt maybe-rows) 8)
        cols (if maybe-cols (Integer/parseInt maybe-cols) 8)
        grid (rect-grid {:rows rows :columns cols})
        grid-type "reactangular"]
    (println "Binary tree algorithm" (str "(grid: " grid-type ",") (str "rows: " rows ",") (str "columns: " cols ")"))
    (println (render (btree/grid->maze grid)))
    (println "Sidewinder algorithm" (str "(grid: " grid-type ",") (str "rows: " rows ",") (str "columns: " cols ")"))
    (println (render (sw/grid->maze grid)))))

(comment
  (-main)

  (def rows "16")
  (def cols "8")
  (-main rows cols)

  (def grid (rect-grid {:rows 4 :columns 6}))

  (println (render (btree/grid->maze grid)))
  (println (render (sw/grid->maze grid)))
  )