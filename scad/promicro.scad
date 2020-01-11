// Pro-Micro dimensions: 18.5mm×33.5mm ground plate (USB socket juts out by 1.9mm)
// PCB is 1.5 mm thick. We can use only 0.5mm for a notch.
// The PCB is mounted 1mm above the ground plate.
//
// USB-socket dimensions:
//  8mm width
//  3mm height
//  mounted directly on the PCB without gap
//
// z=0 is the surface of the base where this mount is placed on
// x/y = 0/0 is the center of the USB socket front
// The Pro Micro is lying on the x/y plane and the USB-socket-to-reset-button axis is the x-axis.
// The USB-Plug is at x=0
//
// x is “width”
// y is “length”


notch_depth = 0.5;
notch_bar_width = 3;
notch_bar_height = 4.8;
notch_bottom_level = 1.6;
notch_height = 2;     // = PCB thickness

ground_plate_width = 26;
ground_plate_length = 35;
ground_plate_thickness = 1;

promic_width = 18.8; 
promic_length = 33.7;

front_notch_depth = 1.5;

usb_socket_width = 8.4;
usb_socket_height = 3.4;
usb_socket_depth = 2; // also smaller cut-away for the USB-plug
usb_plug_width = 13;
usb_plug_height = 8.2;
usb_plug_cutaway_bottom_level = 1;

backbarmount_depth = 5;
backbarmount_overwidth = 4; // how much the back bar is wider than the promic per side
backbarmount_notch_depth = 5;
backbarmount_notch_height = 2.8;
backbarmount_hole_radius = 1.5;
backbarmount_hole_indent = 2.8;
backbarmount_support_width = 1.5;

lock_bar_vert_clearance = 0.16; // clearance at the bottom and below and above the catches
lock_bar_hori_clearance = 0.4;

/*
// additional ground plate
color("gray")
    translate([0, -ground_plate_width/2, -ground_plate_thickness])
        cube([ground_plate_length, ground_plate_width, ground_plate_thickness]);
*/

module notch_bar() {
    // the main notch bar
    difference() {
        cube([promic_length - front_notch_depth, notch_bar_width, notch_bar_height]);
        color("yellow")
            translate([-0.5, -1, notch_bottom_level])
                cube([promic_length - front_notch_depth + 1, notch_depth + 1, notch_height]);
    }
}

module front_notch() {
    color("orange") 
        translate([-front_notch_depth, -promic_width/2, notch_bottom_level])
            cube([front_notch_depth + 1, promic_width, notch_height]);
}

module usb_cut_away() {
    color("red")
        translate([
            -usb_socket_depth - front_notch_depth -1,
            -usb_socket_width/2,
            notch_bottom_level + notch_height - 0.4
        ])
            cube([usb_socket_depth + front_notch_depth + 2, usb_socket_width, usb_socket_height]);
    color("yellow")
        translate([
            -14 - usb_socket_depth - front_notch_depth,
            -usb_plug_width/2,
            usb_plug_cutaway_bottom_level
        ])
            cube([15, usb_plug_width, usb_plug_height]);
}


module promic_mount() {
    // notch bars
    translate([0, promic_width/2 - notch_depth]) notch_bar();
    translate([0, -promic_width/2 + notch_depth]) mirror([0,1,0]) notch_bar();
}

module promic_front_cutaway() {
    front_notch();
    usb_cut_away();
}

module promic_back_notch() {
    translate([promic_length - front_notch_depth, notch_depth-promic_width/2, 0]) {
        translate([-1, -notch_depth, notch_bottom_level])
            color("pink")
                cube([notch_depth + 0.24 + 1, promic_width, notch_height]);
    }
}

promic_mount();
promic_front_cutaway();
promic_back_notch();

