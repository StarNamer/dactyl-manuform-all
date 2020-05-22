module Dactyl.Teensy
open Dactyl.Placement
open Dactyl.Variables
open Dactyl.CaseHelper
open Dactyl.SinglePlate
open OpenSCAD.Fs.Lib.Operator
open OpenSCAD.Fs.Lib.Combinator

let teensyWidth = 20.0
let teensyHeight = 12.0
let teensyLength = 33.0
let teensyPcbThickness = 2.0

let teensyHolderWidth = teensyPcbThickness + 7.0
let teensyHolderHeight = teensyWidth + 6.0
let teensyHolderTopLength = 18.0
let teensyOffsetHeight = 18.0

let teensyTopXY = key_position 0.0 (centerrow - 1) (wall_locate3 -1.0 0.0)
let teensyBottomXY = key_position 0.0 (centerrow + 1) (wall_locate3 -1.0 0.0)
let teensyHolderLength = teensyTopXY.[1] - teensyBottomXY.[1]
let teensyHolderOffset = teensyHolderLength / -2.0
let teensyHolderTopOffset = (teensyHolderTopLength / 2.0) - teensyHolderLength

let holder =
    let shape = 
        let s1 = 
            centeredCube [3.0; teensyHolderLength; teensyWidth + 6.0]
            |> translate [1.5; teensyHolderOffset; 0.0]

        let s2 = 
            centeredCube [teensyPcbThickness; teensyHolderLength; 3.0]
            |> translate [(teensyPcbThickness / 2.0) + 3.0; teensyHolderOffset; -1.5 - (teensyWidth / 2.0)]

        let s3 =
            centeredCube [4.0; teensyHolderLength; 4.0]
            |> translate [teensyPcbThickness + 5.0; teensyHolderOffset; -1.0 - (teensyWidth / 2.0)]

        let s4 = 
            centeredCube [teensyPcbThickness; teensyHolderTopLength; 3.0]
            |> translate [(teensyPcbThickness / 2.0) + 3.0; teensyHolderTopOffset; (teensyWidth / 2.0) + 1.5]

        let s5 =
            centeredCube [4.0; teensyHolderTopLength; 4.0]
            |> translate [teensyPcbThickness + 5.0; teensyHolderTopOffset; (teensyWidth / 2.0) + 1.0]

        [s1; s2; s3; s4; s5] |> List.collect id |> union
    
    shape
    |> translate [-teensyHolderWidth; 0.0; 0.0]
    |> translate [-1.4; 0.0; 0.0]
    |> translate [teensyTopXY.[0]; teensyTopXY.[1] - 1.0; (teensyWidth + 6.0) / 2.0]