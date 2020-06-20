module Dactyl.PCB
open Dactyl.Placement
open Dactyl.Variables
open Dactyl.CaseHelper
open Dactyl.SinglePlate
open OpenSCAD.Fs.Lib.Operator
open OpenSCAD.Fs.Lib.Combinator
open OpenSCAD.Fs.Lib
open Dactyl.ThumbHelper

let mount = 
    let centerMount =
        Cylinder.create()
        |> Cylinder.center
        |> Cylinder.radius 1.99
        |> Cylinder.resize 2.0
        |> Cylinder.fragmentNumber 40.0
        |> Cylinder.toObject

    let mountingPin =
        Cylinder.create()
        |> Cylinder.center
        |> Cylinder.radius 0.9
        |> Cylinder.resize 2.0
        |> Cylinder.fragmentNumber 40.0
        |> Cylinder.toObject

    let mountingPins =
        let pin1 = mountingPin |> translate [5.08; 0.0; 0.0]
        let pin2 = mountingPin |> translate [-5.08; 0.0; 0.0]

        [pin1; pin2] |> List.collect id |> union
    
    [centerMount; mountingPins] |> List.collect id |> union

let socket =
    let socketHole =
        Cylinder.create()
        |> Cylinder.center
        |> Cylinder.radius 1.475
        |> Cylinder.resize 2.0
        |> Cylinder.fragmentNumber 40.0
        |> Cylinder.toObject

    let hole1 = socketHole |> translate [-3.81; 2.54; 0.0]
    let hole2 = socketHole |> translate [2.54; 5.08; 0.0]

    [hole1; hole2] |> List.collect id |> union

let PCBDummy =
    let plate = centeredCube [16.0; 18.0; 1.5]

    let cutout = centeredCube [7.0; 4.0; 2.0]

    let c1 = cutout |> translate [8.0; 9.0; 0.0]
    let c2 = cutout |> translate [-8.0; 9.0; 0.0]
    let c3 = cutout |> translate [8.0; -9.0; 0.0]
    let c4 = cutout |> translate [-8.0; -9.0; 0.0]
    
    [plate; mount; socket; c1; c2; c3; c4] |> List.collect id |> difference

let PCBs =
    let pcb column row = 
        PCBDummy 
        |> translate [0.0; 0.0; -1.0]
        |> color 1.0 0.0 0.0 1.0
        |> key_place column row
    
    let fstTwo = 
        [for col in 0.0 .. 1.0 do
            [ for row in 0.0 .. nrows - 2.0 do pcb col row ] |> List.collect id ]
        |> List.collect id

    let sndTwo = 
        [for col in 2.0 .. 3.0 do
            [ for row in 0.0 .. nrows - 1.0 do pcb col row ] |> List.collect id ]
        |> List.collect id

    let thdTwo = 
        [for col in 4.0 .. 5.0 do
            [ for row in 0.0 .. nrows - 2.0 do pcb col row ] |> List.collect id ]
        |> List.collect id

    let thumbPcb =
        PCBDummy
        |> translate [0.0; 0.0; -1.0]
        |> color 1.0 0.0 0.0 1.0
        |> rotate -90.0<deg> [0.0; 0.0; 1.0]

    let thumb =
        [thumb_1x_layout thumbPcb
        ; thumb_15x_layout thumbPcb
        ] |> List.collect id

    [fstTwo; sndTwo; thdTwo; thumb] |> List.collect id