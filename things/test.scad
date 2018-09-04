difference () {
  union () {
    translate ([-75.44375108833596, 29.30644042799168, 2]) {
      cube ([10.2, 12, 4], center=true);
    }
  }
  union () {
    translate ([-75.44375108833596, 35.30644042799168, 5]) {
      rotate (a=90.0, v=[1, 0, 0]) {
        cylinder ($fn=30, h=20, r=2.55, center=true);
      }
    }
    translate ([-75.44375108833596, 28.30644042799168, 5]) {
      cube ([6.2, 10, 6], center=true);
    }
  }
}
