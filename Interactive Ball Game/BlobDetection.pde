import java.util.ArrayList; 
import java.util.List; 
import java.util.TreeSet;

class BlobDetection {

  PImage findConnectedComponents(PImage input, boolean onlyBiggest) {

    // First pass: label the pixels and store labels' equivalences
    int [] labels = new int [input.width*input.height];
    List<TreeSet<Integer>> labelsEquivalences = new ArrayList<TreeSet<Integer>>();
    int currentLabel = 1; 
    PImage result = input.copy();
    input.loadPixels();
    result.loadPixels();

    for (int x = 0; x < input.height; x++) {
      for (int y = 0; y < input.width; y++) {
        if (brightness(input.pixels[x * input.width + y]) == 255) {
          ArrayList<Integer> neighbors = getNeighbors(input, y, x, labels);
          if (!neighbors.isEmpty()) {
            if (allEqual(neighbors)) {
              labels[x * input.width + y] = neighbors.get(0);
            } else {
              labels[x * input.width + y] = getLabel(neighbors);
              
              TreeSet<Integer> equivalentSet = new TreeSet();
              equivalentSet.addAll(neighbors);
              /*
              for (int j = labelsEquivalences.size()-1; j>=0; j--) {
                for (int n : equivalentSet) {
                  if (labelsEquivalences.get(j).contains(n)) {
                      equivalentSet.addAll(labelsEquivalences.get(j));
                      break;
                    }
                }
              }
              */
              for (int j = 0; j < labelsEquivalences.size(); j++) {
                for (int i = 0; i < j; i++) {
                  for (int n : equivalentSet) {
                    if (labelsEquivalences.get(i).contains(n)) {
                      equivalentSet.addAll(labelsEquivalences.get(i));
                      break;
                    }
                  }
                }
              }
              
              for (int n : equivalentSet) {
                labelsEquivalences.get(n-1).addAll(equivalentSet);
              }
            }
          } else {
            labelsEquivalences.add(new TreeSet<Integer>());
            labelsEquivalences.get(currentLabel - 1).add(currentLabel);
            labels[x * input.width + y] = currentLabel;
            ++currentLabel;
          }
        }
      }
    }

    // Second pass: re-label the pixels by their equivalent class
    // if onlyBiggest==true, count the number of pixels for each label
    HashMap<Integer, Integer> numberOfPixels = new HashMap<Integer, Integer>();
    for (int x = 0; x < input.height; x++) {
      for (int y = 0; y < input.width; y++) {
        if (brightness(input.pixels[x * input.width + y]) == 255) {
          int label = labels[x * input.width + y];
          int labelEquivalence = labelsEquivalences.get(label - 1).first();
          ArrayList<Integer> neighborIndices = getNeighborIndices(input, y, x, labels);
          labels[x * input.width + y] = labelEquivalence;
          for (Integer index : neighborIndices) {
            labels[index] = labelEquivalence;
          }
        }
      }
    }

    if (onlyBiggest) {
      for (int x = 0; x < input.height; x++) {
        for (int y = 0; y < input.width; y++) {
          if (labels[x * input.width + y] != 0) {
            numberOfPixels.put(labels[x * input.width + y], numberOfPixels.getOrDefault(labels[x * input.width + y], 0) + 1);
          }
        }
      }
    }
    /*
      if (onlyBiggest) {
     int pixelCount = numberOfPixels.getOrDefault(labels[x * input.width + y], 0);
     numberOfPixels.putIfAbsent(labels[x * input.width + y], ++pixelCount);
     numberOfPixels.replace(labels[x * input.width + y], ++pixelCount);
     }
     */

    // Finally:
    // if onlyBiggest==false, output an image with each blob colored in one uniform color
    // if onlyBiggest==true, output an image with the biggest blob in white and others in black
    if (onlyBiggest) {
      int biggestBlob = 1;
      for (int blob : numberOfPixels.keySet()) {
        if (numberOfPixels.get(blob) > numberOfPixels.get(biggestBlob)) biggestBlob = blob;
      }
      for (int x = 0; x < input.height; x++) {
        for (int y = 0; y < input.width; y++) {
          if (brightness(input.pixels[x * input.width + y]) == 255) {
            result.pixels[x * input.width + y] = (labels[x * input.width + y] != biggestBlob) ? color(0) : color(255);
          } else {
            result.pixels[x * input.width + y] = color(0);
          }
        }
      }
    } else {
      HashMap<Integer, Integer> colors = new HashMap<Integer, Integer>();
      for (int i = 1; i<=labelsEquivalences.size(); i++) {
        colors.put(i, color(random(255), random(255), random(255)));
      }
      for (int x = 0; x < input.height; x++) {
        for (int y = 0; y < input.width; y++) {
          //colors.putIfAbsent(labels[x * input.width + y], color(random(255), random(255), random(255)));
          if (brightness(input.pixels[x * input.width + y]) == 255) {
            result.pixels[x * input.width + y] = colors.get(labels[x * input.width + y]);
          }
        }
      }
    }
    result.updatePixels();
    return result;
  }
}

boolean allEqual(ArrayList<Integer> neighbors) {
  for (int i = 0; i < neighbors.size(); i++) {
    if ((i + 1) != neighbors.size() && neighbors.get(i) != neighbors.get(i+1)) {
      return false;
    }
  }
  return true;
}

int getLabel(ArrayList<Integer> neighbors) {
  int min = neighbors.get(0);
  for (int i = 0; i < neighbors.size(); i++) {
    if (neighbors.get(i) < min) min = neighbors.get(i);
  }
  return min;
}

ArrayList<Integer> getNeighborIndices(PImage input, int x, int y, int[] labels) {
  ArrayList<Integer> indices = new ArrayList<Integer>();
  int topLeft = getNeighbor(input, x-1, y-1, labels);
  if (topLeft != 0) {
    indices.add((y-1)*input.width +x-1);
  }
  int top = getNeighbor(input, x, y-1, labels);
  if (top != 0) {
    indices.add((y-1)*input.width +x);
  }
  int topRight = getNeighbor(input, x+1, y-1, labels);
  if (topRight != 0) {
    indices.add((y-1)*input.width +x+1);
  }
  int left = getNeighbor(input, x-1, y, labels);
  if (left != 0) {
    indices.add((y)*input.width +x-1);
  }
  return indices;
}

ArrayList<Integer> getNeighbors(PImage input, int x, int y, int[] labels) {
  ArrayList<Integer> neighbors = new ArrayList<Integer>();
  int topLeft = getNeighbor(input, x-1, y-1, labels);
  if (topLeft != 0) {
    neighbors.add(topLeft);
  }
  int top = getNeighbor(input, x, y-1, labels);
  if (top != 0) {
    neighbors.add(top);
  }
  int topRight = getNeighbor(input, x+1, y-1, labels);
  if (topRight != 0) {
    neighbors.add(topRight);
  }
  int left = getNeighbor(input, x-1, y, labels);
  if (left != 0) {
    neighbors.add(left);
  }
  return neighbors;
}

int getNeighbor(PImage input, int i, int j, int[] labels) {
  return (0 <= i && i < input.width && 0 <= j && j < input.height) ? labels[j * input.width + i] : 0;
}
