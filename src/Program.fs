open System
open OpenSCAD.Fs.Lib
open OpenSCAD.Fs.Lib.Operator
open OpenSCAD.Fs.Lib.Combinator
open OpenSCAD.Fs.Lib.Projection
open System.IO
open Dactyl.SinglePlate
open Dactyl.Connections
open Dactyl.Screws
open Dactyl
open FSharpx.Collections
open Dactyl.Original


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
    use sw = new StreamWriter("../things/right.scad")
    [model_right] |> List.collect id |> print sw 

    use sw = new StreamWriter("../things/firstTwo.scad")
    [KeyHoles.part1] |> List.collect id |> print sw 

    use sw = new StreamWriter("../things/secondTwo.scad")
    [KeyHoles.part2] |> List.collect id |> print sw 

    use sw = new StreamWriter("../things/thirdTwo.scad")
    [KeyHoles.part3] |> List.collect id |> print sw 

    use sw = new StreamWriter("../things/topWall.scad")
    [Case.topWall] |> List.collect id |> print sw 

    use sw = new StreamWriter("../things/rightWall.scad")
    [Case.rightWall] |> List.collect id |> print sw 

    use sw = new StreamWriter("../things/leftWall.scad")
    [Case.leftWall] |> List.collect id |> print sw 

    use sw = new StreamWriter("../things/teensy.scad")
    [Teensy.holder] |> List.collect id |> print sw 

    let cube =
         centeredCube [350.0; 350.0; 40.0] |> translate [0.0; 0.0; -20.0]

    use sw = new StreamWriter("../things/parts.scad")
    let walls = 
        [ Case.topWall
        ; Case.rightWall
        ; Case.frontWall
        ; Case.leftWall
        ; Case.thumbWall
        ; Case.thumbConnectionLeft
        ; Teensy.holder
        ; screwInsertOuters
        ] |> List.collect id |> union



    let all = 
        [ KeyHoles.part1
        ; KeyHoles.part2
        ; KeyHoles.part3
        ;   [ walls
            ; screwInsertHoles
        ] |> List.collect id |> difference
        ; KeyHoles.thumb
        ] |> List.collect id |> union

    [all; cube] |> List.collect id |> difference |> print sw 
    0 // return an integer exit code
