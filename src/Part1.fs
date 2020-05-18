module Dactyl.Part1
open Dactyl.Placement
open Dactyl.SingleJoint
open Dactyl.Variables
open OpenSCAD.Fs.Lib.Combinator
open OpenSCAD.Fs.Lib
open OpenSCAD.Fs.Lib.Operator
open Dactyl.SinglePlate


let firstTwo =
    let block y =
        jointBlock
        |> rotate 90.0<deg> [0.0; 0.0; 1.0]
        |> translate [(mount_width / 2.0) - 1.5; y; 0.0]

    let jointBlocks =
        //Top
        [ key_place 0.0 0 (jointBlockTl (block 1.2))
        ; key_place 1.0 0 (jointBlockTl (block 1.2))
        //Bottom
        ; key_place 0.0 4 (jointBlockTl (block -1.1))
        ; key_place 1.0 4 (jointBlockTl (block -1.1))
        //Sides
        ; key_place 0.0 1 (jointBlockTl jointBlock)
        ; key_place 0.0 2 (jointBlockTl jointBlock)
        ; key_place 0.0 3 (jointBlockTl jointBlock)
        ; key_place 1.0 1 (jointBlockTr jointBlock)
        ; key_place 1.0 2 (jointBlockTr jointBlock)
        ; key_place 1.0 3 (jointBlockTr jointBlock)
        ] 
        |> List.collect id

    let fst = keyHoleColumn 0.0 cornerrow
    let snd = keyHoleColumn 1.0 cornerrow
    let conCols = connectionColumn 0.0 cornerrow
    let conFstRow = connectionRow 0.0 (cornerrow)
    let conSndRow = connectionRow 1.0 (cornerrow)
    let conDiag = connectionDiagonal 0.0 (cornerrow)

    [fst; snd
    ; conCols
    ; conFstRow; conSndRow
    ; conDiag
    ; jointBlocks] |> List.collect id |> union


