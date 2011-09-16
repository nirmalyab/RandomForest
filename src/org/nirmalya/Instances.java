package org.nirmalya;

import java.util.Vector;

public class Instances {
	
	private Vector<Instance> instances = null;
	
	public Instances(Vector<Instance> instances) {
		
		this.instances = instances;
		
	}
	
	Vector<Instance> getInsiances() {
		return this.instances;
	}

	/**
	 * Sort the vector elements with respect to the localSplit
	 * elements.
	 * 
	 * @param localSplit
	 */
	public void sort(int localSplit) {
		int i = 0;
		int j = instances.size() - 1;
		quickSort(localSplit, i, j);		
	}	
	
	void quickSort(int localSplit, int left, int right) {
		
		if (left < right) {
			int pivot = partition(localSplit, left, right);
			quickSort(localSplit, left, pivot - 1);
			quickSort(localSplit, pivot + 1, right);			
		}		
	}	
		
	private int partition(int localSplit, int left, int right) {
		
		double x = instance(right).value(localSplit);
		int i = left - 1;
		for (int j = left; j < right; j++) {
			if (instance(j).value(localSplit) <= x) {
				i++;
				swap(i, j);
				
			}
		}
		swap(i + 1, right);
		return (i + 1);
		
	}

    private void swap(int l, int r) {
    	
		Instance temp = instances.get(l);
		instances.set(l, instances.get(r));
		instances.set(r, temp);		
	}	
	

	public Instance instance(int index) {

    	return instances.get(index);
	}

	public int size() {
		// TODO Auto-generated method stub
		return instances.size();
	}
	

	/*
	void quickSort(int attIndex, int left, int right) {

		if (left < right) {
			int middle = partition(attIndex, left, right);
			quickSort(attIndex, left, middle - 1);
			quickSort(attIndex, middle + 1, right);
		}
	}

	private int partition(int attIndex, int l, int r) {
	    
	    double pivot = instance((l + r) / 2).value(attIndex);

	    while (l < r) {
	      while ((instance(l).value(attIndex) < pivot) && (l < r)) {
	        l++;
	      }
	      while ((instance(r).value(attIndex) > pivot) && (l < r)) {
	        r--;
	      }
	      if (l < r) {
	        swap(l, r);
	        l++;
	        r--;
	      }
	    }
	    if ((l == r) && (instance(r).value(attIndex) > pivot)) {
	      r--;
	    } 

	    return r;
	}
	
    */

}
