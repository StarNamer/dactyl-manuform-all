(ns dactyl-keyboard.dactyl
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [dactyl-keyboard.util :refer :all]
            [unicode-math.core :refer :all]))

;;;;;;;;;;;;;;;;;
;; Switch Hole ;;
;;;;;;;;;;;;;;;;;

(def keyswitch-height 14.4) ;; Was 14.1, then 14.25
(def keyswitch-width 14.4)

(def sa-profile-key-height 12.7)

(def plate-thickness 4)
(def mount-width (+ keyswitch-width 3))
(def mount-height (+ keyswitch-height 3))

(def cherry-single-plate
  (let [top-wall (->> (cube (+ keyswitch-width 3) 1.5 plate-thickness)
                      (translate [0
                                  (+ (/ 1.5 2) (/ keyswitch-height 2))
                                  (/ plate-thickness 2)]))
        left-wall (->> (cube 1.5 (+ keyswitch-height 3) plate-thickness)
                       (translate [(+ (/ 1.5 2) (/ keyswitch-width 2))
                                   0
                                   (/ plate-thickness 2)]))
        side-nub (->> (binding [*fn* 30] (cylinder 1 2.75))
                      (rotate (/ π 2) [1 0 0])
                      (translate [(+ (/ keyswitch-width 2)) 0 1])
                      (hull (->> (cube 1.5 2.75 plate-thickness)
                                 (translate [(+ (/ 1.5 2) (/ keyswitch-width 2))
                                             0
                                             (/ plate-thickness 2)]))))
        plate-half (union top-wall left-wall (with-fn 100 side-nub))]
    (union plate-half
           (->> plate-half
                (mirror [1 0 0])
                (mirror [0 1 0])))))

(def alps-width 15.6)
(def alps-notch-width 15.5)
(def alps-notch-height 1)
(def alps-height 13)

(def alps-single-plate
  (let [top-wall (->> (cube (+ keyswitch-width 3) 2.2 plate-thickness)
                      (translate [0
                                  (+ (/ 2.2 2) (/ alps-height 2))
                                  (/ plate-thickness 2)]))
        left-wall (union (->> (cube 1.5 (+ keyswitch-height 3) plate-thickness)
                              (translate [(+ (/ 1.5 2) (/ 15.6 2))
                                          0
                                          (/ plate-thickness 2)]))
                         (->> (cube 1.5 (+ keyswitch-height 3) 1.0)
                              (translate [(+ (/ 1.5 2) (/ alps-notch-width 2))
                                          0
                                          (- plate-thickness
                                             (/ alps-notch-height 2))]))
                         )
        plate-half (union top-wall left-wall)]
    (union plate-half
           (->> plate-half
                (mirror [1 0 0])
                (mirror [0 1 0])))))

(def single-plate cherry-single-plate)


;;;;;;;;;;;;;;;;
;; SA Keycaps ;;
;;;;;;;;;;;;;;;;

(def sa-length 18.25)
(def sa-double-length 37.5)
(def sa-cap {1 (let [bl2 (/ 18.5 2)
                     m (/ 17 2)
                     key-cap (hull (->> (polygon [[bl2 bl2] [bl2 (- bl2)] [(- bl2) (- bl2)] [(- bl2) bl2]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 0.05]))
                                   (->> (polygon [[m m] [m (- m)] [(- m) (- m)] [(- m) m]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 6]))
                                   (->> (polygon [[6 6] [6 -6] [-6 -6] [-6 6]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 12])))]
                 (->> key-cap
                      (translate [0 0 (+ 5 plate-thickness)])
                      (color [220/255 163/255 163/255 1])))
             2 (let [bl2 (/ sa-double-length 2)
                     bw2 (/ 18.25 2)
                     key-cap (hull (->> (polygon [[bw2 bl2] [bw2 (- bl2)] [(- bw2) (- bl2)] [(- bw2) bl2]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 0.05]))
                                   (->> (polygon [[6 16] [6 -16] [-6 -16] [-6 16]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 12])))]
                 (->> key-cap
                      (translate [0 0 (+ 5 plate-thickness)])
                      (color [127/255 159/255 127/255 1])))
             1.5 (let [bl2 (/ 18.25 2)
                       bw2 (/ 28 2)
                       key-cap (hull (->> (polygon [[bw2 bl2] [bw2 (- bl2)] [(- bw2) (- bl2)] [(- bw2) bl2]])
                                          (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                          (translate [0 0 0.05]))
                                     (->> (polygon [[11 6] [-11 6] [-11 -6] [11 -6]])
                                          (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                          (translate [0 0 12])))]
                   (->> key-cap
                        (translate [0 0 (+ 5 plate-thickness)])
                        (color [240/255 223/255 175/255 1])))})

;;;;;;;;;;;;;;;;;;;;;;;;;
;; Placement Functions ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(def columns (range 0 6))
(def rows (range 0 5))

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
                              (translate [0 0 (- row-radius)])
                              (rotate (* α (- 2 row)) [1 0 0])
                              (translate [0 0 row-radius]))
        column-offset (cond
                        (= column 2) [0 2.82 -3.0] ;;was moved -4.5
                        (>= column 4) [0 -5.8 5.64]
                        :else [0 0 0])
        column-angle (* β (- 2 column))
        placed-shape (->> row-placed-shape
                          (translate [0 0 (- column-radius)])
                          (rotate column-angle [0 1 0])
                          (translate [0 0 column-radius])
                          (translate column-offset))]
    (->> placed-shape
         (rotate (/ π 12) [0 1 0])
         (translate [0 0 13]))))

(defn case-place [column row shape]
  (let [row-placed-shape (->> shape
                              (translate [0 0 (- row-radius)])
                              (rotate (* α (- 2 row)) [1 0 0])
                              (translate [0 0 row-radius]))
        column-offset [0 -4.35 5.64]
        column-angle (* β (- 2 column))
        placed-shape (->> row-placed-shape
                          (translate [0 0 (- column-radius)])
                          (rotate column-angle [0 1 0])
                          (translate [0 0 column-radius])
                          (translate column-offset))]
    (->> placed-shape
         (rotate (/ π 12) [0 1 0])
         (translate [0 0 13]))))

(defn desk-case-place [column row shape]
  (->> (case-place column row shape)
       (scale [1 1 0])))

(def key-holes
  (apply union
         (for [column columns
               row rows
               :when (or (not= column 0)
                         (not= row 4))]
           (->> single-plate
                (key-place column row)))))

(def caps
  (apply union
         (for [column columns
               row rows
               :when (or (not= column 0)
                         (not= row 4))]
           (->> (sa-cap (if (= column 5) 1 1))
                (key-place column row)))))

;;;;;;;;;;;;;;;;;;;;
;; Web Connectors ;;
;;;;;;;;;;;;;;;;;;;;

(def web-thickness 3.5)
(def post-size 0.1)
(def web-post (->> (cube post-size post-size web-thickness)
                   (translate [0 0 (+ (/ web-thickness -2)
                                      plate-thickness)])))

(def post-adj (/ post-size 2))
(def web-post-tr (translate [(- (/ mount-width 2) post-adj) (- (/ mount-height 2) post-adj) 0] web-post))
(def web-post-tl (translate [(+ (/ mount-width -2) post-adj) (- (/ mount-height 2) post-adj) 0] web-post))
(def web-post-bl (translate [(+ (/ mount-width -2) post-adj) (+ (/ mount-height -2) post-adj) 0] web-post))
(def web-post-br (translate [(- (/ mount-width 2) post-adj) (+ (/ mount-height -2) post-adj) 0] web-post))

(def connectors
  (apply union
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

;;;;;;;;;;;;
;; Thumbs ;;
;;;;;;;;;;;;

(defn thumb-place [column row shape]
  (let [cap-top-height (+ plate-thickness sa-profile-key-height)
        α (/ π 12)
        row-radius (+ (/ (/ (+ mount-height 1) 2)
                         (Math/sin (/ α 2)))
                      cap-top-height)
        β (/ π 36)
        column-radius (+ (/ (/ (+ mount-width 2) 2)
                            (Math/sin (/ β 2)))
                         cap-top-height)
        #_(+ (/ (/ (+ pillar-width 5) 2)
                            (Math/sin (/ β 2)))
                         cap-top-height)]
    (->> shape
         (translate [0 0 (- row-radius)])
         (rotate (* α row) [1 0 0])
         (translate [0 0 row-radius])
         (translate [0 0 (- column-radius)])
         (rotate (* column β) [0 1 0])
         (translate [0 0 column-radius])
         (translate [mount-width 0 0])
         (rotate (* π (- 1/4 3/16)) [0 0 1])
         (rotate (/ π 12) [1 1 0])
         (translate [-52 -45 40]))))

(defn desk-thumb-place [column row shape]
  (->> (thumb-place column row shape)
       (scale [1 1 0])))

(defn thumb-2x-column [shape]
  (thumb-place 0 -1/2 shape))

(defn thumb-2x+1-column [shape]
  (union (thumb-place 1 -1/2 shape)
         (thumb-place 1 1 shape)))

(defn thumb-1x-column [shape]
  (union (thumb-place 2 -1 shape)
         (thumb-place 2 0 shape)
         (thumb-place 2 1 shape)))

(defn thumb-layout [shape]
  (union
   (thumb-2x-column shape)
   (thumb-2x+1-column shape)
   (thumb-1x-column shape)))

(def double-plates
  (let [plate-height (/ (- sa-double-length mount-height) 2)
        top-plate (->> (cube mount-width plate-height web-thickness)
                       (translate [0 (/ (+ plate-height mount-height) 2)
                                   (- plate-thickness (/ web-thickness 2))]))
        stabilizer-cutout (union (->> (cube 14.2 3.5 web-thickness)
                                      (translate [0.5 12 (- plate-thickness (/ web-thickness 2))])
                                      (color [1 0 0 1/2]))
                                 (->> (cube 16 3.5 web-thickness)
                                      (translate [0.5 12 (- plate-thickness (/ web-thickness 2) 1.4)])
                                      (color [1 0 0 1/2])))
        top-plate (difference top-plate stabilizer-cutout)]
    (union top-plate (mirror [0 1 0] top-plate))))

(def thumbcaps
  (union
   (thumb-2x-column (sa-cap 2))
   (thumb-place 1 -1/2 (sa-cap 2))
   (thumb-place 1 1 (sa-cap 1))
   (thumb-1x-column (sa-cap 1))))

(def thumb-connectors
  (union
   (apply union
          (concat
           (for [column [2] row [1]]
             (triangle-hulls (thumb-place column row web-post-br)
                             (thumb-place column row web-post-tr)
                             (thumb-place (dec column) row web-post-bl)
                             (thumb-place (dec column) row web-post-tl)))
           (for [column [2] row [0 1]]
             (triangle-hulls
              (thumb-place column row web-post-bl)
              (thumb-place column row web-post-br)
              (thumb-place column (dec row) web-post-tl)
              (thumb-place column (dec row) web-post-tr)))))
   (let [plate-height (/ (- sa-double-length mount-height) 2)
         thumb-tl (->> web-post-tl
                       (translate [0 plate-height 0]))
         thumb-bl (->> web-post-bl
                       (translate [0 (- plate-height) 0]))
         thumb-tr (->> web-post-tr
                       (translate [0 plate-height 0]))
         thumb-br (->> web-post-br
                       (translate [0 (- plate-height) 0]))]
     (union

      ;;Connecting the two doubles
      (triangle-hulls (thumb-place 0 -1/2 thumb-tl)
                      (thumb-place 0 -1/2 thumb-bl)
                      (thumb-place 1 -1/2 thumb-tr)
                      (thumb-place 1 -1/2 thumb-br))

      ;;Connecting the double to the one above it
      (triangle-hulls (thumb-place 1 -1/2 thumb-tr)
                      (thumb-place 1 -1/2 thumb-tl)
                      (thumb-place 1 1 web-post-br)
                      (thumb-place 1 1 web-post-bl))

      ;;Connecting the 4 with the double in the bottom left
      (triangle-hulls (thumb-place 1 1 web-post-bl)
                      (thumb-place 1 -1/2 thumb-tl)
                      (thumb-place 2 1 web-post-br)
                      (thumb-place 2 0 web-post-tr))

      ;;Connecting the two singles with the middle double
      (hull (thumb-place 1 -1/2 thumb-tl)
            (thumb-place 1 -1/2 thumb-bl)
            (thumb-place 2 0 web-post-br)
            (thumb-place 2 -1 web-post-tr))
      (hull (thumb-place 1 -1/2 thumb-tl)
            (thumb-place 2 0 web-post-tr)
            (thumb-place 2 0 web-post-br))
      (hull (thumb-place 1 -1/2 thumb-bl)
            (thumb-place 2 -1 web-post-tr)
            (thumb-place 2 -1 web-post-br))

      ;;Connecting the thumb to everything
      (triangle-hulls (thumb-place 0 -1/2 thumb-br)
                      (key-place 1 4 web-post-bl)
                      (thumb-place 0 -1/2 thumb-tr)
                      (key-place 1 4 web-post-tl)
                      (key-place 1 3 web-post-bl)
                      (thumb-place 0 -1/2 thumb-tr)
                      (key-place 0 3 web-post-br)
                      (key-place 0 3 web-post-bl)
                      (thumb-place 0 -1/2 thumb-tr)
                      (thumb-place 0 -1/2 thumb-tl)
                      (key-place 0 3 web-post-bl)
                      (thumb-place 1 -1/2 thumb-tr)
                      (thumb-place 1 1 web-post-br)
                      (key-place 0 3 web-post-bl)
                      (key-place 0 3 web-post-tl)
                      (thumb-place 1 1 web-post-br)
                      (thumb-place 1 1 web-post-tr))
      (hull (thumb-place 0 -1/2 web-post-tr)
            (thumb-place 0 -1/2 thumb-tr)
            (key-place 1 4 web-post-bl)
            (key-place 1 4 web-post-tl))))))

(def thumb
  (union
   thumb-connectors
   (thumb-layout (rotate (/ π 2) [0 0 1] single-plate))
   (thumb-place 0 -1/2 double-plates)
   (thumb-place 1 -1/2 double-plates)))

;;;;;;;;;;
;; Case ;;
;;;;;;;;;;

;; In column units
(def right-wall-column (+ (last columns) 0.55))
(def left-wall-column (- (first columns) 1/2))
(def thumb-back-y 0.93)
(def thumb-right-wall (- -1/2 0.05))
(def thumb-front-row (+ -1 0.07))
(def thumb-left-wall-column (+ 5/2 0.05))
(def back-y 0.02)

(defn range-inclusive [start end step]
  (concat (range start end step) [end]))

(def wall-step 0.2)
(def wall-sphere-n 20) ;;Sphere resolution, lower for faster renders

(defn wall-sphere-at [coords]
  (->> (sphere 1)
       (translate coords)
       (with-fn wall-sphere-n)))

(defn scale-to-range [start end x]
  (+ start (* (- end start) x)))

(defn wall-sphere-bottom [front-to-back-scale]
  (wall-sphere-at [0
                   (scale-to-range
                    (+ (/ mount-height -2) -3.5)
                    (+ (/ mount-height 2) 5.0)
                    front-to-back-scale)
                   -6]))

(defn wall-sphere-top [front-to-back-scale]
  (wall-sphere-at [0
                   (scale-to-range
                    (+ (/ mount-height -2) -3.5)
                    (+ (/ mount-height 2) 3.5)
                    front-to-back-scale)
                   10]))

(def wall-sphere-top-back (wall-sphere-top 1))
(def wall-sphere-bottom-back (wall-sphere-bottom 1))
(def wall-sphere-bottom-front (wall-sphere-bottom 0))
(def wall-sphere-top-front (wall-sphere-top 0))

(defn top-case-cover [place-fn sphere
                 x-start x-end
                 y-start y-end
                 step]
  (apply union
         (for [x (range-inclusive x-start (- x-end step) step)
               y (range-inclusive y-start (- y-end step) step)]
           (hull (place-fn x y sphere)
                 (place-fn (+ x step) y sphere)
                 (place-fn x (+ y step) sphere)
                 (place-fn (+ x step) (+ y step) sphere)))))

(def front-wall
  (let [step wall-step ;;0.1
        wall-step 0.05 ;;0.05
        place case-place
        top-cover (fn [x-start x-end y-start y-end]
                    (top-case-cover place wall-sphere-top-front
                                    x-start x-end y-start y-end
                                    wall-step))]
    (union
      ;; main wall
      (apply union
        (for [x (range-inclusive 0.7 (- right-wall-column step) step)]
          (hull (place x 4 wall-sphere-top-front)
                (place (+ x step) 4 wall-sphere-top-front)
                (place x 4 wall-sphere-bottom-front)
                (place (+ x step) 4 wall-sphere-bottom-front))))
      ;; left side up to thumb cluster wall
      (apply union
        (for [x (range-inclusive 0.5 0.7 0.01)]
          (hull (place x 4 wall-sphere-top-front)
                (place (+ x step) 4 wall-sphere-top-front)
                (place 0.7 4 wall-sphere-bottom-front))))
      ;; wall to desk
      (apply union
        (for [x (range-inclusive 0.7 (- right-wall-column step) step)]
          (hull (desk-case-place x 4 wall-sphere-bottom-front)
                (desk-case-place (+ x step) 4 wall-sphere-bottom-front)
                (place x 4 wall-sphere-bottom-front)
                (place (+ x step) 4 wall-sphere-bottom-front))))
      (top-cover 0.5 1.7 3.6 4)
      (top-cover 1.59 2.41 3.35 4) ;; was 3.32
      (top-cover 2.39 3.41 3.6 4)
      ;; key plate to wall (columns 2 to 5)
      (apply union
        (for [x (range 2 5)]
          (union
            (hull (place (- x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                  (place (+ x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                  (key-place x 4 web-post-bl)
                  (key-place x 4 web-post-br))
            (hull (place (- x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                  (key-place x 4 web-post-bl)
                  (key-place (- x 1) 4 web-post-br)))))
      (hull (place right-wall-column 4 (translate [0 1 1] wall-sphere-bottom-front))
            (place (- right-wall-column 1) 4 (translate [0 1 1] wall-sphere-bottom-front))
            (key-place 5 4 web-post-bl)
            (key-place 5 4 web-post-br))
      (hull (place (+ 4 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
            (place (- right-wall-column 1) 4 (translate [0 1 1] wall-sphere-bottom-front))
            (key-place 4 4 web-post-br)
            (key-place 5 4 web-post-bl))
      ;; key plate to wall (column 1)
      (hull (place 0.7 4 (translate [0 1 1] wall-sphere-bottom-front))
            (place 1.7 4 (translate [0 1 1] wall-sphere-bottom-front))
            (key-place 1 4 web-post-bl)
            (key-place 1 4 web-post-br)))))

(def back-wall
  (let [step wall-step
        wall-sphere-top-backtep 0.05
        place case-place
        front-top-cover (fn [x-start x-end y-start y-end]
                          (apply union
                                 (for [x (range-inclusive x-start (- x-end wall-sphere-top-backtep) wall-sphere-top-backtep)
                                       y (range-inclusive y-start (- y-end wall-sphere-top-backtep) wall-sphere-top-backtep)]
                                   (hull (place x y wall-sphere-top-back)
                                         (place (+ x wall-sphere-top-backtep) y wall-sphere-top-back)
                                         (place x (+ y wall-sphere-top-backtep) wall-sphere-top-back)
                                         (place (+ x wall-sphere-top-backtep) (+ y wall-sphere-top-backtep) wall-sphere-top-back)))))]
    (union
      ;; main wall
      (apply union
        (for [x (range-inclusive left-wall-column (- right-wall-column step) step)]
          (hull (place x back-y wall-sphere-top-back)
                (place (+ x step) back-y wall-sphere-top-back)
                (place x back-y wall-sphere-bottom-back)
                (place (+ x step) back-y wall-sphere-bottom-back))))
      ;; wall to desk
      (apply union
        (for [x (range-inclusive left-wall-column (- right-wall-column step) step)]
          (hull (desk-case-place x back-y wall-sphere-bottom-back)
                (desk-case-place (+ x step) back-y wall-sphere-bottom-back)
                (place x back-y wall-sphere-bottom-back)
                (place (+ x step) back-y wall-sphere-bottom-back))))
      (front-top-cover 1.56 2.44 back-y 0.1)
      (front-top-cover 3.56 4.44 back-y 0.13)
      (front-top-cover 4.3 right-wall-column back-y 0.13)
      (hull (place left-wall-column 0 (translate [1 -1 1] wall-sphere-bottom-back))
            (place (+ left-wall-column 1) 0 (translate [0 -1 1] wall-sphere-bottom-back))
            (key-place 0 0 web-post-tl)
            (key-place 0 0 web-post-tr))
      (hull (place 5 0 (translate [0 -1 1] wall-sphere-bottom-back))
            (place right-wall-column 0 (translate [0 -1 1] wall-sphere-bottom-back))
            (key-place 5 0 web-post-tl)
            (key-place 5 0 web-post-tr))
      (apply union
        (for [x (range 1 5)]
          (union
            (hull (place (- x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                  (place (+ x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                  (key-place x 0 web-post-tl)
                  (key-place x 0 web-post-tr))
            (hull (place (- x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                  (key-place x 0 web-post-tl)
                  (key-place (- x 1) 0 web-post-tr)))))
      (hull (place (- 5 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
            (place 5 0 (translate [0 -1 1] wall-sphere-bottom-back))
            (key-place 4 0 web-post-tr)
            (key-place 5 0 web-post-tl)))))

(def right-wall
  (let [place case-place]
    (union
      ;; main wall
      (apply union
        (map (partial apply hull)
             (partition 2 1
                (for [scale (range-inclusive 0 1 0.01)]
                  (let [x (scale-to-range 4 0.02 scale)]
                    (hull (place right-wall-column x (wall-sphere-top scale))
                          (place right-wall-column x (wall-sphere-bottom scale))))))))
      ;; wall to desk
      (apply union
        (map (partial apply hull)
             (partition 2 1
                (for [scale (range-inclusive 0 1 0.01)]
                  (let [x (scale-to-range 4 0.02 scale)]
                    (hull (desk-case-place right-wall-column x (wall-sphere-bottom scale))
                          (place right-wall-column x (wall-sphere-bottom scale))))))))
      (apply union
        (concat
          ;; from key switches to wall bottom
          (for [x (range 0 5)]
            (hull (place right-wall-column x (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                  (key-place 5 x web-post-br)
                  (key-place 5 x web-post-tr)))
          ;; from bottom to between key switches
          (for [x (range 0 4)]
            (hull (place right-wall-column x (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                  (place right-wall-column (inc x) (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                  (key-place 5 x web-post-br)
                  (key-place 5 (inc x) web-post-tr)))
          ;; from bottom to corners of key plate
          [(union
            (hull (place right-wall-column 0 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                  (place right-wall-column 0.02 (translate [-1 -1 1] (wall-sphere-bottom 1)))
                  (key-place 5 0 web-post-tr))
            (hull (place right-wall-column 4 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                  (place right-wall-column 4 (translate [-1 1 1] (wall-sphere-bottom 0)))
                  (key-place 5 4 web-post-br)))])))))

(def left-wall
  (let [place case-place]
    (union
      ;; main wall
      (apply union
        (for [x (range-inclusive -1 (- 1.6666 wall-step) wall-step)]
          (hull (place left-wall-column x wall-sphere-top-front)
                (place left-wall-column (+ x wall-step) wall-sphere-top-front)
                (place left-wall-column x wall-sphere-bottom-front)
                (place left-wall-column (+ x wall-step) wall-sphere-bottom-front))))
      (hull (place left-wall-column 1.6666 wall-sphere-top-front)
            (place left-wall-column 1.69 wall-sphere-bottom-front)
            (place left-wall-column (- 1.69 wall-step) wall-sphere-bottom-front))
      ;; wall to desk
      (apply union
        (for [x (range-inclusive (- -1 0.05) (- 1.69 wall-step) wall-step)]
          (hull (desk-case-place left-wall-column x wall-sphere-bottom-front)
                (desk-case-place left-wall-column (+ x wall-step) wall-sphere-bottom-front)
                (place left-wall-column x wall-sphere-bottom-front)
                (place left-wall-column (+ x wall-step) wall-sphere-bottom-front))))
      ;; top left joiner between back wall and left
      (hull (place left-wall-column -1 wall-sphere-top-front)
            (place left-wall-column -1 wall-sphere-bottom-front)
            (place left-wall-column 0.02 wall-sphere-top-back)
            (place left-wall-column 0.02 wall-sphere-bottom-back))
      ;; first row to wall
      (hull (place left-wall-column 0 (translate [1 -1 1] wall-sphere-bottom-back))
            (place left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
            (key-place 0 0 web-post-tl)
            (key-place 0 0 web-post-bl))
      ;; second row to wall
      (hull (place left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
            (place left-wall-column 2 (translate [1 0 1] wall-sphere-bottom-back))
            (key-place 0 0 web-post-bl)
            (key-place 0 1 web-post-bl))
      ;; third row to wall
      (hull (place left-wall-column 2 (translate [1 0 1] wall-sphere-bottom-back))
            (place left-wall-column 1.69  (translate [1 0 1] wall-sphere-bottom-front))
            (key-place 0 1 web-post-bl)
            (key-place 0 2 web-post-bl))
      ;; triangles between thumb cluster corner and space between third and fourth rows
      (hull (place left-wall-column 1.69 (translate [1 0 1] wall-sphere-bottom-front))
            (key-place 0 2 web-post-bl)
            (key-place 0 3 web-post-tl))
      (hull (place left-wall-column 1.69 (translate [1 0 1] wall-sphere-bottom-front))
            (thumb-place 1 1 web-post-tr)
            (key-place 0 3 web-post-tl))
      (hull (place left-wall-column 1.69 (translate [1 0 1] wall-sphere-bottom-front))
            (thumb-place 1 1 web-post-tr)
            (thumb-place 1/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))))))

(def thumb-back-wall
  (let [step wall-step
        top-step 0.05
        back-y thumb-back-y]
    (union
      ;; main wall
      (apply union
        (for [x (range-inclusive 1/2 (- (+ 5/2 0.05) step) step)]
          (hull (thumb-place x back-y wall-sphere-top-back)
                (thumb-place (+ x step) back-y wall-sphere-top-back)
                (thumb-place x back-y wall-sphere-bottom-back)
                (thumb-place (+ x step) back-y wall-sphere-bottom-back))))
      ;; wall to desk
      (apply union
        (for [x (range-inclusive 1/2 (- (+ 5/2 0.05) step) step)]
          (hull (desk-thumb-place x back-y wall-sphere-bottom-back)
                (desk-thumb-place (+ x step) back-y wall-sphere-bottom-back)
                (thumb-place x back-y wall-sphere-bottom-back)
                (thumb-place (+ x step) back-y wall-sphere-bottom-back))))
      (hull (desk-thumb-place (- 1/2 0.07) back-y wall-sphere-bottom-back)
            (desk-thumb-place 1/2 back-y wall-sphere-bottom-back)
            (case-place left-wall-column 1.69 wall-sphere-bottom-front)
            (thumb-place 1/2 back-y wall-sphere-bottom-back))
      ;; from back wall to left wall
      (hull (thumb-place 1/2 back-y wall-sphere-top-back)
            (thumb-place 1/2 back-y wall-sphere-bottom-back)
            (case-place left-wall-column 1.6666 wall-sphere-top-front))
      (hull (thumb-place 1/2 back-y wall-sphere-bottom-back)
            (case-place left-wall-column 1.6666 wall-sphere-top-front)
            (case-place left-wall-column 1.69 wall-sphere-bottom-front))
      (hull (thumb-place 1/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
            (thumb-place 1 1 web-post-tr)
            (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
            (thumb-place 1 1 web-post-tl))
      (hull (thumb-place (+ 5/2 0.05) thumb-back-y (translate [1 -1 1] wall-sphere-bottom-back))
            (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
            (thumb-place 1 1 web-post-tl)
            (thumb-place 2 1 web-post-tl)))))

(def thumb-left-wall
  (let [step wall-step
        place thumb-place]
    (union
      ;; main wall
      (apply union
        (for [x (range-inclusive (+ -1 0.07) (- 1.95 step) step)]
          (hull (place thumb-left-wall-column x wall-sphere-top-front)
                (place thumb-left-wall-column (+ x step) wall-sphere-top-front)
                (place thumb-left-wall-column x wall-sphere-bottom-front)
                (place thumb-left-wall-column (+ x step) wall-sphere-bottom-front))))
      ;; wall to desk
      (apply union
        (for [x (range-inclusive (+ -1 0.07) (- 1.98 step) step)]
          (hull (desk-thumb-place thumb-left-wall-column x wall-sphere-bottom-front)
                (desk-thumb-place thumb-left-wall-column (+ x step) wall-sphere-bottom-front)
                (place thumb-left-wall-column x wall-sphere-bottom-front)
                (place thumb-left-wall-column (+ x step) wall-sphere-bottom-front))))
      ;; key plate to wall
      (hull (place thumb-left-wall-column 1.95 wall-sphere-top-front)
            (place thumb-left-wall-column 1.95 wall-sphere-bottom-front)
            (place thumb-left-wall-column thumb-back-y wall-sphere-top-back)
            (place thumb-left-wall-column thumb-back-y wall-sphere-bottom-back))
      (hull (thumb-place thumb-left-wall-column thumb-back-y (translate [1 -1 1] wall-sphere-bottom-back))
            (thumb-place thumb-left-wall-column 0 (translate [1 0 1] wall-sphere-bottom-back))
            (thumb-place 2 1 web-post-tl)
            (thumb-place 2 1 web-post-bl))
      (hull (thumb-place thumb-left-wall-column 0 (translate [1 0 1] wall-sphere-bottom-back))
            (thumb-place 2 0 web-post-tl)
            (thumb-place 2 1 web-post-bl))
      (hull (thumb-place thumb-left-wall-column 0 (translate [1 0 1] wall-sphere-bottom-back))
            (thumb-place thumb-left-wall-column -1 (translate [1 0 1] wall-sphere-bottom-back))
            (thumb-place 2 0 web-post-tl)
            (thumb-place 2 0 web-post-bl))
      (hull (thumb-place thumb-left-wall-column -1 (translate [1 0 1] wall-sphere-bottom-back))
            (thumb-place 2 -1 web-post-tl)
            (thumb-place 2 0 web-post-bl))
      (hull (thumb-place thumb-left-wall-column -1 (translate [1 0 1] wall-sphere-bottom-back))
            (thumb-place thumb-left-wall-column (+ -1 0.07) (translate [1 1 1] wall-sphere-bottom-front))
            (thumb-place 2 -1 web-post-tl)
            (thumb-place 2 -1 web-post-bl)))))

(def thumb-front-wall
  (let [step wall-step ;;0.1
        wall-sphere-top-fronttep 0.05 ;;0.05
        place thumb-place
        plate-height (/ (- sa-double-length mount-height) 2)
        thumb-tl (->> web-post-tl
                      (translate [0 plate-height 0]))
        thumb-bl (->> web-post-bl
                      (translate [0 (- plate-height) 0]))
        thumb-tr (->> web-post-tr
                      (translate [-0 plate-height 0]))
        thumb-br (->> web-post-br
                      (translate [-0 (- plate-height) 0]))]
    (union
      ;; main wall
      (apply union
        (for [x (range-inclusive thumb-right-wall (- (+ 5/2 0.05) step) step)]
          (hull (place x thumb-front-row wall-sphere-top-front)
                (place (+ x step) thumb-front-row wall-sphere-top-front)
                (place x thumb-front-row wall-sphere-bottom-front)
                (place (+ x step) thumb-front-row wall-sphere-bottom-front))))
      ;; thumb front wall to front wall
      (hull (place thumb-right-wall thumb-front-row wall-sphere-top-front)
            (place thumb-right-wall thumb-front-row wall-sphere-bottom-front)
            (case-place 0.5 4 wall-sphere-top-front))
      (hull (place thumb-right-wall thumb-front-row wall-sphere-bottom-front)
            (case-place 0.5 4 wall-sphere-top-front)
            (case-place 0.7 4 wall-sphere-bottom-front))
      ;; wall to desk
      (apply union
        (for [x (range-inclusive thumb-right-wall (- (+ 5/2 0.05) step) step)]
          (hull (desk-thumb-place x thumb-front-row wall-sphere-bottom-front)
                (desk-thumb-place (+ x step) thumb-front-row wall-sphere-bottom-front)
                (place x thumb-front-row wall-sphere-bottom-front)
                (place (+ x step) thumb-front-row wall-sphere-bottom-front))))
      (hull (place thumb-right-wall thumb-front-row wall-sphere-bottom-front)
            (desk-case-place 0.7 4 wall-sphere-bottom-front)
            (case-place 0.7 4 wall-sphere-bottom-front)
            (desk-thumb-place thumb-right-wall thumb-front-row wall-sphere-bottom-front))
      ;; between thumb key plate, wall, and case-plate
      (hull (place thumb-right-wall thumb-front-row wall-sphere-bottom-front)
            (key-place 1 4 web-post-bl)
            (place 0 -1/2 thumb-br)
            (place 0 -1/2 web-post-br)
            (case-place 0.7 4 wall-sphere-bottom-front))
      ;; from thumb-key plate to wall
      (hull (place (+ 5/2 0.05) thumb-front-row (translate [1 1 1] wall-sphere-bottom-front))
            (place (+ 3/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
            (place 2 -1 web-post-bl)
            (place 2 -1 web-post-br))
      (hull (place thumb-right-wall thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
            (place (+ 1/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
            (place 0 -1/2 thumb-bl)
            (place 0 -1/2 thumb-br))
      (hull (place (+ 1/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
            (place (+ 3/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
            (place 0 -1/2 thumb-bl)
            (place 1 -1/2 thumb-bl)
            (place 1 -1/2 thumb-br)
            (place 2 -1 web-post-br)))))

(def arduino-width 18)
(def arduino-length 35)
(def arduino-height 5)

(def arduino-holder
  (let [x left-wall-column
        trrs-length 13
        place desk-case-place
        back-left (place x back-y wall-sphere-bottom-back)
        back-right (place (+ x 1) back-y wall-sphere-bottom-back)]
    (union
      (hull (translate [0 0 0] back-left)
            (translate [0 0 2] back-left)
            (translate [0 0 0] back-right)
            (translate [0 0 2] back-right)
            (translate [0 (- -3 arduino-length) 0] back-left)
            (translate [0 (- -3 arduino-length) 2] back-left)
            (translate [0 (- -3 arduino-length) 0] back-right)
            (translate [0 (- -3 arduino-length) 2] back-right))
      (hull (translate [0 (- -2 arduino-length) 8] back-left)
            (translate [0 (- -2 arduino-length) 2] back-left)
            (translate [0 (- -2 arduino-length) 8] back-right)
            (translate [0 (- -2 arduino-length) 2] back-right)
            (translate [0 (- -3 arduino-length) 8] back-left)
            (translate [0 (- -3 arduino-length) 2] back-left)
            (translate [0 (- -3 arduino-length) 8] back-right)
            (translate [0 (- -3 arduino-length) 2] back-right))
      (hull (translate [0 0 0] (place x 0.5 wall-sphere-bottom-front))
            (translate [0 0 2] (place x 0.5 wall-sphere-bottom-front))
            (translate [0 0 0] (place x 0.2 wall-sphere-bottom-front))
            (translate [0 0 2] (place x 0.2 wall-sphere-bottom-front))
            (translate [0 (- 8 arduino-length) 0] back-left)
            (translate [0 (- 8 arduino-length) 2] back-left)
            (translate [0 (- -3 arduino-length) 0] back-left)
            (translate [0 (- -3 arduino-length) 2] back-left))
    )))

(def trrs-holder
  (let [x 0.5
        step 0.7
        trrs-length 13
        back-y thumb-back-y
        place desk-thumb-place]
    (union
      (hull (translate [0 0 0] (place x back-y wall-sphere-bottom-back))
            (translate [0 0 2] (place x back-y wall-sphere-bottom-back))
            (translate [0 (- trrs-length) 0] (place x back-y wall-sphere-bottom-back))
            (translate [0 (- trrs-length) 2] (place x back-y wall-sphere-bottom-back))
            (translate [0 0 0] (place (+ x step) back-y wall-sphere-bottom-back))
            (translate [0 0 2] (place (+ x step) back-y wall-sphere-bottom-back))
            (translate [4 (- 0.8 trrs-length) 0] (place (+ x step) back-y wall-sphere-bottom-back))
            (translate [4 (- 0.8 trrs-length) 2] (place (+ x step) back-y wall-sphere-bottom-back)))
      (hull (translate [0 (- trrs-length) 0] (place x back-y wall-sphere-bottom-back))
            (translate [0 (- trrs-length) 4] (place x back-y wall-sphere-bottom-back))
            (translate [4 (- 0.8 trrs-length) 0] (place (+ x step) back-y wall-sphere-bottom-back))
            (translate [4 (- 0.8 trrs-length) 4] (place (+ x step) back-y wall-sphere-bottom-back))
            (translate [0 (- (- trrs-length) 2) 0] (place x back-y wall-sphere-bottom-back))
            (translate [0 (- (- trrs-length) 2) 4] (place x back-y wall-sphere-bottom-back))
            (translate [5 (- (- trrs-length) 1.2) 0] (place (+ x step) back-y wall-sphere-bottom-back))
            (translate [5 (- (- trrs-length) 1.2) 4] (place (+ x step) back-y wall-sphere-bottom-back)))
    )))

(def new-case
  (union front-wall
         right-wall
         back-wall
         left-wall
         thumb-back-wall
         thumb-left-wall
         thumb-front-wall
         arduino-holder
         trrs-holder))

;;;;;;;;;;;;
;; Bottom ;;
;;;;;;;;;;;;


(def bottom-key-guard (->> (cube mount-width mount-height web-thickness)
                           (translate [0 0 (+ (- (/ web-thickness 2)) -4.5)])))
(def bottom-front-key-guard (->> (cube mount-width (/ mount-height 2) web-thickness)
                                 (translate [0 (/ mount-height 4) (+ (- (/ web-thickness 2)) -4.5)])))

(def bottom-plate
  (union
   (apply union
          (for [column columns
                row (drop-last rows) ;;
                :when (or (not= column 0)
                          (not= row 4))]
            (->> bottom-key-guard
                 (key-place column row))))
   (thumb-layout (rotate (/ π 2) [0 0 1] bottom-key-guard))
   (apply union
          (for [column columns
                row [(last rows)] ;;
                :when (or (not= column 0)
                          (not= row 4))]
            (->> bottom-front-key-guard
                 (key-place column row))))
   (let [shift #(translate [0 0 (+ (- web-thickness) -5)] %)
         web-post-tl (shift web-post-tl)
         web-post-tr (shift web-post-tr)
         web-post-br (shift web-post-br)
         web-post-bl (shift web-post-bl)
         half-shift-correction #(translate [0 (/ mount-height 2) 0] %)
         half-post-br (half-shift-correction web-post-br)
         half-post-bl (half-shift-correction web-post-bl)
         row-connections (concat
                          (for [column (drop-last columns)
                                row (drop-last rows)
                                :when (or (not= column 0)
                                          (not= row 4))]
                            (triangle-hulls
                             (key-place (inc column) row web-post-tl)
                             (key-place column row web-post-tr)
                             (key-place (inc column) row web-post-bl)
                             (key-place column row web-post-br)))
                          (for [column (drop-last columns)
                                row [(last rows)]
                                :when (or (not= column 0)
                                          (not= row 4))]
                            (triangle-hulls
                             (key-place (inc column) row web-post-tl)
                             (key-place column row web-post-tr)
                             (key-place (inc column) row half-post-bl)
                             (key-place column row half-post-br))))
         column-connections (for [column columns
                                  row (drop-last rows)
                                  :when (or (not= column 0)
                                            (not= row 3))]
                              (triangle-hulls
                               (key-place column row web-post-bl)
                               (key-place column row web-post-br)
                               (key-place column (inc row) web-post-tl)
                               (key-place column (inc row) web-post-tr)))
         diagonal-connections (for [column (drop-last columns)
                                    row (drop-last rows)
                                    :when (or (not= column 0)
                                              (not= row 3))]
                                (triangle-hulls
                                 (key-place column row web-post-br)
                                 (key-place column (inc row) web-post-tr)
                                 (key-place (inc column) row web-post-bl)
                                 (key-place (inc column) (inc row) web-post-tl)))
         main-keys-bottom (concat row-connections
                                  column-connections
                                  diagonal-connections)
         front-wall (concat
                     (for [x (range 2 5)]
                       (union
                        (hull (case-place (- x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                              (case-place (+ x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                              (key-place x 4 half-post-bl)
                              (key-place x 4 half-post-br))
                        (hull (case-place (- x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                              (key-place x 4 half-post-bl)
                              (key-place (- x 1) 4 half-post-br))))
                     [(hull (case-place right-wall-column 4 (translate [0 1 1] wall-sphere-bottom-front))
                            (case-place (- right-wall-column 1) 4 (translate [0 1 1] wall-sphere-bottom-front))
                            (key-place 5 4 half-post-bl)
                            (key-place 5 4 half-post-br))
                      (hull (case-place (+ 4 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                            (case-place (- right-wall-column 1) 4 (translate [0 1 1] wall-sphere-bottom-front))
                            (key-place 4 4 half-post-br)
                            (key-place 5 4 half-post-bl))])
         right-wall (concat
                     (for [x (range 0 4)]
                       (hull (case-place right-wall-column x (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (key-place 5 x web-post-br)
                             (key-place 5 x web-post-tr)))
                     (for [x (range 0 4)]
                       (hull (case-place right-wall-column x (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (case-place right-wall-column (inc x) (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (key-place 5 x web-post-br)
                             (key-place 5 (inc x) web-post-tr)))
                     [(union
                       (hull (case-place right-wall-column 0 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (case-place right-wall-column 0.02 (translate [-1 -1 1] (wall-sphere-bottom 1)))
                             (key-place 5 0 web-post-tr)
                             )
                       (hull (case-place right-wall-column 4 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (case-place right-wall-column 4 (translate [0 1 1] (wall-sphere-bottom 0)))
                             (key-place 5 4 half-post-br)
                             )
                       (hull (case-place right-wall-column 4 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (key-place 5 4 half-post-br)
                             (key-place 5 4 web-post-tr)))])
         back-wall (concat
                    (for [x (range 1 6)]
                      (union
                       (hull (case-place (- x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                             (case-place (+ x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                             (key-place x 0 web-post-tl)
                             (key-place x 0 web-post-tr))
                       (hull (case-place (- x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                             (key-place x 0 web-post-tl)
                             (key-place (- x 1) 0 web-post-tr))))
                    [(hull (case-place left-wall-column 0 (translate [1 -1 1] wall-sphere-bottom-back))
                           (case-place (+ left-wall-column 1) 0  (translate [0 -1 1] wall-sphere-bottom-back))
                           (key-place 0 0 web-post-tl)
                           (key-place 0 0 web-post-tr))])
         left-wall (let [place case-place]
                     [(hull (place left-wall-column 0 (translate [1 -1 1] wall-sphere-bottom-back))
                            (place left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
                            (key-place 0 0 web-post-tl)
                            (key-place 0 0 web-post-bl))
                      (hull (place left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
                            (place left-wall-column 2 (translate [1 0 1] wall-sphere-bottom-back))
                            (key-place 0 0 web-post-bl)
                            (key-place 0 1 web-post-bl))
                      (hull (place left-wall-column 2 (translate [1 0 1] wall-sphere-bottom-back))
                            (place left-wall-column 1.69 (translate [1 0 1] wall-sphere-bottom-front))
                            (key-place 0 1 web-post-bl)
                            (key-place 0 2 web-post-bl))
                      (hull (place left-wall-column 1.69 (translate [1 0 1] wall-sphere-bottom-front))
                            (key-place 0 2 web-post-bl)
                            (key-place 0 3 web-post-tl))])
         thumbs [(hull (thumb-place 0 -1/2 web-post-bl)
                       (thumb-place 0 -1/2 web-post-tl)
                       (thumb-place 1 -1/2 web-post-tr)
                       (thumb-place 1 -1/2 web-post-br))
                 (hull (thumb-place 1 -1/2 web-post-tr)
                       (thumb-place 1 -1/2 web-post-tl)
                       (thumb-place 1 1 web-post-bl)
                       (thumb-place 1 1 web-post-br))
                 (hull (thumb-place 2 -1 web-post-tr)
                       (thumb-place 2 -1 web-post-tl)
                       (thumb-place 2 0 web-post-bl)
                       (thumb-place 2 0 web-post-br))
                 (hull (thumb-place 2 0 web-post-tr)
                       (thumb-place 2 0 web-post-tl)
                       (thumb-place 2 1 web-post-bl)
                       (thumb-place 2 1 web-post-br))
                 (triangle-hulls (thumb-place 2 1 web-post-tr)
                                 (thumb-place 1 1 web-post-tl)
                                 (thumb-place 2 1 web-post-br)
                                 (thumb-place 1 1 web-post-bl)
                                 (thumb-place 2 0 web-post-tr)
                                 (thumb-place 1 -1/2 web-post-tl)
                                 (thumb-place 2 0 web-post-br)
                                 (thumb-place 1 -1/2 web-post-bl)
                                 (thumb-place 2 -1 web-post-tr)
                                 (thumb-place 2 -1 web-post-br))
                 (hull (thumb-place 2 -1 web-post-br)
                       (thumb-place 1 -1/2 web-post-bl)
                       (thumb-place 1 -1 web-post-bl))
                 (hull (thumb-place 1 -1/2 web-post-bl)
                       (thumb-place 1 -1 web-post-bl)
                       (thumb-place 1 -1/2 web-post-br)
                       (thumb-place 1 -1 web-post-br))
                 (hull (thumb-place 0 -1/2 web-post-bl)
                       (thumb-place 0 -1 web-post-bl)
                       (thumb-place 0 -1/2 web-post-br)
                       (thumb-place 0 -1 web-post-br))
                 (hull (thumb-place 0 -1/2 web-post-bl)
                       (thumb-place 0 -1 web-post-bl)
                       (thumb-place 1 -1/2 web-post-br)
                       (thumb-place 1 -1 web-post-br))]
         thumb-back-wall [(hull
                           (thumb-place 1/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
                           (thumb-place 1 1 web-post-tr)
                           (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
                           (thumb-place 1 1 web-post-tl))

                          (hull
                           (thumb-place (+ 5/2 0.05) thumb-back-y (translate [1 -1 1] wall-sphere-bottom-back))
                           (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
                           (thumb-place 1 1 web-post-tl)
                           (thumb-place 2 1 web-post-tl))
                          (hull
                           (thumb-place 1/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
                           (case-place left-wall-column 1.69 (translate [1 0 1] wall-sphere-bottom-front))
                           (key-place 0 3 web-post-tl)
                           (thumb-place 1 1 web-post-tr))
                          ]
         thumb-left-wall [(hull
                           (thumb-place thumb-left-wall-column thumb-back-y (translate [1 -1 1] wall-sphere-bottom-back))
                           (thumb-place thumb-left-wall-column 0 (translate [1 0 1] wall-sphere-bottom-back))
                           (thumb-place 2 1 web-post-tl)
                           (thumb-place 2 1 web-post-bl))
                          (hull
                           (thumb-place thumb-left-wall-column 0 (translate [1 0 1] wall-sphere-bottom-back))
                           (thumb-place 2 0 web-post-tl)
                           (thumb-place 2 1 web-post-bl))
                          (hull
                           (thumb-place thumb-left-wall-column 0 (translate [1 0 1] wall-sphere-bottom-back))
                           (thumb-place thumb-left-wall-column -1 (translate [1 0 1] wall-sphere-bottom-back))
                           (thumb-place 2 0 web-post-tl)
                           (thumb-place 2 0 web-post-bl))
                          (hull
                           (thumb-place thumb-left-wall-column -1 (translate [1 0 1] wall-sphere-bottom-back))
                           (thumb-place 2 -1 web-post-tl)
                           (thumb-place 2 0 web-post-bl))
                          (hull
                           (thumb-place thumb-left-wall-column -1 (translate [1 0 1] wall-sphere-bottom-back))
                           (thumb-place thumb-left-wall-column (+ -1 0.07) (translate [1 1 1] wall-sphere-bottom-front))
                           (thumb-place 2 -1 web-post-tl)
                           (thumb-place 2 -1 web-post-bl))]
         thumb-front-wall [(hull (thumb-place (+ 5/2 0.05) thumb-front-row (translate [1 1 1] wall-sphere-bottom-front))
                                 (thumb-place (+ 3/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 2 -1 web-post-bl)
                                 (thumb-place 2 -1 web-post-br))
                           (hull (thumb-place (+ 1/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place (+ 3/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 0 -1 web-post-bl)
                                 (thumb-place 1 -1 web-post-bl)
                                 (thumb-place 1 -1 web-post-br)
                                 (thumb-place 2 -1 web-post-br))
                           (hull (thumb-place thumb-right-wall thumb-front-row (translate [-1 1 1] wall-sphere-bottom-front))
                                 (thumb-place (+ 1/2 0.05) thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 0 -1 web-post-bl)
                                 (thumb-place 0 -1 web-post-br))]
         thumb-inside [(triangle-hulls
                        (thumb-place 1 1 web-post-tr)
                        (key-place 0 3 web-post-tl)
                        (thumb-place 1 1 web-post-br)
                        (key-place 0 3 web-post-bl)
                        (thumb-place 1 -1/2 web-post-tr)
                        (thumb-place 0 -1/2 web-post-tl)
                        (key-place 0 3 web-post-bl)
                        (thumb-place 0 -1/2 web-post-tr)
                        (key-place 0 3 web-post-br)
                        (key-place 1 3 web-post-bl)
                        (thumb-place 0 -1/2 web-post-tr)
                        (key-place 1 4 web-post-tl)
                        (key-place 1 4 half-post-bl))

                       (hull
                        (thumb-place 0 -1/2 web-post-tr)
                        (thumb-place 0 -1/2 web-post-br)
                        (key-place 1 4 half-post-bl))

                       (hull
                        (key-place 1 4 half-post-bl)
                        (key-place 1 4 half-post-br)
                        (case-place (- 2 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                        (case-place 0.7 4 (translate [0 1 1] wall-sphere-bottom-front)))

                       (hull
                        (thumb-place 0 -1 web-post-br)
                        (thumb-place 0 -1/2 web-post-br)
                        (thumb-place thumb-right-wall thumb-front-row (translate [-1 1 1] wall-sphere-bottom-front))
                        (key-place 1 4 (translate [0 0 8.5] web-post-bl))
                        (key-place 1 4 half-post-bl)
                        )]
         stands (let [bumper-diameter 9.6
                      bumper-radius (/ bumper-diameter 2)
                      stand-diameter (+ bumper-diameter 2)
                      stand-radius (/ stand-diameter 2)
                      stand-at #(difference (->> (sphere stand-radius)
                                                 (translate [0 0 (+ (/ stand-radius -2) -4.5)])
                                                 %
                                                 (bottom-hull))
                                            (->> (cube stand-diameter stand-diameter stand-radius)
                                                 (translate [0 0 (/ stand-radius -2)])
                                                 %)
                                            (->> (sphere bumper-radius)
                                                 (translate [0 0 (+ (/ stand-radius -2) -4.5)])
                                                 %
                                                 (bottom 1.5)))]
                  [(stand-at #(key-place 0 1 %))
                   (stand-at #(thumb-place 1 -1/2 %))
                   (stand-at #(key-place 5 0 %))
                   (stand-at #(key-place 5 3 %))])]
     (apply union
            (concat
             main-keys-bottom
             front-wall
             right-wall
             back-wall
             left-wall
             thumbs
             thumb-back-wall
             thumb-left-wall
             thumb-front-wall
             thumb-inside
             stands)))))

(def bolt-hole
  (union
    (->>  (cylinder 1.2 20)
          (with-fn 50))
    (->>  (cylinder 2.25 10)
          (translate [0 0 6.5])
          (with-fn 6))
  ))

(def bolt-holes
  (union
    (translate [-75 -80 0] bolt-hole)
    (translate [68 -54 0] bolt-hole)
    (translate [68 47 0] bolt-hole)
    (translate [-44 0 0] bolt-hole)
  ))

(def bolt-plates
  (let [x left-wall-column
        trrs-length 13
        place desk-case-place
        back-left (place x back-y wall-sphere-bottom-back)
        back-right (place (+ x 1) back-y wall-sphere-bottom-back)]
    (union
      (->>  (polygon [[-50 8] [-38 7] [-38 -7] [-50 -8]])
            (extrude-linear {:height 4 :twist 0 :convexity 0})
            (translate [0 0 2]))
      (->>  (polygon [[64 53] [74 52] [75 43] [64 43]])
            (extrude-linear {:height 4 :twist 0 :convexity 0})
            (translate [0 0 2]))
      (->>  (polygon [[63 -60] [63 -48] [74.5 -48] [74 -60]])
            (extrude-linear {:height 4 :twist 0 :convexity 0})
            (translate [0 0 2]))
      (->>  (polygon [[-68 -83] [-80 -86] [-82 -76] [-72 -76]])
            (extrude-linear {:height 4 :twist 0 :convexity 0})
            (translate [0 0 2]))
    )
  ))

(defn circuit-cover [width length height]
  (let [cover-sphere-radius 1
        cover-sphere (->> (sphere cover-sphere-radius)
                          (with-fn 20))
        cover-sphere-z (+ (- height) (- cover-sphere-radius))
        cover-sphere-x (+ (/ width 2) cover-sphere-radius)
        cover-sphere-y (+ (/ length 2) (+ cover-sphere-radius))
        cover-sphere-tl (->> cover-sphere
                             (translate [(- cover-sphere-x) (- cover-sphere-y) cover-sphere-z])
                             (key-place 1/2 3/2))
        cover-sphere-tr (->> cover-sphere
                             (translate [cover-sphere-x (- cover-sphere-y) cover-sphere-z])
                             (key-place 1/2 3/2))
        cover-sphere-br (->> cover-sphere
                             (translate [cover-sphere-x cover-sphere-y cover-sphere-z])
                             (key-place 1/2 3/2))
        cover-sphere-bl (->> cover-sphere
                             (translate [(- cover-sphere-x) cover-sphere-y cover-sphere-z])
                             (key-place 1/2 3/2))

        lower-to-bottom #(translate [0 0 (+ (- cover-sphere-radius) -5.5)] %)
        bl (->> cover-sphere lower-to-bottom (key-place 0 1/2))
        br (->> cover-sphere lower-to-bottom (key-place 1 1/2))
        tl (->> cover-sphere lower-to-bottom (key-place 0 5/2))
        tr (->> cover-sphere lower-to-bottom (key-place 1 5/2))

        mlb (->> cover-sphere
                 (translate [(- cover-sphere-x) 0 (+ (- height) -1)])
                 (key-place 1/2 3/2))
        mrb (->> cover-sphere
                 (translate [cover-sphere-x 0 (+ (- height) -1)])
                 (key-place 1/2 3/2))

        mlt (->> cover-sphere
                 (translate [(+ (- cover-sphere-x) -4) 0 -6])
                 (key-place 1/2 3/2))
        mrt (->> cover-sphere
                 (translate [(+ cover-sphere-x 4) 0 -6])
                 (key-place 1/2 3/2))]
    (union
     (hull cover-sphere-bl cover-sphere-br cover-sphere-tl cover-sphere-tr)
     (hull cover-sphere-br cover-sphere-bl bl br)
     (hull cover-sphere-tr cover-sphere-tl tl tr)
     (hull cover-sphere-tl tl mlb mlt)
     (hull cover-sphere-bl bl mlb mlt)
     (hull cover-sphere-tr tr mrb mrt)
     (hull cover-sphere-br br mrb mrt))))

(def io-exp-width 10)
(def io-exp-height 8)
(def io-exp-length 36)

(def teensy-width 20)
(def teensy-height 12)
(def teensy-length 33)

(def io-exp-cover (circuit-cover io-exp-width io-exp-length io-exp-height))
(def teensy-cover (circuit-cover teensy-width teensy-length teensy-height))

(def trrs-diameter 6.6)
(def trrs-radius (/ trrs-diameter 2))
(def trrs-hole-depth 10)

(def trrs-hole (->> (cylinder trrs-radius trrs-hole-depth)
                    (rotate (/ π 14) [0 1 0])
                    (rotate (/ π 2) [1 0 0])
                    (translate [-60 -15 (+ trrs-radius 4)])
                    (with-fn 50)))

(def trrs-hole-just-circle
  (->> (cylinder trrs-radius trrs-hole-depth)
       (rotate (/ π 2) [1 0 0])
       (translate [0 (+ (/ mount-height 2) 4) (- trrs-radius)])
       (with-fn 50)
       (key-place 1/2 0)))

(def trrs-box-hole (->> (cube 14 14 7 )
                        (translate [0 1 -3.5])))


(def trrs-cutout
  (->> (union trrs-hole
              trrs-box-hole)
       (key-place 1/2 0)))

(def teensy-pcb-thickness 1.6)
(def teensy-offset-height 5)

(def teensy-pcb (->> (cube 18 30.5 teensy-pcb-thickness)
                     (translate [0 0 (+ (/ teensy-pcb-thickness -2) (- teensy-offset-height))])
                     (key-place 1/2 3/2)
                     (color [1 0 0])))

(def teensy-support
  (difference
   (union
    (->> (cube 3 3 9)
         (translate [0 0 -2])
         (key-place 1/2 3/2)
         (color [0 1 0]))
    (hull (->> (cube 3 6 9)
               (translate [0 0 -2])
               (key-place 1/2 2)
               (color [0 0 1]))
          (->> (cube 3 3 (+ teensy-pcb-thickness 3))
               (translate [0 (/ 30.5 -2) (+ (- teensy-offset-height)
                                            #_(/ (+ teensy-pcb-thickness 3) -2)
                                            )])
               (key-place 1/2 3/2)
               (color [0 0 1]))))
   teensy-pcb
   (->> (cube 18 30.5 teensy-pcb-thickness)
        (translate [0 1.5 (+ (/ teensy-pcb-thickness -2) (- teensy-offset-height) -1)])
        (key-place 1/2 3/2)
        (color [1 0 0]))))

(def usb-cutout
  (let [hole-height 6.2
        side-radius (/ hole-height 2)
        hole-width 10.75
        side-cylinder (->> (cylinder side-radius arduino-length)
                           (with-fn 20)
                           (translate [(/ (- hole-width hole-height) 2) 0 0]))]
        (->> (hull side-cylinder
                  (mirror [-1 0 0] side-cylinder))
          (rotate (/ π 2) [1 0 0])
          (rotate (/ π 2) [0 1 0])
          (translate [(- 30) (- 54 (/ arduino-length 2)) (+ (/ arduino-width 2) 4)]))))

;;;;;;;;;;;;;;;;;;
;; Final Export ;;
;;;;;;;;;;;;;;;;;;

(def dactyl-bottom-right
  (difference
     bottom-plate
     new-case
     (->> (cube 1000 1000 10) (translate [0 0 -5]))))

(def dactyl-bottom-left
  (mirror [-1 0 0] dactyl-bottom-right))

(def dactyl-top-right
  (difference
    (union key-holes
           connectors
           thumb
           new-case
           bolt-plates
           )
    ; (union thumb-back-wall
    ;        trrs-holder)
    bolt-holes
    trrs-hole
    usb-cutout))

(def dactyl-top-left
  (mirror [-1 0 0] dactyl-top-right))

; (spit "things/switch-hole.scad"
;       (write-scad single-plate))

; (spit "things/alps-holes.scad"
;       (write-scad (union connectors key-holes)))

(spit "things/dactyl-top-right.scad"
      (write-scad dactyl-top-right))

; (spit "things/dactyl-bottom-right.scad"
;       (write-scad dactyl-bottom-right))

; (spit "things/dactyl-top-left.scad"
;       (write-scad dactyl-top-left))

; (spit "things/dactyl-bottom-left.scad"
;       (write-scad dactyl-bottom-left))

; (spit "things/dactyl-top-left-with-teensy.scad"
;       (write-scad (mirror [-1 0 0] dactyl-top-right)))

; (spit "things/dactyl-bottom-left-with-teensy.scad"
;       (write-scad (mirror [-1 0 0] dactyl-bottom-right)))
