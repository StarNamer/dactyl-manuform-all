open System
open OpenSCAD.Fs.Lib
open OpenSCAD.Fs.Lib.Operator
open OpenSCAD.Fs.Lib.Combinator
open OpenSCAD.Fs.Lib.Projection
open System.IO
open Dactyl.SinglePlate
open Dactyl.Placement
open Dactyl.Variables
open Dactyl.Connections
open Dactyl.Thumb
open Dactyl.SingleJoint
open Dactyl.Case
open Dactyl.Part1
open Dactyl.Part2
open FSharpx.Collections
open Dactyl.Part3


let model_right =
    let model = 
        [ key_holes
        ; connectors
        ; thumb
        ; thumb_connections
        ; case
        ] |> List.collect id |> union

    let cube =
         centeredCube [350.0; 350.0; 40.0] |> translate [0.0; 0.0; -20.0]

    [model; cube]
    |> List.collect id
    |> difference

[<EntryPoint>]
let main argv =

    use sw = new StreamWriter("../things/firstTwo.scad")
    [firstTwo] |> List.collect id |> print sw 

    use sw = new StreamWriter("../things/secondTwo.scad")
    [secondTwo] |> List.collect id |> print sw 

    use sw = new StreamWriter("../things/part4.scad")
    [part4] |> List.collect id |> print sw 

    use sw = new StreamWriter("../things/parts.scad")
    [firstTwo; secondTwo; thirdTwo; part4] |> List.collect id |> print sw 
    0 // return an integer exit code
