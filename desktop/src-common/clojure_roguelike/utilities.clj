(ns clojure-roguelike.utilities
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]))

; CONSTANTS

(def player-size 2)
(def map-size 50)
(def tile-size 8)
(def vertical-tiles 20)

(defn move-player
  [player direction]
  (case direction
    :down  (assoc player :y (- (:y player) player-size))
    :up    (assoc player :y (+ (:y player) player-size))
    :left  (assoc player :x (- (:x player) player-size))
    :right (assoc player :x (+ (:x player) player-size))
    nil))

(defn valid-position
  [entity]
  (let [x-pos (:x entity)
        y-pos (:y entity)]
    (and (and (>= x-pos 0) (< x-pos map-size))
         (and (>= y-pos 0) (< y-pos map-size)))))

(defn check-overlap
  [entity1 entity2]
  (and (= (:x entity1) (:x entity2))
       (= (:y entity1) (:y entity2))))

(defn move
  [entities direction screen text-screen]
  (let [player (first entities)
        lich   (second entities)]
    (if (:can-move player)
      (let [moved-player (assoc (move-player player direction) :can-move false)]
        (if (valid-position moved-player)
          (do
            (position! screen (:x moved-player) (:y moved-player))
            (if (check-overlap moved-player lich)
              (screen! text-screen :on-status-change :game-over? true)
              (add-timer! screen :move-lock 0.5))
            (replace {player moved-player} entities))
          entities)))))

(defn lift-lock
  [player]
  (assoc player :can-move true))