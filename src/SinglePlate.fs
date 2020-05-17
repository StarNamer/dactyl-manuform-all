module Dactyl.SinglePlate

open OpenSCAD.Fs.Lib
open OpenSCAD.Fs.Lib.Operator
open OpenSCAD.Fs.Lib.Combinator
open FSharpx.Collections
open Dactyl.Variables

let keyswitch_height = 14.3
let keyswitch_width = 14.3

let mount_width = keyswitch_width + 3.0
let mount_height = keyswitch_height + 3.0

let cherry_bezel_width = 1.5

let centeredCube vec = Cube.create() |> Cube.center |> Cube.resizeByVector vec |> Cube.toObject
let centeredCylinder = Cylinder.create() |> Cylinder.center

let single_plate =
    let top_wall =
        let translation = [0.0; (cherry_bezel_width / 2.0) + (keyswitch_height / 2.0); plate_thickness / 2.0]
        centeredCube [(3.0 + keyswitch_width); cherry_bezel_width; plate_thickness]
        |> translate translation

    let left_wall =
        let translation = [(cherry_bezel_width / 2.0) + (keyswitch_width / 2.0); 0.0; plate_thickness / 2.0]
        centeredCube <| [1.5; 3.0 + keyswitch_height; plate_thickness]
        |> translate translation

    let side_nub =
        let cylinder = 
            centeredCylinder
            |> Cylinder.resize 2.75
            |> Cylinder.radius 1.0
            |> Cylinder.fragmentNumber 30.0
            |> Cylinder.toObject
            |> rotate 90.0<deg> [1.0; 0.0; 0.0]
            |> translate [keyswitch_width / 2.0; 0.0; 1.0]

        let cube = 
            let translation = [(cherry_bezel_width / 2.0) + (keyswitch_width / 2.0); 0.0; plate_thickness / 2.0]
            centeredCube [cherry_bezel_width; 2.75; plate_thickness]
            |> translate translation

        [cylinder; cube] |> List.collect id |> hull

    let half = [top_wall; left_wall; side_nub] |> List.collect id |> union 

    let mirrored_half = 
        half
        |> mirror [1.0; 0.0; 0.0]
        |> mirror [0.0; 1.0; 0.0]

    [half; mirrored_half] |> List.collect id |> union 