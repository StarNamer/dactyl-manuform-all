# In depht documentation

## First Considerations

Before I started this project, I already had some ideas on what I want from a new keyboard, most of them formed by browsing [r/MechanicalKeyboards](https://www.reddit.com/r/MechanicalKeyboards/) for way to long. I was quickly fascinated by split keyboards like the [Iris](https://keeb.io/collections/frontpage/products/iris-keyboard-split-ergonomic-keyboard?variant=29480467267678) and the idea of having vertical staggered keys for better ergonomics and the ability to move one half of the keyboard away as a result of me accidentally mashing my mouse against my previous keyboard regularly. Some of the other features I was looking for were:

* The ability to change my switches
* Being able to see the current layer on a display 
* Having the option to use USB-C and Micro-USB
* Being able to control the volume with a knob

All of this caused me to look into the Dactyl-ManuForm some more, because it already had most of the features I was looking for and because it was easier for me to justify spending more money on a strange keyboard than on another plain one, while still giving me the option to change things like the layout and switches later on if I used hot-swap sockets. 
I then made the decision to build the 60% (5x6) model of the Dactyl-ManuForm, using the pregenerated .stl model as I did not have had any experience using sculpted keyboards before so I didn't know what I would have to change to tailor it any more to my needs.

But I did know that I wanted to use [Holy Panda Switches](https://topclack.com/textclack/2018/12/19/holy-panda-switches-new-and-old-by-quakemz) as they seemed to be the best tactile switch on the market when I did my research on switches (now there are some good other tactile alternatives, e.g. zealios). Since I barely missed the first groupbuy for Massdrop's (now Drop) Holy Pandas and didn't know if another one was coming, I had to buy Massdrops Halo True Switches and Yok Mint Pandas and put them together on my own. While I am really pleased with them, feel free to use whatever switch you want.

Another thing I knew I wanted is to use keycaps with a uniform SA row 3 profile (if this doesn't tell you anything, read this [guide](https://thekeeblog.com/overview-of-different-keycap-profiles/?fbclid=IwAR024t3aLGrk8WNG8VYbqNJBthSYl21XP7Gh6ZFe64_FMi6WjDRkmZTnSYY#MG_High-profile_by_Melgeek) on different keycap profiles) which go well with already sculpted keyboards like the Dactyl-ManuForm as its already curved for your fingers. Another benefit of using a uniform profile is the ability to change the visible layout of the keys (e.g. to Dvorak) without having to buy extra kits of keycaps to make up for the different shapes of the rows. 
I decided to go with Signature Plastic's ortholinear Ice Cap keyset, which also has a nice texture to it by being made out of PBT. I also added some novelty keycaps of Signature Plastic's SA Nuclear Data and SA Lime keysets, just for the look. 

To truly make it my own keyboard and to be able to use it as a school project,  I decided to add an OLED display to the left half and to change its name to Ducktyl-ManuForm (I still got an affection for Donald Duck comics), now having all the features I wanted in one keyboard. How I made these changes will be the topic of the next part.

To use both Micro-USB and USB-C I decided to use a Pro Micro on the left, and an Elite-C on the right side, using a serial connection over a TRRS cable between them. I only later noticed that by doing this I loose the ability to hotplug the halves, but at least they are easier to get than RJ 9 cables and ports while also fitting in the holes made for the USB ports.  

## Changing the 3D-model

You could think adding a hole for a display to a finished 3D model would be an easy task, and so did I. I was wrong. I started with watching some videos on how to use freeCAD to just create a display-sized (width: 12mm, length: 32mm, height: depends on how deep you want to fit it in) block which I'd  be able to subtract from the [left-5x6.stl](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/things/left-5x6.stl) model. After some work to get freeCAD working, I imported the model and tried to make it solid, to be able to cut from it without only cutting the faces. Only problem was that the model was not a closed shell to freecad in the first place, so I didn't even manage to make it solid and by this wasn't able to properly subtract from it. I neither managed to fix the model on my own nor with the help of people from the [freeCAD subreddit](https://www.reddit.com/r/FreeCAD/). The strange thing here is, that Cura, which I used for 3D printing, did accept the .stl model without showing any errors.

To finally change it to my liking, I had to get another, better file of the model by running [create-models.sh](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/create-models.sh) without making changes to the script (I still wanted to get the default 5x6 model, however you might not, so make your changes beforehand) which gave me a left-5x6.scad file. After learning some OpenSCAD basics, I was finally able to cut the display's hole out of the model and to add a block of plastic below the cutout, as not to thin the wall down to much. I then exported the .scad file to a .stl file for printing. This .stl file can still be found [here](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/things/old-ducktyl-left-5x6.stl). 

The takeaway here is not to bother trying to change the .stl file, but instead to create the .scad files and edit them using OpenSCAD. I wasted way to much time trying to edit the .stl files and I think the issue here lies somewhere in OpenSCAD's conversion from .scad to .stl, as my changed files still remained broken to freeCAD. It could as well have something to do with a bug in freeCAD, as Cura was able to use both .stl models. 

I changed the 3D model some more later, so other people don't have to drill out a hole for the display's ribbon connector like I did since I didn't know exactly where it would be located. [This](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/things/ducktyl-left-5x6.stl) is the newer and better model. 

To wrap this part up, I have to apologize for not applying the changes I made to the generation script, which would apply them to any possible generated model. But to my defense I have to say that I simply don't have the knowledge and experience in 3D moddeling and scripting required to do this (yet, hopefully). 


## 3D printing

3D printing was rather straightforward for me. I was able to print at my school and started by testprinting the adapter for the encoder, using the smallest available layer height as it is a small part. For the cases I used the settings mentioned in the build guide. Other than a broken SD-card I didn't face any issues here. 

## Assembly 

Since most of the assembly is already mentioned in the build guide I'll try to make this short. I started by removing the support structures of the prints, cleaned them up, clipped switches to them and the hot-swap sockets to the switches and started soldering the matrix, careful not to wire a diode in the wrong way. The diodes are not necessary, but very useful to avoid ghosting ([here](https://www.dribin.org/dave/keyboard/one_html/) is how this works).
For wiring I used [this guide](https://deskthority.net/viewtopic.php?f=7&t=6050&start=) and the wiring diagrams from the Dactyl-Manuform, all while applying the changes mentioned it the build guide.
I also had to drill the hole for the OLED ribbon cable as mentioned before and use a dremel to cut away the back of the portholes to be able to fit the microcontrollers inside them and put them into position with handmoldable plastic. 
I decided not to use a bottom plate (to keep the keyboard lower) and to just used anti-slip feet to stop a metalpiece from one of the sockets from scratching my desk. 

## Adapting the firmware

First of all, this keyboard is powered by the [QMK-firmware](https://docs.qmk.fm/#/), which is probably the best firmware for custom keyboards and stuffed with features. To build my firmware, I followed the [Complete Noobs Guide](https://docs.qmk.fm/#/newbs) and so should you. After some messing around I finally found all the files of the Dactyl-ManuForm in the /keyboards/handwired folder and followed QMK's guide to fork the project to my own repository where I created a new development branch named, I'm sure you guessed it, [ducktyl_manuform](https://github.com/OutstandingOof/qmk_firmware/tree/ducktyl_manuform). All the changes I did are located there under [keyboards/handwired/ducktyl_manuform](https://github.com/OutstandingOof/qmk_firmware/tree/ducktyl_manuform/keyboards/handwired/ducktyl_manuform), so for now you have to clone the firmware from my repository and run 
`git checkout ducktyl_manuform`
to get to my development branch and to use my configuration and keymaps for your project. I will issue a pull request to QMK's master branch to add the keyboard to the central repository, I'm just not done yet with changing everything to fit their guidelines and would like to test my changes thoroughly before opening one. So for now you're stuck with my repository, but I will update this text once that changes. 

I had a starting point as well, namely all the files from the regular Dactyl-ManuForm, which I copied over to a new folder, named, and I'm sure you guessed it right again, ducktyl_manuform. Afterwards I removed all the non 5x6 folders to be able to switch to the folder easier, so if you build any other configuration, you will have to copy the folder that belongs to it over to the ducktyl_manuform folder and change all references to include e.g the ducktyl_manuform.h file from the parent directory. I continued by changing the  line `#define SOFT_SERIAL_PIN DO` to `#define SOFT_SERIAL_PIN D2` in the ducktyl_manuform's main config.h file to free pin D0 for the use to communicate with the display (I2C is only supported for pins 2 and 3 on the Pro Micro and Elite C). From here on, all I did was to follow the the QMK docs to implement the features I wanted by copy and pasting the examples and adapting them to my usecase. 
This is also when I added this keyboards signature feature, the code to use an OLED. Since I added the OLED to the left side, I didn't have to bother with rotating it, however if you want to add two displays, you will. 

After a lot of thinking, I decided to use an adapted version of the dvorak layout at the keyboard level, and to put special german characters like ö, ä and ü on another layer, as I already knew that I'd be writing in English most of the time. This is also why I decided to use the dvorak layout and not neo, which is optimized for German. As I knew that all the computers,  that I was going to use it at, use the the default German layout, so I adapted my firmware to output all its characters. Sadly QMK does not support foreign layouts, I had to guess which English keycodes retate to the German ones that I needed (I did this based on the position of the keys and trial and error, but it's a good idea to use datasheets where characters are mapped to their keycodes). I also decided to add the ability to change the default layer to QWERTZ so other people are able to use my keyboard. 

My personal keymap is also adapted to my OS, as I use Linux with the I3 window manager which is made to be keyboard operated. One example of this change is the position of the super (or OS) key (QMK's KC_LGUI) on both thumbs. 

The two last notes to add to the keymaps is that I decided to make a seperate Game layer, which is basically QWERTY but shifted one collumn to the right to accommodate the special shape of the keyboard (to save me from remapping my keys in every game) and that I added a MOUSE layer, in case I'm to lazy to move my hand over or if I don't have a mouse at hand. 

## Problems 

During the build I encountered some problems, some on a hardware level, some in software. Starting with the hardware, one of my TRRS jacks got lost (I guess in customs) so I had to order another one. But I had to reorder other things as well: The OLED (I don't know if I broke it when I separated the screen from the controller board or if I just got a broken one at first) and 3 of my Pro Micros, which refused to show up for flashing after some time (I think it is because the version of avrdude on my computer is to new and not yet supported by QMK). Using the 3rd Pro Micro, I found a workaround for this, as I was able to use new keymaps when I only flashed them to the master halve, but in doing so I lost the ability to use both USB C and Micro USB. At least I did't have to order another one and the Elite C just kept working (Maybe because it's using another bootloader?) 

At a software level I had problems using freeCAD as mentioned further up. Using QMK I had some problems like not being able to change the pin for serial communication to my liking, which I was able to fix by reading the documentation properly (it turned out only certain pins can be used for this). But there is one thing I wasn't able to fix: The OLED isn't able to show a change of the default layer because the function can only get and show the highest layer, which is not updated for a change in the default layer. I did not find a function to properly read out the default layer. 

## Conclusion

While all of this has been quite some work, basically everything worked out in the end and I now have a working keyboard with a unique set of features no other keyboard has. While I really enjoy these features and the finished keyboard just for myself, I hope others can profit from my work as well and end up with a better keyboard. Since I spend way to much time and money (around 320€, but you can build it for way cheaper if you use different switches and keycaps and if you have the option to order "locally" to save on shipping and customs) I am glad that it is now finished and that I now have a selfmade keyboard that lowers the "communication barrier"  between me and my computer significantly!
