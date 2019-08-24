(ns dactyl-keyboard.dactyl
  (:refer-clojure :exclude [use import])
  (:require [clojure.core.matrix :refer [array matrix mmul]]
            [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]))


(defn deg2rad [degrees]
  (* (/ degrees 180) pi))

;;;;;;;;;;;;;;;;;;;;;;
;; Shape parameters ;;
;;;;;;;;;;;;;;;;;;;;;;

(def nrows 4)
(def ncols 5)

(def α (/ π 12))                        ; curvature of the columns
(def β (/ π 36))                        ; curvature of the rows
(def centerrow (- nrows 3))             ; controls front-back tilt
(def centercol 3)                       ; controls left-right tilt / tenting (higher number is more tenting)
(def tenting-angle (/ π 4))            ; or, change this for more precise tenting control
(def column-style 
  (if (> nrows 5) :orthographic :standard))  ; options include :standard, :orthographic, and :fixed
; (def column-style :fixed)

(defn column-offset [column] (cond
  (= column 2) [0 2.82 -4.5]
  (>= column 4) [0 -12 5.64]            ; original [0 -5.8 5.64]
  :else [0 0 0]))

(def thumb-offsets [0 -3 5])

(def keyboard-z-offset 34)              ; controls overall height; original=9 with centercol=3; use 16 for centercol=2

(def extra-width 2.5)                   ; extra space between the base of keys; original= 2
(def extra-height 1.0)                  ; original= 0.5

(def wall-z-offset -15)                 ; length of the first downward-sloping part of the wall (negative)
(def wall-xy-offset 5)                  ; offset in the x and/or y direction for the first downward-sloping part of the wall (negative)
(def wall-thickness 2)                  ; wall thickness parameter; originally 5

;; Settings for column-style == :fixed 
;; The defaults roughly match Maltron settings
;;   http://patentimages.storage.googleapis.com/EP0219944A2/imgf0002.png
;; Fixed-z overrides the z portion of the column ofsets above.
;; NOTE: THIS DOESN'T WORK QUITE LIKE I'D HOPED.
(def fixed-angles [(deg2rad 10) (deg2rad 10) 0 0 0 (deg2rad -15) (deg2rad -15)])  
(def fixed-x [-41.5 -22.5 0 20.3 41.4 65.5 89.6])  ; relative to the middle finger
(def fixed-z [12.1    8.3 0  5   10.7 14.5 17.5])  
(def fixed-tenting (deg2rad 0))  

;;;;;;;;;;;;;;;;;;;;;;;
;; General variables ;;
;;;;;;;;;;;;;;;;;;;;;;;

(def lastrow (dec nrows))
(def cornerrow (dec lastrow))
(def lastcol (dec ncols))

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

(def columns (range 0 ncols))
(def rows (range 0 nrows))

(def cap-top-height (+ plate-thickness sa-profile-key-height))
(def row-radius (+ (/ (/ (+ mount-height extra-height) 2)
                      (Math/sin (/ α 2)))
                   cap-top-height))
(def column-radius (+ (/ (/ (+ mount-width extra-width) 2)
                         (Math/sin (/ β 2)))
                      cap-top-height))
(def column-x-delta (+ -1 (- (* column-radius (Math/sin β)))))
(def column-base-angle (* β (- centercol 2)))

(defn apply-key-geometry [translate-fn rotate-x-fn rotate-y-fn column row shape]
  (let [column-angle (* β (- centercol column))   
        placed-shape (->> shape
                          (translate-fn [0 0 (- row-radius)])
                          (rotate-x-fn  (* α (- centerrow row)))      
                          (translate-fn [0 0 row-radius])
                          (translate-fn [0 0 (- column-radius)])
                          (rotate-y-fn  column-angle)
                          (translate-fn [0 0 column-radius])
                          (translate-fn (column-offset column)))
        column-z-delta (* column-radius (- 1 (Math/cos column-angle)))
        placed-shape-ortho (->> shape
                                (translate-fn [0 0 (- row-radius)])
                                (rotate-x-fn  (* α (- centerrow row)))      
                                (translate-fn [0 0 row-radius])
                                (rotate-y-fn  column-angle)
                                (translate-fn [(- (* (- column centercol) column-x-delta)) 0 column-z-delta])
                                (translate-fn (column-offset column)))
        placed-shape-fixed (->> shape
                                (rotate-y-fn  (nth fixed-angles column))
                                (translate-fn [(nth fixed-x column) 0 (nth fixed-z column)])
                                (translate-fn [0 0 (- (+ row-radius (nth fixed-z column)))])
                                (rotate-x-fn  (* α (- centerrow row)))      
                                (translate-fn [0 0 (+ row-radius (nth fixed-z column))])
                                (rotate-y-fn  fixed-tenting)
                                (translate-fn [0 (second (column-offset column)) 0])
                                )]
    (->> (case column-style
          :orthographic placed-shape-ortho 
          :fixed        placed-shape-fixed
                        placed-shape)
         (rotate-y-fn  tenting-angle)
         (translate-fn [0 0 keyboard-z-offset]))))

(defn key-place [column row shape]
  (apply-key-geometry translate 
    (fn [angle obj] (rotate angle [1 0 0] obj)) 
    (fn [angle obj] (rotate angle [0 1 0] obj)) 
    column row shape))

(defn rotate-around-x [angle position] 
  (mmul 
   [[1 0 0]
    [0 (Math/cos angle) (- (Math/sin angle))]
    [0 (Math/sin angle)    (Math/cos angle)]]
   position))

(defn rotate-around-y [angle position] 
  (mmul 
   [[(Math/cos angle)     0 (Math/sin angle)]
    [0                    1 0]
    [(- (Math/sin angle)) 0 (Math/cos angle)]]
   position))

(defn key-position [column row position]
  (apply-key-geometry (partial map +) rotate-around-x rotate-around-y column row position))


(def key-holes
  (apply union
         (for [column columns
               row rows
               :when (or (.contains [2 3] column)
                         (not= row lastrow))]
           (->> single-plate
                (key-place column row)))))

(def caps
  (apply union
         (for [column columns
               row rows
               :when (or (.contains [2 3] column)
                         (not= row lastrow))]
           (->> (sa-cap (if (= column 5) 1 1))
                (key-place column row)))))

; (pr (rotate-around-y π [10 0 1]))
; (pr (key-position 1 cornerrow [(/ mount-width 2) (- (/ mount-height 2)) 0]))

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
;TODO it can be shared with screw insertion
(def web-post-tl (translate [(+ (/ mount-width -2) post-adj) (- (/ mount-height 2) post-adj) 0] web-post))
;TODO it can be shared with screw insertion
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
          (for [column (range 0 (dec ncols))
                row (range 0 lastrow)]
            (triangle-hulls
             (key-place (inc column) row web-post-tl)
             (key-place column row web-post-tr)
             (key-place (inc column) row web-post-bl)
             (key-place column row web-post-br)))

          ;; Column connections
          (for [column columns
                row (range 0 cornerrow)]
            (triangle-hulls
             (key-place column row web-post-bl)
             (key-place column row web-post-br)
             (key-place column (inc row) web-post-tl)
             (key-place column (inc row) web-post-tr)))

          ;; Diagonal connections
          (for [column (range 0 (dec ncols))
                row (range 0 cornerrow)]
            (triangle-hulls
             (key-place column row web-post-br)
             (key-place column (inc row) web-post-tr)
             (key-place (inc column) row web-post-bl)
             (key-place (inc column) (inc row) web-post-tl))))))

;;;;;;;;;;;;
;; Thumbs ;;
;;;;;;;;;;;;

(def thumborigin 
  (map + (key-position 1 cornerrow [(/ mount-width 2) (- (/ mount-height 2)) 0])
         thumb-offsets))
; (pr thumborigin)

(defn thumb-tr-place [shape]
  (->> shape
      ;  (rotate (deg2rad  10) [1 0 0])
      ;  (rotate (deg2rad -23) [0 1 0])
      ;  (rotate (deg2rad  -3) [0 0 1])
       (rotate (deg2rad  10) [1 0 0])
       (rotate (deg2rad -23) [0 1 0])
       (rotate (deg2rad  10) [0 0 1])
       (translate thumborigin)
       (translate [-12 -16 3])
       ))
(defn thumb-tl-place [shape]
  (->> shape
      ;  (rotate (deg2rad  10) [1 0 0])
      ;  (rotate (deg2rad -23) [0 1 0])
      ;  (rotate (deg2rad  -3) [0 0 1])
       (rotate (deg2rad  10) [1 0 0])
       (rotate (deg2rad -23) [0 1 0])
       (rotate (deg2rad  10) [0 0 1])
       (translate thumborigin)
       (translate [-32 -15 -2])))
(defn thumb-mr-place [shape]
  (->> shape
       (rotate (deg2rad  -6) [1 0 0])
       (rotate (deg2rad -34) [0 1 0])
       (rotate (deg2rad  48) [0 0 1])
       (translate thumborigin)
       (translate [-29 -40 -13])
       ))
(defn thumb-ml-place [shape]
  (->> shape
       (rotate (deg2rad   6) [1 0 0])
       (rotate (deg2rad -34) [0 1 0])
       (rotate (deg2rad  40) [0 0 1])
       (translate thumborigin)
       (translate [-51 -25 -12])))
(defn thumb-br-place [shape]
  (->> shape
       (rotate (deg2rad -16) [1 0 0])
       (rotate (deg2rad -33) [0 1 0])
       (rotate (deg2rad  54) [0 0 1])
       (translate thumborigin)
       (translate [-37.8 -55.3 -25.3])
       ))
(defn thumb-bl-place [shape]
  (->> shape
       (rotate (deg2rad  -4) [1 0 0])
       (rotate (deg2rad -35) [0 1 0])
       (rotate (deg2rad  52) [0 0 1])
       (translate thumborigin)
       (translate [-56.3 -43.3 -23.5])
       ))

(defn thumb-1x-layout [shape]
  (union
   (thumb-mr-place shape)
   (thumb-ml-place shape)
   (thumb-br-place shape)
   (thumb-bl-place shape)))

(defn thumb-15x-layout [shape]
  (union
   (thumb-tr-place shape)
   (thumb-tl-place shape)))

(def larger-plate
  (let [plate-height (/ (- sa-double-length mount-height) 3)
        top-plate (->> (cube mount-width plate-height web-thickness)
                       (translate [0 (/ (+ plate-height mount-height) 2)
                                   (- plate-thickness (/ web-thickness 2))]))
        ]
    (union top-plate (mirror [0 1 0] top-plate))))

(def thumbcaps
  (union
   (thumb-1x-layout (sa-cap 1))
   (thumb-15x-layout (rotate (/ π 2) [0 0 1] (sa-cap 1.5)))))


(def thumb
  (union
   (thumb-1x-layout single-plate)
   (thumb-15x-layout single-plate)
   (thumb-15x-layout larger-plate)
   ))

(def thumb-post-tr (translate [(- (/ mount-width 2) post-adj)  (- (/ mount-height  1.15) post-adj) 0] web-post))
(def thumb-post-tl (translate [(+ (/ mount-width -2) post-adj) (- (/ mount-height  1.15) post-adj) 0] web-post))
(def thumb-post-bl (translate [(+ (/ mount-width -2) post-adj) (+ (/ mount-height -1.15) post-adj) 0] web-post))
(def thumb-post-br (translate [(- (/ mount-width 2) post-adj)  (+ (/ mount-height -1.15) post-adj) 0] web-post))

(def thumb-connectors
  (union
      (triangle-hulls    ; top two
             (thumb-tl-place thumb-post-tr)
             (thumb-tl-place thumb-post-br)
             (thumb-tr-place thumb-post-tl)
             (thumb-tr-place thumb-post-bl))
      (triangle-hulls    ; bottom two on the right
             (thumb-br-place web-post-tr)
             (thumb-br-place web-post-br)
             (thumb-mr-place web-post-tl)
             (thumb-mr-place web-post-bl))
      (triangle-hulls    ; bottom two on the left
             (thumb-bl-place web-post-tr)
             (thumb-bl-place web-post-br)
             (thumb-ml-place web-post-tl)
             (thumb-ml-place web-post-bl))
      (triangle-hulls    ; centers of the bottom four
             (thumb-br-place web-post-tl)
             (thumb-bl-place web-post-bl)
             (thumb-br-place web-post-tr)
             (thumb-bl-place web-post-br)
             (thumb-mr-place web-post-tl)
             (thumb-ml-place web-post-bl)
             (thumb-mr-place web-post-tr)
             (thumb-ml-place web-post-br))
      (triangle-hulls    ; top two to the middle two, starting on the left
             (thumb-tl-place thumb-post-tl)
             (thumb-ml-place web-post-tr)
             (thumb-tl-place thumb-post-bl)
             (thumb-ml-place web-post-br)
             (thumb-tl-place thumb-post-br)
             (thumb-mr-place web-post-tr)
             (thumb-tr-place thumb-post-bl)
             (thumb-mr-place web-post-br)
             (thumb-tr-place thumb-post-br)) 
      (triangle-hulls    ; top two to the main keyboard, starting on the left
             (thumb-tl-place thumb-post-tl)
             (key-place 0 cornerrow web-post-bl)
             (thumb-tl-place thumb-post-tr)
             (key-place 0 cornerrow web-post-br)
             (thumb-tr-place thumb-post-tl)
             (key-place 1 cornerrow web-post-bl)
             (thumb-tr-place thumb-post-tr)
             (key-place 1 cornerrow web-post-br)
             (key-place 2 lastrow web-post-tl)
             (key-place 2 lastrow web-post-bl)
             (thumb-tr-place thumb-post-tr)
             (key-place 2 lastrow web-post-bl)
             (thumb-tr-place thumb-post-br)
             (key-place 2 lastrow web-post-br)
             (key-place 3 lastrow web-post-bl)
             (key-place 2 lastrow web-post-tr)
             (key-place 3 lastrow web-post-tl)
             (key-place 3 cornerrow web-post-bl)
             (key-place 3 lastrow web-post-tr)
             (key-place 3 cornerrow web-post-br)
             (key-place 4 cornerrow web-post-bl))
      (triangle-hulls 
             (key-place 1 cornerrow web-post-br)
             (key-place 2 lastrow web-post-tl)
             (key-place 2 cornerrow web-post-bl)
             (key-place 2 lastrow web-post-tr)
             (key-place 2 cornerrow web-post-br)
             (key-place 3 cornerrow web-post-bl)
             )
      (triangle-hulls 
             (key-place 3 lastrow web-post-tr)
             (key-place 3 lastrow web-post-br)
             (key-place 3 lastrow web-post-tr)
             (key-place 4 cornerrow web-post-bl))
  ))

;;;;;;;;;;
;; Case ;;
;;;;;;;;;;

(defn bottom [height p]
  (->> (project p)
       (extrude-linear {:height height :twist 0 :convexity 0})
       (translate [0 0 (- (/ height 2) 10)])))

(defn bottom-hull [& p]
  (hull p (bottom 0.001 p)))

(def left-wall-x-offset 10)
(def left-wall-z-offset  3)

(defn left-key-position [row direction]
  (map - (key-position 0 row [(* mount-width -0.5) (* direction mount-height 0.5) 0]) [left-wall-x-offset 0 left-wall-z-offset]) )

(defn left-key-place [row direction shape]
  (translate (left-key-position row direction) shape))


(defn wall-locate1 [dx dy] [(* dx wall-thickness) (* dy wall-thickness) -1])
(defn wall-locate2 [dx dy] [(* dx wall-xy-offset) (* dy wall-xy-offset) wall-z-offset])
(defn wall-locate3 [dx dy] [(* dx (+ wall-xy-offset wall-thickness)) (* dy (+ wall-xy-offset wall-thickness)) wall-z-offset])
(defn wall-locate4 [dx dy] [(* dx (+ wall-xy-offset wall-thickness)) (* dy (+ wall-xy-offset wall-thickness)) wall-z-offset])

(defn wall-brace [place1 dx1 dy1 post1 place2 dx2 dy2 post2]
  (union
    (hull
      (place1 post1)
      (place1 (translate (wall-locate1 dx1 dy1) post1))
      (place1 (translate (wall-locate2 dx1 dy1) post1))
      (place1 (translate (wall-locate3 dx1 dy1) post1))
      (place2 post2)
      (place2 (translate (wall-locate1 dx2 dy2) post2))
      (place2 (translate (wall-locate2 dx2 dy2) post2))
      (place2 (translate (wall-locate3 dx2 dy2) post2))
    )
    (bottom-hull
      (place1 (translate (wall-locate2 dx1 dy1) post1))
      (place1 (translate (wall-locate3 dx1 dy1) post1))
      (place2 (translate (wall-locate2 dx2 dy2) post2))
      (place2 (translate (wall-locate3 dx2 dy2) post2))
    )
  )
)

(defn wall-brace2 [place1 dx1 dy1 post1 place2 dx2 dy2 post2]
  (union
    (hull
      (place1 post1)
      (place1 (translate (wall-locate1 dx1 dy1) post1))
      (place1 (translate (wall-locate2 dx1 dy1) post1))
      (place1 (translate (wall-locate3 dx1 dy1) post1))
      (place2 post2)
      (place2 (translate (wall-locate1 dx2 dy2) post2))
    ;  (place2 (translate (wall-locate2 dx2 dy2) post2))
    ;  (place2 (translate (wall-locate3 dx2 dy2) post2))
    )
    ;(bottom-hull
    ;  (place1 (translate (wall-locate2 dx1 dy1) post1))
    ;  (place1 (translate (wall-locate3 dx1 dy1) post1))
    ;  (place2 (translate (wall-locate2 dx2 dy2) post2))
    ;  (place2 (translate (wall-locate3 dx2 dy2) post2)))
  ))

(defn wall-brace3 [place1 dx1 dy1 post1 place2 dx2 dy2 post2]
  (union
    (hull
      ;(place1 post1)
      ;(place1 (translate (wall-locate1 dx1 dy1) post1))
      (place1 (translate (wall-locate2 dx1 dy1) post1))
      (place1 (translate (wall-locate3 dx1 dy1) post1))
      (place2 post2)
      (place2 (translate (wall-locate1 dx2 dy2) post2))
      (place2 (translate (wall-locate2 dx2 dy2) post2))
      (place2 (translate (wall-locate3 dx2 dy2) post2))
    )
    (bottom-hull
      (place1 (translate (wall-locate2 dx1 dy1) post1))
      (place1 (translate (wall-locate3 dx1 dy1) post1))
      (place2 (translate (wall-locate2 dx2 dy2) post2))
      (place2 (translate (wall-locate3 dx2 dy2) post2)))
  ))

(defn key-wall-brace [x1 y1 dx1 dy1 post1 x2 y2 dx2 dy2 post2] 
  (wall-brace (partial key-place x1 y1) dx1 dy1 post1 
              (partial key-place x2 y2) dx2 dy2 post2))

(defn key-wall-brace2 [x1 y1 dx1 dy1 post1 x2 y2 dx2 dy2 post2] 
  (wall-brace (partial key-place x1 y1) (+ dx1 1.0) dy1 post1 
              (partial key-place x2 y2)    dx2      dy2 post2))

(def case-walls
  (union
   ; back wall
   (for [x (range 0 ncols)] (key-wall-brace x 0 0 1 web-post-tl x       0 0 1 web-post-tr))
   (for [x (range 1 ncols)] (key-wall-brace x 0 0 1 web-post-tl (dec x) 0 0 1 web-post-tr))
   (key-wall-brace lastcol 0 0 1 web-post-tr lastcol 0 1 0 web-post-tr)
   ; right wall
   (for [y (range 0 lastrow)] (key-wall-brace lastcol y 1 0 web-post-tr lastcol y       1 0 web-post-br))
   (for [y (range 1 lastrow)] (key-wall-brace lastcol (dec y) 1 0 web-post-br lastcol y 1 0 web-post-tr))
   (key-wall-brace lastcol cornerrow 0 -1 web-post-br lastcol cornerrow 1 0 web-post-br)
   ; left wall
   (for [y (range 0 lastrow)] (union (wall-brace (partial left-key-place y 1)       -1 0 web-post (partial left-key-place y -1) -1 0 web-post)
                                     (hull (key-place 0 y web-post-tl)
                                           (key-place 0 y web-post-bl)
                                           (left-key-place y  1 web-post)
                                           (left-key-place y -1 web-post))))
   (for [y (range 1 lastrow)] (union (wall-brace (partial left-key-place (dec y) -1) -1 0 web-post (partial left-key-place y  1) -1 0 web-post)
                                     (hull (key-place 0 y       web-post-tl)
                                           (key-place 0 (dec y) web-post-bl)
                                           (left-key-place y        1 web-post)
                                           (left-key-place (dec y) -1 web-post))))
   ;(wall-brace (partial key-place 0 0)      0    1   web-post-tl (partial left-key-place 0 1) 0 1 web-post)
        ; original one
   (wall-brace2 (partial key-place 0 0)       0    1   web-post-tl (partial left-key-place 0 1) -1 0 web-post)
   (wall-brace3 (partial key-place 0 0)       0    1   web-post-tl (partial left-key-place 0 1) -1 0 web-post)
        ; corner wall between left and back face to monitor - point to screw-insert
   ;(wall-brace (partial left-key-place 0 1)  0    1.5 web-post    (partial left-key-place 0 1) -1 0 web-post)
        ;   TODO back left corner face to monitor
   ; front wall
   (key-wall-brace lastcol 0 0 1 web-post-tr lastcol 0 1 0 web-post-tr)
   (key-wall-brace 3 lastrow   0 -1 web-post-bl 3 lastrow 0.5 -1 web-post-br)
        ;
   (key-wall-brace 3 lastrow 0.5 -1 web-post-br 4 cornerrow 1 -1 web-post-bl)
        ;   TODO front corner faced to user - point to screw-insert
   (for [x (range 4 ncols)] (key-wall-brace2 x cornerrow 0 -1 web-post-bl      x  cornerrow 0 -1 web-post-br))
        ;   front corner at last column faced to user 
   (for [x (range 5 ncols)] (key-wall-brace2 x cornerrow 0 -1 web-post-bl (dec x) cornerrow 0 -1 web-post-br))
   ; thumb walls
   (wall-brace thumb-mr-place  0 -1 web-post-br thumb-tr-place  0 -1 thumb-post-br)
   (wall-brace thumb-mr-place  0 -1 web-post-br thumb-mr-place  0 -1 web-post-bl)
   (wall-brace thumb-br-place  0 -1 web-post-br thumb-br-place  0 -1 web-post-bl)
   (wall-brace thumb-ml-place -0.3  1 web-post-tr thumb-ml-place  0  1 web-post-tl)
   (wall-brace thumb-bl-place  0  1 web-post-tr thumb-bl-place  0  1 web-post-tl)
   (wall-brace thumb-br-place -1  0 web-post-tl thumb-br-place -1  0 web-post-bl)
   (wall-brace thumb-bl-place -1  0 web-post-tl thumb-bl-place -1  0 web-post-bl)
   ; thumb corners
   (wall-brace thumb-br-place -1  0 web-post-bl thumb-br-place  0 -1 web-post-bl)
        ;   screw-insert #7
   (wall-brace thumb-bl-place -1  0 web-post-tl thumb-bl-place  0  1 web-post-tl)
        ;   screw-insert #8
   ; thumb tweeners
   (wall-brace thumb-mr-place  0 -1 web-post-bl thumb-br-place  0 -1 web-post-br)
   (wall-brace thumb-ml-place  0  1 web-post-tl thumb-bl-place  0  1 web-post-tr)
   (wall-brace thumb-bl-place -1  0 web-post-bl thumb-br-place -1  0 web-post-tl)
   (wall-brace thumb-tr-place  0 -1 thumb-post-br (partial key-place 3 lastrow)  0 -1 web-post-bl)
   ; clunky bit on the top left thumb connection  (normal connectors don't work well)
   (bottom-hull
     (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
     (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
     (thumb-ml-place (translate (wall-locate2 -0.3 1) web-post-tr))
     (thumb-ml-place (translate (wall-locate3 -0.3 1) web-post-tr)))
   (hull
     (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
     (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
     (thumb-ml-place (translate (wall-locate2 -0.3 1) web-post-tr))
     (thumb-ml-place (translate (wall-locate3 -0.3 1) web-post-tr))
     (thumb-tl-place thumb-post-tl))
   (hull
     (left-key-place cornerrow -1 web-post)
     (left-key-place cornerrow -1 (translate (wall-locate1 -1 0) web-post))
     (left-key-place cornerrow -1 (translate (wall-locate2 -1 0) web-post))
     (left-key-place cornerrow -1 (translate (wall-locate3 -1 0) web-post))
     (thumb-tl-place thumb-post-tl))
   (hull
     (left-key-place cornerrow -1 web-post)
     (left-key-place cornerrow -1 (translate (wall-locate1 -1 0) web-post))
     (key-place 0 cornerrow web-post-bl)
     (key-place 0 cornerrow (translate (wall-locate1 -1 0) web-post-bl))
     (thumb-tl-place thumb-post-tl))
   (hull
     (thumb-ml-place web-post-tr)
     (thumb-ml-place (translate (wall-locate1 -0.3 1) web-post-tr))
     (thumb-ml-place (translate (wall-locate2 -0.3 1) web-post-tr))
     (thumb-ml-place (translate (wall-locate3 -0.3 1) web-post-tr))
     (thumb-tl-place thumb-post-tl))
  ))


(def rj9-start  (map + [0 -3  0] (key-position 0 0 (map + (wall-locate3 0 1) [0 (/ mount-height  2) 0]))))
(def rj9-position  [(first rj9-start) (second rj9-start) 11])
(def rj9-cube   (cube 14.78 13 22.38))
(def rj9-space  (translate rj9-position rj9-cube))
(def rj9-holder (translate rj9-position
                  (difference rj9-cube
                              (union (translate [0 2 0] (cube 10.78  9 18.38))
                                     (translate [0 0 5] (cube 10.78 13  5))))))
(def teensy-width 20)  
(def teensy-height 12)
(def teensy-length 40)
(def teensy-pcb-thickness 3) 
(def teensy-holder-width  (+ 7 teensy-pcb-thickness))
(def teensy-holder-height (+ 6 teensy-width))
(def teensy-offset-height 5)
(def teensy-holder-top-length 18)
(def teensy-top-xy (key-position 0       lastrow (map + (wall-locate1  -80.5 -9.5) [(/ mount-width 2) 0 0])))
(def teensy-bot-xy (key-position lastcol lastrow (map + (wall-locate2  -14.5 -11.6) [(/ mount-width 2) 0 0])))
;(def teensy-holder-length (- (second teensy-top-xy) (second teensy-bot-xy)))
(def teensy-holder-length teensy-length)
(def teensy-holder-offset (/ teensy-holder-length -2))
(def teensy-holder-top-offset (- (/ teensy-holder-top-length 2) teensy-length))

(def screw-insert-height 3.8)
(def screw-insert-bottom-radius (/ 5.31 2))
(def screw-insert-top-radius (/ 5.1 2))
(def teensy-screw-hole-distance -28.8)

(defn screw-insert-shape [bottom-radius top-radius height] 
   (union (cylinder [bottom-radius top-radius] height)
          (translate [0 0 (/ height 2)] (sphere top-radius))))

(def usb-holder-position (key-position 1 0 (map + (wall-locate2 0 1) [0 (/ mount-height 2) 0])))
(def usb-holder-size [14.2 10 20.0])
(def usb-holder-thickness 4)
(defn usb-holder-location [holder]
    (translate [(+ (first teensy-top-xy) (- teensy-width 4))
                (+ (second teensy-top-xy) 3)
                (/ (+ (last usb-holder-size) usb-holder-thickness 2) 2)] holder)
)
(defn usb-holder-position [holder]
    (translate [(+ (/ (+ (first usb-holder-size) screw-insert-height screw-insert-top-radius) 2) 3.5)
                (+ (/ (second usb-holder-size) 2) 3)
                0]
                holder
    )
)

(def usb-holder-outer
    (->> (cube (+ (first usb-holder-size) usb-holder-thickness)
               (second usb-holder-size)
               (+ (last usb-holder-size) usb-holder-thickness)
         )
         (usb-holder-position)
    )
)

(defn usb-holder-hole-position [holder]
    (->> holder
         (rotate (deg2rad  52) [0 0 1])
         (translate [(+ (first teensy-top-xy) (- teensy-width 6))
                     (- (second teensy-top-xy) 13)
                     (/ (+ 6 teensy-width) 2)])
    )
)

(def micro-usb-hole-size [10.5 2.7 7.75]) ; [from the bottom   height   width  ]
(def trrs-hole-size  [5.1  3.00 -3.0])    ; [from the bottom   radius   to left]
(def reset-hole-size [5.6  1.75 +3.1])    ; [from the bottom   radius   to left]
(def usb-holder-wall-thickness 2)

(defn usb-holder-hole [mirror]
    (->> (union 
            (translate [0 usb-holder-wall-thickness 0]
               (apply cube [(first usb-holder-size) (+ (second usb-holder-size) 2) (last usb-holder-size)])
            )
            (translate [-5 (+ usb-holder-wall-thickness 2) 0]
               (apply cube [(first usb-holder-size)
                            (+ (second usb-holder-size) 2)
                            (+ (last usb-holder-size) 3 usb-holder-wall-thickness)
                           ]
               )
            )
            (translate [0.5 (+ usb-holder-wall-thickness 5.2) 0]
               (rotate (deg2rad 60) [0 0 1]
                  (apply cube [(first usb-holder-size)
                               (+ (second usb-holder-size) 2)
                               (+ (last usb-holder-size) 3 usb-holder-wall-thickness)
                              ]
                  )
               )
            )
            ; micro USB
            (translate [(- (first micro-usb-hole-size) (/ (first usb-holder-size) 2)) 0 0]
              (apply cube [ (second micro-usb-hole-size) (last usb-holder-size) (last micro-usb-hole-size)])
            )
            ; TRRS
            (->> (cylinder [(second trrs-hole-size) (second trrs-hole-size)] (last usb-holder-size))
                 (rotate (deg2rad 90) [1 0 0])
                 (translate [(- (first  trrs-hole-size) (/ (first usb-holder-size) 2))
                             0
                             (* (last trrs-hole-size) mirror)
                            ])
            )
            ; Reset switch
            (->> (cylinder [(second reset-hole-size) (second reset-hole-size)] (last usb-holder-size))
                 (rotate (deg2rad 90) [1 0 0])
                 (translate [(- (first  reset-hole-size) (/ (first usb-holder-size) 2))
                             0
                             (* (last reset-hole-size) mirror)
                            ])
            )
         )
         (usb-holder-position)
    )
)

(def usb-holder
    (->> (difference usb-holder-outer (usb-holder-hole 1))
    )
)

 
(def teensy-holder 
    (->> 
        (union 
            (difference
               (->> (cube (+ screw-insert-height screw-insert-top-radius) teensy-holder-length (+ 6 teensy-width))     ; base
                    (translate [3.5 teensy-holder-offset 0])
               )
               (->> (screw-insert-shape screw-insert-bottom-radius screw-insert-top-radius (+ 1 screw-insert-height))
                    (rotate (deg2rad  -90) [0 1 0])
                    (translate [(+ 3.5 screw-insert-top-radius) teensy-screw-hole-distance 0])
               )
            )
            usb-holder-outer
            (->> (cube teensy-pcb-thickness teensy-holder-length 3)  ;   mid section
                 (translate [(+ screw-insert-height screw-insert-top-radius 1.75) teensy-holder-offset (- -1.5 (/ teensy-width 2))]))
            (->> (cube teensy-pcb-thickness teensy-holder-top-length 3)  ;   mid section
                 (translate [(+ screw-insert-height screw-insert-top-radius 1.75) teensy-holder-offset (+ 1.5 (/ teensy-width 2))]))
        )
        (usb-holder-hole-position)
    )
)

(defn screw-insert [column row bottom-radius top-radius height] 
  (let [is-first-column       (= column 0)
        is-last-column        (= column lastcol)
        is-center-column      (= column centercol)
        is-first-row          (= row 0)
        is-thumb-row          (= row lastrow)
        is-corner-row         (= row cornerrow)
        is-left-down          (and is-last-column   is-corner-row)  ; #01
        is-left-up            (and is-last-column   is-first-row)   ; #02
        is-right-up           (and is-first-column  is-first-row)   ; #05
        is-right-down         (and is-first-column  is-corner-row)  ; #06
        is-middle-down        (and is-center-column is-thumb-row)   ; #03
        is-middle-up          (and is-center-column is-first-row)   ; #04
        is-thumb-down         (and is-first-column  is-thumb-row)   ; #07
        is-thumb-up           (and is-last-column   is-thumb-row)   ; #08
        position    (if is-left-up
                       (key-position column row (map + (wall-locate2  2  0.8) [0 (/ mount-height 2) 0]))
                       (if is-left-down
                          (key-position column row (map - (wall-locate2  2 -0.8) [0 (/ mount-height 2) 0]))
                          (if is-right-down
                             (key-position column row (map + (wall-locate3 -2.2 -1.8) [0 (/ mount-height 2) 0]))
                             (if is-right-up
                                (key-position column row (map - (wall-locate3  -1.6  2.3) [0 (/ mount-height 2) 0]))
                                (if is-middle-down
                                   (key-position column row (map + (wall-locate2 -6.3 -5.7) [0 (/ mount-height 2) 0]))
                                   (if is-middle-up
                                      (key-position column row (map - (wall-locate2  0  4.5) [0 (/ mount-height 2) 0]))
                                      (if is-thumb-up
                                          (key-position column row (map + (wall-locate1  -80.5 -9.5) [(/ mount-width 2) 0 0]))
                                          (key-position column row (map + (wall-locate2  -14.5 -11.6) [(/ mount-width 2) 0 0]))
                                            ; is-thumb-down
                                      )
                                   )
                                )
                             )
                          )
                        )
                      )
        ]
    (->> (screw-insert-shape bottom-radius top-radius height)
         (translate [(first position) (second position) (/ height 2)])
    )))

(defn screw-insert-all-shapes [bottom-radius top-radius height]
  (union 
     (screw-insert lastcol cornerrow  bottom-radius top-radius height) ; (#01)
        ;   left up
     (screw-insert lastcol 0          bottom-radius top-radius height) ; (#02)
        ;   left down
     (screw-insert centercol 0        bottom-radius top-radius height) ; (#03)
        ;   middle up
     (screw-insert centercol lastrow  bottom-radius top-radius height) ; (#04)
        ;   middle down
     (screw-insert 0 0                bottom-radius top-radius height) ; (#05)
        ;   right up
     (screw-insert 0       cornerrow  bottom-radius top-radius height) ; (#06)
        ;   right down
     (screw-insert 0        lastrow   bottom-radius top-radius height) ; (#07)
        ;   thumb down
     (screw-insert lastcol  lastrow   bottom-radius top-radius height) ; (#08)
        ;   thumb up
     ))
(def screw-insert-holes  (screw-insert-all-shapes screw-insert-bottom-radius screw-insert-top-radius screw-insert-height))
(def screw-insert-outers (screw-insert-all-shapes (+ screw-insert-bottom-radius 1.6) (+ screw-insert-top-radius 1.6) (+ screw-insert-height 1.5)))
(def screw-insert-screw-holes  (screw-insert-all-shapes 1.7 1.7 350))

(def wire-post-height 7)
(def wire-post-overhang 3.5)
(def wire-post-diameter 2.6)
(defn wire-post [direction offset]
   (->> (union (translate [0 (* wire-post-diameter -0.5 direction) 0] (cube wire-post-diameter wire-post-diameter wire-post-height))
               (translate [0 (* wire-post-overhang -0.5 direction) (/ wire-post-height -2)] (cube wire-post-diameter wire-post-overhang wire-post-diameter)))
        (translate [0 (- offset) (+ (/ wire-post-height -2) 3) ])
        (rotate (/ α -2) [1 0 0])
        (translate [3 (/ mount-height -2) 0])))

(def wire-posts
  (union
     (thumb-ml-place (translate [-5 0 -2] (wire-post  1 0)))
     (thumb-ml-place (translate [ 0 0 -2.5] (wire-post -1 6)))
     (thumb-ml-place (translate [ 5 0 -2] (wire-post  1 0)))
     (for [column (range 0 lastcol)
           row (range 0 cornerrow)]
       (union
        (key-place column row (translate [-5 0 0] (wire-post 1 0)))
        (key-place column row (translate [0 0 0] (wire-post -1 6)))
        (key-place column row (translate [5 0 0] (wire-post  1 0)))))))


(defn model-right [mirror] (difference 
                   (union
                    key-holes
                    connectors
                    thumb
                    thumb-connectors
                    (difference (union case-walls 
                                       screw-insert-outers 
                                       teensy-holder
                                )
                                (usb-holder-hole-position (usb-holder-hole mirror))
                                screw-insert-holes
                    )
                    )
                   (translate [0 0 -20] (cube 350 350 40)) 
                  ))

(spit "things/right.scad"
      (write-scad (model-right 1)))
 
(spit "things/left.scad"
      (write-scad (mirror [-1 0 0] (model-right -1))))
                  
(spit "things/right-test.scad"
      (write-scad (difference
                    (union
                        key-holes
                        connectors
                        thumb
                        thumb-connectors
                        (difference (union case-walls 
                                           screw-insert-outers 
                                           teensy-holder
                                           ;usb-holder
                                    )
                                    (usb-holder-hole-position (usb-holder-hole 1))
                                    screw-insert-holes
                        )
                        thumbcaps
                        caps
                     )
                     (translate [0 0 -20] (cube 350 350 40)) 
                    )
      )
)

(spit "things/right-plate.scad"
      (write-scad 
                   (cut
                     (translate [0 0 -0.1]
                       (difference (union case-walls
                                          ;teensy-holder
                                          ; rj9-holder
                                          screw-insert-outers)
                                   (translate [0 0 -10] screw-insert-screw-holes))
                  ))))

(spit "things/test.scad"
    (write-scad 
        (difference
            (union
               ;(difference usb-holder-outer usb-holder-hole)
                ;web-post-tl
                ;screw-insert-outers 
                ;(thumb-bl-place (translate (wall-locate3 -1 0) (screw-insert-shape screw-insert-bottom-radius screw-insert-top-radius screw-insert-height)))
               ;(difference teensy-holder (usb-holder-hole-position usb-holder-hole))
            )
            teensy-holder
            (usb-holder-hole 1)
            (usb-holder-hole-position (usb-holder-hole 1))
            ;(translate [0 0 -20] (cube 350 350 40)) 
        )
    )
)



(defn -main [dum] 1)  ; dummy to make it easier to batch
