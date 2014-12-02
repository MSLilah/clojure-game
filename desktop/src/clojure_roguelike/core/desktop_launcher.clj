(ns clojure-roguelike.core.desktop-launcher
  (:require [clojure-roguelike.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. clojure-roguelike "clojure-roguelike" 800 600)
  (Keyboard/enableRepeatEvents true))
