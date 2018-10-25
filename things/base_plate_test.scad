difference () {
    union () {
        translate ([0, 0, 1]) {
            import("right-plate3-edit.svg_2mm.stl");
        };
        
        translate ([3.95, 3.45, 2.6]) {
            scale ([0.99, 0.99, 1]) {
        import("right-plate-relief3-edit.svg_5mm.stl");
        };
        };
        
    };
    translate ([23, 65, 2]) {
        cube([10,50,8]);
    };
    translate ([55, 20, 2]) {
        cube([15,15,8]);
    };
    translate ([115, 10, 2]) {
        cube([17,25,8]);
    };
    translate ([35, 125, 2]) {
        cube([45,25,8]);
    };
    translate ([103, 78, 2]) {
        cube([20,20,8]);
    };
    translate ([0, 0, 5]) {
        cube([200,200,8]);
    };

};    