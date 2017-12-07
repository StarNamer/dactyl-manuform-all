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

(def single-plate
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

#_(def single-plate
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

(defn case-place [column row shape offset]
  (let [row-placed-shape (->> shape
                              (translate [0 0 (- row-radius)])
                              (rotate (* α (- 2 row)) [1 0 0])
                              (translate [0 0 row-radius]))
        ; column-offset [0 -4.5 5.64]
        column-offset offset
        column-angle (* β (- 2 column))
        placed-shape (->> row-placed-shape
                          (translate [0 0 (- column-radius)])
                          (rotate column-angle [0 1 0])
                          (translate [0 0 column-radius])
                          (translate column-offset))]
    (->> placed-shape
         (rotate (/ π 12) [0 1 0])
         (translate [0 0 13]))))
(def case-place-side #(case-place %1 %2 %3 [0 -4.5 5.64]))
(def case-place-right #(case-place %1 %2 %3 [0 -4.5 5.40]))
(def case-place-left #(case-place %1 %2 %3 [0 -4.5 5.64]))
(def case-place-front #(case-place %1 %2 %3 [0 -2.8 4.0]))
(def case-place-back #(case-place %1 %2 %3 [0 -3.2 5.64]))

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
            (thumb-place 2 0 web-post-tr)
            (thumb-place 2 0 web-post-br))
      (hull (thumb-place 1 -1/2 thumb-tl)
            (thumb-place 1 -1/2 thumb-bl)
            (thumb-place 2 0 web-post-br)
            (thumb-place 2 -1 web-post-tr))
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
(def right-wall-column (+ (last columns) 0.48))
(def left-wall-column (- (first columns) 0.45))
(def thumb-back-y 0.86)
(def thumb-right-wall-column (- -1/2 0.05))
(def thumb-front-row -0.9)
(def thumb-left-wall-column 2.45)
(def back-y 0.02)
(def front-min-column 0.7)

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
                    (+ (/ mount-height 2) 4)
                    front-to-back-scale)
                   -6]))

(defn wall-sphere-top [front-to-back-scale]
  (wall-sphere-at [0
                   (scale-to-range
                    (+ (/ mount-height -2) -3.5)
                    (+ (/ mount-height 2) 3.5)
                    front-to-back-scale)
                   3.5]))

(def wall-sphere-top-back (wall-sphere-top 1))
(def wall-sphere-bottom-back (wall-sphere-bottom 1))
(def wall-sphere-bottom-front (wall-sphere-bottom 0))
(def wall-sphere-top-front (wall-sphere-top 0))

(def front-wall
  (union
    (hull (case-place-front front-min-column 4 (translate [0 1 1] wall-sphere-bottom-front))
          (case-place-front (+ 1 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
          (key-place 1 4 web-post-bl)
          (key-place 1 4 web-post-br))

    (hull (key-place 2 4 web-post-bl)
          (key-place 2 4 web-post-br)
          (key-place 1 4 web-post-br)
          (key-place 3 4 web-post-bl))
    (hull (case-place-front (- 2 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
          (case-place-front (+ 2 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
          (key-place 1 4 web-post-br)
          (key-place 3 4 web-post-bl))

    (apply union
           (for [x (range 3 5)]
             (union
              (hull (case-place-front (- x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                    (case-place-front (+ x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                    (key-place x 4 web-post-bl)
                    (key-place x 4 web-post-br)))))

    (hull (case-place-front (+ 3 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
          (key-place 3 4 web-post-br)
          (key-place 4 4 web-post-bl))
    (hull (case-place-front (+ 4 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
          (key-place 4 4 web-post-br)
          (key-place 5 4 web-post-bl))
    (hull (case-place-front (+ 4 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
          (case-place-front right-wall-column 4 (translate [-1 1 1] wall-sphere-bottom-front))
          (key-place 5 4 web-post-bl)
          (key-place 5 4 web-post-br))))

(def back-wall
  (union
   (hull (case-place-back left-wall-column 0 (translate [1 -1 1] wall-sphere-bottom-back))
         (case-place-back (- 1 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
         (key-place 0 0 web-post-tr)
         (key-place 0 0 web-post-tl))
   (apply union
      (for [x (range 1 5)]
        (union
         (hull (case-place-back (- x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
               (case-place-back (+ x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
               (key-place x 0 web-post-tl)
               (key-place x 0 web-post-tr))
         (hull (case-place-back (- x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
               (key-place x 0 web-post-tl)
               (key-place (- x 1) 0 web-post-tr)))))
   (hull (case-place-back right-wall-column 0 (translate [-1 -1 1] (wall-sphere-bottom 1)))
         (case-place-back (- 5 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
         (key-place 5 0 web-post-tr)
         (key-place 5 0 web-post-tl))
   (hull (case-place-back (- 5 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
         (key-place 5 0 web-post-tl)
         (key-place 4 0 web-post-tr))))

(def right-wall
  (union
    (apply union
      (concat
       [(hull (case-place-back right-wall-column 0 (translate [-1 -1 1] (wall-sphere-bottom 1)))
              (case-place-right right-wall-column 0 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
              (key-place 5 0 web-post-tr))]
       (for [x (range 0 4)]
        (hull (case-place-right right-wall-column x (translate [-1 0 1] (wall-sphere-bottom 1/2)))
              (key-place 5 x web-post-tr)
              (key-place 5 x web-post-br)))
       (for [x (range 0 4)]
        (hull (case-place-right right-wall-column x (translate [-1 0 1] (wall-sphere-bottom 1/2)))
              (case-place-right right-wall-column (inc x) (translate [-1 0 1] (wall-sphere-bottom 1/2)))
              (key-place 5 (inc x) web-post-tr)
              (key-place 5 x web-post-br)))
       [(union
         (hull (case-place-right right-wall-column 4 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
               (key-place 5 4 web-post-tr)
               (key-place 5 4 web-post-br))
         (hull (case-place-right right-wall-column 4 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
               (case-place-front right-wall-column 4 (translate [-1 1 1] (wall-sphere-bottom 0)))
               (key-place 5 4 web-post-br)))]))))

(def left-wall
  (union
   (hull (case-place-back left-wall-column 0 (translate [1 -1 1] wall-sphere-bottom-back))
         (case-place-left left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
         (key-place 0 0 web-post-tl)
         (key-place 0 0 web-post-bl))
   (hull (case-place-left left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
         (key-place 0 0 web-post-bl)
         (key-place 0 1 web-post-tl))
   (hull (case-place-left left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
         (case-place-left left-wall-column 1.9 (translate [1 0 1] wall-sphere-bottom-back))
         (key-place 0 1 web-post-tl)
         (key-place 0 1 web-post-bl))
   (hull (case-place-left left-wall-column 1.9 (translate [1 0 1] wall-sphere-bottom-back))
         (key-place 0 1 web-post-bl)
         (key-place 0 2 web-post-tl))
   (hull (case-place-left left-wall-column 1.9 (translate [1 0 1] wall-sphere-bottom-back))
         (case-place-left left-wall-column 1.6666 (translate [1 0 1] wall-sphere-bottom-front))
         (key-place 0 2 web-post-tl)
         (key-place 0 2 web-post-bl))
   (hull (case-place-left left-wall-column 1.6666 (translate [1 0 1] wall-sphere-bottom-front))
         (key-place 0 2 web-post-bl)
         (key-place 0 3 web-post-tl))
   (hull (case-place-left left-wall-column 1.6666 (translate [1 0 1] wall-sphere-bottom-front))
         (thumb-place 1/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
         (key-place 0 3 web-post-tl)
         (thumb-place 1 1 web-post-tr))))

(def thumb-back-wall
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
     (hull
      (thumb-place 1/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
      (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
      (thumb-place 1 1 web-post-tr)
      (thumb-place 1 1 web-post-tl))
     (hull
      (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
      (thumb-place 1 1 web-post-tl)
      (thumb-place 2 1 web-post-tr))
     (hull
      (thumb-place thumb-left-wall-column thumb-back-y (translate [1 -1 1] wall-sphere-bottom-back))
      (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
      (thumb-place 2 1 web-post-tr)
      (thumb-place 2 1 web-post-tl)))))

(def thumb-left-wall
  (let [step wall-step
        plate-height (/ (- sa-double-length mount-height) 2)
        thumb-tl (->> web-post-tl
                      (translate [0 plate-height 0]))
        thumb-bl (->> web-post-bl
                      (translate [0 (- plate-height) 0]))
        thumb-tr (->> web-post-tr
                      (translate [0 plate-height 0]))
        thumb-br (->> web-post-br
                      (translate [0 (- plate-height) 0]))]
    (union
     (hull
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
      (thumb-place thumb-left-wall-column thumb-front-row (translate [1 1 1] wall-sphere-bottom-front))
      (thumb-place 2 -1 web-post-tl)
      (thumb-place 2 -1 web-post-bl)))))

(def thumb-right-wall
  (let [plate-height (/ (- sa-double-length mount-height) 2)
        thumb-tl (->> web-post-tl
                      (translate [0 plate-height 0]))
        thumb-bl (->> web-post-bl
                      (translate [0 (- plate-height) 0]))
        thumb-tr (->> web-post-tr
                      (translate [-0 plate-height 0]))
        thumb-br (->> web-post-br
                      (translate [-0 (- plate-height) 0]))]
    (union
     (hull (case-place-front front-min-column 4 (translate [0 1 1] wall-sphere-bottom-front))
           (thumb-place thumb-right-wall-column thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (thumb-place 0 -1/2 thumb-br))
     (hull (case-place-front front-min-column 4 (translate [0 1 1] wall-sphere-bottom-front))
           (thumb-place 0 -1/2 thumb-br)
           (key-place 1 4 web-post-bl)))))

(def thumb-front-wall
  (let [plate-height (/ (- sa-double-length mount-height) 2)
        thumb-tl (->> web-post-tl
                      (translate [0 plate-height 0]))
        thumb-bl (->> web-post-bl
                      (translate [0 (- plate-height) 0]))
        thumb-tr (->> web-post-tr
                      (translate [-0 plate-height 0]))
        thumb-br (->> web-post-br
                      (translate [-0 (- plate-height) 0]))]
    (union
     (hull (thumb-place thumb-right-wall-column thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (thumb-place 1/2 thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (thumb-place 0 -1/2 thumb-br)
           (thumb-place 0 -1/2 thumb-bl))
     (hull (thumb-place 1/2 thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (thumb-place 0 -1/2 thumb-bl)
           (thumb-place 1 -1/2 thumb-br))
     (hull (thumb-place 1/2 thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (thumb-place 3/2 thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (thumb-place 1 -1/2 thumb-br)
           (thumb-place 1 -1/2 thumb-bl))
     (hull (thumb-place 3/2 thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (thumb-place 1 -1/2 thumb-bl)
           (thumb-place 2 -1 web-post-br))
     (hull (thumb-place 3/2 thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
           (thumb-place thumb-left-wall-column thumb-front-row (translate [1 1 1] wall-sphere-bottom-front))
           (thumb-place 2 -1 web-post-bl)
           (thumb-place 2 -1 web-post-br)))))

(def new-case
  (union front-wall
         right-wall
         back-wall
         left-wall
         thumb-back-wall
         thumb-left-wall
         thumb-right-wall
         thumb-front-wall))

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
                row (drop-last rows)
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
         plate-height (/ (- sa-double-length mount-height) 2)
         thumb-tl (->> web-post-tl
                       (translate [0 plate-height 0]))
         thumb-bl (->> web-post-bl
                       (translate [0 (- plate-height) 0]))
         thumb-tr (->> web-post-tr
                       (translate [-0 plate-height 0]))
         thumb-br (->> web-post-br
                       (translate [-0 (- plate-height) 0]))
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
                     [(hull (case-place-front front-min-column 4 (translate [0 1 1] wall-sphere-bottom-front))
                            (case-place-front (- 2 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                            (key-place 1 4 half-post-bl)
                            (key-place 1 4 half-post-br))]
                     (for [x (range 2 5)]
                       (union
                        (hull (case-place-front (- x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                              (case-place-front (+ x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                              (key-place x 4 half-post-bl)
                              (key-place x 4 half-post-br))
                        (hull (case-place-front (- x 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                              (key-place x 4 half-post-bl)
                              (key-place (- x 1) 4 half-post-br))))
                     [(hull (case-place-front (+ 4 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                            (key-place 4 4 half-post-br)
                            (key-place 5 4 half-post-bl))
                      (hull (case-place-front (+ 4 1/2) 4 (translate [0 1 1] wall-sphere-bottom-front))
                            (case-place-front right-wall-column 4 (translate [-1 1 1] wall-sphere-bottom-front))
                            (key-place 5 4 half-post-bl)
                            (key-place 5 4 half-post-br))])
         right-wall (concat
                     [(hull (case-place-back right-wall-column 0 (translate [-1 -1 1] (wall-sphere-bottom 1)))
                            (case-place-right right-wall-column 0 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                            (key-place 5 0 web-post-tr))]
                     (for [x (range 0 4)]
                       (hull (case-place-right right-wall-column x (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (key-place 5 x web-post-tr)
                             (key-place 5 x web-post-br)))
                     (for [x (range 0 4)]
                       (hull (case-place-right right-wall-column x (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (case-place-right right-wall-column (inc x) (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (key-place 5 (inc x) web-post-tr)
                             (key-place 5 x web-post-br)))
                     [(union
                       (hull (case-place-right right-wall-column 4 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (key-place 5 4 web-post-tr)
                             (key-place 5 4 half-post-br))
                       (hull (case-place-right right-wall-column 4 (translate [-1 0 1] (wall-sphere-bottom 1/2)))
                             (case-place-front right-wall-column 4 (translate [-1 1 1] (wall-sphere-bottom 0)))
                             (key-place 5 4 half-post-br)))])
         back-wall (concat
                    [(hull (case-place-back right-wall-column 0 (translate [-1 -1 1] (wall-sphere-bottom 1)))
                           (case-place-back (- 5 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                           (key-place 5 0 web-post-tr)
                           (key-place 5 0 web-post-tl))
                     (hull (case-place-back (- 5 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                           (key-place 5 0 web-post-tl)
                           (key-place 4 0 web-post-tr))]
                    (for [x (range 1 5)]
                      (union
                       (hull (case-place-back (- x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                             (case-place-back (+ x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                             (key-place x 0 web-post-tl)
                             (key-place x 0 web-post-tr))
                       (hull (case-place-back (- x 1/2) 0 (translate [0 -1 1] wall-sphere-bottom-back))
                             (key-place x 0 web-post-tl)
                             (key-place (- x 1) 0 web-post-tr))))
                    [(hull (case-place-back (- 1 1/2) 0  (translate [0 -1 1] wall-sphere-bottom-back))
                           (case-place-back left-wall-column 0 (translate [1 -1 1] wall-sphere-bottom-back))
                           (key-place 0 0 web-post-tr)
                           (key-place 0 0 web-post-tl))])
         left-wall [(hull (case-place-back left-wall-column 0 (translate [1 -1 1] wall-sphere-bottom-back))
                          (case-place-left left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
                          (key-place 0 0 web-post-tl)
                          (key-place 0 0 web-post-bl))
                    (hull (case-place-left left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
                          (key-place 0 0 web-post-bl)
                          (key-place 0 1 web-post-tl))
                    (hull (case-place-left left-wall-column 1 (translate [1 0 1] wall-sphere-bottom-back))
                          (case-place-left left-wall-column 1.9 (translate [1 0 1] wall-sphere-bottom-back))
                          (key-place 0 1 web-post-tl)
                          (key-place 0 1 web-post-bl))
                    (hull (case-place-left left-wall-column 1.9 (translate [1 0 1] wall-sphere-bottom-back))
                          (key-place 0 1 web-post-bl)
                          (key-place 0 2 web-post-tl))
                    (hull (case-place-left left-wall-column 1.9 (translate [1 0 1] wall-sphere-bottom-back))
                          (case-place-left left-wall-column 1.6666 (translate [1 0 1] wall-sphere-bottom-front))
                          (key-place 0 2 web-post-tl)
                          (key-place 0 2 web-post-bl))
                    (hull (case-place-left left-wall-column 1.6666 (translate [1 0 1] wall-sphere-bottom-front))
                          (key-place 0 2 web-post-bl)
                          (key-place 0 3 web-post-tl))
                    (hull (case-place-left left-wall-column 1.6666 (translate [1 0 1] wall-sphere-bottom-front))
                          (thumb-place 1/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
                          (key-place 0 3 web-post-tl)
                          (thumb-place 1 1 web-post-tr))
                    ]
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
                           (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
                           (thumb-place 1 1 web-post-tr)
                           (thumb-place 1 1 web-post-tl))
                          (hull
                           (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
                           (thumb-place 1 1 web-post-tl)
                           (thumb-place 2 1 web-post-tr))
                          (hull
                           (thumb-place thumb-left-wall-column thumb-back-y (translate [1 -1 1] wall-sphere-bottom-back))
                           (thumb-place 3/2 thumb-back-y (translate [0 -1 1] wall-sphere-bottom-back))
                           (thumb-place 2 1 web-post-tr)
                           (thumb-place 2 1 web-post-tl))]
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
                           (thumb-place thumb-left-wall-column thumb-front-row (translate [1 1 1] wall-sphere-bottom-front))
                           (thumb-place 2 -1 web-post-tl)
                           (thumb-place 2 -1 web-post-bl))]
         thumb-right-wall [(hull (case-place-front front-min-column 4 (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place thumb-right-wall-column thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 0 -1 web-post-br))
                           (hull (case-place-front front-min-column 4 (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 0 -1 web-post-br)
                                 (thumb-place 0 -1/2 web-post-br))
                           (hull (case-place-front front-min-column 4 (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 0 -1/2 web-post-br)
                                 (key-place 1 4 half-post-bl))]
         thumb-front-wall [(hull (thumb-place thumb-right-wall-column thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 1/2 thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 0 -1 web-post-br)
                                 (thumb-place 0 -1 web-post-bl))
                           (hull (thumb-place 1/2 thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 0 -1 web-post-bl)
                                 (thumb-place 1 -1 web-post-br))
                           (hull (thumb-place 1/2 thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 3/2 thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 1 -1 web-post-br)
                                 (thumb-place 1 -1 web-post-bl))
                           (hull (thumb-place 3/2 thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place 1 -1 web-post-bl)
                                 (thumb-place 2 -1 web-post-br))
                           (hull (thumb-place 3/2 thumb-front-row (translate [0 1 1] wall-sphere-bottom-front))
                                 (thumb-place thumb-left-wall-column thumb-front-row (translate [1 1 1] wall-sphere-bottom-front))
                                 (thumb-place 2 -1 web-post-br)
                                 (thumb-place 2 -1 web-post-bl))]
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
                        (key-place 1 4 half-post-bl))]
         stands (let [bumper-diameter 9.0
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
                                                 (bottom 0.5)))]
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
             thumb-right-wall
             thumb-inside
             stands)))))

(def screw-hole (->> (cylinder 1.5 60)
                     (translate [0 0 3/2])
                     (with-fn wall-sphere-n)))

(def screw-holes
  (union
   (key-place 0 1/2 screw-hole)
   (key-place 5 1/2 screw-hole)
   (key-place 5 (+ 3 1/2) screw-hole)
   (thumb-place 2 -1/2 screw-hole)))

(def circuit-cover-offset-x 0)
(def circuit-cover-offset-y 0)
(defn circuit-cover [width length height]
  (let [cover-slope-y 0.14
        cover-sphere-radius 1
        cover-sphere (->> (sphere cover-sphere-radius)
                          (with-fn 20))
        cover-sphere-z (+ (- height) (- cover-sphere-radius))
        cover-sphere-x (+ (/ width 2) cover-sphere-radius)
        cover-sphere-y (+ (/ length 2) (+ cover-sphere-radius))
        cover-sphere-tl (->> cover-sphere
                             (translate [(- cover-sphere-x) (- cover-sphere-y) cover-sphere-z])
                             (key-place (+ 1/2 circuit-cover-offset-x) (+ 3/2 circuit-cover-offset-y)))
        cover-sphere-tr (->> cover-sphere
                             (translate [cover-sphere-x (- cover-sphere-y) cover-sphere-z])
                             (key-place (+ 1/2 circuit-cover-offset-x) (+ 3/2 circuit-cover-offset-y)))
        cover-sphere-br (->> cover-sphere
                             (translate [cover-sphere-x cover-sphere-y cover-sphere-z])
                             (key-place (+ 1/2 circuit-cover-offset-x) (+ 3/2 circuit-cover-offset-y)))
        cover-sphere-bl (->> cover-sphere
                             (translate [(- cover-sphere-x) cover-sphere-y cover-sphere-z])
                             (key-place (+ 1/2 circuit-cover-offset-x) (+ 3/2 circuit-cover-offset-y)))

        lower-to-bottom #(translate [0 0 (+ (- cover-sphere-radius) -5)] %)
        bl (->> cover-sphere lower-to-bottom (key-place (+ 0 circuit-cover-offset-x) (+ (+ 1/2 cover-slope-y) circuit-cover-offset-y)))
        br (->> cover-sphere lower-to-bottom (key-place (+ 1 circuit-cover-offset-x) (+ (+ 1/2 cover-slope-y) circuit-cover-offset-y)))
        tl (->> cover-sphere lower-to-bottom (key-place (+ 0 circuit-cover-offset-x) (+ (- 5/2 cover-slope-y) circuit-cover-offset-y)))
        tr (->> cover-sphere lower-to-bottom (key-place (+ 1 circuit-cover-offset-x) (+ (- 5/2 cover-slope-y) circuit-cover-offset-y)))

        mlb (->> cover-sphere
                 (translate [(- cover-sphere-x) 0 (+ (- height) -1)])
                 (key-place (+ 1/2 circuit-cover-offset-x) (+ 3/2 circuit-cover-offset-y)))
        mrb (->> cover-sphere
                 (translate [cover-sphere-x 0 (+ (- height) -1)])
                 (key-place (+ 1/2 circuit-cover-offset-x) (+ 3/2 circuit-cover-offset-y)))

        mlt (->> cover-sphere
                 (translate [(+ (- cover-sphere-x) -4) 0 -5.5])
                 (key-place (+ 1/2 circuit-cover-offset-x) (+ 3/2 circuit-cover-offset-y)))
        mrt (->> cover-sphere
                 (translate [(+ cover-sphere-x 4) 0 -5.5])
                 (key-place (+ 1/2 circuit-cover-offset-x) (+ 3/2 circuit-cover-offset-y)))]
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

(def teensy-width 22)
(def teensy-height 12)
(def teensy-length 30)

(def io-exp-cover (circuit-cover io-exp-width io-exp-length io-exp-height))
(def teensy-cover (circuit-cover teensy-width teensy-length teensy-height))

(def trrs-diameter 6.6)
(def trrs-radius (/ trrs-diameter 2))
(def trrs-hole-depth 10)
(def trrs-offset-z -0.1)

(def trrs-hole (->> (union (cylinder trrs-radius trrs-hole-depth)
                           (->> (cube trrs-diameter (+ trrs-radius 5) trrs-hole-depth)
                                (translate [0 (/ (+ trrs-radius 5) 2) 0])))
                    (rotate (/ π 2) [1 0 0])
                    (translate [0 (+ (/ mount-height 2) 4) (+ (- trrs-radius) trrs-offset-z)])
                    (with-fn 50)))

(def trrs-hole-just-circle
  (->> (cylinder trrs-radius trrs-hole-depth)
       (rotate (/ π 2) [1 0 0])
       (translate [0 (+ (/ mount-height 2) 4) (+ (- trrs-radius) trrs-offset-z)])
       (with-fn 50)
       (key-place 1/2 0)))

(def trrs-box-hole (->> (cube 14 14 7 )
                        (translate [0 1 -3.5])))


(def trrs-cutout
  (->> (union trrs-hole
              trrs-box-hole)
       (key-place (+ 1/2 circuit-cover-offset-x) 0)))

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
        side-cylinder (->> (cylinder side-radius teensy-length)
                           (with-fn 20)
                           (translate [(/ (- hole-width hole-height) 2) 0 0]))]
    (->> (hull side-cylinder
               (mirror [-1 0 0] side-cylinder))
         (rotate (/ π 2) [1 0 0])
         (translate [0 (- teensy-length 10) (- side-radius)])
         (translate [0 0 -2]) ;;-1
         (translate [0 0 (- teensy-offset-height)])
         (key-place (+ 1/2 circuit-cover-offset-x) (+ 3/2 circuit-cover-offset-y)))))

;;;;;;;;;;;;;;;;;;
;; Final Export ;;
;;;;;;;;;;;;;;;;;;

(def top-plate-diff [0 0 0.1])

(def dactyl-top-right
  (difference
   (union key-holes
          connectors
          thumb
          new-case
          #_caps
          #_thumbcaps)
   trrs-hole-just-circle
   screw-holes))

(def dactyl-bottom-right
  (difference
   (union
    teensy-cover
    (difference
     bottom-plate
     (hull teensy-cover)
     (->> dactyl-top-right (translate top-plate-diff))
     teensy-cover
     trrs-cutout
     (->> (cube 200 200 10) (translate [0 0 -5]))
     screw-holes)
    #_(->> dactyl-top-right (translate top-plate-diff)))
   usb-cutout))

(def dactyl-top-left
  (mirror [-1 0 0]
          dactyl-top-right))

(def dactyl-bottom-left
  (mirror [-1 0 0]
          (union
           io-exp-cover
           (difference
            bottom-plate
            (hull io-exp-cover)
            (->> dactyl-top-right (translate top-plate-diff))
            io-exp-cover
            trrs-cutout
            (->> (cube 200 200 10) (translate [0 0 -5]))
            screw-holes)
           #_(->> dactyl-top-right (translate top-plate-diff)))))

(spit "things/dactyl-teensy-cover.scad"
      (write-scad teensy-cover))

(spit "things/dactyl-top-right.scad"
      (write-scad dactyl-top-right))

(spit "things/dactyl-bottom-right.scad"
      (write-scad dactyl-bottom-right))

(spit "things/dactyl-top-left.scad"
      (write-scad dactyl-top-left))

(spit "things/dactyl-bottom-left.scad"
      (write-scad dactyl-bottom-left))

(spit "things/dactyl-top-left-with-teensy.scad"
      (write-scad (mirror [-1 0 0] dactyl-top-right)))

(spit "things/dactyl-bottom-left-with-teensy.scad"
      (write-scad (mirror [-1 0 0] dactyl-bottom-right)))
