module Dactyl.Connections

open OpenSCAD.Fs.Lib
open OpenSCAD.Fs.Lib.Operator
open OpenSCAD.Fs.Lib.Combinator
open System.IO
open Dactyl.SinglePlate
open Dactyl.Placement
open Dactyl.Variables
open FSharpx.Collections

let web_thickness = 3.5
let post_size = 0.1
let web_post = 
    centeredCube [post_size; post_size; web_thickness]
    |> translate [0.0; 0.0; (web_thickness / -2.0) + plate_thickness]

let post_adj = post_size / 2.0
let web_post_tr = translate [(mount_width / 2.0) - post_adj; (mount_height / 2.0) - post_adj; 0.0] web_post
let web_post_tl = translate [(mount_width / -2.0) + post_adj; (mount_height / 2.0) - post_adj; 0.0] web_post
let web_post_bl = translate [(mount_width / -2.0) + post_adj; (mount_height / -2.0) + post_adj; 0.0] web_post
let web_post_br = translate [(mount_width / 2.0) - post_adj; (mount_height / -2.0) + post_adj; 0.0] web_post

let triangle_hulls shapes =
    shapes
    |> List.windowed 3
    |> List.map (fun l -> l |> hull)
    |> List.collect id
    |> union

let row_connections =
    [for column in 0 .. ncols - 2 do
        for row in 0 .. lastrow - 1 do
            [ key_place (float(column + 1)) row web_post_tl 
            ; key_place (float(column)) row web_post_tr
            ; key_place (float(column + 1)) row web_post_bl
            ; key_place (float(column)) row web_post_br]
            |> List.collect id
            |> triangle_hulls ]
    |> List.collect id

let column_connections = 
    [for column in columns do
        for row in 0 .. cornerrow - 1 do
            [ key_place (float(column)) row web_post_bl
            ; key_place (float(column)) row web_post_br
            ; key_place (float(column)) (row + 1) web_post_tl
            ; key_place (float(column)) (row + 1) web_post_tr]
            |> List.collect id
            |> triangle_hulls ]
    |> List.collect id

let diagonal_connections = 
    [for column in 0 .. ncols - 2 do
        for row in 0 .. cornerrow - 1 do
            [ key_place (float(column)) row web_post_br
            ; key_place (float(column)) (row + 1) web_post_tr
            ; key_place (float(column + 1)) row web_post_bl
            ; key_place (float(column + 1)) (row + 1) web_post_tl]
            |> List.collect id
            |> triangle_hulls ]
    |> List.collect id

let connectors =
    [row_connections; column_connections; diagonal_connections]
    |> List.collect id
    |> union
    

