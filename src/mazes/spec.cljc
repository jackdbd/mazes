(ns mazes.spec
  "Specs for the mazes project."
  {:author "Giacomo Debidda"}
  (:require [clojure.spec.alpha :as s]))

(s/def ::rows nat-int?)
(s/def ::columns nat-int?)
(s/def ::row-index int?)
(s/def ::column-index int?)

(comment
  (s/explain ::rows 2.2)
  (s/explain-data ::rows 2.2)
  (s/explain-str ::rows 2.2)
  (s/conform ::rows 2)
  (s/explain ::rows 2)
  (s/conform ::columns 2)
  )

(s/def ::pos-2d (s/tuple ::row-index ::column-index))

(comment
  (s/explain ::pos-2d "")
  (s/explain ::pos-2d [])
  (s/explain ::pos-2d [-1 1])
  (s/explain ::pos-2d [1 2])
  (s/conform ::pos-2d [2 3])
  (s/explain ::pos-2d '(2 3))
  (s/explain ::pos-2d {})
  )

(defn- direction? [x]
  (or (= :north x) (= :east x) (= :south x) (= :west x)))

(s/def ::directions (s/coll-of #{:north :east :south :west}))
(s/def ::direction direction?)

(comment
  (s/explain ::direction :north)  
  (s/explain ::directions [:north :south])
  (s/explain ::directions '(:north :south))
  (s/explain ::directions (set [:north :south]))
  (s/explain ::directions (set (:north :south)))
  )

(s/def ::links (s/map-of ::pos-2d ::directions))

(comment 
  (s/explain ::links {})
  (s/explain ::links {::pos-2d [] ::directions []})
  (s/explain-data ::links {::pos-2d [0 1] ::directions [:south]})
  (s/explain ::links {[0 0] #{:south :east}})
  ;; (def rows 8)
  ;; (def columns 4)
  ;; (s/explain ::links {[rows columns] #{:north}})
  ;; (s/conform ::links {[rows columns] #{:north}})
  )