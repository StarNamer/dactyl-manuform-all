module Dactyl.Part3
open Dactyl.Placement
open Dactyl.SingleJoint
open Dactyl.Variables
open OpenSCAD.Fs.Lib.Combinator

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
