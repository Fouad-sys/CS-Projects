class Cylinder {
  
  PShape openCylinder;
  
  final int cylinderResolution = 40;
  
  Cylinder() {
   openCylinder = new PShape();
  }
  
  void update() {
    float angle;
    float[] x = new float[cylinderResolution+ 1];
    float[] y = new float[cylinderResolution+ 1];//get the x and y position on a circle for all the sides
    for (int i = 0; i < x.length; i++) {
      angle= (TWO_PI/ cylinderResolution) * i;
      x[i] = sin(angle) * CYLINDER_BASE_SIZE;
      y[i] = cos(angle) * CYLINDER_BASE_SIZE;
    }
    openCylinder= createShape();

    openCylinder.beginShape(LINES);
    for (int i = 0; i <= cylinderResolution/2; i++) {
      openCylinder.vertex(x[i], y[i], CYLINDER_HEIGHT);
      openCylinder.vertex(x[(i+20)], y[(i+20)], CYLINDER_HEIGHT);
    }
    openCylinder.endShape();

    openCylinder.beginShape(QUAD_STRIP);//draw the border of the cylinder
    for (int i = 0; i < x.length; i++) {
      openCylinder.vertex(x[i], y[i], 0);
      openCylinder.vertex(x[i], y[i], CYLINDER_HEIGHT);
    }
    openCylinder.endShape();
  }

  void display2D(float x, float y) {
    gameSurface.pushMatrix();
    gameSurface.translate(x, y, 0);
    gameSurface.shape(openCylinder);
    gameSurface.popMatrix();
  }
  
  void display3D(float x, float z){
    gameSurface.pushMatrix();
    gameSurface.translate(x, 0, z);
    gameSurface.rotateX(PI/2);
    gameSurface.shape(openCylinder);
    gameSurface.popMatrix();
  }
}
