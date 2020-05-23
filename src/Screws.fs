module Dactyl.Screws

open Dactyl.SinglePlate
open Dactyl.Variables
open Dactyl.Placement
open OpenSCAD.Fs.Lib
open OpenSCAD.Fs.Lib.Operator
open OpenSCAD.Fs.Lib.Combinator
open Dactyl.CaseHelper

let screwInsertShape bottomRadius topRadius height =
    let cylinder = 
        centeredCylinder 
        |> Cylinder.bottomRadius bottomRadius
        |> Cylinder.topRadius topRadius
        |> Cylinder.resize height
        |> Cylinder.toObject

    let sphere =
        Sphere.create()
        |> Sphere.resize topRadius
        |> Sphere.toObject
        |> translate [0.0; 0.0; height / 2.0]

    [cylinder; sphere] |> List.collect id |> union

let screwInsert bottomRadius topRadius height (position : float list) =
    screwInsertShape bottomRadius topRadius height
    |> translate [position.[0]; position.[1]; height / 2.0]

let screwInsertAllShapes bottomRadius topRadius height =
    let si = screwInsert bottomRadius topRadius height
    [ si (List.map2 (+) (left_key_position 0.0 0.0) (wall_locate3 -0.9 0.0))
    ; si (List.map2 (+) (left_key_position lastrow 0.0) (wall_locate3 -0.3 -0.3))
    ; si (key_position 2.0 lastrow (List.map2 (-) (wall_locate2 0.0 -1.0) [-5.0; (mount_height / 2.0) + 7.0; 0.0]))
    ; si (key_position 3.0 0.0 (List.map2 (+) (wall_locate2 0.0 1.0) [0.0; (mount_height / 2.0) + 7.0; 0.0]))
    ; si (key_position lastcol 1.0 (List.map2 (+) (wall_locate2 1.0 0.0) [mount_width / 2.0; 0.0; 0.0]))
    ] |> List.collect id |> union

let screwInsertHeight = 3.8
let screwInsertBottomRadius = (5.31 / 2.0)
let screwInsertTopRadius = (5.1 / 2.0)
let screwInsertHoles = screwInsertAllShapes screwInsertBottomRadius screwInsertTopRadius screwInsertHeight
let screwInsertOuters = screwInsertAllShapes (screwInsertBottomRadius + 1.6) (screwInsertTopRadius + 1.6) (screwInsertHeight + 1.5)
let secrewInsertScrewHoles = screwInsertAllShapes 1.7 1.7 350.0