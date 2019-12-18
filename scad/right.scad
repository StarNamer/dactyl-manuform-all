use <promicro_plate.scad>
use <promicro.scad>

jack_diameter = 8.8;
jack_front_diameter = 6.4;
jack_nut_distance = 2.5;  // distance between the nut and the end of the thinner part with the thread
jack_nut_diameter = 9;

base_pos = [-68, 0, 0];

module jack_hole() {
    translate(base_pos) translate([-3.2, 7.5, 7.5]) {
        rotate(-90, [0, 1, 0]) {
            color("pink") {
                cylinder(d=jack_diameter, h=10, $fn=30);
                translate([0, 0, 9.99]) cylinder(d=jack_front_diameter, h=10, $fn=30);
            }
            color("red") translate([0, 0, 10 + jack_nut_distance]) cylinder(d=jack_nut_diameter, h=10, $fn=30);
        }
    }
}

module promic_mount() {
    difference(){
    translate(base_pos) translate([0, 54, 13])
        rotate(270,[0,0,1])
            rotate(270,[1,0,0])
                promic_plate();
    screws();
    }
}

module promic_bar() {
    difference(){
    translate(base_pos) translate([0, 54, 13])
        rotate(270,[0,0,1])
            rotate(270,[1,0,0]) {
                difference() {
                translate([32.2, -12, 0]) 
                    cube([7.8, 24, 5]);
                promic_back_notch();
                }
            }
    screws();
    }
}

module screw() {
    rotate(90, [0, 1, 0]) color("red") {
        translate([0,0,-10]) cylinder(d=1.8, h=9.6, $fn=20);
        translate([0,0,-0.5]) cylinder(d1=1.8, d2=5.6, h=2.01, $fn=20);
        translate([0,0,1.5]) cylinder(d=5.6, h=8, $fn=20);
    }
}

module screws() {
    translate(base_pos) {
        translate([-1, 35, 13]) screw();
        translate([1, 18, 7]) screw();
        translate([1, 18, 20]) screw();
    }
}

module dactyl() {
    difference(){
        union(){
            include <../things/right.scad>
        }
        translate(base_pos) translate([0,54,13])
            rotate(270,[0,0,1])
                rotate(270,[1,0,0])
                    promic_front_cutaway();
        screws();
        jack_hole();
    }
}


intersection() {
//translate([-90, -10, 0]) cube([49, 80, 70]);
dactyl();
}
//promic_mount();
//promic_bar();
//
//screw();
//screws();
