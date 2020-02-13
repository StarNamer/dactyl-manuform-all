# Dactyl Manu Tide (Work in Progress, Not Ready to Print)

![test print](main.png)
![test print](https://i.redd.it/fhacqnvk64241.jpg)

This is a fork of the [dactyl-manuform-mini-keyboard](https://github.com/l4u/dactyl-manuform-mini-keyboard) that was forked from [Dactyl-ManuForm](https://github.com/tshort/dactyl-keyboard) is a for of the Dactyl-Manuform which is a fork of the [Dactyl](https://github.com/adereth/dactyl-keyboard) with the thumb cluster from [ManuForm](https://github.com/jeffgran/ManuForm).

## Features

- Maximum curve
- Maximum keys
- Minimum rehoming

## Generate OpenSCAD and STL models

* Run `lein generate` or `lein auto generate`
* This will regenerate the `things/*.scad` files
* Use OpenSCAD to open a `.scad` file.
* Make changes to design, repeat `load-file`, OpenSCAD will watch for changes and rerender.
* When done, use OpenSCAD to export STL files

## License

Copyright © 2015-2018 Matthew Adereth, Tom Short and Leo Lou

The source code for generating the models is distributed under the [GNU AFFERO GENERAL PUBLIC LICENSE Version 3](LICENSE).

The generated models are distributed under the [Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0)](LICENSE-models).
