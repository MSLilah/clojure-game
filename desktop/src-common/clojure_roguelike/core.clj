(ns clojure-roguelike.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [clojure-roguelike.utilities :as util]))

(declare clojure-roguelike main-screen text-screen)

(defscreen text-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic) :renderer (stage))
    (assoc (label "  " (color :white))
           :id :status-message
           :x 5))

  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-status-change
  (fn [screen entities]
    (let [message (if (:game-over? screen)
                    "Game Over"
                    "You Win!")
          label (first entities)
          x-pos (- (/ (width screen) 2) (/ (label! label :get-width) 2))
          y-pos (- (/ (height screen) 2) (/ (label! label :get-height) 2))]
      (label! (first entities) :set-text message)
      (assoc label :x x-pos :y y-pos)))

  :on-resize
  (fn [screen entities]
    (height! screen 300)
    (let [label (first entities)
          x-pos (- (/ (width screen) 2) (/ (label! label :get-width) 2))
          y-pos (- (/ (height screen) 2) (/ (label! label :get-height) 2))]
      (assoc label :x x-pos :y y-pos))))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [screen  (->> (/ 1 util/tile-size)
                       (orthogonal-tiled-map "level1.tmx")
                       (update! screen :camera (orthographic) :renderer))
          player  (assoc (texture "Player.gif")
                         :x 0 :y 0 :width util/player-size :height util/player-size :can-move true
                         :player? true)
          monster (assoc (texture "Lich.gif")
                         :x 10 :y 10 :width util/player-size :height util/player-size
                         :player? false :enemy true)]
      [player monster]))

  :on-resize
  (fn [screen entities]
    (height! screen util/vertical-tiles))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))

  :on-key-down
  (fn [screen entities]
    (cond
      (= (:key screen) (key-code :dpad-up))
      (util/move entities :up screen text-screen)
      (= (:key screen) (key-code :dpad-down))
      (util/move entities :down screen text-screen)
      (= (:key screen) (key-code :dpad-left))
      (util/move entities :left screen text-screen)
      (= (:key screen) (key-code :dpad-right))
      (util/move entities :right screen text-screen)
      (= (:key screen) (key-code :r))
      (set-screen! clojure-roguelike main-screen text-screen)))

  :on-touch-down
  (fn [screen entities]
    (cond
      (> (game :y) (* (game :height) (/ 2 3)))
      (util/move entities :up screen text-screen)
      (< (game :y) (/ (game :height) 3))
      (util/move entities :down screen text-screen)
      (> (game :x) (* (game :height) (/ 2 3)))
      (util/move entities :right screen text-screen)
      (< (game :x) (/ (game :height) 3))
      (util/move entities :left screen text-screen)))
  
  :on-timer
  (fn [screen entities]
    (let [player (first entities)]
      (case (:id screen)
      :move-lock (replace {player (util/lift-lock player)} entities)
      nil))))

(defgame clojure-roguelike
  :on-create
  (fn [this]
    (set-screen! this main-screen text-screen)))

(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)
                           (set-screen! clojure-roguelike main-screen text-screen)))))
