(ns user
  (:require [mazes.ascii :as ascii :refer [render]]
            [mazes.binary-tree :refer [maze]]
            [portal.api :as p]))

(comment
  (def portal (p/open {:window-title "Portal UI"}))
  (add-tap #'p/submit)

  (tap> {:foo "bar"})
  (p/clear)

  (tap> (maze 3 4))

  (println (render (maze 3 4)))
  (println (render (maze 8 8)))

  (p/close portal)
  )