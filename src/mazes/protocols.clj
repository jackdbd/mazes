(ns mazes.protocols)

(defprotocol IGrid
  "Abstraction of a grid composed of positions."
  (has-pos? [this p] "Checks whether an arbitrary position is within the boundaries of the grid or not.")
  (neighbors [this p] "Neighbors of a position on the grid.")
  (positions [this] "Positions available on the grid."))

(defprotocol ILinkable
  "Abstraction representing the concept that two points can be linked."
  (link! [this p0 p1] "Creates a bidirectional link between 2 cells."))
