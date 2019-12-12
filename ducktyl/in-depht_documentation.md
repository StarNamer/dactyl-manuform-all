## In depht documentation

# First Considerations

Before I started this project, I already had some ideas on what I want from a new keyboard, most of them formed by browsing [r/MechanicalKeyboards](https://www.reddit.com/r/MechanicalKeyboards/) for way to long. I was quickly fascinated by split keyboards like the [Iris](https://keeb.io/collections/frontpage/products/iris-keyboard-split-ergonomic-keyboard?variant=29480467267678) and the idea of having vertical staggered keys for better ergonomics and the ability to move one half of the keyboard away as a result of me accidentally mashing my mouse against my previous keyboard regularly. Some of the other features I was looking for were:

* The ability to change my switches
* Being able to see the current layer on a display 
* Having the option to use USB-C and Micro-USB

All of this caused me to look into the Dactyl-ManuForm some more, because it already had most of the features I was looking for and because it was easier for me to justify spending more money on a strange keyboard than on another plain one, while still giving me the option to change things like the layout and switches later on if I used hot-swap sockets. 
I then made the decision to build the 60% (5x6) model of the Dactyl-ManuForm, using the pregenerated .stl model as I did not have had any experience using sculpted keyboards before so I didn't know what I would have to change to tailor it any more to my needs.

But I did know that I wanted to use [Holy Panda Switches](https://topclack.com/textclack/2018/12/19/holy-panda-switches-new-and-old-by-quakemz) as they seemed to be the best tactile switch on the market when I did my research on switches (now there are some good other tactile alternatives, e.g. zealios). Since I barely missed the first groupbuy for Massdrop's (now Drop) Holy Pandas and didn't know if another one was coming, I had to buy Massdrops Halo True Switches and Yok Mint Pandas and put them together on my own. While I am really pleased with them, feel free to use whatever switch you want.

Another thing I knew I wanted is to use keycaps with a uniform SA row 3 profile (if this doesn't tell you anything, read this [guide](https://thekeeblog.com/overview-of-different-keycap-profiles/?fbclid=IwAR024t3aLGrk8WNG8VYbqNJBthSYl21XP7Gh6ZFe64_FMi6WjDRkmZTnSYY#MG_High-profile_by_Melgeek) on different keycap profiles) which go well with already sculpted keyboards like the Dactyl-ManuForm as its already curved for your fingers. Another benefit of using a uniform profile is the ability to change the visible layout of the keys (e.g. to Dvorak) without having to buy extra kits of keycaps to make up for the different shapes of the rows. 
I decided to go with Signature Plastic's ortholinear Ice Cap keyset, which also has a nice texture to it by being made out of PBT. For looks I also added some novelty keycaps of Signature Plastic's SA Nuclear Data and SA Lime keysets. 

To truly make it my own keyboard and to be able to use it as a school project,  I decided to add an OLED display to the left half and to change its name to Ducktyl-ManuForm (I still got an affection for Donald Duck comics), now having all the features I wanted in one keyboard. How I made these changes will be the topic of the next part.

To use both Micro-USB and USB-C I decided to use a Pro Micro on the left, and an Elite-C on the right side, using a serial connection over a TRRS cable between the m. I only later noticed that by doing this I loose the ability to hotplug the halves, but at least they are easier to get than RJ 9 cables and ports while also fitting in the holes made for the USB ports.  

## Changing the 3D-model

You could think adding a hole for a display to a finished 3D model would be an easy task, and so did I. I was wrong. I started with watching some videos on how to use freeCAD to just create a display-sized (width: 12mm, length: 32mm, height: depends on how deep you want to fit it in) block which I'd  be able to subtract from the [left-5x6.stl](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/things/left-5x6.stl) model. After some work to get freeCAD working, I imported the model and tried to make it solid, to be able to cut from it without only cutting the faces. Only problem was that the model was not a closed shell to freecad in the first place, so I didn't even manage to make it solid and by this wasn't able to properly subtract from it. I neither managed to fix the model on my own nor with the help of people from the [freeCAD subreddit](https://www.reddit.com/r/FreeCAD/). The strange thing here is, that Cura, which I used for 3D printing, did accept the .stl model without showing any errors.

To finally change it to my liking, I had to get another, better file of the model by running [create-models.sh](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/create-models.sh) without making changes to the script (I still wanted to get the default 5x6 model, however you might not, so make your changes beforehand) which gave me a left-5x6.scad file. After learning some OpenSCAD basics, I was finally able to cut the display's hole out of the model and to add a block of plastic below the cutout, as not to thin the wall down to much. I then exported the .scad file to a .stl file for printing. This .stl file can still be found [here](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/things/old-ducktyl-left-5x6.stl). 

The takeaway here is not to bother trying to change the .stl file, but instead to create the .scad files and edit them using OpenSCAD. I wasted way to much time trying to edit the .stl files and I think the issue here lies somewhere in OpenSCAD's conversion from .scad to .stl, as my changed files still remained broken to freeCAD. It could as well have something to do with a bug in freeCAD, as Cura was able to use both .stl models. 

I changed the 3D model some more later, so other people don't have to drill out a hole for the display's ribbon connector like I did, since I didn't know exactly where it would be located. [This](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/things/ducktyl-left-5x6.stl) is the newer and better model. 

To wrap this part up, I have to apologize for not applying the changes I made to the generation script, which would apply them to any possible generated model. But to my defense I have to say that I simply don't have the knowledge and experience in 3D moddeling and scripting required to do this (yet, hopefully). 


## 3D printing

3D printing was rather straightforward for me. I was able to print at my school and started by testprinting the adapter for the encoder, using the smallest available layer height as it is a small part. For the cases I used the settings mentioned in the build guide. Other than a broken SD-card I didn't face any issues here. 

## Assembly 

Since most of the assembly is already mentioned in the build guide I'll try to make this short. I started by removing the support structures of the prints, cleaned them up, clipped switches to them and the hot-swap sockets to the switches and started soldering the matrix, careful not to wire a diode in the wrong way. The diodes are not necessary, but very useful to avoid ghosting ([here](https://www.dribin.org/dave/keyboard/one_html/) is how this works).
For wiring I used [this](https://deskthority.net/viewtopic.php?f=7&t=6050&start=) guide and the wiring diagrams from the Dactyl-Manuform, but applying the changes from the build guide.
I also had to drill the hole for the OLED ribbon cable as mentioned before and use a dremel to cut away the back of the portholes to be able to fit the microcontrollers inside them and put them into position with handmoldable plastic. 
I decided not to use a bottom plate (to keep the keyboard lower) and to just used anti-slip feet to stop a metalpiece from one of the sockets scratching my desk. 

## Adapting the firmware

To add my own keyboard and keymap to the [QMK-firmware](https://qmk.fm/), which currently is the best firmware for 

