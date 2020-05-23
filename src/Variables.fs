module Dactyl.Variables

open System
open OpenSCAD.Fs.Lib


let nrows = 5.0
let ncols = 6.0
let plate_thickness = 4.0

let alphaDeg = 15.0<deg> //curvature of the columns
let betaDeg = 5.0<deg> // curvature of the rows
let alphaRad = alphaDeg |> degToRad //curvature of the columns
let betaRad = betaDeg |> degToRad // curvature of the rows
let centerrow = nrows - 3.0 // controls front-back tilt
let centercol = 3.0 // controls left-right tilt / tenting (higher number is more tenting)
let tenting_angle = 15.0<deg> // or, change this for more precise tenting control

type ColumnStyle =
    | Orhtographic
    | Standard
    | Fixed

let column_style =
    if nrows > 5.0 then Orhtographic else Standard

let thumb_offsets = [6.0; -10.0; 7.0]

let keyboard_z_offset = 9.0

let extra_width = 2.5
let extra_height = 1.0

let wall_z_offset = -15.0
let wall_xy_offset = 5.0
let wall_thickness = 2.0

let lastrow = nrows - 1.0
let cornerrow = lastrow - 1.0
let lastcol = ncols - 1.0

let column_offset = function
    | x when x = 2.0 -> [0.0; 2.82; -4.5]
    | x when x >= 4.0 -> [0.0; -12.0; 5.64]
    | _ -> [0.0; 0.0; 0.0]
