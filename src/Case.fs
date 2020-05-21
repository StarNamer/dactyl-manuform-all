module Dactyl.Case
open Dactyl.Placement
open Dactyl.SingleJoint
open Dactyl.Variables
open OpenSCAD.Fs.Lib.Combinator
open OpenSCAD.Fs.Lib
open Dactyl.CaseHelper
open Dactyl.SinglePlate
open OpenSCAD.Fs.Lib.Operator
open Dactyl.ThumbHelper

let topWall =
    let jointBlock =
        let slope =
            centeredCube [3.0; 8.0; 7.0]
            |> rotate 35.0<deg> [0.0; 1.0; 0.0]
            |> translate [-5.0; 0.0; 3.5]

        let block = 
            centeredCube [5.0; 7.0; 7.0]
            |> translate [-3.9; 0.0; 0.5]
            
        [block; slope]
        |> List.collect id
        |> difference

    let jointBolts =
        let jbl =
            jointBlock
            |> jointBoltLeftWithBlock
            |> rotate 90.0<deg> [0.0; 0.0; 1.0]
            |> jointBlockTl
            |> translate [(mount_width / 2.0) - 1.5; 1.6; 0.0]

        [ key_place 0.0 0 jbl
        ; key_place 1.0 0 jbl
        ; key_place 2.0 0 jbl
        ; key_place 3.0 0 jbl
        ; key_place 4.0 0 jbl
        ; key_place 5.0 0 jbl
        ] |> List.collect id

    let bla x1 y1 dx dy block = 
        let p1 = key_place x1 y1
        p1 (translate (wall_locate3 dx dy) block)

    let tempBlock = 
        jointBlock
        |> bla (float(lastcol)) -1 0.0 0.0


    [ [for x in 0 .. ncols - 1 do key_wall_brace (float(x)) -1 0.0 1.0 web_post_bl (float(x)) -1 0.0 1.0 web_post_br] |> List.collect id
    ; [for x in 1 .. ncols - 1 do key_wall_brace (float(x)) -1 0.0 1.0 web_post_bl (float(x - 1)) -1 0.0 1.0 web_post_br] |> List.collect id
    ; jointBolts
    ] |> List.collect id

let rightWall =
    let jointBlock =
        let slope =
            centeredCube [6.0; 8.0; 8.0]
            |> rotate 20.0<deg> [0.0; 1.0; 0.0]
            |> translate [-6.0; 0.0; 0.5]

        let block = 
            centeredCube [5.0; 7.0; 7.0]
            |> translate [-3.9; 0.0; 0.5]
            
        [block; slope]
        |> List.collect id
        |> difference

    let jointBolts =
        let jbl =
            jointBlock
            |> jointBoltLeftWithBlock
            |> jointBlockTr
            |> translate [0.1; 0.0; 0.0]

        [ key_place 5.0 1 jbl
        ; key_place 5.0 2 jbl
        ; key_place 5.0 3 jbl
        ] |> List.collect id

    [ [ for y in 0 .. lastrow - 1  do key_wall_brace (float(lastcol)) y 1.0 0.0 web_post_tr (float(lastcol)) y 1.0 0.0 web_post_br ] |> List.collect id
    ; [ for y in 0 .. lastrow - 1 do key_wall_brace (float(lastcol)) (y - 1) 1.0 0.0 web_post_br (float(lastcol)) y 1.0 0.0 web_post_tr] |> List.collect id
    ; key_wall_brace (float(lastcol)) -1 0.0 1.0 web_post_br (float(lastcol)) -1 1.0 0.0 web_post_br
    ; key_wall_brace (float(lastcol)) lastrow 0.0 -1.0 web_post_tr (float(lastcol)) lastrow 1.0 0.0 web_post_tr
    ; key_wall_brace (float(lastcol)) cornerrow 1.0 0.0 web_post_br (float(lastcol)) lastrow 1.0 0.0 web_post_tr
    ; jointBolts
    ] |> List.collect id


let frontWall =
    let jointBlock =
        let slope =
            centeredCube [3.0; 8.0; 9.0]
            |> rotate 20.0<deg> [0.0; 1.0; 0.0]
            |> translate [-6.0; 0.0; 1.5]

        let block = 
            centeredCube [5.0; 7.0; 7.0]
            |> translate [-3.9; 0.0; 0.5]
            
        [block; slope]
        |> List.collect id
        |> difference

    let jointBolts =
        let jbl block y =
            block
            |> jointBoltLeftWithBlock
            |> rotate -90.0<deg> [0.0; 0.0; 1.0]
            |> jointBlockTr
            |> translate [(mount_width / -2.0) + 1.5; y; 0.0]

        [ key_place 4.0 4 (jbl jointBlock -1.2)
        ; key_place 5.0 4 (jbl jointBlock -1.2)
        ; key_place 3.0 5 (jbl jointBlock -1.2)
        ; key_place 2.0 5 (jbl jointBlock -1.3)
        ] |> List.collect id

    [ [for x in 4 .. ncols - 1 do key_wall_brace (float(x)) lastrow 0.0 -1.0 web_post_tl (float(x)) lastrow 0.0 -1.0 web_post_tr] |> List.collect id
    ; [for x in 5 .. ncols - 1 do key_wall_brace (float(x)) lastrow 0.0 -1.0 web_post_tl (float(x - 1)) lastrow 0.0 -1.0 web_post_tr] |> List.collect id
    ; key_wall_brace 2.0 (lastrow + 1) 1.0 -1.0 web_post_tl 2.0 (lastrow + 1) 0.0 -1.0 web_post_tr
    ; key_wall_brace 2.0 (lastrow + 1) 0.0 -1.0 web_post_tr 3.0 (lastrow + 1) 0.0 -1.0 web_post_tl
    ; key_wall_brace 3.0 (lastrow + 1) 0.0 -1.0 web_post_tl 3.0 (lastrow + 1) 0.5 -1.0 web_post_tr
    ; key_wall_brace 3.0 (lastrow + 1) 0.5 -1.0 web_post_tr 4.0 lastrow 1.0 -1.0 web_post_tl
    ; jointBolts
    ] |> List.collect id

let leftWall =
    let jointBlock =
        let slope =
            centeredCube [3.0; 8.0; 8.0]
            |> rotate 55.0<deg> [0.0; 1.0; 0.0]
            |> translate [-6.0; 0.0; 4.0]

        let block = 
            centeredCube [7.0; 7.0; 7.0]
            |> translate [-4.9; 0.0; 0.5]
            
        [block; slope]
        |> List.collect id
        |> difference

    let jointBolts =
        let jbl =
            jointBlock
            |> jointBoltRightWithBlock
            |> jointBlockTl
            |> translate [-0.1; 0.0; 0.0]

        [ key_place 0.0 1 jbl
        ; key_place 0.0 2 jbl
        ; key_place 0.0 3 jbl
        ] |> List.collect id

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
        [ for y in 0 .. lastrow do
            [ wall_brace (left_key_place (y - 1) -1.0) -1.0 0.0 web_post (left_key_place y 1.0) -1.0 0.0 web_post
            ; inner y (y - 1)
            ] |> List.collect id |> union
        ] |> List.collect id

    [ one
    ; two
    ; wall_brace (key_place 0.0 -1) 0.0 1.0 web_post_bl (left_key_place -1 -1.0) -1.0 0.0 web_post
    ; jointBolts
    ] |> List.collect id

let thumbWall =
    [ wall_brace thumb_mr_place 0.0 -1.0 web_post_br thumb_tr_place -1.5 -1.5 thumb_post_br
    ; wall_brace thumb_mr_place 0.0 -1.0 web_post_br thumb_mr_place 0.0 -1.0 web_post_bl
    ; wall_brace thumb_br_place 0.0 -1.0 web_post_br thumb_br_place 0.0 -1.0 web_post_bl
    ; wall_brace thumb_ml_place -0.3 1.0 web_post_tr thumb_ml_place 0.0 1.0 web_post_tl
    ; wall_brace thumb_bl_place 0.0 1.0 web_post_tr thumb_bl_place 0.0 1.0 web_post_tl
    ; wall_brace thumb_br_place -1.0 0.0 web_post_tl thumb_br_place -1.0 0.0 web_post_bl
    ; wall_brace thumb_bl_place -1.0 0.0 web_post_tl thumb_bl_place -1.0 0.0 web_post_bl
    //corners
    ; wall_brace thumb_br_place -1.0 0.0 web_post_bl thumb_br_place 0.0 -1.0 web_post_bl
    ; wall_brace thumb_bl_place -1.0 0.0 web_post_tl thumb_bl_place 0.0 1.0 web_post_tl
    ////tweeners
    ; wall_brace thumb_mr_place 0.0 -1.0 web_post_bl thumb_br_place 0.0 -1.0 web_post_br
    ; wall_brace thumb_ml_place 0.0 1.0 web_post_tl thumb_bl_place 0.0 1.0 web_post_tr
    ; wall_brace thumb_bl_place -1.0 0.0 web_post_bl thumb_br_place -1.0 0.0 web_post_tl
    ; wall_brace thumb_tr_place -1.5 -1.5 thumb_post_br (key_place 2.0 (lastrow + 1)) 1.0 -1.0 web_post_tl
    ] |> List.collect id

let thumbConnectionLeft =
    let part1 = 
        [ left_key_place (lastrow) 1.0 (translate (wall_locate2 -1.0 0.0) web_post)
        ; left_key_place (lastrow) 1.0 (translate (wall_locate3 -1.0 0.0) web_post)
        ; thumb_ml_place (translate (wall_locate2 -0.3 1.0) web_post_tr)
        ; thumb_ml_place (translate (wall_locate3 -0.3 1.0) web_post_tr)
        ] |> List.collect id|> bottom_hull

    let part2 =
        [ left_key_place (lastrow) 1.0 (translate (wall_locate2 -1.0 0.0) web_post)
        ; left_key_place (lastrow) 1.0 (translate (wall_locate3 -1.0 0.0) web_post)
        ; thumb_ml_place (translate (wall_locate2 -0.3 1.0) web_post_tr)
        ; thumb_ml_place (translate (wall_locate3 -0.3 1.0) web_post_tr)
        ; thumb_tl_place (thumb_post_tl)
        ] |> List.collect id |> hull

    let part3 = 
        [ left_key_place lastrow 1.0 web_post
        ; left_key_place lastrow 1.0 (translate (wall_locate1 -1.0 0.0) web_post)
        ; left_key_place lastrow 1.0 (translate (wall_locate2 -1.0 0.0) web_post)
        ; left_key_place lastrow 1.0 (translate (wall_locate3 -1.0 0.0) web_post)
        ; thumb_tl_place thumb_post_tl 
        ] |> List.collect id |> hull

    let part4 = 
        [ left_key_place lastrow 1.0 web_post
        ; left_key_place lastrow 1.0 (translate (wall_locate1 -1.0 0.0) web_post)
        ; key_place 0.0 lastrow web_post_tl
        ; key_place 0.0 lastrow (translate (wall_locate1 -1.0 0.0) web_post_tl)
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