use <promicro.scad>

module promic_plate(){
    color("pink") translate([0, -12, -2]) cube([40, 24, 2]);
    color("orange") promic_mount();
}
