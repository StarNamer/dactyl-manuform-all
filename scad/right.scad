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
    }
}

module promic_mount() {
    translate([-68, 54, 13])
        rotate(270,[0,0,1])
            rotate(270,[1,0,0])
                promic_plate();
}

module promic_bar() {
    translate([-68, 54, 13])
        rotate(270,[0,0,1])
            rotate(270,[1,0,0]) {
                difference() {
                translate([32.2, -12, 0]) 
                    cube([7.8, 24, 5]);
                promic_back_notch();
                }
            }
}

module screw() {
    color("red") {
        translate([0,0,-10]) cylinder(d=1.6, h=9.5, $fn=12);
        translate([0,0,-0.5]) cylinder(d1=1.6, d2=5.6, h=2, $fn=12);
        translate([0,0,1.5]) cylinder(d=5.6, h=8, $fn=12);
    }
}

dactyl();
promic_mount();
promic_bar();
screw();
