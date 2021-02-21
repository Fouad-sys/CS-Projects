import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class LineDetection {


  int[]accumulator;
  int phiDim;
  int rDim;
  PImage ei;


  List<PVector> hough(PImage edgeImg, int nLines) {
    ei = edgeImg;
    float discretizationStepsPhi = 0.06f;
    float discretizationStepsR = 2.5f;
    int minVotes = 100;

    // dimensions of the accumulator
    phiDim = (int) (Math.PI / discretizationStepsPhi+1);

    //The max radius is the image diagonal, but it can be also negative
    rDim = (int) ((sqrt(edgeImg.width*edgeImg.width+edgeImg.height*edgeImg.height) * 2) / discretizationStepsR+1);

    // pre-compute the sin and cos values
    float[]tabSin=new float[phiDim];
    float[]tabCos=new float[phiDim];
    float ang=0;
    float inverseR=1.f/discretizationStepsR;
    for (int accPhi=0; accPhi<phiDim; ang+=discretizationStepsPhi, accPhi++) {
      // we can also pre-multiply by (1/discretizationStepsR) since we need it in the Hough loop
      tabSin[accPhi]=(float)(Math.sin(ang)*inverseR);
      tabCos[accPhi]=(float)(Math.cos(ang)*inverseR);
    }

    // our accumulator
    accumulator=new int[phiDim* rDim];

    // Fill the accumulator: on edge points (ie, white pixels of the edge
    // image), store all possible (r, phi) pairs describing lines going
    // through the point.
    for (int y = 0; y < edgeImg.height; y++) {
      for (int x = 0; x < edgeImg.width; x++) {
        // Are we on an edge?
        if (brightness(edgeImg.pixels[y * edgeImg.width+ x]) != 0) {
          for (int phi = 0; phi < phiDim; phi++) {
            int rAcc =(int)(x*tabCos[phi] + y*tabSin[phi]);
            accumulator[phi * rDim + rAcc + rDim/2] += 1;
          }
        }
      }
    }
    ArrayList<PVector> lines=new ArrayList<PVector>();
    ArrayList<Integer> bestCandidates = new ArrayList<Integer>();

    int regionSize = 10;

    //Isolate local maxima algorithm, 
    for (int idx= 0; idx < accumulator.length; idx++) {
      if (accumulator[idx] > minVotes) {
        boolean isMax = true;
        for (int a = idx-regionSize/2; a < idx + regionSize/2; ++a) {
          if (accumulator[a] > accumulator[idx])
            isMax = false;
        }
        if (isMax)
          bestCandidates.add(idx);
      }
    }
    //Sort the best candidates by number of votes
    Collections.sort(bestCandidates, new HoughComparator(accumulator));

    // Only add the best nLines to the list
    for (int i = 0; i < nLines; i++) {
      int idx = bestCandidates.get(i);
      // first, compute back the (r, phi) polar coordinates:
      int accPhi= (int) (idx / (rDim));
      int accR= idx- (accPhi) * (rDim);
      float r = (accR- (rDim) * 0.5f) * discretizationStepsR;
      float phi = accPhi* discretizationStepsPhi;
      lines.add(new PVector(r, phi));
    }



    return lines;
  }

  void draw() {


    PImage houghImg= createImage(rDim, phiDim, ALPHA  );
    for (int i = 0; i < accumulator.length; i++) {
      houghImg.pixels[i] = color(min(255, accumulator[i]));
    }// You may want to resize the accumulator to make it easier to see:// 
    houghImg.resize(600, 600);
    houghImg.updatePixels();

    image(houghImg, 0, 0);
  }
}

class HoughComparator implements java.util.Comparator<Integer> {
  int[]accumulator;
  public HoughComparator(int[]accumulator) {
    this.accumulator=accumulator;
  }
  @Override
    public int compare(Integer l1, Integer l2) {
    if (accumulator[l1]>accumulator[l2]||(accumulator[l1]==accumulator[l2]&&l1<l2))return-1;
    return 1;
  }
}
