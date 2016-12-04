# The OctaDactyl Keyboard
The OctaDactyl is a parameterized, split-hand, concave, columnar, ergonomic keyboard forked from the [Dactyl keyboard](https://github.com/adereth/dactyl-keyboard).

The OctaDactyl extends and modifies the Dactyl by adding a few extra columns of keys and applying a few other minor tweaks.

<img src="https://raw.githubusercontent.com/praabjerg/octadactyl-keyboard/master/resources/octadactyl.png"/>

## Assembly

### Generating a Design

**Setting up the Clojure environment**
* [Install the Clojure runtime](https://clojure.org)
* [Install the Leiningen project manager](http://leiningen.org/)
* [Install OpenSCAD](http://www.openscad.org/)

**Generating the design**
* Run `lein repl`
* Load the file `(load-file "src/dactyl_keyboard/dactyl.clj")`
* This will regenerate the `things/*.scad` files
* Use OpenSCAD to open a `.scad` file.
* Make changes to design, repeat `load-file`, OpenSCAD will watch for changes and rerender.
* When done, use OpenSCAD to export STL files

**Tips**
* [Some other ways to evaluate the clojure design file](http://stackoverflow.com/a/28213489)
* [Example designing with clojure](http://adereth.github.io/blog/2014/04/09/3d-printing-with-clojure/)


### Printing
Pregenerated STL files are available in the [things/](things/) directory.

### Wiring
Masks for the flexible PCBs I used are available for the [left](resources/pcb-left.svg) and [right](resources/pcb-right.svg) side.

A [very rough guide for the brave is here](guide/README.org#wiring) - It will be improved over time (**TODO**)!

## License

Copyright © 2015 Matthew Adereth, 2016 Palle Raabjerg

The source code for generating the models (everything excluding the [things/](things/) and [resources/](resources/) directories is distributed under the [GNU AFFERO GENERAL PUBLIC LICENSE Version 3](LICENSE).  The generated models and PCB designs are distributed under the [Creative Commons Attribution-NonCommercial-ShareAlike License Version 3.0](LICENSE-models).
