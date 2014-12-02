(ns clojure-roguelike.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [clojure-roguelike.utilities :as util]))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [screen (->> (/ 1 util/tile-size)
                      (orthogonal-tiled-map "level1.tmx")
                      (update! screen :camera (orthographic) :renderer))])
    (assoc (texture "Player.gif")
           :x 0 :y 0 :width util/player-size :height util/player-size :can-move true
           :player? true))

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
      (util/move entities :up screen)
      (= (:key screen) (key-code :dpad-down))
      (util/move entities :down screen)
      (= (:key screen) (key-code :dpad-left))
      (util/move entities :left screen)
      (= (:key screen) (key-code :dpad-right))
      (util/move entities :right screen)))

  :on-touch-down
  (fn [screen entities]
    (cond
      (> (game :y) (* (game :height) (/ 2 3)))
      (util/move entities :up screen)
      (< (game :y) (/ (game :height) 3))
      (util/move entities :down screen)
      (> (game :x) (* (game :height) (/ 2 3)))
      (util/move entities :right screen)
      (< (game :x) (/ (game :height) 3))
      (util/move entities :left screen)))
  
  :on-timer
  (fn [screen entities]
    (let [player (first entities)]
      (case (:id screen)
      :move-lock (replace {player (util/lift-lock player)} entities)
      nil))))

;(defscreen text-screen
;  :on-show
;  (fn [screen entities]
;    (update! screen :camera (orthographic) :renderer (stage))
;    (assoc (label "0" (color :white))
;           :id :fps
;           :x 5))
;
;  :on-render
;  (fn [screen entities]
;    (->> (for [entity entities]
;           (case (:id entity)
;             :fps (doto entity (label! :set-text (str (game :fps))))
;             entity))
;         (render! screen)))
;
;  :on-resize
;  (fn [screen entities]
;    (height! screen 300)))

(defgame clojure-roguelike
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)
                           (set-screen! clojure-roguelike main-screen)))))
