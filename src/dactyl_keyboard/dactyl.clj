(ns dactyl-keyboard.dactyl
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
			[unicode-math.core :refer :all]))
            ;[clojure.core.matrix.operators :refer [+ - / *]]))

(def ^:const LEFT 1)
(def ^:const RIGHT 2)
(def ^:const FAST_RENDER false)
(def ^:const RESTS_SEPERATE false)
(def ^:const STANDS_SEPERATE false)

;;;;;;;;;;;;;;;;;
;; Switch Hole ;;
;;;;;;;;;;;;;;;;;

(def plate-thickness 4) ; was 4

(def keyswitch-height 14.4) ;; Was 14.1, then 14.25
(def keyswitch-width 14.4)

(def key-height 10.4) ; was 12.7, then 10.4
(def dsa-profile-key-height 7.4)
(def key-z (+ plate-thickness 3)) ; 3 is pressed, 7 is released
(def sa-profile-key-height 12.7)

(def mount-width (+ keyswitch-width 3))
(def mount-height (+ keyswitch-height 3))

(def old-single-plate
  (let [top-wall (->> (cube (+ keyswitch-width 3) 1.5 plate-thickness)
                      (translate [0
                                  (+ (/ 1.5 2) (/ keyswitch-height 2))
                                  (/ plate-thickness 2)]))
        left-wall (->> (cube 1.5 (+ keyswitch-height 3) plate-thickness)
                       (translate [(+ (/ 1.5 2) (/ keyswitch-width 2))
                                   0
                                   (/ plate-thickness 2)]))
        side-nub (->> (binding [*fn* 30] (cylinder 1 2.75)) ; spec says 5.7
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

(def single-plate
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

(def sa-length 18.5)
(def sa-double-length 37.5)
(def sa-cap {1 (let [bl2 (/ sa-length 2)
                     m (/ dsa-profile-key-height 2)
                     key-cap (hull (->> (polygon [[bl2 bl2] [bl2 (- bl2)] [(- bl2) (- bl2)] [(- bl2) bl2]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 0.05]))
                                   (->> (polygon [[m m] [m (- m)] [(- m) (- m)] [(- m) m]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 6]))
                                   (->> (polygon [[6 6] [6 -6] [-6 -6] [-6 6]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 dsa-profile-key-height])))]
                 (->> key-cap
                      (translate [0 0 key-z])
                      (color [220/255 163/255 163/255 1])))
             2 (let [bl2 (/ sa-double-length 2)
                     bw2 (/ 18.25 2)
                     key-cap (hull (->> (polygon [[bw2 bl2] [bw2 (- bl2)] [(- bw2) (- bl2)] [(- bw2) bl2]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 0.05]))
                                   (->> (polygon [[6 16] [6 -16] [-6 -16] [-6 16]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 dsa-profile-key-height])))]
                 (->> key-cap
                      (translate [0 0 key-z])
                      (color [127/255 159/255 127/255 1])))
             1.5 (let [bl2 (/ 18.25 2)
                       bw2 (/ 28 2)
                       key-cap (hull (->> (polygon [[bw2 bl2] [bw2 (- bl2)] [(- bw2) (- bl2)] [(- bw2) bl2]])
                                          (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                          (translate [0 0 0.05]))
                                     (->> (polygon [[11 6] [-11 6] [-11 -6] [11 -6]])
                                          (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                          (translate [0 0 dsa-profile-key-height])))]
                   (->> key-cap
                        (translate [0 0 key-z])
                        (color [240/255 223/255 175/255 1])))})

;;;;;;;;;;;;;;;;;;;;;;;;;
;; Placement Functions ;;
;;;;;;;;;;;;;;;;;;;;;;;;;

(def columns (range 0 6))
(def rows (range 0 5))

(def α (/ π 12))
(def β (/ π 36))
(def cap-top-height (+ plate-thickness key-height))
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
        column-row-offset (cond
                        (= column 2) [0 2.4 -4.5]
                        (= column 4) [0 -5.8 5.64]
                        (and (= column 5) (not= row 4)) [0.5 -5.8 7.01]
                        (and (= column 5) (= row 4)) [0.5 -5.8 5.7]
                        (and (= column 6) (= row 4)) [1 -5.8 6.2] ; extended connector
                        (= column 6) [1.1 -5.8 7.2] ; extended connector
                        :else [0 0 0])
        column-angle (* β (- 2 column))
        placed-shape (->> row-placed-shape
                          (translate [0 0 (- column-radius)])
                          (rotate column-angle [0 1 0])
                          (translate [0 0 column-radius])
                          (translate column-row-offset))]
    (->> placed-shape
         (rotate (/ π 12) [0 1 0])
         (translate [0 0 14.5]))))

(defn case-place [column row shape]
  (let [row-placed-shape (->> shape
                              (translate [0 0 (- row-radius)])
                              (rotate (* α (- 2 row)) [1 0 0])
                              (translate [0 0 row-radius]))
        column-offset (if (= column 6)
                        [-7.25 -5.8 2.1]
                        [0 -3.35 4.9])
        row-offset (if (= row 0)
                     [0 -2.3 0]
                     [0 0 0])
        column-row-offset (if (and (= row 0) (= column 6))
                            [0 2.25 0]
                            [0 0 0])
        column-angle (* β (- 2 column))
        placed-shape (->> row-placed-shape
                          (translate [0 0 (- column-radius)])
                          (rotate column-angle [0 1 0])
                          (translate [0 0 column-radius])
                          (translate column-row-offset)
                          (translate column-offset)
                          (translate row-offset))]
    (->> placed-shape
         (rotate (/ π 12) [0 1 0])
         (translate [0 0 14.5]))))

(defn bottom-place [column row shape]
  (let [row-placed-shape (->> shape
                              (translate [0 0 (- row-radius)])
                              (rotate (* α (- 2 row)) [1 0 0])
                              (translate [0 0 row-radius]))
        column-offset (cond (< column 1.5) [-1 0 0]
                       :else [0 0 0])
        column-row-offset (if (not= column 6) [0 -4.35 4.8]
                        (if (not= row 4) [-7.25 -5.8 2.1]
                                         [-7.89 -5.8 3.6]))
        column-angle (* β (- 2 column))
        placed-shape (->> row-placed-shape
                          (translate [0 0 (- column-radius)])
                          (rotate column-angle [0 1 0])
                          (translate [0 0 column-radius])
                          (translate column-row-offset)
                          (translate column-offset))]
    (->> placed-shape
         (rotate (/ π 12) [0 1 0])
         (translate [0 0 13]))))

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
           (->> (sa-cap (if (and (= column 5) (not= row 4)) 1 1))
                (key-place column row)))))

(defn prism [w l h taper-1 taper-2]
  (let [t1 taper-1
        t2 taper-2
        wt (- w taper-1)
        lt (- l taper-2)]
    (polyhedron [[0 0 0]
                 [t1 t1 h]
                 [wt t1 h]
                 [w 0 0]
                 [0 l 0]
                 [t1 lt h]
                 [wt lt h]
                 [w l 0]]
                [[0 1 2] [2 3 0]
                 [3 2 6] [6 7 3]
                 [7 6 5] [5 4 7]
                 [4 5 1] [1 0 4]
                 [1 5 2] [2 5 6]
                 [4 0 3] [7 4 3]])))


;;;;;;;;;;;;;;;;;;;;
;; Web Connectors ;;
;;;;;;;;;;;;;;;;;;;;

(def web-thickness plate-thickness) ; was 3.5
(def post-size 0.1)
(def web-post (->> (cube post-size post-size web-thickness)
                   (translate [0 0 (+ (/ web-thickness -2)
                                      plate-thickness)])))

(def post-adj (/ post-size 2))
(def web-post-tr (translate [(- (/ mount-width 2) post-adj) (- (/ mount-height 2) post-adj) 0] web-post))
(def web-post-tl (translate [(+ (/ mount-width -2) post-adj) (- (/ mount-height 2) post-adj) 0] web-post))
(def web-post-bl (translate [(+ (/ mount-width -2) post-adj) (+ (/ mount-height -2) post-adj) 0] web-post))
(def web-post-br (translate [(- (/ mount-width 2) post-adj) (+ (/ mount-height -2) post-adj) 0] web-post))

(defn triangle-hulls [& shapes]
  (apply union
         (map (partial apply hull)
              (partition 3 1 shapes))))

(def connectors
  (apply union
         (concat
          ;; Row connections
          (for [column columns ;(drop-last columns)
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
          (for [column columns;(drop-last columns)
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

(defn thumb-place-old [column row shape]
  (let [cap-top-height (+ plate-thickness key-height)
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

(def thumb-stagger-up 6)
(def thumb-stagger-mid  2)
(def thumb-stagger-dn -2)
(def thumb-stagger-zero 0)
		 
(def thumb-cluster-spacing 2) ; key spacing for flat thumb cluster

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
                         cap-top-height)
		heightfudge (cond (= row -1) thumb-stagger-dn
					  (= row -1/2) thumb-stagger-zero
					  (= row 0) thumb-stagger-mid
					  (= row 1) thumb-stagger-up
                      :else 0)	
		 zoffset0 (+ 40 heightfudge)]
    (->> shape
         (translate [mount-width 0 0])
		 (translate [  (* (- (+ mount-width thumb-cluster-spacing)) column) (* (+ mount-width thumb-cluster-spacing) row) 0])
         (rotate (* π (- 1/4 3/16)) [0 0 1])
         (rotate (/ π 12) [1 1 0])
         (translate [-52 -45 zoffset0]))))		 
		 
(defn thumb-2x-column [shape]
  (thumb-place-new 0 -1/2 shape))

(defn thumb-2x+1-column [shape]
  (union (thumb-place-new 1 -1/2  shape)
         (thumb-place-new 1 1   shape)))

(defn thumb-1x-column [shape]
  (union (thumb-place-new 2 -1   shape)
         (thumb-place-new 2 0    shape)
         (thumb-place-new 2 1   shape)))

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
   (thumb-place-new 1 -1/2  (sa-cap 2))
   (thumb-place-new 1 1   (sa-cap 1))
   (thumb-1x-column (sa-cap 1))))

(def thumb-connectors
  (union
   (apply union
          (concat
           (for [column [2] row [1]]
             (triangle-hulls (thumb-place-new column row   web-post-br)
                             (thumb-place-new column row    web-post-tr)
                             (thumb-place-new (dec column) row   web-post-bl)
                             (thumb-place-new (dec column) row    web-post-tl)))
           (for [column [2] row [1]]
             (triangle-hulls
              (thumb-place-new column row   web-post-bl)
              (thumb-place-new column row   web-post-br)
              (thumb-place-new column (dec row)   web-post-tl)
              (thumb-place-new column (dec row)   web-post-tr)))
			(for [column [2] row [0]]
             (triangle-hulls
              (thumb-place-new column row   web-post-bl)
              (thumb-place-new column row   web-post-br)
              (thumb-place-new column (dec row)   web-post-tl)
              (thumb-place-new column (dec row)   web-post-tr)))))
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
      (triangle-hulls (thumb-place-new 0 -1/2  thumb-tl)
                      (thumb-place-new 0 -1/2  thumb-bl)
                      (thumb-place-new 1 -1/2  thumb-tr)
                      (thumb-place-new 1 -1/2  thumb-br))

      ;;Connecting the double to the one above it
      (triangle-hulls (thumb-place-new 1 -1/2  thumb-tr)
                      (thumb-place-new 1 -1/2  thumb-tl)
                      (thumb-place-new 1 1   web-post-br)
                      (thumb-place-new 1 1   web-post-bl))

      ;;Connecting the 4 with the double in the bottom left
      (triangle-hulls (thumb-place-new 1 1   web-post-bl)
                      (thumb-place-new 1 -1/2  thumb-tl)
                      (thumb-place-new 2 1   web-post-br)
                      (thumb-place-new 2 0   web-post-tr))

      ;;Connecting the two singles with the middle double
      (hull (thumb-place-new 1 -1/2  thumb-tl)
            (thumb-place-new 2 0   web-post-tr)
			(thumb-place-new 2 0   web-post-br))
	  (hull (thumb-place-new 2 0   web-post-br)
			(thumb-place-new 2 -1   web-post-tr)
			(thumb-place-new 1 -1/2  thumb-tl))
	  (hull (thumb-place-new 1 -1/2  thumb-tl)
	         (thumb-place-new 1 -1/2  thumb-bl)
			 (thumb-place-new 2 -1   web-post-tr))
      (hull (thumb-place-new 1 -1/2  thumb-tl)
            (thumb-place-new 2 0   web-post-tr)
            (thumb-place-new 2 0   web-post-br))
      (hull (thumb-place-new 1 -1/2  thumb-bl)
            (thumb-place-new 2 -1   web-post-tr)
            (thumb-place-new 2 -1   web-post-br))

      ;;Connecting the thumb to everything
      (triangle-hulls (thumb-place-new 0 -1/2  thumb-br)
                      (key-place 1 4 web-post-bl)
                      (thumb-place-new 0 -1/2  thumb-tr)
                      (key-place 1 4 web-post-tl)
                      (key-place 1 3 web-post-bl)
                      (thumb-place-new 0 -1/2  thumb-tr)
                      (key-place 0 3 web-post-br)
                      (key-place 0 3 web-post-bl)
                      (thumb-place-new 0 -1/2  thumb-tr)
                      (thumb-place-new 0 -1/2  thumb-tl)
                      (key-place 0 3 web-post-bl)
                      (thumb-place-new 1 -1/2  thumb-tr)
                      (thumb-place-new 1 1   web-post-br)
                      (key-place 0 3 web-post-bl)
                      (key-place 0 3 web-post-tl)
                      (thumb-place-new 1 1   web-post-br)
                      (thumb-place-new 1 1   web-post-tr))
      (hull (thumb-place-new 0 -1/2  web-post-tr)
            (thumb-place-new 0 -1/2  thumb-tr)
            (key-place 1 4 web-post-bl)
            (key-place 1 4 web-post-tl))))))

(def thumb
  (union
   thumb-connectors
   (thumb-layout (rotate (/ π 2) [0 0 1] single-plate))
   (thumb-place-new 0 -1/2 double-plates)
   (thumb-place-new 1 -1/2 double-plates)))
   
   

;;;;;;;;;;
;; Case ;;
;;;;;;;;;;

;; In column units
(def right-wall-column (+ (last columns) 1.1))
(def left-wall-column (- (first columns) 1/2))
(def thumb-back-y 0.93)
(def thumb-case-z 3)
(def thumb-right-wall (- -1/2 0.05))
(def thumb-front-row (+ -1 0.07))
(def thumb-left-wall-column (+ 5/2 0.05))
(def back-y 0.02)

(defn range-inclusive [start end step]
  (concat (range start end step) [end]))

(def wall-step 0.2)
(def wall-sphere-n (if FAST_RENDER 10 20))

(defn wall-cube-at [coords]
  (->> (cube 3 3 3)
       (translate coords)))

(defn scale-to-range [start end x]
  (+ start (* (- end start) x)))

(defn wall-cube-bottom [front-to-back-scale]
  (wall-cube-at [0
                   (scale-to-range
                    (+ (/ mount-height -2) -3.5)
                    (+ (/ mount-height 2) 5.0)
                    front-to-back-scale)
                   -6])) ; was -6, then 2

(defn wall-cube-top [front-to-back-scale]
  (wall-cube-at [0
                   (scale-to-range
                    (+ (/ mount-height -2) -3.5)
                    (+ (/ mount-height 2) 3.5)
                    front-to-back-scale)
                   4])) ; case height

(def wall-cube-top-back (wall-cube-top 1))
(def wall-cube-bottom-back (wall-cube-bottom 1))
(def wall-cube-bottom-front (wall-cube-bottom 0))
(def wall-cube-top-front (wall-cube-top 0))

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

(def case-back-cutout
  (let [a (+ mount-width 8)]
      (->> (prism a a a 0 6)
           (rotate π [0 0 1])
           (rotate (/ π 15) [1 0 0])
           (translate [(/ a 2) (+ a 9.3) -15.25])
           (key-place 2 0))))

(def case-inside-cutout
  (let [a (+ mount-width 8)
        b (+ mount-width 3.8)
        c 6]
    (union
      (->> (prism b b c 2 2)
           (rotate π [1 0 0])
           (translate [(- (/ b 2)) 10.5 (+ c 4)])
           (key-place 2 0)))))

(def front-wall
  (let [step wall-step ;;0.1
        wall-step 0.05 ;;0.05
        place case-place
        top-cover (fn [x-start x-end y-start y-end]
                    (top-case-cover place wall-cube-top-front
                                    x-start x-end y-start y-end
                                    wall-step))]
    (union
     (apply union
            (for [x (range 2 5)]
              (union
               (hull (place (- x 1/2) 4 (translate [0 1 1] wall-cube-bottom-front))
                     (place (+ x 1/2) 4 (translate [0 1 1] wall-cube-bottom-front))
                     (key-place x 4 web-post-bl)
                     (key-place x 4 web-post-br))
               (hull (place (- x 1/2) 4 (translate [0 1 1] wall-cube-bottom-front))
                     (key-place x 4 web-post-bl)
                     (key-place (- x 1) 4 web-post-br)))))
     (hull (place right-wall-column 4 (translate [-1 1 1] wall-cube-bottom-front))
           (place (- right-wall-column 1) 4 (translate [0 1 1] wall-cube-bottom-front))
           (key-place 5 4 web-post-bl)
           (key-place 5 4 web-post-br))
     (hull (place right-wall-column 4 (translate [-1 1 1] wall-cube-bottom-front))
           (place (- right-wall-column 1) 4 (translate [0 1 1] wall-cube-bottom-front))
           (key-place 6 4 web-post-bl)
           (key-place 5 4 web-post-br))
     (hull (place (+ 4 1/2) 4 (translate [0 1 1] wall-cube-bottom-front))
           (place (- right-wall-column 1) 4 (translate [0 1 1] wall-cube-bottom-front))
           (key-place 4 4 web-post-br)
           (key-place 5 4 web-post-bl))
     (hull (place 0.75 4 (translate [0 1.73 -0.805] wall-cube-bottom-front))
           (place 1.5 4 (translate [0 1 1] wall-cube-bottom-front))
           (key-place 1 4 (translate [0.001 0 0] web-post-bl))
           (key-place 1 4 (translate [0.001 0 0] web-post-br))))))

          ; It's not clear why the above translateions of 0.001 units are needed
          ; but they resolve an issue where the normals were invered.

(def back-wall
  (let [step wall-step
        wall-cube-top-backtep 0.05
        place case-place]
    (difference
      (union
        (union
           (hull (place left-wall-column 0 (translate [1 -1 1] wall-cube-bottom-back))
                 (place (+ left-wall-column 1) 0  (translate [0 -1 1] wall-cube-bottom-back))
                 (key-place 0 0 web-post-tl)
                 (key-place 0 0 web-post-tr))

           (hull (place 5 0 (translate [0 -0.91 1.32] wall-cube-bottom-back))
                 (place right-wall-column 0 (translate [-1.2 -1.52 0.91] wall-cube-bottom-back))
                 (key-place 5 0 web-post-tl)
                 (key-place 5 0 (translate [5.6 0 0.05] web-post-tr)))

           (apply union
                  (for [x (range 1 5)]
                    (union
                     (hull (place (- x 1/2) 0 (translate [0 -1 1] wall-cube-bottom-back))
                           (place (+ x 1/2) 0 (translate [0 -1 1] wall-cube-bottom-back))
                           (key-place x 0 web-post-tl)
                           (key-place x 0 web-post-tr))
                     (hull (place (- x 1/2) 0 (translate [0 -1 1] wall-cube-bottom-back))
                           (key-place x 0 web-post-tl)
                           (key-place (- x 1) 0 web-post-tr))))))

         (hull (place (- 5 1/2) 0 (translate [0 -1 1] wall-cube-bottom-back))
               (place 5 0 (translate [0 -0.91 1.32] wall-cube-bottom-back))
               (key-place 4 0 web-post-tr)
               (key-place 5 0 web-post-tl)))
      (union case-back-cutout
             case-inside-cutout))))

(def right-wall
  (let [place case-place]
    (union
          (apply union
            (concat
             (for [x (range 0 5)]
               (union
                (hull (place right-wall-column x (translate [-1 -1 1] (wall-cube-bottom 1/2)))
                      (place 6 x web-post-br)
                      (place 6 x web-post-tr))))
             (for [x (range 0 4)]
               (union
                (hull (place right-wall-column x (translate [-1 -1 1] (wall-cube-bottom 1/2)))
                      (place right-wall-column (inc x) (translate [-1 -1 1] (wall-cube-bottom 1/2)))
                      (place 6 x web-post-br)
                      (place 6 (inc x) web-post-tr))))
             [(union
               (hull (place right-wall-column 0 (translate [-1 -1 1] (wall-cube-bottom 1/2)))
                     (place right-wall-column 0 (translate [-1.2 -1.5 0.9] (wall-cube-bottom 1)))
                     (place 6 0 web-post-tr))
               (hull (place right-wall-column 4 (translate [-1 -1 1] (wall-cube-bottom 1/2)))
                     (place right-wall-column 4 (translate [-1 1 1] (wall-cube-bottom 0)))
                     (place 6 4 web-post-br)))])))))

(def left-wall
  (let [place case-place]
    (union
     (hull (place left-wall-column 0 (translate [1 -1.5 1] wall-cube-bottom-back))
           (place left-wall-column 1 (translate [1 0 1] wall-cube-bottom-back))
           (key-place 0 0 web-post-tl)
           (key-place 0 0 web-post-bl))
     (hull (place left-wall-column 1 (translate [1 0 1] wall-cube-bottom-back))
           (place left-wall-column 2 (translate [1 0 1] wall-cube-bottom-back))
           (key-place 0 0 web-post-bl)
           (key-place 0 1 web-post-bl))
     (hull (place left-wall-column 2 (translate [1 0 1] wall-cube-bottom-back))
           (place left-wall-column 1.71 (translate [1 0 3.5] wall-cube-bottom-front))
           (key-place 0 1 web-post-bl)
           (key-place 0 2 web-post-bl)))))

(def left-inside-wall
  (let [place case-place]
    (union
     (hull (place left-wall-column 1.71 (translate [1 0 3] wall-cube-bottom-front))
           (key-place 0 2 web-post-bl)
           (key-place 0 3 web-post-tl))
     (hull (place left-wall-column 1.71 (translate [2 0 3] wall-cube-bottom-front))
           (thumb-place 1 1 web-post-tr)
           (key-place 0 3 web-post-tl)
           (place left-wall-column 1.71 (translate [1 0 3] wall-cube-bottom-front))
           (thumb-place 1 1 web-post-tr)
           (thumb-place 1/2 thumb-back-y (translate [0 -1.7 thumb-case-z] wall-cube-bottom-back))
           (thumb-place 1/2 thumb-back-y (translate [1 -1.7 (- thumb-case-z 2.9)] wall-cube-bottom-back))))))

(def thumb-back-wall
  (let [step wall-step
        top-step 0.05
        back-y thumb-back-y]
    (union
     (hull
      (thumb-place 1/2 thumb-back-y (translate [0 -1.7 thumb-case-z] wall-cube-bottom-back))
      (thumb-place 1 1 web-post-tr)
      (thumb-place 3/2 thumb-back-y (translate [0 -1.7 thumb-case-z] wall-cube-bottom-back))
      (thumb-place 1 1 web-post-tl))
     (hull
      (thumb-place (+ 5/2 0.05) thumb-back-y (translate [1.5 -1.7 thumb-case-z] wall-cube-bottom-back))
      (thumb-place 3/2 thumb-back-y (translate [0 -1.7 thumb-case-z] wall-cube-bottom-back))
      (thumb-place 1 1 web-post-tl)
      (thumb-place 2 1 web-post-tl)))))

(def thumb-left-wall
  (let [step wall-step
        place thumb-place
        wall (+ thumb-left-wall-column 0.001)]
    (union
     (hull
      (thumb-place wall thumb-back-y (translate [1.5 -1.7 thumb-case-z] wall-cube-bottom-back))
      (thumb-place wall 0 (translate [1.5 0 thumb-case-z] wall-cube-bottom-back))
      (thumb-place 2 1 web-post-tl)
      (thumb-place 2 1 web-post-bl))
     (hull
      (thumb-place wall 0 (translate [1.5 0 thumb-case-z] wall-cube-bottom-back))
      (thumb-place 2 0 web-post-tl)
      (thumb-place 2 1 web-post-bl))
     (hull
      (thumb-place wall 0 (translate [1.5 0 thumb-case-z] wall-cube-bottom-back))
      (thumb-place wall -1 (translate [1.5 0 thumb-case-z] wall-cube-bottom-back))
      (thumb-place 2 0 web-post-tl)
      (thumb-place 2 0 web-post-bl))
     (hull
      (thumb-place wall -1 (translate [1.5 0 thumb-case-z] wall-cube-bottom-back))
      (thumb-place 2 -1 web-post-tl)
      (thumb-place 2 0 web-post-bl))
     (hull
      (thumb-place wall -1 (translate [1.5 0 thumb-case-z] wall-cube-bottom-back))
      (thumb-place wall (+ -1 0.07) (translate [1.5 0.6 (+ thumb-case-z 0.2)] wall-cube-bottom-front))
      (thumb-place 2 -1 web-post-tl)
      (thumb-place 2 -1 web-post-bl)))))

(def thumb-front-wall
  (let [step wall-step
        wall-cube-top-front 0.05
        place thumb-place
        wall (- thumb-front-row 0.04)
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

     (hull (place (+ 5/2 0.05) wall (translate [1.5 1.5 thumb-case-z] wall-cube-bottom-front))
           (place (+ 3/2 0.05) wall (translate [0 1.5 thumb-case-z] wall-cube-bottom-front))
           (place 2 -1 web-post-bl)
           (place 2 -1 web-post-br))

     (hull (place thumb-right-wall wall (translate [-1 1.5 thumb-case-z] wall-cube-bottom-front))
           (place (+ 1/2 0.05) wall (translate [0 1.5 thumb-case-z] wall-cube-bottom-front))
           (place 0 -1/2 thumb-bl)
           (place 0 -1/2 thumb-br))
     (hull (place (+ 1/2 0.05) wall (translate [0 1.5 thumb-case-z] wall-cube-bottom-front))
           (place (+ 3/2 0.05) wall (translate [0 1.5 thumb-case-z] wall-cube-bottom-front))
           (place 0 -1/2 thumb-bl)
           (place 1 -1/2 thumb-bl)
           (place 1 -1/2 thumb-br)
           (place 2 -1 web-post-br)))))

(def thumb-inside-wall
  (let [place thumb-place
        wall (- thumb-front-row 0.04)
        plate-height (/ (- sa-double-length mount-height) 2)
        thumb-bl (->> web-post-bl
                      (translate [0 (- plate-height) 0]))
        thumb-br (->> web-post-br
                      (translate [-0 (- plate-height) 0]))
        thumb-bottom (->> (cube 3 3 0.001)
                       (translate [13.6 -15 -8]))
        thumb-top (->> (cube 1 1 1)
                       (translate [13 -11.7 -5.4]))]
     (hull (place thumb-right-wall wall (translate [-1 1.5 thumb-case-z] wall-cube-bottom-front))
           (key-place 1 4 web-post-bl)
           (place 0 -1/2 thumb-br)
           (case-place 0 4 thumb-top)
           (case-place 0 4 (translate [-1 10 0] thumb-bottom)))))

(def new-case
    (union front-wall
           right-wall
           back-wall
           left-wall
           left-inside-wall
           thumb-back-wall
           thumb-left-wall
           thumb-inside-wall
           thumb-front-wall))

		   
		   
 (spit "things/debug.scad"
      (write-scad (union key-holes thumb connectors new-case)))
;;;;;;;;;;;;
;; Bottom ;;
;;;;;;;;;;;;


(defn bottom [height p]
  (->> (project p)
       (extrude-linear {:height height :twist 0 :convexity 0})
       (translate [0 0 (/ height 2)])))

(defn bottom-hull [p]
  (hull p (bottom 1 p)))


(def bottom-key-guard (->> (cube mount-width mount-height web-thickness)
                           (translate [0 0 (+ (- (/ web-thickness 2)) -5)])))
(def bottom-front-key-guard (->> (cube mount-width (/ mount-height 2) web-thickness)
                                 (translate [0 (/ mount-height 4) (+ (- (/ web-thickness 2)) -5)])))

(defn stand-at [diameter placement]
  (let [bumper-radius (/ diameter 2)
       stand-diameter (+ diameter 2)
       stand-radius (/ stand-diameter 2)]
    (difference (->> (sphere stand-radius)
                     (translate [0 0 (+ (/ stand-radius -2) -4.5)])
                      placement
                      bottom-hull)
                (->> (cube stand-diameter stand-diameter stand-radius)
                     (translate [0 0 (/ stand-radius -2)])
                      placement)
                (->> (sphere bumper-radius)
                     (translate [0 0 (+ (/ stand-radius -2) -4.5)])
                      placement
                     (bottom 1.5)))))

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
         thumb-ridge-height 1
         thumb-back-offset -1.28
         thumb-left-offset 1.13
         thumb-front-offset 0.56
         front-offset 0.63
         left-offset 0.9
         right-offset -0.73
         back-offset -0.85
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
                        (hull (bottom-place (- x 1/2) 4 (translate [0 front-offset 1] wall-cube-bottom-front))
                              (bottom-place (+ x 1/2) 4 (translate [0 front-offset 1] wall-cube-bottom-front))
                              (key-place x 4 half-post-bl)
                              (key-place x 4 half-post-br))
                        (hull (bottom-place (- x 1/2) 4 (translate [0 front-offset 1] wall-cube-bottom-front))
                              (key-place x 4 half-post-bl)
                              (key-place (- x 1) 4 half-post-br))))
                     [(hull (bottom-place right-wall-column 4 (translate [right-offset front-offset 1] wall-cube-bottom-front))
                            (bottom-place (- right-wall-column 1) 4 (translate [0 front-offset 1] wall-cube-bottom-front))
                            (key-place 5 4 half-post-bl)
                            (key-place 5 4 half-post-br))
                      (hull (bottom-place (+ 4 1/2) 4 (translate [0 front-offset 1] wall-cube-bottom-front))
                            (bottom-place (- right-wall-column 1) 4 (translate [0 front-offset 1] wall-cube-bottom-front))
                            (key-place 4 4 half-post-br)
                            (key-place 5 4 half-post-bl))])
         right-wall (concat
                     (for [x (range 0 4)]
                       (hull (bottom-place right-wall-column x (translate [right-offset 0 1] (wall-cube-bottom 1/2)))
                             (key-place 5 x web-post-br)
                             (key-place 5 x web-post-tr)))
                     (for [x (range 0 4)]
                       (hull (bottom-place right-wall-column x (translate [right-offset 0 1] (wall-cube-bottom 1/2)))
                             (bottom-place right-wall-column (inc x) (translate [right-offset 0 1] (wall-cube-bottom 1/2)))
                             (key-place 5 x web-post-br)
                             (key-place 5 (inc x) web-post-tr)))
                     [(union
                       (hull (bottom-place right-wall-column 0 (translate [right-offset 0 1] (wall-cube-bottom 1/2)))
                             (bottom-place right-wall-column 0.017 (translate [(- right-offset 0.29) -1 1.15] (wall-cube-bottom 1)))
                             (key-place 5 0 web-post-tr))
                       (hull (bottom-place right-wall-column 4 (translate [right-offset 0 1] (wall-cube-bottom 1/2)))
                             (bottom-place right-wall-column 4 (translate [right-offset front-offset 1] (wall-cube-bottom 0)))
                             (key-place 5 4 half-post-br))
                       (hull (bottom-place right-wall-column 4 (translate [right-offset 0 1] (wall-cube-bottom 1/2)))
                             (key-place 5 4 half-post-br)
                             (key-place 5 4 web-post-tr)))])
         back-wall (concat
                    (for [x (range 1 6)]
                      (union
                       (hull
                             (do (bottom-place (- x 1/2) 0 (translate [0 back-offset 1] wall-cube-bottom-back)))
                              (if (= x 5)
                               (do (bottom-place (+ x 1/2) 0 (translate [10.6 (- back-offset 0.35) 1.30] wall-cube-bottom-back)))
                               (do (bottom-place (+ x 1/2) 0 (translate [0 back-offset 1] wall-cube-bottom-back))))
                             (key-place x 0 web-post-tl)
                             (key-place x 0 web-post-tr))
                       (hull (bottom-place (- x 1/2) 0 (translate [0 back-offset 1] wall-cube-bottom-back))
                             (key-place x 0 web-post-tl)
                             (key-place (- x 1) 0 web-post-tr))))
                    [(hull (bottom-place left-wall-column 0 (translate [left-offset back-offset 1.2] wall-cube-bottom-back))
                           (bottom-place (+ left-wall-column 1) 0  (translate [0 back-offset 1.2] wall-cube-bottom-back))
                           (key-place 0 0 web-post-tl)
                           (key-place 0 0 web-post-tr))]

                    )
         left-wall (let [place bottom-place]
               [
                (hull (place left-wall-column 0 (translate [left-offset back-offset 1.2] wall-cube-bottom-back))
                      (place left-wall-column 1 (translate [left-offset 1.5 1/2] wall-cube-bottom-back))
                      (key-place 0 0 web-post-tl)
                      (key-place 0 0 web-post-bl))
                (hull (place left-wall-column 1 (translate [left-offset 0 1/2] wall-cube-bottom-back))
                      (place left-wall-column 2 (translate [left-offset 1.5 0.7] wall-cube-bottom-back))
                      (key-place 0 0 web-post-bl)
                      (key-place 0 1 web-post-bl))
                (hull (place left-wall-column 2 (translate [left-offset 0 0.7] wall-cube-bottom-back))
                      (place left-wall-column 1.6666  (translate [left-offset 0 3] wall-cube-bottom-front))
                      (place left-wall-column 1.6666  (translate [left-offset 0 1] wall-cube-bottom-front))
                      (key-place 0 1 web-post-bl)
                      (key-place 0 2 web-post-bl))
                (hull (place left-wall-column 1.6666  (translate [left-offset 0 1] wall-cube-bottom-front))
                      (key-place 0 2 web-post-bl)
                      (key-place 0 3 web-post-tl))

                ])
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
         thumb-back-wall [
                          (hull
                           (thumb-place 1/2 thumb-back-y (translate [-0.2 thumb-back-offset thumb-ridge-height] wall-cube-bottom-back))
                           (thumb-place 1 1 web-post-tr)
                           (thumb-place 3/2 thumb-back-y (translate [-0.2 thumb-back-offset thumb-ridge-height] wall-cube-bottom-back))
                           (thumb-place 1 1 web-post-tl))
                          (hull
                           (thumb-place (+ 5/2 0.05) thumb-back-y (translate [thumb-left-offset thumb-back-offset thumb-ridge-height] wall-cube-bottom-back))
                           (thumb-place 3/2 thumb-back-y (translate [-0.2 thumb-back-offset thumb-ridge-height] wall-cube-bottom-back))
                           (thumb-place 1 1 web-post-tl)
                           (thumb-place 2 1 web-post-tl))
                          (hull
                           (thumb-place 1 1 web-post-tr)
                           (key-place 0 3 web-post-tl)
                           (thumb-place 1 1 web-post-br)
                           (key-place 0 3 web-post-bl)
                           (thumb-place 1/2 thumb-back-y (translate [-0.2 thumb-back-offset thumb-ridge-height] wall-cube-bottom-back))
                           (thumb-place 1/2 thumb-back-y (translate [-3.2 (+ thumb-back-offset 0.07) thumb-ridge-height] wall-cube-bottom-back))
                           (bottom-place left-wall-column 1.6666 (translate [left-offset 0 (- thumb-ridge-height 0.2)] wall-cube-bottom-front))
                           (key-place 0 3 web-post-tl)
                           (thumb-place 1 1 web-post-tr)

                           )
                          ]
         thumb-left-wall [(hull
                           (thumb-place thumb-left-wall-column thumb-back-y (translate [thumb-left-offset thumb-back-offset thumb-ridge-height] wall-cube-bottom-back))
                           (thumb-place thumb-left-wall-column 0 (translate [thumb-left-offset 0 thumb-ridge-height] wall-cube-bottom-back))
                           (thumb-place 2 1 web-post-tl)
                           (thumb-place 2 1 web-post-bl))
                          (hull
                           (thumb-place thumb-left-wall-column 0 (translate [thumb-left-offset 0 thumb-ridge-height] wall-cube-bottom-back))
                           (thumb-place 2 0 web-post-tl)
                           (thumb-place 2 1 web-post-bl))
                          (hull
                           (thumb-place thumb-left-wall-column 0 (translate [thumb-left-offset 0 thumb-ridge-height] wall-cube-bottom-back))
                           (thumb-place thumb-left-wall-column -1 (translate [thumb-left-offset 0 thumb-ridge-height] wall-cube-bottom-back))
                           (thumb-place 2 0 web-post-tl)
                           (thumb-place 2 0 web-post-bl))
                          (hull
                           (thumb-place thumb-left-wall-column -1 (translate [thumb-left-offset 0 thumb-ridge-height] wall-cube-bottom-back))
                           (thumb-place 2 -1 web-post-tl)
                           (thumb-place 2 0 web-post-bl))
                          (hull
                           (thumb-place thumb-left-wall-column -1 (translate [thumb-left-offset 0 thumb-ridge-height] wall-cube-bottom-back))
                           (thumb-place thumb-left-wall-column (+ -1 0.07) (translate [thumb-left-offset 1 thumb-ridge-height] wall-cube-bottom-front))
                           (thumb-place 2 -1 web-post-tl)
                           (thumb-place 2 -1 web-post-bl))]
         thumb-front-wall [(hull (thumb-place (+ 5/2 0.05) thumb-front-row (translate [thumb-left-offset thumb-front-offset thumb-ridge-height] wall-cube-bottom-front))
                                 (thumb-place (+ 3/2 0.05) thumb-front-row (translate [0 thumb-front-offset thumb-ridge-height] wall-cube-bottom-front))
                                 (thumb-place 2 -1 web-post-bl)
                                 (thumb-place 2 -1 web-post-br))
                           (hull (thumb-place (+ 1/2 0.05) thumb-front-row (translate [0 thumb-front-offset thumb-ridge-height] wall-cube-bottom-front))
                                 (thumb-place (+ 3/2 0.05) thumb-front-row (translate [0 thumb-front-offset thumb-ridge-height] wall-cube-bottom-front))
                                 (thumb-place 0 -1 web-post-bl)
                                 (thumb-place 1 -1 web-post-bl)
                                 (thumb-place 1 -1 web-post-br)
                                 (thumb-place 2 -1 web-post-br))
                           (hull (thumb-place thumb-right-wall thumb-front-row (translate [-1.12 thumb-front-offset thumb-ridge-height] wall-cube-bottom-front))
                                 (thumb-place (+ 1/2 0.05) thumb-front-row (translate [0 thumb-front-offset thumb-ridge-height] wall-cube-bottom-front))
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
                        (bottom-place (- 2 1/2) 4 (translate [0 front-offset 1] wall-cube-bottom-front))
                        (bottom-place 0.75 4 (translate [0 (+ front-offset 0.65) 0] wall-cube-bottom-front)))

                       (hull
                        (thumb-place 0 -1 web-post-br)
                        (thumb-place 0 -1/2 web-post-br)
                        (thumb-place thumb-right-wall thumb-front-row (translate [-1.12 thumb-front-offset thumb-ridge-height] wall-cube-bottom-front))
                        (key-place 1 4 (translate [2.5 -2 -0.8] web-post-bl))
                        (key-place 1 4 (translate [1.23 -4 0] web-post-bl))
                        (key-place 1 4 half-post-bl))]]

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
             thumb-inside)))))

(defn stands-at [diameter]
  (union
    [(stand-at diameter #(key-place 0.06 1 %))
     (stand-at diameter #(thumb-place 1 -1/2 %))
     (stand-at diameter #(key-place 5 0 %))
     (stand-at diameter #(key-place 5 3 %))]))

(defn stands-alignment [side]
  (let
    [hole (->> (cylinder 2 15)
               (translate [0 0 -8])
               (with-fn wall-sphere-n))]
    (union [(if (= side RIGHT)
              (translate [0 0 -7] (key-place 0.06 1 hole))
              (key-place 0.06 1 hole))
           (thumb-place 1 -1/2 hole)
           (key-place 5 0 hole)
           (key-place 5 3 hole)])))

(defn stands-diff [shape]
  (let [-tolerance (if STANDS_SEPERATE (- 0.2) 0)
       diff (union
              bottom-plate
              (hull shape))]
   (union (translate [0 -tolerance 0] diff)
          (translate [-tolerance 0 0] diff))))

(def stands (stands-at 9.8)) ; 3/8 = 9.6 1/2 = 12.7

(def screw-hole (->> (cylinder 1.5 60)
                     (translate [0 0 3/2])
                     (with-fn wall-sphere-n)))

(def screw-holes
  (union
   (key-place (+ 4.7) 1/2 screw-hole)
   (key-place (+ 4.7) (+ 3 1/2) screw-hole)
   (thumb-place 2 -1/2 screw-hole)))

(defn circuit-cover [width length height]
  (let [cover-sphere-radius 2
        cover-sphere (->> (sphere cover-sphere-radius)
                          (with-fn 2))
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

(def trrs-hole (->> (union (cylinder trrs-radius trrs-hole-depth)
                           (->> (cube trrs-diameter (+ trrs-radius 5) trrs-hole-depth)
                                (translate [0 (/ (+ trrs-radius 5) 2) 0])))
                    (rotate (/ π 2) [1 0 0])
                    (translate [0 (+ (/ mount-height 2) 4) (- trrs-radius)])
                    (with-fn 50)))

(def trrs-hole-just-circle
  (->> (cylinder trrs-radius trrs-hole-depth)
       (rotate (/ π 2) [1 0 0])
       (translate [0 (+ (/ mount-height 2) 4) (- trrs-radius)])
       (with-fn 50)
       (key-place 1/2 0)))

(def trrs-box-hole (->> (cube 14 14 7 )
                        (translate [0 1 -2])))


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
    (->> (cube 5 5 9)
         (translate [0 0 -2])
         (key-place 1/2 3/2)
         (color [0 1 0]))
    (hull (->> (cube 5 6 9)
               (translate [0 0 -2])
               (key-place 1/2 2)
               (color [0 0 1]))
          (->> (cube 5 5 (+ teensy-pcb-thickness 5))
               (translate [0 (/ 30.5 -2) (+ (- teensy-offset-height)
                                            #_(/ (+ teensy-pcb-thickness 5) -2)
                                            )])
               (key-place 1/2 3/2)
               (color [0 0 1]))

          ))
   teensy-pcb
   (->> (cube 18 31 (+ teensy-pcb-thickness 1))
        (translate [0 1.5 (+ (/ teensy-pcb-thickness -2) (- teensy-offset-height) -1.5)])
        (key-place 1/2 3/2)
        (color [1 0 0]))
   ))

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
         (translate [0 (/ teensy-length 2) (- side-radius)])
         (translate [0 0 (- 1)])
         (translate [0 0 (- teensy-offset-height)])
         (key-place 1/2 3/2))))


;;;;;;;;;;;;;;;;
;; Tolerances ;;
;;;;;;;;;;;;;;;;

(def tolerance 0.2)

(defn offset-case-place [offset block]
  (->> block
       (translate [0 0 1])
       (translate offset)))

(def case-tolerance
  (let [place offset-case-place
        t tolerance
        -t (- tolerance)
        th (/ t 2)
        -th (/ -t 2)
        -tq (/ -t 4)
        t2 (* t 2)
        -t2 (* -t 2)]
    (union
      (place [0 0 0] case-inside-cutout)
      (place [0 0 0] new-case)
      (if-not FAST_RENDER
        (union
          (place [0 0 -t] new-case)
          (place [t2 -t -tq] front-wall)
          (place [t t -th] front-wall)
          (place [0 -t -th] front-wall)
          (place [-t 0 -t] right-wall)
          (place [t 0 -t] right-wall)
          (place [0 th -th] back-wall)
          (place [0 -th -th] back-wall)
          (place [-45 50 54.3] (rotate (/ π 5.5) [1 0 0] (cube 10 10 10)))
          (place [80 49 29.83] (rotate (/ π 5.5) [1 0 0] (cube 10 10 10)))
          (place [-t -t -t] left-wall)
          (place [t 0 -t2] left-wall)
          (place [-t 0 0] left-inside-wall)
          (place [-t 0 -t] left-inside-wall)
          (place [-t -t -t] left-inside-wall)
          (place [0 -t -t2] thumb-back-wall)
          (place [-t t -th] thumb-back-wall)
          (place [0 t -t] thumb-back-wall)
          (place [t 0 (* -t 1.5)] thumb-left-wall)
          (place [-t 0 -t] thumb-left-wall)
          (place [t t -t] thumb-inside-wall)
          (place [-t 0 -t] thumb-inside-wall)
          (place [-t t -t] thumb-front-wall)
          (place [t 0 -t] thumb-front-wall)
          (place [0 (* 2 -t) -t] thumb-front-wall))))))



;;;;;;;;;;;;;;;;;;
;; Final Export ;;
;;;;;;;;;;;;;;;;;;

(def floor
  (->> (cube 1000 1000 10)
       (translate [0 0 -5])))

(def dactyl-stands-left
  (mirror [-1 0 0]
    (difference stands
                (if STANDS_SEPERATE (stands-alignment LEFT)
                   (stands-diff io-exp-cover)))))

(def dactyl-stands-right
  (difference stands
              (stands-diff teensy-cover)
              (if STANDS_SEPERATE
                ((stands-alignment RIGHT)
                 (stands-diff (key-place 0 1 (cube 10 10 10)))
                 (stands-diff (key-place 0 1 (translate [0 0 -5] (cube 15 15 15) )))))))


(def dactyl-keycaps-left
  (mirror [-1 0 0]
      (union thumbcaps caps)))

(def dactyl-keycaps-right
      (union thumbcaps caps))

(def dactyl-bottom-right
  (union
    (if-not STANDS_SEPERATE dactyl-stands-right)
    (difference
      (union teensy-cover
             (difference bottom-plate
                         case-tolerance
                         (hull teensy-cover)
                         teensy-cover
                         trrs-cutout
                         screw-holes
                         floor))
      (if STANDS_SEPERATE (stands-alignment RIGHT))
      usb-cutout)))

(def dactyl-bottom-left
  (union
    (if-not STANDS_SEPERATE dactyl-stands-left)
    (mirror [-1 0 0]
      (difference
        (union io-exp-cover
              (difference bottom-plate
                          case-tolerance
                          (hull io-exp-cover)
                          io-exp-cover
                          trrs-cutout
                          screw-holes
                          floor))
        (if STANDS_SEPERATE (stands-alignment LEFT))))))

(def dactyl-top-right
  (offset-case-place [0 0 0]
    (union
      (difference
        (union key-holes
              connectors
              thumb
              new-case
              teensy-support)
      trrs-hole-just-circle
      screw-holes))))

(def dactyl-top-left
  (mirror [-1 0 0]
    (offset-case-place [0 0 0]
      (union
        (difference
          (union key-holes
                 connectors
                 thumb
                 new-case)
          trrs-hole-just-circle
          screw-holes)))))

;;;;;;;;;;;;;
;; Outputs ;;
;;;;;;;;;;;;;
(comment
(spit "things/dactyl-top-right.scad"
      (write-scad dactyl-top-right))

(spit "things/dactyl-bottom-right.scad"
      (write-scad dactyl-bottom-right))

(spit "things/dactyl-top-left.scad"
      (write-scad dactyl-top-left))

(spit "things/dactyl-bottom-left.scad"
      (write-scad dactyl-bottom-left))

(spit "things/dactyl-keycaps-left.scad"
      (write-scad dactyl-keycaps-left))

(spit "things/dactyl-keycaps-right.scad"
      (write-scad dactyl-keycaps-right))

(if STANDS_SEPERATE
  (do
   (spit "things/dactyl-stands-left.scad"
         (write-scad dactyl-stands-left))

   (spit "things/dactyl-stands-right.scad"
         (write-scad dactyl-stands-right))))
)