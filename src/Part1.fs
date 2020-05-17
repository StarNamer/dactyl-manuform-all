module Dactyl.Part1
open Dactyl.Placement
open Dactyl.SingleJoint
open Dactyl.Variables
open OpenSCAD.Fs.Lib.Combinator

let jointBlocksFirst =
    [ key_place 0.0 1 (jointBlockTl jointBlock)
    ; key_place 0.0 2 (jointBlockTl jointBlock)
    ; key_place 0.0 3 (jointBlockTl jointBlock)
    ; key_place 1.0 1 (jointBlockTr jointBlock)
    ; key_place 1.0 2 (jointBlockTr jointBlock)
    ; key_place 1.0 3 (jointBlockTr jointBlock)
    ] 
    |> List.collect id

let firstTwo =
    let fst = keyHoleColumn 0.0 cornerrow
    let snd = keyHoleColumn 1.0 cornerrow
    let conCols = connectionColumn 0.0 cornerrow
    let conFstRow = connectionRow 0.0 (cornerrow - 1)
    let conSndRow = connectionRow 1.0 (cornerrow - 1)
    let conDiag = connectionDiagonal 0.0 (cornerrow - 1)

    [fst; snd; conCols; conFstRow; conSndRow; conDiag; jointBlocksFirst] |> List.collect id |> union


