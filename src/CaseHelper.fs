module Dactyl.CaseHelper

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
open FSharpx.Collections

let bottom height p =
    p
    |> project
    |> Extrusion.linear height 0.0 0
    |> translate [0.0; 0.0; (height / 2.0) - 10.0]

let bottom_hull p =
    [p; bottom 0.001 p]
    |> List.collect id
    |> hull

let left_wall_x_offset = 10.0
let left_wall_z_offset = 3.0

let left_key_position row direction =
    let v = key_position 0.0 row [mount_width * -0.5;direction * mount_height * 0.5; 0.0]
    List.map2 (fun a b -> a - b) v [left_wall_x_offset; 0.0; left_wall_z_offset]

let left_key_place row direction shape =
    translate (left_key_position row direction) shape

let wall_locate1 dx dy = [dx * wall_thickness; dy * wall_thickness; -1.0]
let wall_locate2 dx dy = [dx * wall_xy_offset; dy * wall_xy_offset; wall_z_offset]
let wall_locate3 dx dy = [dx * (wall_xy_offset + wall_thickness); dy * (wall_xy_offset + wall_thickness); wall_z_offset]

let wall_brace place1 dx1 dy1 post1 place2 dx2 dy2 post2 =
    let post =
        [ place1 post1
        ; place1 (translate (wall_locate1 dx1 dy1) post1)
        ; place1 (translate (wall_locate2 dx1 dy1) post1)
        ; place1 (translate (wall_locate3 dx1 dy1) post1)
        ; place2 post2
        ; place2 (translate (wall_locate1 dx2 dy2) post2)
        ; place2 (translate (wall_locate2 dx2 dy2) post2)
        ; place2 (translate (wall_locate3 dx2 dy2) post2)
        ] |> List.collect id |> hull
    
    let bottom = 
        [ place1 (translate (wall_locate2 dx1 dy1) post1)
        ; place1 (translate (wall_locate3 dx1 dy1) post1)
        ; place2 (translate (wall_locate2 dx2 dy2) post2)
        ; place2 (translate (wall_locate3 dx2 dy2) post2)
        ] |> List.collect id |> bottom_hull

    [post; bottom]
    |> List.collect id
    |> union

let key_wall_brace x1 y1 dx1 dy1 post1 x2 y2 dx2 dy2 post2 =
    let p1 = key_place x1 y1
    let p2 = key_place x2 y2

    wall_brace p1 dx1 dy1 post1 p2 dx2 dy2 post2







