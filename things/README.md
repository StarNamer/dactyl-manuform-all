## 3D Print File for Dactyl Keyboard

This folder contains the 3d print file for Dactyl keyboard. `.scad` file is the source file of model, and `.stl` file is used for 3d printing.

I modified original design so that both left-side and right-side can contain a micro-controller. (I basically mirror all of the right-side design to left side).

## Combined

Combined contains 4 parts, 
- `dactyl-bottom-left`; 
- `dactyl-bottom-right`;
- `dactyl-top-left`;
- `dactyl-top-right`.

To print these files, the 3d printer must at least be able to print `200 * 200 * 100 mm`.

`.stl` file is under [combined/bin](combined/bin) folder and `.scad` file is under [combined/src](combined/src) folder.

To connect `bottom` and `top`, 3 ??? screw is needed for each side.

## Separate

Separate contains 8 parts,
- `dactyl-bottom-left`; 
- `dactyl-bottom-right`;
- `dactyl-rest-left`;
- `dactyl-rest-right`;
- `dactyl-stand-left`;
- `dactyl-stand-right`;
- `dactyl-top-left`;
- `dactyl-top-right`.

To print these files, the 3d printer must at least be able to print `200 * 150 * 800 mm`. 

`.stl` file is under [separate/bin](separate/bin) folder and `.scad` file is under [separate/src](separate/src) folder.

We can use 6 ??? screw to connect `bottom`, `rest` and `stand` together on each side; and then 3 ??? screw to connect with `top`.

## OpenSCAD

To modified model, you need [OpenSCAD](https://www.openscad.org/) to open `.scad` file, and then change the code and re-compile.

## License

Copyright © 2019 Yongda Fan

Copyright © 2017 kennykaye

Copyright © 2015 Matthew Adereth

Everything in this folder is under license [Creative Commons Attribution-NonCommercial-ShareAlike License Version 3.0](LICENSE).