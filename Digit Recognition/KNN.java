package cs107KNN;

import java.util.Arrays;

public class KNN {
	public static void main(String[] args) {
		byte b1 = 40; // 00101000
		byte b2 = 20; // 00010100
		byte b3 = 10; // 00001010
		byte b4 = 5; // 00000101

		// [00101000 | 00010100 | 00001010 | 00000101] = 672401925
		int result = extractInt(b1, b2, b3, b4);
		System.out.println(result);

		String bits = "10000001";
		System.out.println("La sequence de bits " + bits + "\n\tinterprétée comme byte non signé donne "
				+ Helpers.interpretUnsigned(bits) + "\n\tinterpretée comme byte signé donne "
				+ Helpers.interpretSigned(bits));

		int TESTS = 1000;
		int K = 7;
		byte[][][] trainImages = parseIDXimages(Helpers.readBinaryFile("datasets/5000-per-digit_images_train"));
		byte[] trainLabels = parseIDXlabels(Helpers.readBinaryFile("datasets/5000-per-digit_labels_train"));
		byte[][][] testImages = parseIDXimages(Helpers.readBinaryFile("datasets/10k_images_test"));
		byte[] testLabels = parseIDXlabels(Helpers.readBinaryFile("datasets/10k_labels_test"));
		byte[] predictions = new byte[TESTS];
		for (int i = 0; i < TESTS; i++) {
			predictions[i] = knnClassify(testImages[i], trainImages, trainLabels, K);
		}
		Helpers.show("Test", testImages, predictions, testLabels, 20, 35);

		byte[] predictionss = new byte[TESTS];
		long start = System.currentTimeMillis();
		for (int i = 0; i < TESTS; i++) {
			predictionss[i] = knnClassify(testImages[i], trainImages, trainLabels, K);
		}
		long end = System.currentTimeMillis();
		double time = (end - start) / 1000d;
		System.out.println("Accuracy = " + accuracy(predictionss, Arrays.copyOfRange(testLabels, 0, TESTS)));
		System.out.println("Time = " + time + " seconds");
		System.out.println("Time per test image = " + (time / TESTS));

	}

    /*Methode pour decaler la valeur d'un byte signe*/
	public static byte pixelValue(byte b) {
		return (byte) ((b & 0xFF) - 128);
	}

	/**
	 * Composes four bytes into an integer using big endian convention.
	 *
	 * @param bXToBY The byte containing the bits to store between positions X and Y
	 * 
	 * @return the integer having form [ b31ToB24 | b23ToB16 | b15ToB8 | b7ToB0 ]
	 */
	public static int extractInt(byte b31ToB24, byte b23ToB16, byte b15ToB8, byte b7ToB0) {
		int extracted = (((b7ToB0) & 0xFF) | (((b15ToB8 & 0xFF) << 8)) | (((b23ToB16 & 0xFF) << 16))
				| (((b31ToB24 & 0xFF) << 24)));
		return extracted;
	}

	/**
	 * Parses an IDX file containing images
	 *
	 * @param data the binary content of the file
	 *
	 * @return A tensor of images
	 */
	public static byte[][][] parseIDXimages(byte[] data) {

		int magicnum = extractInt(data[0], data[1], data[2], data[3]);
		int imagenum = extractInt(data[4], data[5], data[6], data[7]);
		int imagehauteur = extractInt(data[8], data[9], data[10], data[11]);
		int imagelargeur = extractInt(data[12], data[13], data[14], data[15]);
		byte[][][] tensor = new byte[imagenum][imagehauteur][imagelargeur];
		int p = 16;
		for (int i = 0; i < imagenum; i++) {
			for (int k = 0; k < imagehauteur; k++) {
				for (int m = 0; m < imagelargeur; m++) {
					tensor[i][k][m] = pixelValue(data[p]);
					p = p + 1;
				}
			}
		}

		if (magicnum == 2051) {
			return tensor;

		} else {
			return null;
		}
	}

	/**
	 * Parses an idx images containing labels
	 *
	 * @param data the binary content of the file
	 *
	 * @return the parsed labels
	 */
	public static byte[] parseIDXlabels(byte[] data) {
		int magicnum2 = extractInt(data[0], data[1], data[2], data[3]);
		int labelnum = extractInt(data[4], data[5], data[6], data[7]);
		byte[] labels = new byte[labelnum];
		int n = 8;
		for (int i = 0; i < labelnum; i++) {
			labels[i] = data[n];
			n = n + 1;
		}
		if (magicnum2 == 2049) {
			return labels;
		} else {
			return null;
		}
	}

	/**
	 * @brief Computes the squared L2 distance of two images
	 * 
	 * @param a, b two images of same dimensions
	 * 
	 * @return the squared euclidean distance between the two images
	 */
	public static float squaredEuclideanDistance(byte[][] a, byte[][] b) {
		float Eudi = 0f;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				float temp = (float) Math.pow((a[i][j] - b[i][j]), 2);
				Eudi = Eudi + temp;
			}
		}
		return Eudi;
	}

	/*methode pour calculer les moyennes des valeurs de deux tableaux de bytes*/
	public static float[] average(byte[][] a, byte[][] b) {
		float p = 0f;
		float m = 0f;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				p = (p + a[i][j]);
				m = (m + b[i][j]);
			}
		}
		p = p / (a[0].length * a.length);
		m = m / (b[0].length * b.length);
		float[] Avo = { p, m };
		return Avo;
	}

	/**
	 * @brief Computes the inverted similarity between 2 images.
	 * 
	 * @param a, b two images of same dimensions
	 * 
	 * @return the inverted similarity between the two images
	 */
	public static float invertedSimilarity(byte[][] a, byte[][] b) {
		float[] moy = average(a, b);
		float k = 0f;
		float p = 0f;
		float g = 0f;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				float s = (float) ((a[i][j] - moy[0]) * (b[i][j] - moy[1]));
				float m = (float) ((a[i][j] - moy[0]) * (a[i][j] - moy[0]));
				float q = (float) ((b[i][j] - moy[1]) * (b[i][j] - moy[1]));
				p = (float) p + s;
				g = (float) g + m;
				k = (float) k + q;
			}
		}
		float n = (float) (p / (Math.sqrt(g * k)));
		if (g == 0) {
			return 2;
		} else {
			return 1 - n;
		}
	}

	/**
	 * @brief Quicksorts and returns the new indices of each value.
	 * 
	 * @param values the values whose indices have to be sorted in non decreasing
	 *               order
	 * 
	 * @return the array of sorted indices
	 * 
	 *         Example: values = quicksortIndices([3, 7, 0, 9]) gives [2, 0, 1, 3]
	 */
	public static int[] quicksortIndices(float[] values) {
		int[] tab = new int[values.length];
		for (int i = 0; i < tab.length; i++) {
			tab[i] = i;
		}
		quicksortIndices(values, tab, 0, values.length - 1);

		return tab;
	}

	/**
	 * @brief Sorts the provided values between two indices while applying the same
	 *        transformations to the array of indices
	 * 
	 * @param values  the values to sort
	 * @param indices the indices to sort according to the corresponding values
	 * @param         low, high are the **inclusive** bounds of the portion of array
	 *                to sort
	 */
	public static void quicksortIndices(float[] values, int[] indices, int low, int high) {
		int l = low;
		int h = high;
		float pivot = values[low];
		while (l <= h) {
			if (values[l] < pivot) {
				l++;
			} else if (values[h] > pivot) {
				h--;
			} else {
				swap(l, h, values, indices);
				l++;
				h--;
			}
		}
		if (low < h) {
			quicksortIndices(values, indices, low, h);
		}
		if (high > l) {
			quicksortIndices(values, indices, l, high);
		}
	}

	/**
	 * @brief Swaps the elements of the given arrays at the provided positions
	 * 
	 * @param         i, j the indices of the elements to swap
	 * @param values  the array floats whose values are to be swapped
	 * @param indices the array of ints whose values are to be swapped
	 */
	public static void swap(int i, int j, float[] values, int[] indices) {
		float a = values[i];
		values[i] = values[j];
		values[j] = a;
		int b = indices[i];
		indices[i] = indices[j];
		indices[j] = b;
	}

	/**
	 * @brief Returns the index of the largest element in the array
	 * 
	 * @param array an array of integers
	 * 
	 * @return the index of the largest integer
	 */
	public static int indexOfMax(int[] array) {
		int largest = 0;
		int max = 0;
		for (int i = 0; i < array.length; i++) {
			if (max <= array[i]) {
				max = array[i];
				largest = i;
			}
		}
		return largest;
	}

	/**
	 * The k first elements of the provided array vote for a label
	 *
	 * @param sortedIndices the indices sorted by non-decreasing distance
	 * @param labels        the labels corresponding to the indices
	 * @param k             the number of labels asked to vote
	 *
	 * @return the winner of the election
	 */
	public static byte electLabel(int[] sortedIndices, byte[] labels, int k) {
		int[] results = new int[10];
		for (int i = 0; i < k; i++) {
			++results[labels[sortedIndices[i]]];
		}
		return (byte) indexOfMax(results);
	}

	/**
	 * Classifies the symbol drawn on the provided image
	 *
	 * @param image       the image to classify
	 * @param trainImages the tensor of training images
	 * @param trainLabels the list of labels corresponding to the training images
	 * @param k           the number of voters in the election process
	 *
	 * @return the label of the image
	 */
	public static byte knnClassify(byte[][] image, byte[][][] trainImages, byte[] trainLabels, int k) {
		float[] distances = new float[trainImages.length];
		for (int i = 0; i < trainImages.length; i++) {
			distances[i] = squaredEuclideanDistance(image, trainImages[i]);
		}
		return electLabel(quicksortIndices(distances), trainLabels, k);
	}

	/**
	 * Computes accuracy between two arrays of predictions
	 * 
	 * @param predictedLabels the array of labels predicted by the algorithm
	 * @param trueLabels      the array of true labels
	 * 
	 * @return the accuracy of the predictions. Its value is in [0, 1]
	 */
	public static double accuracy(byte[] predictedLabels, byte[] trueLabels) {
		int n = trueLabels.length;
		int s = 0;
		for (int i = 0; i < n; i++) {
			if (predictedLabels[i] == trueLabels[i]) {
				s = s + 1;
			}
		}
		return (double) s / n;
	}
}
