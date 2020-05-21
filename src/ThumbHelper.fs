module Dactyl.ThumbHelper

open System
open OpenSCAD.Fs.Lib
open OpenSCAD.Fs.Lib.Operator
open OpenSCAD.Fs.Lib.Combinator
open System.IO
open Dactyl.SinglePlate
open Dactyl.Placement
open Dactyl.Variables
open Dactyl.Connections
open FSharpx.Collections

let sa_double_length = 37.5

let thumborigin =
    let v = key_position 1.0 cornerrow [mount_width / 2.0; - (mount_height / 2.0); 0.0]
    List.map2 (fun a b -> a + b) v thumb_offsets

let thumb_tr_place shape =
    shape
    |> rotate 10.0<deg> [1.0; 0.0; 0.0]
    |> rotate -23.0<deg> [0.0; 1.0; 0.0]
    |> rotate 20.0<deg> [0.0; 0.0; 1.0]
    |> translate thumborigin
    |> translate [-12.0; -16.0; 3.0]

let thumb_tl_place shape =
    shape
    |> rotate 10.0<deg> [1.0; 0.0; 0.0]
    |> rotate -23.0<deg> [0.0; 1.0; 0.0]
    |> rotate 20.0<deg> [0.0; 0.0; 1.0]
    |> translate thumborigin
    |> translate [-32.0; -15.0; -2.0]

let thumb_mr_place shape =
    shape
    |> rotate -6.0<deg> [1.0; 0.0; 0.0]
    |> rotate -34.0<deg> [0.0; 1.0; 0.0]
    |> rotate 48.0<deg> [0.0; 0.0; 1.0]
    |> translate thumborigin
    |> translate [-26.0; -40.0; -13.0]

let thumb_ml_place shape =
    shape
    |> rotate 6.0<deg> [1.0; 0.0; 0.0]
    |> rotate -34.0<deg> [0.0; 1.0; 0.0]
    |> rotate 40.0<deg> [0.0; 0.0; 1.0]
    |> translate thumborigin
    |> translate [-47.0; -25.0; -12.0]

let thumb_br_place shape =
    shape
    |> rotate -16.0<deg> [1.0; 0.0; 0.0]
    |> rotate -33.0<deg> [0.0; 1.0; 0.0]
    |> rotate 54.0<deg> [0.0; 0.0; 1.0]
    |> translate thumborigin
    |> translate [-37.8; -55.3; -25.3]

let thumb_bl_place shape =
    shape
    |> rotate -4.0<deg> [1.0; 0.0; 0.0]
    |> rotate -35.0<deg> [0.0; 1.0; 0.0]
    |> rotate 52.0<deg> [0.0; 0.0; 1.0]
    |> translate thumborigin
    |> translate [-56.3; -43.3; -23.5]

let thumb_1x_layout shape =
    [ thumb_mr_place shape
    ; thumb_ml_place shape
    ; thumb_br_place shape
    ; thumb_bl_place shape ]
    |> List.collect id
    |> union

let thumb_15x_layout shape =
    [ thumb_tr_place shape
    ; thumb_tl_place shape]
    |> List.collect id
    |> union

let larger_plate =
    let plate_height = (sa_double_length - mount_height) / 3.0
    let top_plate =
        centeredCube [mount_width; plate_height; web_thickness]
        |> translate [0.0; (plate_height + mount_height) / 2.0; plate_thickness - (web_thickness / 2.0)]

    [top_plate; mirror [0.0; 1.0; 0.0] top_plate]
    |> List.collect id
    |> union

let thumb_caps = 0.0 //TODO

let thumb_post_tr = translate [(mount_width / 2.0) - post_adj; (mount_height / 1.15) - post_adj; 0.0] web_post
let thumb_post_tl = translate [(mount_width / -2.0) + post_adj; (mount_height / 1.15) - post_adj; 0.0] web_post
let thumb_post_bl = translate [(mount_width / -2.0) + post_adj; (mount_height / -1.15) + post_adj; 0.0] web_post
let thumb_post_br = translate [(mount_width / 2.0) - post_adj; (mount_height / -1.15) + post_adj; 0.0] web_post

let thumb_top_two_connection =
    [ thumb_tl_place thumb_post_tr
    ; thumb_tl_place thumb_post_br
    ; thumb_tr_place thumb_post_tl
    ; thumb_tr_place thumb_post_bl]
    |> List.collect id
    |> triangle_hulls

let thumb_bottom_right_connection =
    [ thumb_br_place web_post_tr
    ; thumb_br_place web_post_br
    ; thumb_mr_place web_post_tl
    ; thumb_mr_place web_post_bl]
    |> List.collect id
    |> triangle_hulls

let thumb_bottom_left_connection =
    [ thumb_bl_place web_post_tr
    ; thumb_bl_place web_post_br
    ; thumb_ml_place web_post_tl
    ; thumb_ml_place web_post_bl]
    |> List.collect id
    |> triangle_hulls

let thumb_center_connection =
    [ thumb_br_place web_post_tl
    ; thumb_bl_place web_post_bl
    ; thumb_br_place web_post_tr
    ; thumb_bl_place web_post_br
    ; thumb_mr_place web_post_tl
    ; thumb_ml_place web_post_bl
    ; thumb_mr_place web_post_tr
    ; thumb_ml_place web_post_br]
    |> List.collect id
    |> triangle_hulls

let thumb_top_middle_connection =
    [ thumb_tl_place thumb_post_tl
    ; thumb_ml_place web_post_tr
    ; thumb_tl_place thumb_post_bl
    ; thumb_ml_place web_post_br
    ; thumb_tl_place thumb_post_br
    ; thumb_mr_place web_post_tr
    ; thumb_tr_place thumb_post_bl
    ; thumb_mr_place web_post_br
    ; thumb_tr_place thumb_post_br]
    |> List.collect id
    |> triangle_hulls

let thumb_top_main_connection =
    [ thumb_tl_place thumb_post_tl
    ; key_place 0.0 cornerrow web_post_bl
    ; thumb_tl_place thumb_post_tr
    ; key_place 0.0 cornerrow web_post_br
    ; thumb_tr_place thumb_post_tl
    ; key_place 1.0 cornerrow web_post_bl
    ; thumb_tr_place thumb_post_tr
    ; key_place 1.0 cornerrow web_post_br
    ; key_place 2.0 lastrow web_post_tl
    ; key_place 2.0 lastrow web_post_bl
    ; thumb_tr_place thumb_post_tr
    ; key_place 2.0 lastrow web_post_bl
    ; thumb_tr_place thumb_post_br
    ; key_place 2.0 lastrow web_post_br
    ; key_place 3.0 lastrow web_post_bl
    ; key_place 2.0 lastrow web_post_tr
    ; key_place 3.0 lastrow web_post_tl
    ; key_place 3.0 cornerrow web_post_bl
    ; key_place 3.0 lastrow web_post_tr
    ; key_place 3.0 cornerrow web_post_br
    ; key_place 4.0 cornerrow web_post_bl]
    |> List.collect id
    |> triangle_hulls

let thumb_add_one_connection =
    [ key_place 1.0 cornerrow web_post_br
    ; key_place 2.0 lastrow web_post_tl
    ; key_place 2.0 cornerrow web_post_bl
    ; key_place 2.0 lastrow web_post_tr
    ; key_place 2.0 cornerrow web_post_br
    ; key_place 3.0 cornerrow web_post_bl]
    |> List.collect id
    |> triangle_hulls

let thumb_add_two_connections =
    [ key_place 3.0 lastrow web_post_tr
    ; key_place 3.0 lastrow web_post_br
    ; key_place 3.0 lastrow web_post_tr
    ; key_place 4.0 cornerrow web_post_bl]
    |> List.collect id
    |> triangle_hulls



