use <screw_flat_head.scad>

module right_plate(){
    holes = [
        [14.5, 60.2],
        [75.8, 37.2],
        [14.5, 146.9],
        [95.6, 166],
        [151.5,  117.9],
    ];
    difference(){
        translate([0,-135.5]) {
            linear_extrude(height=2.5) 
            import(file="right-plate-shape.svg");

        }
        for(p = holes){
            translate(p) rotate(180, [1,0,0]) translate([0, 0, -0.2])
                screw_flat_head(d=3.2, dh=5.8, ah=90, l=5, head_elongation=2);
        }
    }
}

right_plate();
