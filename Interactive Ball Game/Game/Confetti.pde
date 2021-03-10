// Confetti based on a particle system
class Confetti {
  
  ArrayList<PVector> particles;
  ArrayList<PVector> velocities;
  ArrayList<Float> colors;
  PVector acceleration;
  PVector origin;
  float gravityConstant = 0.5;
  int maxConfettiSize = 500;
  float ballSize = 10;
  
  Confetti(PVector origin) {
    this.origin = origin;
    particles = new ArrayList<PVector> ();
    velocities = new ArrayList<PVector> ();
    acceleration = new PVector(0, gravityConstant);
    colors = new ArrayList<Float>();
  }
  
  void addParticle() {
    for (PVector v: velocities) {
      v.add(acceleration);
    }
    for (PVector p: particles) {
      p.add(velocities.get(particles.indexOf(p)));
    }
    
    if(particles.size() < maxConfettiSize) {
      for(int i = 0; i< random(20); i++) {
        colors.add(random(255));
        colors.add(random(255));
        colors.add(random(255));
        float angle = random(TWO_PI);
        float v0 = random(20);
    
        PVector vinit = new PVector(v0*cos(angle), v0*sin(angle));
        velocities.add(vinit.copy());
        particles.add(origin.copy());
      }
    }
  }
  
  // The player or user has the option here to give the confetti pieces changing colors or one constant color  
  // throughout the explosion by uncommenting the two lines in the for loop
  void display() {
    noStroke();
    for (PVector p : particles) {
      fill(random(255), random(255), random(255));
      //int index = particles.indexOf(p);
      //fill(colors.get(index), colors.get(index+1), colors.get(index+2));
      ellipse(p.x, p.y, ballSize, ballSize);
    }
  }
}
