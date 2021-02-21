import processing.video.*;

PGraphics gameSurface;
PGraphics databoard;
PGraphics topView;
PGraphics scoreboard;
PGraphics barChart;
HScrollbar hs;

Confetti confetti;
boolean victory = false;

//public ImageProcessing imgproc;

float score = 0;
float lastScore = 0;

float squareX = 0;
float squareY;
float squareSize = 5;
int startingPoint = 0;
ArrayList<Integer> bars;
float barExpansion = 15;
float barChartX = 475;
float barChartY;
float s = millis();
float vicsec;


PImage img;
PImage background;
PShape robotnik;

Sphere sphere;
Cylinder cylinder;
ParticleSystem cylinders;

float rotX = 0;
float rotZ = 0;
PVector rot = new PVector(0, 0, 0);
float incre = 0.05;
int value = 0;
int robotnikScale = 30;

static final int PLATE_SIZE = 300;
static final int PLATE_X = 600;
static final int PLATE_Y = 40;
static final int PLATE_Z = PLATE_X;

static final int SPHERE_RADIUS = 20;
static final int SPHERE_DIAMETER = 2 * SPHERE_RADIUS;

static final int CYLINDER_BASE_SIZE = 35;
static final int CYLINDER_HEIGHT = 60;
static final int CYLINDER_DIAMETER = 2 * CYLINDER_BASE_SIZE;

void settings() {
  size(1000, 1000, P3D);
}

void setup() {/*
  imgproc=new ImageProcessing();
  String[]args= {"Image processing window"};
  PApplet.runSketch(args, imgproc);*/

  gameSurface = createGraphics(width, height - 300, P3D);
  databoard = createGraphics(width, 600, P2D);
  topView = createGraphics(300, 300, P2D);
  scoreboard = createGraphics(155, 280, P2D);
  barChart = createGraphics(525, 280, P2D);
  bars = new ArrayList<Integer>();
  hs = new HScrollbar(width - 410, height - 40, 300, 20);
  squareY = barChart.height/2;
  barChartY = height - 290;
  img= loadImage("earth10.jpeg");
  background = loadImage("spaceres.jpg");
  cylinder = new Cylinder();
  confetti = new Confetti(new PVector(width/2, height/2));

  sphere = new Sphere();
  sphere.setup();



  cylinders = new ParticleSystem();

  robotnik = loadShape("robotnik.obj");
  texture(loadImage("robotnik.png"));
}

void drawGame() {
  gameSurface.beginDraw();
  if (keyPressed && key == CODED && keyCode == SHIFT) {
    cylinder.update();
    if (mousePressed && mouseButton == LEFT) {
      boolean collision = checkCollision(mouseX, mouseY);
      if (!collision) {
        gameSurface.pushMatrix();
        gameSurface.background(background);
        gameSurface.translate(gameSurface.width/2, gameSurface.height/2, 0);
        gameSurface.rect(-PLATE_SIZE, -PLATE_SIZE, PLATE_X, PLATE_Z);
        gameSurface.circle(0, 0, PLATE_Y);
        gameSurface.popMatrix();
        cylinders.centers.clear();
        cylinders = new ParticleSystem(new PVector(mouseX, mouseY));
        cylinder.display2D(mouseX, mouseY);
      }
    }
    displayRobotnik2D();
  } else {/*
    rot = imgproc.getRot();
    if (rot == null) {
      rot = new PVector(0, 0, 0);
    }*/
    rotX = rot.x*0.75;
    rotZ = rot.z*0.75;
    gameSurface.background(background);
    gameSurface.lights();
    gameSurface.pushMatrix();
    gameSurface.translate(gameSurface.width/2, gameSurface.height/2, 0);
    gameSurface.rotateX(rotX);
    gameSurface.rotateZ(rotZ);

    gameSurface.box(PLATE_X, PLATE_Y, PLATE_Z);

    cylinders.addParticle();
    cylinders.run(cylinder);
    displayRobotnik3D();
    sphere.update(rotX, rotZ);
    sphere.checkXEdges();
    sphere.checkZEdges();
    checkCylinderCollision();
    sphere.display();
    gameSurface.popMatrix();
  }
  gameSurface.endDraw();
}

void drawTopView() {
  topView.beginDraw();
  topView.fill(57, 100, 195);
  topView.rect(0, 0, topView.width, topView.height);
  topView.fill(0, 0, 200);
  topView.pushMatrix();
  topView.translate(topView.width/2, topView.height/2);
  topView.ellipse((sphere.location.x/PLATE_X)*topView.width, (sphere.location.z/PLATE_Z)*topView.height, SPHERE_RADIUS, SPHERE_RADIUS);
  for (int i = 0; i< cylinders.centers.size(); i++) {
    float cylX = cylinders.centers.get(i).x - gameSurface.width/2;
    float cylY = cylinders.centers.get(i).y - gameSurface.height/2;
    topView.noStroke();

    if (cylX == cylinders.origin.x - gameSurface.width/2 && cylY == cylinders.origin.y - gameSurface.height/2) {
      topView.fill(250, 0, 0);
      topView.ellipse(((cylinders.origin.x - gameSurface.width/2)/PLATE_X)*topView.width, ((cylinders.origin.y - gameSurface.height/2)/PLATE_Z)*topView.height, CYLINDER_BASE_SIZE, CYLINDER_BASE_SIZE);
    } else {
      topView.fill(250);
      topView.ellipse(((cylinders.centers.get(i).x - gameSurface.width/2)/PLATE_X)*topView.width, ((cylinders.centers.get(i).y - gameSurface.height/2)/PLATE_Z)*topView.height, CYLINDER_BASE_SIZE, CYLINDER_BASE_SIZE);
    }
  }
  topView.popMatrix();
  topView.endDraw();
}

void drawScoreBoard() {
  scoreboard.beginDraw();
  scoreboard.background(150);
  scoreboard.stroke(250);
  scoreboard.fill(215);
  scoreboard.rect(2.5, 2.5, scoreboard.width-5, scoreboard.height-5);
  scoreboard.textSize(20);
  scoreboard.fill(50);
  scoreboard.text("Total Score :", 10, 30);
  if (cylinders.added) score -= 100;
  scoreboard.text(score, 10, 60);
  scoreboard.text("Velocity : ", 10, 130);
  scoreboard.text(sphere.velocity.mag(), 10, 160);
  scoreboard.text("Last Score :", 10, 230);
  scoreboard.text(lastScore, 10, 260);
  scoreboard.endDraw();
}

int squareNum(float score) {
  //float divider = lastScore == 0 ? 10000 : lastScore;
  return ceil((score/8000)*(barChart.height/2));
}

void scrollDisplay() {
  hs.update();
  barChart.noStroke();
  barChart.fill(204);
  barChart.rect(hs.xPosition - barChartX, hs.yPosition - barChartY, hs.barWidth, hs.barHeight);
  if (hs.mouseOver || hs.locked) {
    barChart.fill(0, 0, 0);
  } else {
    barChart.fill(102, 102, 102);
  }
  barChart.rect(hs.sliderPosition - barChartX, hs.yPosition - barChartY, hs.barHeight, hs.barHeight);
}

void drawChart() {
  barChart.beginDraw();
  barChart.fill(0, 0, 250);
  barChart.stroke(250);
  if (millis()-s >=1000) {
    bars.add(squareNum(score));
    s = millis();
  }
  float newSize = squareSize + barExpansion*(hs.getPos() - 0.5);
  for (int j = startingPoint; j<bars.size(); j++) {
    if (bars.get(j) >= 0) {
      for (int i = 0; i<abs(bars.get(j)); i++) {
        barChart.rect(squareX, squareY, newSize, squareSize);
        squareY -= squareSize;
      }
    } else {
      for (int i = 0; i<abs(bars.get(j)); i++) {
        barChart.rect(squareX, squareY, newSize, squareSize);
        squareY += squareSize;
      }
    }
    squareY = barChart.height/2;
    squareX += newSize;
  }
  squareX = 0;
  if (squareX >= barChart.width - newSize) startingPoint++;
  scrollDisplay();
  barChart.endDraw();
}


void drawBarChart() {
  barChart.beginDraw();
  barChart.background(150);
  barChart.stroke(250);
  barChart.fill(235);
  barChart.rect(2.5, 2.5, barChart.width-5, barChart.height-5);
  drawChart();
  barChart.endDraw();
}

void draw() {
  drawGame();
  image(gameSurface, 0, 0);
  databoard.beginDraw();
  databoard.background(200);
  databoard.endDraw();
  image(databoard, 0, height-300);
  drawTopView();
  image(topView, 0, height-300);
  drawScoreBoard();
  image(scoreboard, 310, height-290);
  drawBarChart();
  image(barChart, barChartX, barChartY);
    if(victory) {
    fill(0);
    noStroke();
    rect(0, 0, width, height);
    confetti.addParticle();
    confetti.display();
    if (millis() - vicsec > 6000) {
      victory = false;
      confetti.particles = new ArrayList<PVector>();
      confetti.velocities = new ArrayList<PVector>();
    }
  }
}

void mouseDragged() {
 if (!(mouseY < gameSurface.height)) return;
 if (pmouseY > mouseY) {
 if (rotX < PI/3)
 rotX = rotX + incre;
 } else if (pmouseY < mouseY) {
 if (rotX > -PI/3)
 rotX = rotX - incre;
 } else if (pmouseX < mouseX) {
 if (rotZ < PI/3)
 rotZ = rotZ + incre;
 } else if (pmouseX > mouseX) {
 if (rotZ > -PI/3)
 rotZ = rotZ - incre;
 }
 }

void mouseWheel(MouseEvent event) {
  if (event.getCount() < 0 && incre < 0.15) {
    incre = incre + 0.01;
  } else if (event.getCount() > 0 && incre > 0.05) {
    incre = incre - 0.01;
  }
}

void keyPressed() {
  if (key == CODED) {
    if (keyCode == SHIFT) {
      gameSurface.beginDraw();
      gameSurface.pushMatrix();
      gameSurface.background(background);
      gameSurface.translate(gameSurface.width/2, gameSurface.height/2, 0);
      gameSurface.rect(-PLATE_SIZE, -PLATE_SIZE, PLATE_X, PLATE_Z);
      gameSurface.circle(0, 0, PLATE_Y);
      gameSurface.popMatrix();
      for (int i = 0; i < cylinders.centers.size(); i++) {
        cylinder.display2D(cylinders.centers.get(i).x, cylinders.centers.get(i).y);
      }
      gameSurface.endDraw();
    }
  }
}

boolean checkCollision(float mouseX, float mouseY) {
  boolean collision = false;
  //Checking that x and y values of mouse are not within a diameter of 70 from another cylinder
  for (int i = 0; i < cylinders.centers.size(); i++) {
    if (!(mouseX > cylinders.centers.get(i).x + CYLINDER_DIAMETER || mouseX < cylinders.centers.get(i).x - CYLINDER_DIAMETER || 
      mouseY < cylinders.centers.get(i).y - CYLINDER_DIAMETER || mouseY > cylinders.centers.get(i).y + CYLINDER_DIAMETER))
      collision = true;
  }

  //Check that the mouse is not on top of the ball
  if (!(mouseY > gameSurface.height/2 + PLATE_Y + SPHERE_RADIUS || mouseY < gameSurface.height/2 - PLATE_Y + SPHERE_RADIUS || 
    mouseX > gameSurface.width/2 + PLATE_Y + SPHERE_RADIUS || mouseX < gameSurface.width/2 - PLATE_Y + SPHERE_RADIUS))
    collision = true;

  //Check that the mouse is not outside the plate
  if ((mouseX + CYLINDER_BASE_SIZE > gameSurface.width/2 + PLATE_SIZE || mouseX - CYLINDER_BASE_SIZE < gameSurface.width/2 - PLATE_SIZE || 
    mouseY - CYLINDER_BASE_SIZE < gameSurface.height/2 - PLATE_SIZE || mouseY + CYLINDER_BASE_SIZE > gameSurface.height/2 + PLATE_SIZE))
    collision = true;

  return collision;
}

void checkCylinderCollision() {
  PVector n = new PVector(0, 0, 0);
  float dot;
  float sphereX = sphere.location.x;
  float sphereZ = sphere.location.z;

  for (int i = 0; i< cylinders.centers.size(); i++) {
    float cylX = cylinders.centers.get(i).x - gameSurface.width/2;
    float cylY = cylinders.centers.get(i).y - gameSurface.height/2;

    if ((sphereX  < cylX + CYLINDER_BASE_SIZE) && 
      (sphereX  > cylX - CYLINDER_BASE_SIZE) && 
      (sphereZ  < cylY + CYLINDER_BASE_SIZE) && 
      (sphereZ > cylY - CYLINDER_BASE_SIZE)) {

      n.x = sphere.location.x - cylX;
      n.z = sphere.location.z - cylY;
      n.normalize();
      dot = sphere.velocity.dot(n);
      n.mult(dot * 2);
      sphere.velocity.sub(n);

      if (cylX == cylinders.origin.x - gameSurface.width/2 && cylY == cylinders.origin.y - gameSurface.height/2) {
        cylinders.centers.clear();
        cylinders = new ParticleSystem(); 
        score += 50*sphere.velocity.mag();
        lastScore = score;
        victory = true;
        vicsec = millis();
        return;
      } else { 
        cylinders.centers.remove(i);
        score += 20*sphere.velocity.mag();
        lastScore = score;
      }
    }
  }
}

void displayRobotnik2D() {
  if (cylinders.centers.contains(cylinders.origin)) {
    gameSurface.pushMatrix();
    gameSurface.translate(cylinders.origin.x, cylinders.origin.y, CYLINDER_HEIGHT);
    gameSurface.rotateX(PI/2);
    gameSurface.scale(robotnikScale);
    gameSurface.shape(robotnik);
    gameSurface.popMatrix();
  }
}

void displayRobotnik3D() {
  if (cylinders.centers.contains(cylinders.origin)) {
    PVector robotnikFollowingSphere = new PVector(sphere.location.x - cylinders.origin.x + gameSurface.width/2, sphere.location.z - cylinders.origin.y + gameSurface.height/2);
    float angle = PVector.angleBetween(new PVector(0, 1), robotnikFollowingSphere);
    if (sphere.location.x - cylinders.origin.x + gameSurface.width/2 > 0) {
      angle = -angle;
    }
    gameSurface.pushMatrix();
    gameSurface.translate(0, -CYLINDER_HEIGHT, 0);
    gameSurface.translate(cylinders.origin.x - gameSurface.width/2, 0, cylinders.origin.y - gameSurface.height/2);
    gameSurface.scale(robotnikScale);
    gameSurface.rotateX(PI);
    gameSurface.rotateY(angle + PI);
    gameSurface.shape(robotnik);
    gameSurface.popMatrix();
  }
}
