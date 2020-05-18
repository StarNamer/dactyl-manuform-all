module Dactyl.Part2
open Dactyl.Placement
open Dactyl.SingleJoint
open Dactyl.Variables
open OpenSCAD.Fs.Lib.Combinator
open OpenSCAD.Fs.Lib
open Dactyl.SinglePlate
open OpenSCAD.Fs.Lib.Operator


let secondTwo = 
    let blockLeftRows =
        let slope =
            centeredCube [3.0; 8.0; 7.0]
            |> rotate 45.0<deg> [0.0; 1.0; 0.0]
            |> translate [-4.0; 0.0; 4.5]

        let block = 
            centeredCube [5.0; 7.0; 7.0]
            |> translate [-3.9; 0.0; 0.5]
            
        [block; slope]
        |> List.collect id
        |> difference

    let blockRightRows deg =
        let slope =
            centeredCube [4.0; 8.0; 15.0]
            |> rotate (deg * 1.0<deg>) [0.0; 1.0; 0.0]
            |> translate [-4.0; 0.0; 5.0]

        let block = 
            centeredCube [5.0; 7.0; 7.0]
            |> translate [-3.9; 0.0; 0.5]
            
        [block; slope]
        |> List.collect id
        |> difference

    let jointBolts =
        let jbl =
            blockLeftRows
            |> jointBoltLeftWithBlock
            |> jointBlockTr

        let jbr deg =
            deg
            |> blockRightRows 
            |> jointBoltRightWithBlock
            |> jointBlockTl

        let block y =
            jointBlock
            |> rotate 90.0<deg> [0.0; 0.0; 1.0]
            |> translate [(mount_width / 2.0) - 1.5; y; 0.0]
            
        //Top
        [ key_place 2.0 0 (jointBlockTl (block 1.2))
        ; key_place 3.0 0 (jointBlockTl (block 1.2))
        //bottom
        ; key_place 2.0 5 (jointBlockTl (block -1.1))
        ; key_place 3.0 5 (jointBlockTl (block -1.1))
        //Sides
        ; key_place 1.0 1 jbl
        ; key_place 1.0 2 jbl
        ; key_place 1.0 3 jbl
        ; key_place 4.0 1 (jbr 25.0)
        ; key_place 4.0 2 (jbr 45.0)
        ; key_place 4.0 3 (jbr 65.0)
        ] 
        |> List.collect id

    let fst = keyHoleColumn 2.0 lastrow
    let snd = keyHoleColumn 3.0 lastrow
    let conFstCols = connectionColumn 1.0 cornerrow
    let conSndCols = connectionColumn 2.0 lastrow
    let conThdCols = connectionColumn 3.0 lastrow
    let conFstRow = connectionRow 2.0 (lastrow)
    let conSndRow = connectionRow 3.0 (lastrow)
    let conFstDiag = connectionDiagonal 1.0 (cornerrow - 1)
    let conSndDiag = connectionDiagonal 2.0 (cornerrow)
    let conThdDiag = connectionDiagonal 3.0 (cornerrow)

    [fst; snd
    ; conFstCols; conSndCols; conThdCols
    ; conFstRow; conSndRow 
    ; conFstDiag; conSndDiag; conThdDiag
    ; jointBolts] |> List.collect id |> union


