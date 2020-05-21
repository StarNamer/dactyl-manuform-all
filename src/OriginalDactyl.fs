module Dactyl.Original
open Dactyl.Placement
open Dactyl.SingleJoint
open Dactyl.Variables
open OpenSCAD.Fs.Lib.Combinator
open OpenSCAD.Fs.Lib
open Dactyl.SinglePlate
open Dactyl.CaseHelper
open Dactyl.ThumbHelper
open OpenSCAD.Fs.Lib.Operator

let key_holes =
    [for column in columns do
        for row in rows do
            if column = 2 then yield single_plate |> key_place (float(column)) row
            if column = 3 then yield single_plate |> key_place (float(column)) row
            if row <> lastrow then yield single_plate |> key_place (float(column)) row]
    |> List.collect id
    |> union

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

let thumb =
    [ thumb_1x_layout single_plate 
    ; thumb_15x_layout single_plate  
    ; thumb_15x_layout larger_plate] 
    |> List.collect id
    |> union

let thumb_connections =
    [ thumb_top_two_connection
    ; thumb_bottom_right_connection
    ; thumb_bottom_left_connection
    ; thumb_center_connection
    ; thumb_top_middle_connection
    ; thumb_top_main_connection
    ; thumb_add_one_connection
    ; thumb_add_two_connections 
    ]
    |> List.collect id
    |> union