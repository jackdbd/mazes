(ns mazes.grid.protocols)

(defprotocol IGrid
  "Interface representing a grid of finite dimensions (i.e. the grid has some boundaries)."

  (link [this p0 p1]
    "Creates a bidirectional link between two cells.")
  
  (positions [this]
    "Positions available on the grid.")

  (neighbors [this pos]
    "Neighbors of a position `pos`. A neighbor must fall within the boundaries of the grid.")

  (unlink [this p0 p1]
    "Removes a bidirectional link between two cells.")

  (within-boundaries? [this pos]
    "Predicate that checks whether a position `pos` falls within the boundaries of the grid or not."))