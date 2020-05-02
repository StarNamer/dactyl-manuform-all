(ns porkostomus.trochee
  (:require [scad-clj.scad :as scad]
            [scad-clj.model :as model]))

;;;;;;;;;;;;;;;;;
;; Switch Hole ;;
;;;;;;;;;;;;;;;;;

(def keyswitch-height 14.4) ;; Was 14.1, then 14.25
(def keyswitch-width 14.4)

(def sa-profile-key-height 12.7)

(def plate-thickness 4)
(def mount-width (+ keyswitch-width 3))
(def mount-height (+ keyswitch-height 3))

(def alps-width 15.6)
(def alps-notch-width 15.5)
(def alps-notch-height 1)
(def alps-height 13)

(def single-plate
  (let [top-wall (->> (model/cube (+ keyswitch-width 3) 2.2 plate-thickness)
                      (model/translate [0
                                  (+ (/ 2.2 2) (/ alps-height 2))
                                  (/ plate-thickness 2)]))
        left-wall (model/union (->> (model/cube 1.5 (+ keyswitch-height 3) plate-thickness)
                              (model/translate [(+ (/ 1.5 2) (/ 15.6 2))
                                          0
                                          (/ plate-thickness 2)]))
                         (->> (model/cube 1.5 (+ keyswitch-height 3) 1.0)
                              (model/translate [(+ (/ 1.5 2) (/ alps-notch-width 2))
                                          0
                                          (- plate-thickness
                                             (/ alps-notch-height 2))])))
        plate-half (model/union top-wall left-wall)]
    (model/union plate-half
           (->> plate-half
                (model/mirror [1 0 0])
                (model/mirror [0 1 0])))))

;;;;;;;;;;;;;;;;;;;;;;;;;
;; Placement Functions ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(def columns (range 0 7))
(def rows (range 0 8))

(def π 3.14159)

(def α (/ π 12))
(def β (/ π 36))
(def cap-top-height (+ plate-thickness sa-profile-key-height))
(def row-radius (+ (/ (/ (+ mount-height 1/2) 2)
                      (Math/sin (/ α 2)))
                   cap-top-height))
(def column-radius (+ (/ (/ (+ mount-width 2.0) 2)
                         (Math/sin (/ β 2)))
                      cap-top-height))

(defn key-place [column row shape]
  (let [row-placed-shape (->> shape
                              (model/translate [0 0 (- row-radius)])
                              (model/rotate (* α (- 2 row)) [1 0 0])
                              (model/translate [0 0 row-radius]))
        column-offset (cond
                        (= column 2) [0 2.82 -3.0] ;;was moved -4.5
                        (>= column 4) [0 -5.8 5.64]
                        :else [0 0 0])
        column-angle (* β (- 2 column))
        placed-shape (->> row-placed-shape
                          (model/translate [0 0 (- column-radius)])
                          (model/rotate column-angle [0 1 0])
                          (model/translate [0 0 column-radius])
                          (model/translate column-offset))]
    (->> placed-shape
         (model/rotate (/ π 12) [0 1 0])
         (model/translate [0 0 13]))))

(defn case-place [column row shape]
  (let [row-placed-shape (->> shape
                              (model/translate [0 0 (- row-radius)])
                              (model/rotate (* α (- 2 row)) [1 0 0])
                              (model/translate [0 0 row-radius]))
        column-offset [0 -4.35 5.64]
        column-angle (* β (- 2 column))
        placed-shape (->> row-placed-shape
                          (model/translate [0 0 (- column-radius)])
                          (model/rotate column-angle [0 1 0])
                          (model/translate [0 0 column-radius])
                          (model/translate column-offset))]
    (->> placed-shape
         (model/rotate (/ π 12) [0 1 0])
         (model/translate [0 0 13]))))

(def key-holes
  (apply model/union
         (for [column columns
               row rows
               :when (or (not= column 0)
                         (not= row 4))]
           (->> single-plate
                (key-place column row)))))

;;;;;;;;;;;;;;;;;;;;
;; Web Connectors ;;
;;;;;;;;;;;;;;;;;;;;

(def web-thickness 3.5)
(def post-size 0.1)
(def web-post (->> (model/cube post-size post-size web-thickness)
                   (model/translate [0 0 (+ (/ web-thickness -2)
                                      plate-thickness)])))

(def post-adj (/ post-size 2))
(def web-post-tr (model/translate [(- (/ mount-width 2) post-adj) (- (/ mount-height 2) post-adj) 0] web-post))
(def web-post-tl (model/translate [(+ (/ mount-width -2) post-adj) (- (/ mount-height 2) post-adj) 0] web-post))
(def web-post-bl (model/translate [(+ (/ mount-width -2) post-adj) (+ (/ mount-height -2) post-adj) 0] web-post))
(def web-post-br (model/translate [(- (/ mount-width 2) post-adj) (+ (/ mount-height -2) post-adj) 0] web-post))

(defn triangle-hulls [& shapes]
  (apply model/union
         (map (partial apply model/hull)
              (partition 3 1 shapes))))

(def connectors
  (apply model/union
         (concat
          ;; Row connections
          (for [column (drop-last columns)
                row rows
                :when (or (not= column 0)
                          (not= row 4))]
            (triangle-hulls
             (key-place (inc column) row web-post-tl)
             (key-place column row web-post-tr)
             (key-place (inc column) row web-post-bl)
             (key-place column row web-post-br)))

          ;; Column connections
          (for [column columns
                row (drop-last rows)
                :when (or (not= column 0)
                          (not= row 3))]
            (triangle-hulls
             (key-place column row web-post-bl)
             (key-place column row web-post-br)
             (key-place column (inc row) web-post-tl)
             (key-place column (inc row) web-post-tr)))

          ;; Diagonal connections
          (for [column (drop-last columns)
                row (drop-last rows)
                :when (or (not= column 0)
                          (not= row 3))]
            (triangle-hulls
             (key-place column row web-post-br)
             (key-place column (inc row) web-post-tr)
             (key-place (inc column) row web-post-bl)
             (key-place (inc column) (inc row) web-post-tl))))))


(comment
  
   (spit "resources/switch-hole.scad"
         (scad/write-scad single-plate))
  
(spit "resources/switch-holes.scad"
      (scad/write-scad (model/union connectors key-holes)))
  
  )
