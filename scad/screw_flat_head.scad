/*
 * A screw with countersunk, flat head.
 * This model has no thread. It's used as cutting die for screw holes.
 *
 * d: diameter of the screw
 * dh: diameter of the head
 * ah: angle of the head
 * l: length of the *complete* screw (including head)
 * head_elongation: length of optional cylinder with diameter dh, placed on top of the head.
 *                  Useful since this model is mostly used as a cutting die. (default 0)
 * fn: circle resolution $fn for OpenSCAD (default 30)
 * echo_hh: Print the head height (for debug purposes, default: false)
 * screw_color: color of the screw (bolt and head)
 * elongation_color: color of the elongation cylinder (if specified)
 *
 * The screw head is lying on z=0, the screw bolt is going downwards (-z).
 * The screw is centered on x/y.
 * The elongation (if specified), goes upwards (+z).
 *
 * DIN 963 metric screws have a head angle (ah) of 90°.
 *
 * DIN 963 metric screw head diameters:
 *  • M 2     3,8 mm
 *  • M 2,5   4,7 mm
 *  • M 3     5,6 mm
 *  • M 4     7,5 mm
 *  • M 5     9,2 mm
 *  • M 6     11 mm
 *  • M 8     14,5 mm
 *  • M 10    18 mm
 *  • M 12    22  mm 
*/

module screw_flat_head(d, dh, ah, l, head_elongation=0, fn=30, echo_hh=false, screw_color="DodgerBlue", elongation_color="Orchid") {
    head_cylinder_height = (dh/2)/tan(ah/2);
    if (echo_hh) {
        echo(head_height=head_cylinder_height);
    }
    color(screw_color) {
        translate([0,0,-l]) cylinder(d=d, h=l, $fn=fn);
        translate([0,0,-head_cylinder_height]) cylinder(d1=0, d2=dh, h=head_cylinder_height, $fn=fn);
    }
    if (head_elongation > 0) {
        color(elongation_color)
            translate([0,0,-0.01]) cylinder(d=dh, h=head_elongation, $fn=fn);
    }
}

module sfh_m2_16() {
    screw_flat_head(d=2, dh=3.8, ah=90, l=16, echo_hh=true, head_elongation=3);
}


sfh_m2_16();
