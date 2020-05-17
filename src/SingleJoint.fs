module Dactyl.SingleJoint

open OpenSCAD.Fs.Lib
open OpenSCAD.Fs.Lib.Operator
open OpenSCAD.Fs.Lib.Combinator
open FSharpx.Collections
open Dactyl.Variables
open Dactyl.SinglePlate


let block =
    centeredCube [3.0; 7.0; 8.0]
    |> translate [0.0; 0.0; 1.0]

let cylinder =
    centeredCylinder
    |> Cylinder.resize 3.1
    |> Cylinder.radius 2.0
    |> Cylinder.fragmentNumber 30.0
    |> Cylinder.toObject
    |> rotate 90.0<deg> [0.0; 1.0; 0.0]

let bevel =
    centeredCylinder
    |> Cylinder.resize 1.5
    |> Cylinder.bottomRadius 2.5
    |> Cylinder.topRadius 1.25
    |> Cylinder.fragmentNumber 30.0
    |> Cylinder.toObject
    |> rotate 90.0<deg> [0.0; 1.0; 0.0]
    |> translate [-1.0; 0.0; 0.0]

let jointBlock =
    let mirroredBevel =
        bevel
        |> mirror [1.0; 0.0; 0.0]
    [block; cylinder; bevel; mirroredBevel]
    |> List.collect id
    |> difference

let shaft =
    centeredCylinder
    |> Cylinder.resize 3.0
    |> Cylinder.radius 1.95
    |> Cylinder.fragmentNumber 30.0
    |> Cylinder.toObject
    |> rotate 90.0<deg> [0.0; 1.0; 0.0]

let boltTip = 
    centeredCylinder
    |> Cylinder.resize 1.0
    |> Cylinder.bottomRadius 1.95
    |> Cylinder.topRadius 1.8
    |> Cylinder.fragmentNumber 30.0
    |> Cylinder.toObject
    |> rotate 90.0<deg> [0.0; 1.0; 0.0]
    |> translate [2.0; 0.0; 0.0]

let jointBolt =
    [shaft; boltTip]
    |> List.collect id
    |> union

let jointBoltRightWithBlock block =
    let b = 
        block
        |> translate [-3.0; 0.0; 0.0]

    [jointBolt; block]
    |> List.collect id
    |> union

let jointBoltLeftWithBlock block = 
    block
    |> jointBoltRightWithBlock
    |> mirror [1.0; 0.0; 0.0]