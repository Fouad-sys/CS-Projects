class Sphere {
  
  PVector location;
  PVector velocity;
  
  PVector gravityForce;
  PShape s = new PShape();
  
  float gravityConstant = 0.5;
  float normalForce = 1;
  float mu = 0.1;
  float frictionMagnitude = normalForce * mu;

  Sphere() {
    location = new PVector(0, -SPHERE_DIAMETER, 0);
    velocity = new PVector(0, 0, 0);
    gravityForce = new PVector(0, 0, 0);
  }
  
  void setup() {
   sphere.s = createShape(SPHERE, SPHERE_RADIUS);
    sphere.s.setStroke(false);
    sphere.s.setTexture(img); 
  }
  
  void update(float rotX, float rotZ) {
    gravityForce.x = sin(rotZ) * gravityConstant;
    gravityForce.z = -sin(rotX) * gravityConstant;
    
    //Only add gravity if the ball is on the plate else it will roll off!
    if(Game.PLATE_SIZE > location.z && location.z > -Game.PLATE_SIZE && 
       Game.PLATE_SIZE > location.x && location.x > -Game.PLATE_SIZE) {
      velocity.add(gravityForce);
      
      //Add friction
      PVector friction = velocity.copy();
      friction.mult(-1);
      friction.normalize();
      friction.mult(frictionMagnitude);
      velocity.add(friction);
    }
    
    location.add(velocity);
  }
  
  void display() {
    gameSurface.pushMatrix();
    gameSurface.translate(location.x, location.y, location.z);
    gameSurface.shape(s);
    gameSurface.popMatrix();
  }
  
  void checkXEdges() {
    if (location.x > (Game.PLATE_SIZE)) {
      velocity.x *= -1;
    }
    else if (location.x < (-Game.PLATE_SIZE)) {
      velocity.x *= -1;
    }
  }
  
  void checkZEdges() {
    if (location.z > (Game.PLATE_SIZE)) {
      velocity.z *= -1;
    } else if (location.z < (-Game.PLATE_SIZE)) {
      velocity.z *= -1;
    }
  }
}
