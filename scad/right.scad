use <promicro_plate.scad>
use <promicro.scad>

difference(){
    union(){
        include <../things/right.scad>
    }
    translate([-68,58.5,13])
        rotate(270,[0,0,1])
            rotate(270,[1,0,0])
                promic_front_cutaway();
}

translate([-68,58.5,13])
rotate(270,[0,0,1])
rotate(270,[1,0,0])
promic_plate();
