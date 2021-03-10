class EdgeDetection {
  BlobDetection blobdetection =new BlobDetection();
 final int minHue = 80;
  final int maxHue = 140;
  final int minSaturation = 80;
  final int maxSaturation = 255;
  final int minBrightness = 0;
  final int maxBrightness = 255;
   /* final int minHue = 91;
  final int maxHue = 140;
  final int minSaturation = 80;
  final int maxSaturation = 255;
  final int minBrightness = 25;
  final int maxBrightness = 180;
  */

  final int threshold = 100;

  final float convoluteNormFactor = 99;

  final float[][] gaussianKernel = {{9, 12, 9}, 
    {12, 15, 12}, 
    {9, 12, 9}};                                     

  final float[][] scharrVKernel = {{ 3, 0, -3}, 
    {10, 0, -10}, 
    { 3, 0, -3}};

  final float[][] schaarHKernel = {{ 3, 10, 3}, 
    { 0, 0, 0}, 
    { -3, -10, -3}};

  PImage findEdgeDetection(PImage img) {
    PImage thresholdHSBImage = thresholdHSB(img, minHue, maxHue, minSaturation, maxSaturation, minBrightness, maxBrightness);
   //PImage blobDetectionImage = blobdetection.findConnectedComponents(thresholdHSBImage, true);
    PImage gaussianImage = convolute(thresholdHSBImage);
    PImage scharrImage = scharr(gaussianImage);
    PImage thresholdImage = threshold(scharrImage, threshold);
    return thresholdImage.copy  ();
  }

  PImage thresholdHSB(PImage img, int minH, int maxH, int minS, int maxS, int minB, int maxB) {
    PImage result = createImage(img.width, img.height, RGB);
    for (int x = 0; x < result.width; x++) {
      for (int y = 0; y < result.height; y++) {
        boolean hueThreshold = minH > hue(img.pixels[y*img.width+x]) || hue(img.pixels[y*img.width+x]) > maxH;
        boolean saturationThreshold = minS > saturation(img.pixels[y*img.width+x]) || saturation(img.pixels[y*img.width+x]) > maxS;
        boolean brightnessThreshold = minB > brightness(img.pixels[y*img.width+x]) || brightness(img.pixels[y*img.width+x]) > maxB;
        colorMode(HSB, 255); 
        result.pixels[y*result.width+x] = hueThreshold || saturationThreshold || brightnessThreshold ? color(0, 0, 0) : color(0, 0, 255);
      }
    }
    result.updatePixels();
    return result;
  }

  PImage convolute(PImage img) {
    float[][] kernel = gaussianKernel;
    float normFactor = convoluteNormFactor;
    PImage result = createImage(img.width, img.height, ALPHA);
    for (int x = 1; x < img.width - 1; x++) {
      for (int y = 1; y < img.height - 1; y ++) {
        result.pixels[y*img.width + x] = color(0, 0, (sumIntensities(img, x, y, kernel)/normFactor));
      }
    }
    result.updatePixels();
    return result;
  }

  PImage scharr(PImage img) {
    float[][] vKernel = scharrVKernel;
    float[][] hKernel = schaarHKernel;
    PImage result = createImage(img.width, img.height, ALPHA);
    for (int i = 0; i < img.width * img.height; i++) {
      result.pixels[i] = color(0);
    }
    float max = 0; 
    float[] buffer = new float[img.width * img.height];
    int sum_h = 0; 
    int sum_v = 0;
    float sum = 0;
    for (int x = 1; x < img.width - 1; x++) {
      for (int y = 1; y < img.height - 1; y ++) {
        sum_h = sumIntensities(img, x, y, hKernel);
        sum_v = sumIntensities(img, x, y, vKernel);
        sum = (float) Math.sqrt(Math.pow(sum_h, 2) + Math.pow(sum_v, 2));
        buffer[y * img.width + x] = sum;
        if (sum > max) max = sum;
      }
    }   
    for (int y = 1; y < img.height - 1; y++) { // Skip top and bottom edges 
      for (int x = 1; x < img.width - 1; x++) { // Skip left and right
        int val = (int) ((buffer[y * img.width + x] / max) * 255);
        result.pixels[y * img.width + x] = color(val);
      }
    }
    return result;
  }  

  PImage threshold(PImage img, int threshold) {
    // create a new, initially transparent, 'result' image 
    PImage result = createImage(img.width, img.height, RGB); 
    colorMode(HSB, 255);
    for (int i = 0; i < img.width * img.height; i++) {
      result.pixels[i] = (brightness(result.pixels[i]) > threshold) ? color(0, 0, 0) : color(0, 0, 255);
      result.updatePixels();
      result.pixels[i] = (brightness(img.pixels[i]) > threshold) ? color(0, 0, 255) : color(0, 0, 0);
      result.updatePixels();
    }
    return result;
  }

  int sumIntensities(PImage img, int x, int y, float[][] kernel) {
    int sum = 0;
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j <  2; j++) {
        sum += brightness(img.pixels[(y + j)*img.width + (x + i)]) * kernel[i + 1][j + 1];
      }
    }
    return sum;
  }
}
