// A class to describe a group of Particles
class ParticleSystem { 
  
  ArrayList<PVector> centers;
  PVector origin;
  boolean added = false;

  ParticleSystem(PVector origin) {
    this.origin = origin.copy();
    centers = new ArrayList<PVector>(); 
    centers.add(origin);
  }
  
  ParticleSystem() {
    this.origin = null;
    centers = new ArrayList<PVector>(); 
  }

  void addParticle() {
    added = false;
    if(centers.isEmpty()) return;
    PVector center;
    int numAttempts = 100;
    for(int i=0; i < numAttempts; i++) {
      // Pick a cylinder and its center.
      int index = int(random(centers.size())); 
      center = centers.get(index).copy();
      
      // Try to add an adjacent cylinder.
      float angle = random(TWO_PI);
      center.x += sin(angle) * CYLINDER_DIAMETER; center.y += cos(angle) * CYLINDER_DIAMETER; 
      if(checkPosition(center)) {
        centers.add(center);
        added = true;
        break; 
      }
    }
   }
   
  void run(Cylinder cylinder) {
    if(centers.isEmpty()) return;
    for(PVector center : centers) {
      cylinder.display3D(center.x - gameSurface.width/2, center.y - gameSurface.height/2);
    }
  }
  
  boolean checkPosition(PVector otherCenter) {
    boolean isInsideBoard = otherCenter.x + CYLINDER_BASE_SIZE < gameSurface.width/2 + Game.PLATE_SIZE && 
                            otherCenter.x - CYLINDER_BASE_SIZE > gameSurface.width/2 - Game.PLATE_SIZE && 
                            otherCenter.y + CYLINDER_BASE_SIZE < gameSurface.height/2 + Game.PLATE_SIZE&& 
                            otherCenter.y - CYLINDER_BASE_SIZE > gameSurface.height/2 - Game.PLATE_SIZE;
                            
    boolean isNotOnTopOfBall = otherCenter.x + CYLINDER_BASE_SIZE > gameSurface.width/2 + PLATE_Y + SPHERE_RADIUS || 
                               otherCenter.x + CYLINDER_BASE_SIZE < gameSurface.width/2 - PLATE_Y + SPHERE_RADIUS ||
                               otherCenter.y + CYLINDER_BASE_SIZE > gameSurface.height/2 + PLATE_Y + SPHERE_RADIUS || 
                               otherCenter.y + CYLINDER_BASE_SIZE < gameSurface.height/2 - PLATE_Y + SPHERE_RADIUS;
    for(PVector center : centers) {
      if(checkOverlap(center, otherCenter)) {
        return false;
      }
    }
    return isInsideBoard && isNotOnTopOfBall;
  }
  
  // Check if a particle with center c1
  // and another particle with center c2 overlap. 
  boolean checkOverlap(PVector c1, PVector c2) {
    return c1.x < c2.x + CYLINDER_DIAMETER && c1.x > c2.x - CYLINDER_DIAMETER && 
           c1.y < c2.y + CYLINDER_DIAMETER && c1.y > c2.y - CYLINDER_DIAMETER;
  }
}
