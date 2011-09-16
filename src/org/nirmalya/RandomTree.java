package org.nirmalya;

import java.util.Random;
import java.util.Vector;

public class RandomTree implements Runnable{

	
	/* Minimum number of instances in a leaf */
	int minInstLeaf = 1;
	
	/* Total number of attributes */
	int numAttributes;
	
	/* Number of random attributes to consider before splitting for this tree */
	int numSelectedAttributes;
	
	/* proportion of two classes in the tree */
	double[] classDistributions;
	
	/* The instances that reside inside this node */
	Instances instances;
	
	int bestSplitVar;
	
	double bestSplitVal;
	
	/* Check if this tree is a leaf or not */
	boolean isLeaf = false;
	
	/* Set -1 as the default value */
	int classId = -1;
	
	/* Random number seed */
	int randomSeed = 0;
	
	/* Random number generator */
	Random rand;
	
	/* Two random tree for partitioning : left and right arm */
	RandomTree left;
	RandomTree right;	
	
	/**
	 * 	
	 * @param numSelectedAttributes
	 * @param numAttributes
	 * @param instances
	 * @param distributions
	 * @param random 
	 */
	public RandomTree(int numSelectedAttributes, 
			int numAttributes, 
			Instances instances, 
			double[] distributions, Random random) {
		
		this.numSelectedAttributes = numSelectedAttributes;
		this.numAttributes = numAttributes;
		this.instances = instances;
		this.classDistributions = distributions;
		this.rand = random;
	}
	
	/* Return the class label of a test point */
	public double testClass(Instance instance) {
		
		if (isLeaf) {
			return classId;
		}		
		return ((instance.value(bestSplitVar) < bestSplitVal)? 
				left.testClass(instance) : right.testClass(instance));
	}
	
	public void buildTree() {
		
		/* Check if this node has less than minimum number of nodes,
		 * of its a pure node or it has reached the maximum allowable depth.
		 * If yes, please make it a leaf node.
		 */
		
		if (Utils.sum(classDistributions) == minInstLeaf 
				|| Utils.eq(Utils.max(classDistributions), Utils.sum(classDistributions))) {
			isLeaf = true;
			classId = (classDistributions[0] > classDistributions[1]) ? 0 : 1; 
			return;
		}
		
		/* Select the set of split variables along with the corresponding split value
		 * and the gain.
		 */
		
		int windowSize = numAttributes;
		int[] localWindow = new int[numAttributes];
		for (int i = 0; i < localWindow.length; i++) {
			localWindow[i] = i;
		}
		
		int splitCount = 0;
		
		/* The array contains the split variables to explore */
		int[] splitArr = new int[numSelectedAttributes];
		/* Best value of split at the split variables */
		double[] splitValues = new double[numSelectedAttributes];
		/* Array of gains at the split variable at the best split points */
		double[] gainArr = new double[numSelectedAttributes];
		/* Distribution */
		double[][][] dists = new double[numSelectedAttributes][2][2]; 
		
		while (windowSize > 0 && splitCount < numSelectedAttributes) {
			
			int localSplit = rand.nextInt(windowSize);
			
			/* Now exchange between the last element and the selected element */
			splitArr[splitCount] = localWindow[localSplit];
			localWindow[localSplit] = localWindow[windowSize - 1];
			localWindow[windowSize - 1] = splitArr[splitCount];	
			
			splitCount++;
			windowSize--;			
		}
		
		/* now for all the elements in the splitArr calculate the gains and the splitValues */
			
		for (int i = 0 ; i < numSelectedAttributes; i++) {
			int localSplit = splitArr[i];
			splitValues[i] = getSplitValues(localSplit, classDistributions, dists[i]);
			gainArr[i] = Utils.gain(dists[i], Utils.measure(classDistributions));
		}
		
		/* Now get the best of the attributes */
		double bestVal = 0;
		int bestIndex = -1;
		
		for (int i = 0; i < numSelectedAttributes; i++) {
			if (gainArr[i] > bestVal) {
				bestVal = gainArr[i];
				bestIndex = i;
			}
		}		
		
		/* if there best splitting does not improve the information
		 * gain then keep it as a leaf.
		 */
		if (0 == bestVal) {
			isLeaf = true;
			classId = (classDistributions[0] > classDistributions[1]) ? 0 : 1; 
			return;
		}		
		
		/* Calculate the best split and the corresponding distribution array */
		bestSplitVar = splitArr[bestIndex];
		bestSplitVal = splitValues[bestIndex];
		
		double[][] bestDist = dists[bestIndex];
					
		/* Now that we obtained the best attribute and the corresponding 
		 * distributions we can recursively call the function itself.
		 * Get the instances for the two subtrees.
		 */
		Vector<Instance> leftVec = new Vector<Instance>();
		Vector<Instance> rightVec = new Vector<Instance>();
		
		/* Now that we know at which split variable we are splitting,
		 * lets sort the instances.
		 */
		instances.sort(bestSplitVar);
		
		for(Instance inst : instances.getInsiances()) {
			if (inst.value(bestSplitVar) < bestSplitVal) {
				leftVec.add(inst);
			} else {
				rightVec.add(inst);
			}
		}

		Instances leftInst = new Instances(leftVec);
		Instances rightInst = new Instances(rightVec);
		
		/* Create the left and right subtree and call the buildTree procedure */
		left = new RandomTree(this.numSelectedAttributes, 
				this.numAttributes, leftInst, bestDist[0], rand);
		left.buildTree();
		
		right = new RandomTree(this.numSelectedAttributes, 
				this.numAttributes, rightInst, bestDist[1], rand);
		
		right.buildTree();
			
	}

	/**
	 * For the all possible values of localSplit variables select the best
	 * split value as the split value. Here the best value indicates the one
	 * that will provide the maximum gain.
	 * 
	 * @param attr
	 * 		The variable for the possible split.
	 * @param dist
	 * 		Original distribution of the instances 
	 * 			
	 * @return
	 */
	private double getSplitValues(int attr, double[] dist, double[][] bestDist) {
				
		
		/* First sort the instances of this class with respect to the local 
		 * split variable. This will enable us to complete this sep in O(N^2)
		 * instead of O(N log N).
		 */
		
		instances.sort(attr);
		
		/* Two classes 0 and 1 */
		double[][] currDist = new double[2][2];
				
		/* Now put all the samples in the second group */
		
		for (int i = 0; i < instances.size(); i++) {
			
			Instance local = instances.instance(i);
			currDist[1][(int)local.getLabel()] += 1;
			bestDist[1][(int)local.getLabel()] += 1;
		}		
		
		/* Now traverse through each data samples in the instances object
		 * and use them as the split value. 
		 */		
		
		double splitPoint = -Double.MAX_VALUE;
	    double currSplit = -Double.MAX_VALUE;
	    
	    double currVal;
	    double bestVal = 0;
	    
	    double priorVal = Utils.measure(currDist);
	      
        for (int i = 0; i < instances.size(); i++) {
    	
        	Instance inst = instances.instance(i);

          // Can we place a sensible split point here?
        	if (inst.value(attr) > currSplit) {

        		// Compute gain for split point
        		currVal = Utils.gain(currDist, priorVal);

        		// Is the current split point the best point so far?
        		if (currVal > bestVal) {

        			// Store value of current point
        			bestVal = currVal;

        			// Save split point
        			splitPoint = (inst.value(attr) + currSplit) / 2.0;

        			// Save distribution
        			for (int j = 0; j < 2; j++) {
        				for (int k = 0; k < 2; k++ ) {
        					bestDist[j][k] = currDist[j][k];
        				}
        			}
        		}
        	}
        	currSplit = inst.value(attr);

        	// Shift over the weight
        	currDist[0][(int) inst.getLabel()] += 1;
        	currDist[1][(int) inst.getLabel()] -= 1;
        }   
	
        return splitPoint;
   }


  
	@Override
	public void run() {
		
		/* Call the main method */
		buildTree();		
	}
}
