<div align="center">

# Dactyl ManuForm Keyboard

<img src="https://user-images.githubusercontent.com/8348199/90482639-198ff500-e166-11ea-9a9b-e9ca39cbb729.png" height="500px">

This is a fork of the [Dactyl-ManuForm](https://github.com/tshort/dactyl-keyboard).  
The Dactyl-Manuform is a fork of the [Dactyl](https://github.com/adereth/dactyl-keyboard) with the thumb cluster from [ManuForm](https://github.com/jeffgran/ManuForm).

</div>

## Differences to [carbonfet's](https://github.com/carbonfet/dactyl-manuform) dactyl-manuform
This version is adapted to my preferences and requirements. As such, there are a number of changes made to the root projects; the keyboard is still customizable, but changing the variables/parameters may require code changes as well.  
Changes to root project:

- Inclusion of [crystalhand's](https://github.com/crystalhand/dactyl-keyboard) wrist-rest
- All 1.0u keys (most ortho kits should provide all caps required for this build)
- Default manuform thumb cluster, but moved out a little bit (I have big hands and want my thumb to rest on the first thumb key)
- Some fixes to the generated bottom plate, which was faulty if extra bottom row is used (which I do)
- Usage of Bossard BN1936 brass inserts (M3) for screws
- Screw nubs moved inside the case

## Future changes (planned)

- Removal of controller board / TRRS jack holder breakout and putting of USB-C jack instead
- Development of my own controller board, QMK (and maybe VIA) programmable
- (Hopefully) Usage of BLE (as two separate keyboards) so no connection between the halves required
- (Alternatively) Usage of higher-quality 5-pin jack (no TRRS or audio jacks)

## Generate OpenSCAD and STL models

* Run `lein generate` or `lein auto generate`
* This will regenerate the `things/*.scad` files
* Use OpenSCAD to open a `.scad` file.
* Make changes to design, repeat `load-file`, OpenSCAD will watch for changes and rerender.
* When done, use OpenSCAD to export STL files



## License

Copyright © 2015-2020 Matthew Adereth, Tom Short and Leo Lou, Andreas Hauser

The source code for generating the models is distributed under the [GNU AFFERO GENERAL PUBLIC LICENSE Version 3](LICENSE).

The generated models are distributed under the [Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0)](LICENSE-models).
