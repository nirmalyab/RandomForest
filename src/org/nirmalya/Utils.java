package org.nirmalya;

public class Utils {

	/** The natural logarithm of 2 */
	private static double log2 = Math.log(2);

	public static double sum(double[] array) {

		double total = 0.0;
		for (double temp : array) {
			total += temp;
		}

		return total;
	}

	public static double max(double[] array) {
		double max = array[0];

		for (double temp : array) {
			if (temp > max) {
				max = temp;
			}
		}
		return max;
	}

	public static boolean eq(double first, double sec) {
		return first == sec;
	}

	static double gain(double[][] dist, double priorVal) {
		return priorVal - measure(dist);
	}

	static double measure(double[][] dist) {
		return Utils.entropy(dist);
	}

	static double measure(double[] dist2) {
		/* Two classes 0 and 1 */
		double[][] currDist = new double[2][2];

		/* Now put all the samples in the second group */

		for (int i = 0; i < 2; i++) {

			currDist[1][i] = dist2[i];
		}

		return measure(currDist);
	}

	public static double entropy(double[][] matrix) {

		double[] nodePerChild = new double[2];
		double[] entropyPerNode = new double[2];

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				nodePerChild[i] += matrix[i][j];
			}
		}
		
		for (int i = 0; i < 2; i++) {
			if (nodePerChild[i] > 0 ) {
				for (int j = 0; j < 2; j++) {
					entropyPerNode[i] += log2Func(matrix[i][j] / nodePerChild[i]);
				}
			}
		}

		double totalEntropy = 0;
		double totalNode = 0;
		for (int i = 0; i < 2; i++) {
			totalEntropy += (nodePerChild[i] * entropyPerNode[i]);
			totalNode += nodePerChild[i];
		}

		totalEntropy /= totalNode;

		return -totalEntropy;
	}

	private static double log2Func(double num) {

		// Constant hard coded for efficiency reasons
		if (num < 1e-6) {
			return 0;
		} else {
			return num * log2(num);
		}
	}

	public static double log2(double num) {
		return (Math.log(num) / log2);
	}
}
