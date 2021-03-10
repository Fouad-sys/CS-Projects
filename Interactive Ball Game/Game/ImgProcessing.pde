
import java.io.*; 
import gab.opencv.*;
import processing.video.*;

class ImageProcessing extends PApplet {

  Movie cam;
  PImage img;
  BlobDetection blobDetection;
  EdgeDetection edgeDetection;
  LineDetection lineDetection;
  QuadGraph quadGraph;
  OpenCV opencv;
  TwoDThreeD twothree;
  PVector rots;

  void settings() {
    size(1000, 1000);
  }


  void setup() {
    cam=new Movie(this, "testvideo.avi");
    cam.loop();
    //img = loadImage("board4.jpg");

    opencv= new OpenCV(this, 100, 100);

    blobDetection = new BlobDetection();
    edgeDetection = new EdgeDetection();
    lineDetection = new LineDetection();
    quadGraph = new QuadGraph();
    twothree = new TwoDThreeD(cam.width, cam.height, 1000);
    //noLoop();
  }

  void draw() {

    if (cam.available() ==true) {
      cam.read();
      cam.loadPixels();
    }

    img= cam.get();
    img.resize(700, 600);
    image(img, 0, 0);

    PImage edgeDetectionImage = edgeDetection.findEdgeDetection(img);

    List<PVector> lines = lineDetection.hough(edgeDetectionImage, 8);
    plotLines(lines, edgeDetectionImage);
    List<PVector> corners = quadGraph.findBestQuad(lines, img.height, img.width, img.height*img.width, 10, false);

    for (PVector corner : corners) {
      corner.z = 1;
      fill(random(255), random(255), random(255));
      circle(corner.x, corner.y, 30);
    }
    rots = twothree.get3DRotations(corners);
    if (rots.x < 0) {
      rots.x += PI;
    } else {
      rots.x -= PI;
    }
  }

  PVector getRot() {
    return rots;
  }


  boolean imagesEqual(PImage img1, PImage img2) {
    if (img1.width != img2.width || img1.height != img2.height)
      return false;
    for (int i = 0; i < img1.width*img1.height; i++)
      if (red(img1.pixels[i]) != red(img2.pixels[i])) 
        return false;
    return true;
  }


  void plotLines(List<PVector> lines, PImage ei) {
    // Plot the nLines from the list
    for (int idx = 0; idx< lines.size(); idx++) {
      PVector line=lines.get(idx);
      float r = line.x;
      float phi = line.y;
      // Cartesian equation of a line: y = ax + b
      // in polar, y = (-cos(phi)/sin(phi))x + (r/sin(phi))
      // => y = 0 : x = r / cos(phi)
      // => x = 0 : y = r / sin(phi)
      // compute the intersection of this line with the 4 borders of
      // the image
      int x0 = 0;
      int y0 = (int) (r / sin(phi));
      int x1 = (int) (r / cos(phi));
      int y1 = 0;
      int x2 = ei.width;
      int y2 = (int) (-cos(phi) / sin(phi) * x2 + r / sin(phi));
      int y3 = ei.width;
      int x3 = (int) (-(y3 - r / sin(phi)) * (sin(phi) / cos(phi)));
      // Finally, plot the lines

      stroke(204, 102, 0);
      if (y0 > 0) {
        if (x1 > 0)line(x0, y0, x1, y1);
        else if (y2 > 0)line(x0, y0, x2, y2);
        else line(x0, y0, x3, y3);
      } else {
        if (x1 > 0) {
          if (y2 > 0)line(x1, y1, x2, y2);
          else line(x1, y1, x3, y3);
        } else line(x2, y2, x3, y3);
      }
    }
  }
  
  PImage addImages(PImage img1, PImage img2) {
    if (img1.width != img2.width || img1.height != img2.height) {
      return null;
    }
    
    img1.loadPixels();
    img2.loadPixels();
    PImage result = img1.copy();
    for (int i = 0; i<img1.height; i++) {
      for (int j = 0; j< img1.width; j++) {
        result.pixels[i * img1.width + j] = img1.pixels[i * img1.width + j] + img2.pixels[i * img1.width + j];
      }
    }
    result.updatePixels();
    return result;
  }
}
