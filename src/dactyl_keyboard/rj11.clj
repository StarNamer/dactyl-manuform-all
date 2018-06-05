(ns dactyl-keyboard.rj11
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [dactyl-keyboard.placement :refer [key-place]]
            [dactyl-keyboard.layout :refer [key-place-fn rj11-socket-at rj11-socket-region]]
            [dactyl-keyboard.switch-hole :refer [mount-height]]
            [dactyl-keyboard.sides
             :refer [sides-radius
                     sides-downness
                     partial-sides]]
            [unicode-math.core :refer :all]))

;; https://www.mouser.com/ds/2/18/61835-1003706.pdf
(def rj11-face-width 11.18)
(def rj11-face-height 15.87)
(def rj11-body-width 13.72)
(def rj11-cutout-depth 20)
(def rj11-plate-thickness 12)
(def rj11-bezel 3)


(def rj11-cutout
  (cube rj11-body-width rj11-cutout-depth rj11-face-height))

(defn rj11-plate-shape [margin thickness]
  (cube (+ rj11-body-width margin)
        thickness
        (+ rj11-face-height margin)))

(defn rj11-cutout-place [shape]
  (->> shape
       (translate [0 (/ mount-height 2) 0])
       (translate [0 sides-radius
                   (- sides-radius)])
       (translate [0 0 (- sides-downness)])
       (translate [0 (* -4/5 rj11-plate-thickness) 0]) ; fudged
       ((key-place-fn (rest rj11-socket-at)))))

(def rj11-nice-plate
  (let [rj11-plate (rj11-plate-shape rj11-bezel rj11-plate-thickness)
        rj11-region (rj11-plate-shape (* 2 rj11-bezel) rj11-cutout-depth)
        rj11-intersect (intersection
                        (rj11-cutout-place rj11-region)
                        (partial-sides rj11-socket-region))]
    (hull rj11-intersect (rj11-cutout-place rj11-plate))))
