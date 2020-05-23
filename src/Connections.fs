module Dactyl.Connections

open OpenSCAD.Fs.Lib
open OpenSCAD.Fs.Lib.Operator
open OpenSCAD.Fs.Lib.Combinator
open System.IO
open Dactyl.SinglePlate
open Dactyl.Placement
open Dactyl.Variables
open FSharpx.Collections

let row_connections =
    [for column in 0.0 .. ncols - 2.0 do
        for row in 0.0 .. lastrow - 1.0 do
            [ key_place (float(column + 1.0)) row web_post_tl 
            ; key_place (float(column)) row web_post_tr
            ; key_place (float(column + 1.0)) row web_post_bl
            ; key_place (float(column)) row web_post_br]
            |> List.collect id
            |> triangle_hulls ]
    |> List.collect id

let column_connections = 
    [for column in columns do
        for row in 0.0 .. cornerrow - 1.0 do
            [ key_place (float(column)) row web_post_bl
            ; key_place (float(column)) row web_post_br
            ; key_place (float(column)) (row + 1.0) web_post_tl
            ; key_place (float(column)) (row + 1.0) web_post_tr]
            |> List.collect id
            |> triangle_hulls ]
    |> List.collect id

let diagonal_connections = 
    [for column in 0.0 .. ncols - 2.0 do
        for row in 0.0 .. cornerrow - 1.0 do
            [ key_place (float(column)) row web_post_br
            ; key_place (float(column)) (row + 1.0) web_post_tr
            ; key_place (float(column + 1.0)) row web_post_bl
            ; key_place (float(column + 1.0)) (row + 1.0) web_post_tl]
            |> List.collect id
            |> triangle_hulls ]
    |> List.collect id

let connectors =
    [row_connections; column_connections; diagonal_connections]
    |> List.collect id
    |> union
    

