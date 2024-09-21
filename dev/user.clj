(ns user
  (:require
   [clojure.string :as str]
   [mazes.algorithm.binary-tree :as btree]
   [mazes.algorithm.sidewinder :as sw]
   [mazes.grid.rectangular-grid :refer [rect-grid]]
   [mazes.renderer.ascii :refer [render]]
   [portal.api :as p]
   [random-seed.core :refer [set-random-seed!]]))

(comment
  ;; This is a reminder on how to animate the maze generation process.
  ;; Store the maze in a :grid key in an atom, so I can store other things in that atom (e.g. renderer)
  (def maze (atom {:grid (rect-grid {:rows 3 :columns 4})}))
  (println (render (:grid @maze)))

  (reset! maze {:grid (btree/gen-binary-tree (:grid @maze) [0 0])}) 
  (println (render (:grid @maze)))

  (reset! maze {:grid (btree/gen-binary-tree (:grid @maze) [0 1])}) 
  (println (render (:grid @maze)))

  (reset! maze {:grid (btree/gen-binary-tree (:grid @maze) [0 2])}) 
  (println (render (:grid @maze)))

  (reset! maze {:grid (btree/gen-binary-tree (:grid @maze) [0 3])}) 
  (println (render (:grid @maze)))

  (reset! maze {:grid (btree/gen-binary-tree (:grid @maze) [1 0])}) 
  (println (render (:grid @maze))) 
  )

(comment
  (def portal (p/open {:window-title "Portal UI"}))
  (add-tap #'p/submit)

  (tap> {:foo "bar"})
  (p/clear)

  (def grid (rect-grid {:rows 3 :columns 4}))
  (tap> grid)

  (set-random-seed! 123)

  (def maze (btree/grid->maze grid))
  (println (render maze)) 

  (println (render (btree/grid->maze grid)))
  (println (render (sw/grid->maze grid)))

  (tap> {:algorithm "binary tree"
         :grid "rectangular"
         :maze (str/split-lines (render (btree/grid->maze grid)))})
  
  (tap> {:algorithm "sidewinder"
         :grid "rectangular"
         :maze (str/split-lines (render (sw/grid->maze grid)))}) 
  
  (p/close portal)
  )