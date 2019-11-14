use <promicro_plate.scad>
use <promicro.scad>

module dactyl() {
    difference(){
        union(){
            include <../things/right.scad>
        }
        translate([-68,54,13])
            rotate(270,[0,0,1])
                rotate(270,[1,0,0])
                    promic_front_cutaway();
        screws();
    }
}

module promic_mount() {
    difference(){
    translate([-68, 54, 13])
        rotate(270,[0,0,1])
            rotate(270,[1,0,0])
                promic_plate();
    screws();
    }
}

module promic_bar() {
    difference(){
    translate([-68, 54, 13])
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
        translate([0,0,-10]) cylinder(d=1.6, h=9.6, $fn=12);
        translate([0,0,-0.5]) cylinder(d1=1.6, d2=5.6, h=2.01, $fn=12);
        translate([0,0,1.5]) cylinder(d=5.6, h=8, $fn=12);
    }
}

module screws() {
    translate([-69, 35, 13]) screw();
    translate([-67, 18, 7]) screw();
    translate([-67, 18, 20]) screw();
}

dactyl();
//promic_mount();
//promic_bar();
//
//screw();
//screws();
