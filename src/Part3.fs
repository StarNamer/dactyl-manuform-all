module Dactyl.Part3
open Dactyl.Placement
open Dactyl.SingleJoint
open Dactyl.Variables
open OpenSCAD.Fs.Lib.Combinator
open OpenSCAD.Fs.Lib
open Dactyl.Case
open Dactyl.SinglePlate
open OpenSCAD.Fs.Lib.Operator

let jointBlocksThird =
    [ key_place 4.0 1 (jointBlockTl jointBlock)
    ; key_place 4.0 2 (jointBlockTl jointBlock)
    ; key_place 4.0 3 (jointBlockTl jointBlock)
    ; key_place 5.0 1 (jointBlockTr jointBlock)
    ; key_place 5.0 2 (jointBlockTr jointBlock)
    ; key_place 5.0 3 (jointBlockTr jointBlock)
    ] 
    |> List.collect id

let thirdTwo = 
    let fst = keyHoleColumn 4.0 cornerrow
    let snd = keyHoleColumn 5.0 cornerrow
    let conCols = connectionColumn 4.0 cornerrow
    let conFstRow = connectionRow 4.0 (cornerrow - 1)
    let conSndRow = connectionRow 5.0 (cornerrow - 1)
    let conDiag = connectionDiagonal 4.0 (cornerrow - 1)


    [fst; snd
    ; conCols
    ; conFstRow; conSndRow
    ; conDiag
    ; jointBlocksThird ] |> List.collect id |> union







let part4 =
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
        ] |> List.collect id

    [ [for x in 0 .. ncols - 1 do key_wall_brace (float(x)) -1 0.0 1.0 web_post_bl (float(x)) -1 0.0 1.0 web_post_br] |> List.collect id
    ; [for x in 1 .. ncols - 1 do key_wall_brace (float(x)) -1 0.0 1.0 web_post_bl (float(x - 1)) -1 0.0 1.0 web_post_br] |> List.collect id
    ; jointBolts
    ] |> List.collect id