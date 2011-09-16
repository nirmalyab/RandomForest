package org.nirmalya;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the starting point. This will create an environment for
 * a five fold cross validation.
 * 
 * @author nirmalya
 *
 */
public class StartingPoint {	

	/* Data read from the input file */
	private double[][] dataMatrix;
	/* labels corresponding t the data */
	private double[] labels;
	/* Data file */
	private String dataFile;
	/* Number of data points */
	private int numData;
	/* Number of total attributes */
	private int numAttributes;
	/* Number of trees in the random forest */
	//private int numTrees;
	/* Number of threads */
	private int numThreads;
	/* Number of attributes that we select before each splitting */
	private int numSelectedAttributes;
	private static String outFileName;	
	/* Number of folds for cross validation */
	private static final int numFolds = 5;
	
	/**
	 * This file anticipates two datasets, once for the predictors
	 * which is a matrix and one for the response dataset which is a 
	 * vector. It also anticipates the other parameters, such as 
	 * the dimension of the matrix and the number of trees and the 
	 * number of attributes for split point selection.
	 * 
	 * @param string
	 */
	private void initialize(String file) {
		
		try {
			BufferedReader argFile = new BufferedReader(new FileReader(file));
			String regex = "^(\\S+)\\s+(\\S+)";
			Pattern pat = Pattern.compile(regex);
			
			String line = null;
			while (null != (line = argFile.readLine())) {
				Matcher mat = pat.matcher(line);
				if (mat.find()) {
					String key = mat.group(1);
					String value = mat.group(2);
					
					if (key.equals("dataFile")) {
						dataFile = value;
					} else if (key.equals("numData")) {
						numData = Integer.parseInt(value);
					} else if (key.equals("numAttributes")) {
						numAttributes = Integer.parseInt(value);
						numSelectedAttributes = (int)Math.sqrt(numAttributes);
					} /* else if (key.equals("numTrees")) {
						numTrees = Integer.parseInt(value);
					} */ else if (key.equals("numThreads")) {
						numThreads = Integer.parseInt(value);
					} /* else if (key.equals("numSelectedAttributes")) {
						numSelectedAttributes = Integer.parseInt(value);
					} */ else if (key.equals("outFileName")) {
						outFileName = value;
					}
					else {
											System.out.println("Error: Un supported arguments: " + key);
						throw new RuntimeException("Error: Un supported arguments: " + key);
					}
				}				
			}
			
			/* After the argument parsing, load the data and the label */
			dataMatrix = new double[numData][numAttributes];
			labels = new double[numData];
			DataReader.readMatrix(dataFile, dataMatrix, labels);
			
			/* Do a shuffling of the data */
			//doShuffle(dataMatrix, labels);

			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/*

	private void doShuffle(double[][] dataMatrix2, double[] labels2) {
		Random rand = new Random(0);
		
		int localSize = dataMatrix2.length;
		for (int i = 0; i < localSize; i++) {
			int first = rand.nextInt(localSize);
			int second = rand.nextInt(localSize);
			
			double[] tempData = dataMatrix2[first];
			dataMatrix2[first] = dataMatrix2[second];
			dataMatrix2[second] = tempData;
			
			double tempLabel = labels2[first];
			labels2[first] = labels2[second];
			labels2[second] = tempLabel;
		}
		
	}
	*/
	private double executeRandomForestFoldWise(int i, int numTrees) {
		
		/* Create the training and testing data for this fold */
		int testNum = (int)Math.floor(numData / numFolds);
		int trainNum = numData - testNum;
		
		double[][] localTrainData = new double[trainNum][numAttributes];
		double[][] localTestData = new double[testNum][numAttributes];
		double[] localTrainLabels = new double[trainNum];
		double[] localTestLabels = new double[testNum];
		
		int testIndexStart = (i - 1) * testNum ;
		int testIndexEnd = i * testNum - 1;
				
		int k = 0;
		int m = 0;
		for (int j = 0; j < numData; j++) {
			
			if (j >= testIndexStart && j <= testIndexEnd) {
				localTestData[k] = dataMatrix[j];
				localTestLabels[k] = labels[j];
				k++;
			} else {
				localTrainData[m] = dataMatrix[j];
				localTrainLabels[m] = labels[j];
				m++;
			}			
		}
		
		/* Now that we have obtained the train and test data and label
		 * we can start execute the random forest for the current fold.
		 */
		RandomForest rf = new RandomForest(	numTrees, 
											numAttributes,
											numSelectedAttributes,
											numThreads);
		rf.trainRandomForest(localTrainData,
				localTrainLabels);

		ErrorMetric errorM = rf.testRandomForest(localTestData, 
				localTestLabels);
		
		//System.out.println("Accuracy in fold " + i + " is: " + errorM.accuracy);
		return errorM.accuracy;		
			
	}

	public static void main(String[] args) {
		
		StartingPoint sPoint = new StartingPoint();
		sPoint.initialize(args[0]);
		int[] treeNumArr = {1, 2, 5, 10, 20, 50, 100, 200, 500, 1000};
		
		try {
				PrintWriter outFile = new PrintWriter(new FileWriter(outFileName));
			
			for (int j = 1; j < treeNumArr.length; j++) {
				int localTreeNum = treeNumArr[j];
				double totalAccuracy = 0;
				for (int i = 1; i <= numFolds; i++ ) {
					totalAccuracy += sPoint.executeRandomForestFoldWise(i, localTreeNum);
				
				}
				double accuracy = totalAccuracy / numFolds;
				outFile.println(localTreeNum + " " + accuracy);
				
			}
			outFile.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
}

class ErrorMetric {
	
	public ErrorMetric(double accuracy) {
		this.accuracy = accuracy;
	}
	double accuracy ;
	double truePositive;
	double falsePositive;
	double trueNegative;
	double falseNegative;
}
