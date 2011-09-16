package org.nirmalya;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This is the main function for the random forest. This will create multiple
 * random tree along with a bootstrap method and shall train those trees with the
 * bootstrap method.
 * 
 * @author nirmalya
 *
 */
public class RandomForest {
	
	private static int NTHREADS = 4;
	private  ExecutorService exec 
				= Executors.newFixedThreadPool(NTHREADS);

	int numTrees;
	int numAttributes;
	int numSelectedAttributes;
	Vector<Instance> oriTrainData;
	Vector<Instance> testData;

	/* An array of vector of random forest */
	RandomTree[] randomTreeArrays;
	/* Create also an array of random number generator */
	Random[] randomArr;
	
	/**
	 * 
	 * @param numTrees
	 * @param numSelectedAttributes
	 * @param numSelectedAttributes2 
	 */
	public RandomForest(int numTrees,
						int numAttributes, 
						int numSelectedAttributes, 
						int numThreads) {
		
		this.numTrees = numTrees;
		this.numAttributes = numAttributes;
		this.numSelectedAttributes = numSelectedAttributes;
		NTHREADS = numThreads;
		randomTreeArrays = new RandomTree[numTrees];
		
		randomArr = new Random[numTrees];
		for (int i = 0; i < numTrees; i++) {
			randomArr[i] = new Random(i);
		}
		
		oriTrainData = new Vector<Instance>();
			
	}

	public void trainRandomForest(double[][] trainData,
								   double[] trainLabels) {		
		
		
		/* Fill the oriData up */
		for (int i = 0; i < trainData.length; i++) {
			Instance instTemp = new Instance(trainLabels[i], trainData[i]);
			oriTrainData.add(instTemp);
		}		
		
		
		/* Start building the tree */
		for (int i = 0; i < numTrees; i++) {
			
			/* Create the data set for this tree using bootstrapping method */
			Vector<Instance> bootTrainData 
							= BootStrapping.getBootStrappedData(oriTrainData, randomArr[i]);
			
			/* Create the Instances object */
			Instances instances = new Instances(bootTrainData);
			
			/* Create the class distribution */
			
			double[] distributions = calculatedistribution(instances);
			RandomTree treeTemp = new RandomTree(numSelectedAttributes, numAttributes, 
													instances, distributions, randomArr[i]);
			randomTreeArrays[i] = treeTemp;
			
			/* Now execute the thread */
			exec.execute(randomTreeArrays[i]);
			
		}
		/* Call a shutdown for graceful completion of the tasks */
		exec.shutdown();
		
		/* Wait for some time till the jobs complete gracefully. */
		
		try {
			if (exec.awaitTermination(1, TimeUnit.DAYS)) {
				return;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private double[] calculatedistribution(Instances instances) {
		double[] distribution = new double[2];
		for (Instance inst : instances.getInsiances()) {
			distribution[(int)inst.getLabel()] += 1;
		}
		
		return distribution;
	}

	public ErrorMetric testRandomForest(double[][] testData,
										double[] testLabels) {
		
		/* Do the testing for all the data points */
		double correctPrediction = 0;
		
		for (int i = 0; i < testData.length; i++) {
			Instance tempInstance = new Instance(testLabels[i], testData[i]);
			
			/* Test it on all the trees */
			double[] tempClassArray = new double[numTrees];
			int countClass[] = new int[2];
			for (int j = 0; j < numTrees; j++) {
				RandomTree tempTree = randomTreeArrays[j];
				tempClassArray[j] = tempTree.testClass(tempInstance);
				countClass[(int)tempClassArray[j]] += 1;
			}
			
			//System.out.println(countClass[0] + " " + countClass[1]);
			int predictedClass = countClass[0] > countClass[1] ? 0 : 1;
			if (tempInstance.getLabel() == predictedClass) {
				correctPrediction++;
			}
			
			
		}
		double accuracy = correctPrediction / testData.length;
		return new ErrorMetric(accuracy);

	}
}
