## In depht documentation

# First Considerations

Before I started this project, I already had some ideas on what I want from a new keyboard, most of them formed by browsing [r/MechanicalKeyboards](https://www.reddit.com/r/MechanicalKeyboards/) for way to long. I was quickly fascinated by split keyboards like the [Iris](https://keeb.io/collections/frontpage/products/iris-keyboard-split-ergonomic-keyboard?variant=29480467267678) and the idea of having vertical staggered keys for better ergonomics and the ability to move one half of the keyboard away as a result of me accidentally mashing my mouse against my previous keyboard regularly. Some of the other features I was looking for were:

* The ability to change my switches
* Being able to see the current layer on a display 
* 

All of this caused me to look into the Dactyl-ManuForm some more, because it already had most of the features I was looking for and because it was easier for me to justify spending more money on a strange keyboard than on another plain one, while still giving me the option to change things like the layout and switches later on if I used hot-swap sockets. 
I then made the decision to build the 60% (5x6) model of the Dactyl-ManuForm, using the pregenerated .stl model as I did not have had any experience using sculpted keyboards before so I didn't know what I would have to change to tailor it any more to my needs.

But I did know that I wanted to use [Holy Panda Switches](https://topclack.com/textclack/2018/12/19/holy-panda-switches-new-and-old-by-quakemz)as they seemed to be the best tactile switch on the market when I did my research on switches (now there are some good other tactile alternatives, e.g. zealios). Since I barely missed the first groupbuy for Massdrop's (now Drop) Holy Pandas and didn't know if another one was coming, I had to buy Massdrops Halo True Switches and Yok Mint Pandas and put them together on my own. While I am really pleased with them, feel free to use whatever switch you want.

Another thing I knew I wanted is to use keycaps with a uniform SA row 3 profile (if this doesn't tell you anything, read this [guide](https://thekeeblog.com/overview-of-different-keycap-profiles/?fbclid=IwAR024t3aLGrk8WNG8VYbqNJBthSYl21XP7Gh6ZFe64_FMi6WjDRkmZTnSYY#MG_High-profile_by_Melgeek) on different keycap profiles) which go well with already sculpted keyboards like the Dactyl-ManuForm as its already curved for your fingers. Another benefit of using a uniform profile is the ability to change the visible layout of the keys (e.g. to Dvorak) without having to buy extra kits of keycaps to make up for the different shapes of the rows. 
I decided to go with Signature Plastic's ortholinear Ice Cap keyset, which also has a nice texture to it by being made out of PBT. For looks I also added some novelty keycaps of Signature Plastic's SA Nuclear Data and SA Lime keysets. 

To truly make it my own keyboard and to be able to use it as a school project,  I decided to add an OLED display to the left half and to change its name to Ducktyl-ManuForm (I still got an affection for Donald Duck comics), now having all the features I wanted in one keyboard. How I made these changes will be the topic of the next part.


## Changing the 3D-model

You could think adding a hole for a display to a finished 3D model would be an easy task, and so did I. I was wrong. I started with watching some videos on how to use freeCAD to just create a display-sized block which I'd  be able to subtract from the [left-5x6.stl](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/things/left-5x6.stl) model. After some work to get freeCAD working, I imported the model and tried to make it solid, to be able to cut from it without only cutting the faces. Only problem was that the model was not a closed shell to freecad in the first place, so I didn't even manage to make it solid and by this wasn't able to properly subtract from it. I neither managed to fix the model on my own nor with the help of people from the [freeCAD subreddit](https://www.reddit.com/r/FreeCAD/). The strange thing here is, that Cura, which I used for 3D printing, did accept the .stl model without showing any errors.

To finally change it to my liking, I had to get another, better file of the model by running [create-models.sh](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/create-models.sh) without making changes to the script (I still wanted to get the default 5x6 model, however you might not, so make your changes beforehand) which gave me a left-5x6.scad file. After learning some OpenSCAD basics, I was finally able to cut the display's hole out of the model and to add a block of plastic below the cutout, as not to thin the wall down to much. I then exported the .scad file to a .stl file for printing. This .stl file can still be found [here](https://github.com/OutstandingOof/ducktyl-manuform/blob/master/things/old-ducktyl-left-5x6.stl). 

The takeaway here is not to bother trying to change the .stl file, but instead to create the .scad files and edit them using OpenSCAD. I wasted way to much time trying to edit the .stl files and I think the issue here lies somewhere in OpenSCAD's conversion from .scad to .stl, as my changed files still remained broken to freeCAD. It could as well have something to do with a bug in freeCAD, as Cura was able to use both .stl models. 
