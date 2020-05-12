module Dactyl.Case

open System
open OpenSCAD.Fs.Lib
open OpenSCAD.Fs.Lib.Operator
open OpenSCAD.Fs.Lib.Combinator
open OpenSCAD.Fs.Lib.Projection
open System.IO
open Dactyl.SinglePlate
open Dactyl.Placement
open Dactyl.Variables
open Dactyl.Connections
open Dactyl.Thumb
open FSharpx.Collections

let bottom height p =
    p
    |> project
    |> Extrusion.linear height 0.0 0
    |> translate [0.0; 0.0; (height / 2.0) - 10.0]

let bottom_hull p =
    [p; bottom 0.001 p]
    |> List.collect id
    |> hull

let left_wall_x_offset = 10.0
let left_wall_z_offset = 3.0

let left_key_position row direction =
    let v = key_position 0.0 row [mount_width * -0.5;direction * mount_height * 0.5; 0.0]
    List.map2 (fun a b -> a - b) v [left_wall_x_offset; 0.0; left_wall_z_offset]

let left_key_place row direction shape =
    translate (left_key_position row direction) shape

let wall_locate1 dx dy = [dx * wall_thickness; dy * wall_thickness; -1.0]
let wall_locate2 dx dy = [dx * wall_xy_offset; dy * wall_xy_offset; wall_z_offset]
let wall_locate3 dx dy = [dx * (wall_xy_offset + wall_thickness); dy * (wall_xy_offset + wall_thickness); wall_z_offset]

let wall_brace place1 dx1 dy1 post1 place2 dx2 dy2 post2 =
    let post =
        [ place1 post1
        ; place1 (translate (wall_locate1 dx1 dy1) post1)
        ; place1 (translate (wall_locate2 dx1 dy1) post1)
        ; place1 (translate (wall_locate3 dx1 dy1) post1)
        ; place2 post2
        ; place2 (translate (wall_locate1 dx2 dy2) post2)
        ; place2 (translate (wall_locate2 dx2 dy2) post2)
        ; place2 (translate (wall_locate3 dx2 dy2) post2)
        ] |> List.collect id |> hull
    
    let bottom = 
        [ place1 (translate (wall_locate2 dx1 dy1) post1)
        ; place1 (translate (wall_locate3 dx1 dy1) post1)
        ; place2 (translate (wall_locate2 dx2 dy2) post2)
        ; place2 (translate (wall_locate3 dx2 dy2) post2)
        ] |> List.collect id |> bottom_hull

    [post; bottom]
    |> List.collect id
    |> union

let key_wall_brace x1 y1 dx1 dy1 post1 x2 y2 dx2 dy2 post2 =
    let p1 = key_place x1 y1
    let p2 = key_place x2 y2

    wall_brace p1 dx1 dy1 post1 p2 dx2 dy2 post2

let back_wall =
    [ [for x in 0 .. ncols - 1 do key_wall_brace (float(x)) 0 0.0 1.0 web_post_tl (float(x)) 0 0.0 1.0 web_post_tr] |> List.collect id
    ; [for x in 1 .. ncols - 1 do key_wall_brace (float(x)) 0 0.0 1.0 web_post_tl (float(x - 1)) 0 0.0 1.0 web_post_tr] |> List.collect id
    ; key_wall_brace (float(lastcol)) 0 0.0 1.0 web_post_tr (float(lastcol)) 0 1.0 0.0 web_post_tr
    ] |> List.collect id

let right_wall =
    [ [ for y in 0 .. lastrow - 1 do key_wall_brace (float(lastcol)) y 1.0 0.0 web_post_tr (float(lastcol)) y 1.0 0.0 web_post_br ] |> List.collect id
    ; [ for y in 1 .. lastrow - 1 do key_wall_brace (float(lastcol)) (y - 1) 1.0 0.0 web_post_br (float(lastcol)) y 1.0 0.0 web_post_tr] |> List.collect id
    ; key_wall_brace (float(lastcol)) cornerrow 0.0 -1.0 web_post_br (float(lastcol)) cornerrow 1.0 0.0 web_post_br
    ] |> List.collect id

let left_wall =
    let inner y1 y2 =
        [ key_place 0.0 y1 web_post_tl
        ; key_place 0.0 y2 web_post_bl
        ; left_key_place y1 1.0 web_post
        ; left_key_place y2 -1.0 web_post
        ] |> List.collect id |> hull

    let one = 
        [ for y in 0 .. lastrow - 1 do 
           [ wall_brace (left_key_place y 1.0) -1.0 0.0 web_post (left_key_place y -1.0) -1.0 0.0 web_post
           ; inner y y] |> List.collect id |> union
           ] |> List.collect id

    let two =
        [ for y in 1 .. lastrow - 1 do
            [ wall_brace (left_key_place (y - 1) -1.0) -1.0 0.0 web_post (left_key_place y 1.0) -1.0 0.0 web_post
            ; inner y (y - 1)] |> List.collect id |> union
        ] |> List.collect id

    [ one
    ; two
    ; wall_brace (key_place 0.0 0) 0.0 1.0 web_post_tl (left_key_place 0 1.0) 0.0 1.0 web_post
    ; wall_brace (left_key_place 0 1.0) 0.0 1.0 web_post (left_key_place 0 1.0) -1.0 0.0 web_post
    ] |> List.collect id

let front_wall =
    [ key_wall_brace (float(lastcol)) 0 0.0 1.0 web_post_tr (float(lastcol)) 0 1.0 0.0 web_post_tr
    ; key_wall_brace 3.0 lastrow 0.0 -1.0 web_post_bl 3.0 lastrow 0.5 -1.0 web_post_br
    ; key_wall_brace 3.0 lastrow 0.5 -1.0 web_post_br 4.0 cornerrow 1.0 -1.0 web_post_bl
    ; [for x in 4 .. ncols - 1 do key_wall_brace (float(x)) cornerrow 0.0 -1.0 web_post_bl (float(x)) cornerrow 0.0 -1.0 web_post_br] |> List.collect id
    ; [for x in 5 .. ncols - 1 do key_wall_brace (float(x)) cornerrow 0.0 -1.0 web_post_bl (float(x - 1)) cornerrow 0.0 -1.0 web_post_br] |> List.collect id
    ] |> List.collect id

let thumb_wall =
    [ wall_brace thumb_mr_place 0.0 -1.0 web_post_br thumb_tr_place 0.0 -1.0 thumb_post_br
    ; wall_brace thumb_mr_place 0.0 -1.0 web_post_br thumb_mr_place 0.0 -1.0 web_post_bl
    ; wall_brace thumb_br_place 0.0 -1.0 web_post_br thumb_br_place 0.0 -1.0 web_post_bl
    ; wall_brace thumb_ml_place -0.3 1.0 web_post_tr thumb_ml_place 0.0 1.0 web_post_tl
    ; wall_brace thumb_bl_place 0.0 1.0 web_post_tr thumb_bl_place 0.0 1.0 web_post_tl
    ; wall_brace thumb_br_place -1.0 0.0 web_post_tl thumb_br_place -1.0 0.0 web_post_bl
    ; wall_brace thumb_bl_place -1.0 0.0 web_post_tl thumb_bl_place -1.0 0.0 web_post_bl
    //corners
    ; wall_brace thumb_br_place -1.0 0.0 web_post_bl thumb_br_place 0.0 -1.0 web_post_bl
    ; wall_brace thumb_bl_place -1.0 0.0 web_post_tl thumb_bl_place 0.0 1.0 web_post_tl
    //tweeners
    ; wall_brace thumb_mr_place 0.0 -1.0 web_post_bl thumb_br_place 0.0 -1.0 web_post_br
    ; wall_brace thumb_ml_place 0.0 1.0 web_post_tl thumb_bl_place 0.0 1.0 web_post_tr
    ; wall_brace thumb_bl_place -1.0 0.0 web_post_bl thumb_br_place -1.0 0.0 web_post_tl
    ; wall_brace thumb_tr_place 0.0 -1.0 thumb_post_br (key_place 3.0 lastrow) 0.0 -1.0 web_post_bl
    ] |> List.collect id

let thumb_connection_top =
    let part1 = 
        [ left_key_place (cornerrow) -1.0 (translate (wall_locate2 -1.0 0.0) web_post)
        ; left_key_place (cornerrow) -1.0 (translate (wall_locate3 -1.0 0.0) web_post)
        ; thumb_ml_place (translate (wall_locate2 -0.3 1.0) web_post_tr)
        ; thumb_ml_place (translate (wall_locate3 -0.3 1.0) web_post_tr)
        ] |> List.collect id|> bottom_hull

    let part2 =
        [ left_key_place (cornerrow) -1.0 (translate (wall_locate2 -1.0 0.0) web_post)
        ; left_key_place (cornerrow) -1.0 (translate (wall_locate3 -1.0 0.0) web_post)
        ; thumb_ml_place (translate (wall_locate2 -0.3 1.0) web_post_tr)
        ; thumb_ml_place (translate (wall_locate3 -0.3 1.0) web_post_tr)
        ; thumb_tl_place (thumb_post_tl)
        ] |> List.collect id |> hull

    let part3 = 
        [ left_key_place cornerrow -1.0 web_post
        ; left_key_place cornerrow -1.0 (translate (wall_locate1 -1.0 0.0) web_post)
        ; left_key_place cornerrow -1.0 (translate (wall_locate2 -1.0 0.0) web_post)
        ; left_key_place cornerrow -1.0 (translate (wall_locate3 -1.0 0.0) web_post)
        ; thumb_tl_place thumb_post_tl 
        ] |> List.collect id |> hull

    let part4 = 
        [ left_key_place cornerrow -1.0 web_post
        ; left_key_place cornerrow -1.0 (translate (wall_locate1 -1.0 0.0) web_post)
        ; key_place 0.0 cornerrow web_post_bl
        ; key_place 0.0 cornerrow (translate (wall_locate1 -1.0 0.0) web_post_bl)
        ; thumb_tl_place thumb_post_tl
        ] |> List.collect id |> hull

    let part5 = 
        [ thumb_ml_place web_post_tr
        ; thumb_ml_place (translate (wall_locate1 -0.3 1.0) web_post_tr)
        ; thumb_ml_place (translate (wall_locate2 -0.3 1.0) web_post_tr)
        ; thumb_ml_place (translate (wall_locate3 -0.3 1.0) web_post_tr)
        ; thumb_tl_place thumb_post_tl
        ] |> List.collect id |> hull

    [part1; part2; part3; part4; part5] |> List.collect id


let case = 
    [back_wall; right_wall; left_wall; front_wall; thumb_wall; thumb_connection_top] 
    |> List.collect id
    |> union



