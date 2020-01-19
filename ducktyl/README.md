# Documentation for the Ducktyl-ManuForm

The documentation is split into a simple [build log/guide](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/ducktyl/README.md#simple-build-log) of the already generated default 5x6 model and a more [in-depht documentation](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/ducktyl/in-depht_documentation.md) of the whole process with some notes on the configuration of the QMK firmware.
While you should be able to build a fully functional Ducktyl-ManuForm by following the build log/guide and using the default 5x6 model, I'd advise you to read the in-depht documentation if you want to customize anything, so you can better understand my approach and learn from my mistakes. 

# Simple build log/guide

What you will need: 

* 2 Pro Micros (Or a drop in replacement like the Elite C)
* 1 SSD1306 128x32 0,91" OLED Display (same as the [crkbd](https://github.com/foostan/crkbd))
* Mechanical Switches (Cherry profile) of your choice
* Keycaps of your choice
* 64 1N4148 Diodes
* Wire and isolated cables for wiring
* 2 TRRS jacks
* TRRS cable

Optional:

* EC11 rotary encoder + knob
* [Adapter](https://www.reddit.com/r/MechanicalKeyboards/comments/chs82g/designed_a_new_adapter_for_installing_rotary/) to use the encoder in place of a switch
* Kailh hot swap sockets CPG151101S11

And the obvious part: The 3D printed cases. As nobody sells the cases, you'll have to 3D-print them by yourself, or ask someone to do it for you. I for example was able to print them at my school, using an Ultimaker 2+ with a layerheight of 0.15 mm and 80% infill out of PLA. While the right half is just the [right-5x6.stl](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/things/right-5x6.stl) model, the left half is the [ducktyl-left-5x6.stl](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/things/ducktyl-left-5x6.stl) model, which has the cutout for the OLED-display.

Picture of the printed left side: 

![Imgur](https://i.imgur.com/dHPAJN4.jpg)

Notice that there is no hole for the OLED ribbon yet, as I added it to the model afterwards. The old model is still available as [old-ducktyl-left-5x6.stl](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/things/old-ducktyl-left-5x6.stl) for people who want to be at the safe side, as the new model is not yet tested. Keep in mind though, that if you go with the old model, you will have to drill the hole by yourself. 

Another thing I have to add is that if you want to mount the microcontrollers like I did, you'll have to take away parts of the back wall of the ports with a dremel to make the microcontroller fit it there. This is not a problem if you use an USB cable to bring the microcontroller's USB port to the outside. 



## Creating the matrix

Assuming you have already printed and cleaned both halves, it is now time to put the switches in place and wire them up to your microcontroller. If you want to be able to change the switches later, like I  did, you'll have to clip the hot swap sockets onto the switches now. 
For wiring the matrix, you'll have to solder one diode to each switch (with the black stripe on one end facing away from the switch) and solder the legs of the diodes into rows, like in this picture: 

![Imgur](https://i.imgur.com/6tdU25J.jpg)

Next you have to connect the other contacts of the switches (or sockets, if you use them) into rows. All in all, the whole matrix should be wired up like in the following diagrams taken from the Dactyl-ManuForm build, but with some changes to it. First of all, this build is using a TRRS cable for communication between halves, so the wires to the RJ 9 port will have to be connected to TRRS jacks. Second of all, you will have to wire the data line (which is wired to pin `3` in the diagramms) to the `RX1` pin, as the pins `2` and `3` will be used to connect the display to the microcontroller over I2C on the left side. Last but not least, you will have to split up `GND` and `VCC` on the left side, to send power to the display and the serial connection,and `GND` on the right side, for the serial connection and the rotary encoder. 

![Left Wire Diagram](/resources/dactyl_manuform_left_wire_diagram.png)

![Right Wire Diagram](/resources/dactyl_manuform_right_wire_diagram.png)

A picture of my complete wiring can be found at the end of the build log/guide.

## Adding the display and rotary encoder

To add the rotary encoder to the right side, you'll have to put it inside the case using the adapter and wire the two pins on one side into the matrix, just like any other switch (those pins *are* for the switch inside the encoder) and the center pin of the other side (the one with 3 pins) to `GND`. The two outer pins of the other side have to be wired to the pins `A2` and `A3`.
To add the OLED display to the left side, you'll have to pull the display off of the controller board (it's just glued on), careful not to accidentally break the ribbon cable connecting it. Now you have to pull the display part through the hole and fit it into the small dent, leaving the controller board inside the case and gluing it in place using double sided tape. All that is left now, is to connect `GND` to `GND`, `VCC` to `VCC`, `SDA`
to pin `2` and `SCL` to pin `3`. Putting the display in place should look like this: 

![Imgur](https://i.imgur.com/3NIb3jX.jpg)

![Imgur](https://i.imgur.com/ac253XE.jpg)

![Imgur](https://i.imgur.com/4qDCBPm.jpg)

## Flashing the firmware

You'll find information on how to use and flash the firmware [here](https://github.com/OutstandingOof/qmk_firmware/tree/ducktyl_manuform/keyboards/handwired/ducktyl_manuform). The firmware should work if you did everything like i did. If you changed things up, I'd advise you to read my [in-depht documentation](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/ducktyl/in-depht_documentation.md) so you can replicate my steps to adapt the firmware to your needs. 

## Finishing touches

You should now have a working keyboard, but everything inside is still loose. To clean it up a little, its nice to glue in the connectors and put the microcontrollers in place with some hand moldable plastic like I did. Looking back however, with this model it is probably smarter to glue the microcontrollers to the holder on the side and use an USB cable to bring the connectors to the outside, like in the original Dactyl-ManuForm. If you need a bottom plate, also make sure to look up the original tutorial. I didn't add one, as not to add more height, but instead went with glueing anti-slip feet to the screwholes. This is the finished keyboard from the bottom: 

![Imgur](https://i.imgur.com/VCkwyaX.jpg?1)

This is the finished IO. It's not perfect, but it does the job!

![Imgur](https://i.imgur.com/olPG2hG.jpg)

## License 

This build guide and the in-depht documentation by OutstandingOof are licensed under the [Creative Commons Attribution-NonCommercial-ShareAlike License Version 3.0](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/LICENSE-models).
