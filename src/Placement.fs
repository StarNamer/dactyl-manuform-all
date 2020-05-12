module Dactyl.Placement

open System
open Dactyl.Variables
open Dactyl.SinglePlate
open OpenSCAD.Fs.Lib
open OpenSCAD.Fs.Lib.Operator
open OpenSCAD.Fs.Lib.Combinator
open MathNet.Numerics.LinearAlgebra

let columns = [0 .. ncols - 1]
let rows = [0 .. nrows - 1]
let sa_profile_key_height = 12.7

let cap_top_height = (plate_thickness + sa_profile_key_height)

let row_radius = ((mount_height + extra_height) / 2.0) / (Math.Sin (alphaRad / 2.0<rad>)) + cap_top_height
let column_radius = ((mount_width + extra_width) / 2.0) / (Math.Sin (betaRad / 2.0<rad>)) + cap_top_height
let column_x_delta = Math.Abs((sin betaRad) * column_radius) - 1.0
let column_base_angle = (centercol - 2.0) * betaDeg

let apply_key_geometry translate_fn rotate_x_fn rotate_y_fn column row shape = 
    let column_angle = (centercol - column) * betaDeg
    let column_z_delta = (1.0 - (column_angle |> degToRad |> cos)) * column_radius

    let style =
        match column_style with
        | Standard ->
            shape
            |> translate_fn [0.0; 0.0; - row_radius]
            |> rotate_x_fn (float(centerrow - row) * alphaDeg)
            |> translate_fn [0.0; 0.0; row_radius]
            |> translate_fn [0.0; 0.0; - column_radius]
            |> rotate_y_fn column_angle
            |> translate_fn [0.0; 0.0; column_radius]
            |> translate_fn (column_offset column)
        | _ ->
            shape
            |> translate_fn [0.0; 0.0; -row_radius]
            |> rotate_x_fn (float(centerrow - row) * alphaDeg)
            |> translate_fn [0.0; 0.0; row_radius]
            |> rotate_y_fn column_angle
            |> translate_fn [-(float(column - centercol) * column_x_delta); 0.0; column_z_delta]
            |> translate_fn (column_offset column)

    style
    |> rotate_y_fn tenting_angle
    |> translate_fn [0.0; 0.0; keyboard_z_offset]
    

let key_place column row shape =
    let rot_x angle obj = rotate angle [1.0; 0.0; 0.0] obj
    let rot_y angle obj = rotate angle [0.0; 1.0; 0.0] obj
    apply_key_geometry translate rot_x rot_y column row shape

let key_holes =
    [for column in columns do
        for row in rows do
            if column = 2 then yield single_plate |> key_place (float(column)) row
            if column = 3 then yield single_plate |> key_place (float(column)) row
            if row <> lastrow then yield single_plate |> key_place (float(column)) row]
    |> List.collect id
    |> union

let rotate_around_x angle position =
    let angleRad = angle |> degToRad
    matrix
        [ [1.0; 0.0; 0.0]
        ; [0.0; cos(angleRad); - sin(angleRad)]
        ; [0.0; sin(angleRad); cos(angleRad)]]
    * vector position
    |> Vector.toList

let rotate_around_y angle position =
    let angleRad = angle |> degToRad
    matrix
        [ [cos(angleRad); 0.0; sin(angleRad)]
        ; [0.0; 1.0; 0.0] 
        ; [- sin(angleRad); 0.0; cos(angleRad)]]
    * vector position
    |> Vector.toList

let key_position column row position =
    apply_key_geometry (List.map2 (fun x y -> x + y)) rotate_around_x rotate_around_y column row position