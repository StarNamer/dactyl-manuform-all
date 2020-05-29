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

    let firstJointXY = key_position 0.0 0.0 (wall_locate1 0.6 4.9)
    let lastJointXY = key_position 5.0 0.0 (wall_locate1 0.6 4.9)

    let jointBolts =
        let jbt blockPos pos =
            let slope =
                centeredCube [3.0; 8.0; 8.0]
                |> rotate 90.0<deg> [0.0; 0.0; 1.0]
                |> blockPos
                |> translate pos 
                |> translate [0.0; 4.7; 0.0]

            let block = 
                SingleJoint.jointBlock
                |> rotate -90.0<deg> [1.0; 0.0; 0.0]
                |> blockPos
                |> translate pos

            [block; slope]
            |> List.collect id
            |> difference

        let jbl =
            jointBlock
            |> jointBoltLeftWithBlock
            |> rotate 90.0<deg> [0.0; 0.0; 1.0]
            |> jointBlockTl
            |> translate [(mount_width / 2.0) - 1.5; 1.6; 0.0]

        [ key_place 0.0 0.0 jbl
        ; key_place 1.0 0.0 jbl
        ; key_place 2.0 0.0 jbl
        ; key_place 3.0 0.0 jbl
        ; key_place 4.0 0.0 jbl
        ; key_place 5.0 0.0 jbl
        ; jbt jointBlockTl [firstJointXY.[0]; firstJointXY.[1]; 12.0]
        ; jbt jointBlockTl [firstJointXY.[0]; firstJointXY.[1]; 30.0]
        ; jbt jointBlockTr [lastJointXY.[0] - 1.5; lastJointXY.[1]; 14.0]
        ] |> List.collect id

    [ [ key_wall_brace (float(0.0)) -1.0 0.0 1.0 web_post_bl (float(0.0)) -1.0 0.0 1.0 web_post_br
      ; wall_brace (key_place 0.0 -1.0) 0.0 1.0 web_post_bl (left_key_place -1.0 -1.0) -1.0 0.0 web_post
    ] |> List.collect id |> difference

    ; [for x in 1.0 .. lastcol - 1.0 do key_wall_brace (float(x)) -1.0 0.0 1.0 web_post_bl (float(x)) -1.0 0.0 1.0 web_post_br] |> List.collect id

    ; [ key_wall_brace lastcol -1.0 0.0 1.0 web_post_bl lastcol -1.0 0.0 1.0 web_post_br
      ; key_wall_brace (float(lastcol)) -1.0 0.0 1.0 web_post_br (float(lastcol)) -1.0 1.0 0.0 web_post_br
    ] |> List.collect id |> difference

    ; [for x in 1.0 .. lastcol do key_wall_brace (float(x)) -1.0 0.0 1.0 web_post_bl (float(x - 1.0)) -1.0 0.0 1.0 web_post_br] |> List.collect id
    ; jointBolts
    ] |> List.collect id |> union

let rightWall =
    let jointBolts =
        let firstJointXY = key_position 5.0 0.0 (wall_locate1 0.6 4.9)
        let lastJointXY = key_position 5.0 lastrow (wall_locate1 -0.7 -6.0)

        let jbt blockPos pos =
            centeredCube [5.0; 7.0; 6.0]
            |> translate [-3.9; 0.0; 0.0]
            |> jointBoltLeftWithBlock
            |> rotate -90.0<deg> [1.0; 0.0; 0.0]
            |> blockPos
            |> translate pos

        let jbb blockPos pos =
            let slope =
                centeredCube [12.0; 12.0; 5.0]
                |> rotate 120.0<deg> [0.0; 1.0; 0.0]
                |> rotate -45.0<deg> [1.0; 0.0; 0.0]
                |> translate [-5.0; 2.0; 4.0]

            let block = 
                centeredCube [5.0; 7.0; 7.0]
                |> translate [-3.9; 0.0; 0.5]
                
            [block; slope]
            |> List.collect id
            |> difference
            |> jointBoltLeftWithBlock
            |> rotate 90.0<deg> [1.0; 0.0; 0.0]
            |> blockPos
            |> translate pos

        let jbl =
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
            |> jointBoltLeftWithBlock
            |> jointBlockTr
            |> translate [0.1; 0.0; 0.0]

        [ key_place 5.0 1.0 jbl
        ; key_place 5.0 2.0 jbl
        ; key_place 5.0 3.0 jbl
        ; jbt jointBlockTr [firstJointXY.[0] - 1.2; firstJointXY.[1]; 14.0]
        ; jbb jointBlockTr [lastJointXY.[0]; lastJointXY.[1] + 0.5; 6.7]
        ] |> List.collect id

    [ [ for y in 0.0 .. lastrow - 1.0  do key_wall_brace (float(lastcol)) y 1.0 0.0 web_post_tr (float(lastcol)) y 1.0 0.0 web_post_br ] |> List.collect id
    ; [ for y in 0.0 .. lastrow - 1.0 do key_wall_brace (float(lastcol)) (y - 1.0) 1.0 0.0 web_post_br (float(lastcol)) y 1.0 0.0 web_post_tr] |> List.collect id
    ; key_wall_brace (float(lastcol)) -1.0 0.0 1.0 web_post_br (float(lastcol)) -1.0 1.0 0.0 web_post_br
    ; key_wall_brace (float(lastcol)) cornerrow 1.0 0.0 web_post_br (float(lastcol)) lastrow 1.0 0.0 web_post_tr
    ; key_wall_brace (float(lastcol)) lastrow 0.0 -1.0 web_post_tr (float(lastcol)) lastrow 1.0 0.0 web_post_tr
    ; jointBolts
    ] |> List.collect id |> union

let frontWall =

    let lastJointXY = key_position 5.0 lastrow (wall_locate1 -0.7 -6.0)

    let jointBolts =
        let jbw = 
            jointBlock
            |> rotate 90.0<deg> [1.0; 0.0; 0.0]
            |> jointBlockTr
            |> translate [lastJointXY.[0] - 0.5; lastJointXY.[1] + 0.5; 6.7]

        let jbl y =
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
            |> jointBoltLeftWithBlock
            |> rotate -90.0<deg> [0.0; 0.0; 1.0]
            |> jointBlockTr
            |> translate [(mount_width / -2.0) + 1.5; y; 0.0]

        [ key_place 4.0 4.0 (jbl -1.2)
        ; key_place 5.0 4.0 (jbl -1.2)
        ; key_place 3.0 5.0 (jbl -1.2)
        ; key_place 2.0 5.0 (jbl -1.3)
        ; jbw
        ] |> List.collect id

    [ [for x in 4.0 .. ncols - 2.0 do key_wall_brace (float(x)) lastrow 0.0 -1.0 web_post_tl (float(x)) lastrow 0.0 -1.0 web_post_tr] |> List.collect id
    ; [for x in 5.0 .. ncols - 1.0 do key_wall_brace (float(x)) lastrow 0.0 -1.0 web_post_tl (float(x - 1.0)) lastrow 0.0 -1.0 web_post_tr] |> List.collect id
    ; key_wall_brace 2.0 (lastrow + 1.0) 1.0 -1.0 web_post_tl 2.0 (lastrow + 1.0) 0.0 -1.0 web_post_tr
    ; key_wall_brace 2.0 (lastrow + 1.0) 0.0 -1.0 web_post_tr 3.0 (lastrow + 1.0) 0.0 -1.0 web_post_tl
    ; key_wall_brace 3.0 (lastrow + 1.0) 0.0 -1.0 web_post_tl 3.0 (lastrow + 1.0) 0.5 -1.0 web_post_tr
    ; key_wall_brace 3.0 (lastrow + 1.0) 0.5 -1.0 web_post_tr 4.0 lastrow 1.0 -1.0 web_post_tl
    ; [ key_wall_brace (ncols - 1.0) lastrow 0.0 -1.0 web_post_tl (ncols - 1.0) lastrow 0.0 -1.0 web_post_tr
      ; key_wall_brace lastcol lastrow 0.0 -1.0 web_post_tr lastcol lastrow 1.0 0.0 web_post_tr 
    ] |> List.collect id |> difference
    ; jointBolts
    ] |> List.collect id |> union

let leftWall =
    let jointBlock =
        let slope =
            centeredCube [3.0; 8.0; 8.5]
            |> rotate 50.0<deg> [0.0; 1.0; 0.0]
            |> translate [-6.2; 0.0; 4.0]

        let block = 
            centeredCube [7.0; 7.0; 7.0]
            |> translate [-4.9; 0.0; 0.5]
            
        [block; slope]
        |> List.collect id
        |> difference

    let firstJointXY = key_position 0.0 0.0 (wall_locate1 0.4 4.9)
    let lastJointXY = key_position 0.0 lastrow (wall_locate3 -1.0 0.9)

    let jointBolts =
        let jbt height =
            jointBlock
            |> jointBoltRightWithBlock
            |> rotate -90.0<deg> [1.0; 0.0; 0.0]
            |> jointBlockTl
            |> translate [firstJointXY.[0]; firstJointXY.[1]; height]

        let jbw = 
            SingleJoint.jointBlock
            |> rotate 90.0<deg> [1.0; 0.0; 0.0]
            |> rotate -90.0<deg> [0.0; 0.0; 1.0]
            |> jointBlockTl
            |> translate [lastJointXY.[0]; lastJointXY.[1]; 18.0]

        let jbl =
            jointBlock
            |> jointBoltRightWithBlock
            |> jointBlockTl
            |> translate [-0.1; 0.0; 0.0]

        [ key_place 0.0 1.0 jbl
        ; key_place 0.0 2.0 jbl
        ; key_place 0.0 3.0 jbl
        ; jbt 12.0
        ; jbt 30.0
        ; jbw
        ] |> List.collect id

    let inner y1 y2 =
        [ key_place 0.0 y1 web_post_tl
        ; key_place 0.0 y2 web_post_bl
        ; left_key_place y1 1.0 web_post
        ; left_key_place y2 -1.0 web_post
        ] |> List.collect id |> hull

    let one = 
        [ for y in 0.0 .. lastrow - 1.0 do 
           [ wall_brace (left_key_place y 1.0) -1.0 0.0 web_post (left_key_place y -1.0) -1.0 0.0 web_post
           ; inner y y] |> List.collect id |> union
           ] |> List.collect id

    let two =
        [ for y in 0.0 .. lastrow do
            [ wall_brace (left_key_place (y - 1.0) -1.0) -1.0 0.0 web_post (left_key_place y 1.0) -1.0 0.0 web_post
            ; inner y (y - 1.0)
            ] |> List.collect id |> union
        ] |> List.collect id

    [ one
    ; two
    ; wall_brace (key_place 0.0 -1.0) 0.0 1.0 web_post_bl (left_key_place -1.0 -1.0) -1.0 0.0 web_post
    ; jointBolts
    ] |> List.collect id |> union

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
    ; wall_brace thumb_tr_place -1.5 -1.5 thumb_post_br (key_place 2.0 (lastrow + 1.0)) 1.0 -1.0 web_post_tl
    ] |> List.collect id

let thumbConnectionLeft =
    let jointBlock =
        let slope =
            centeredCube [5.0; 8.0; 10.0]
            |> rotate 60.0<deg> [0.0; 1.0; 0.0]
            |> translate [-8.5; 0.0; 3.0]

        let block = 
            centeredCube [9.0; 7.0; 7.0]
            |> translate [-5.9; 0.0; 0.5]
            
        [block; slope]
        |> List.collect id
        |> difference

    let topXY = key_position 0.0 lastrow (wall_locate3 -1.0 0.9)

    let jointBolts =
        let jbl =
            jointBlock
            |> jointBoltRightWithBlock
            |> rotate 90.0<deg> [1.0; 0.0; 0.0]
            |> rotate 90.0<deg> [0.0; 0.0; 1.0]
            |> rotate 180.0<deg> [0.0; 1.0; 0.0]
            |> jointBlockTl
            |> translate [topXY.[0]; topXY.[1] - 0.3; 18.0]

        [ jbl ] |> List.collect id

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

    [part1; part2; part3; part4; part5; jointBolts] |> List.collect id