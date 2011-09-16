package org.nirmalya;

public class Instance {
	/* label of the data */
	private double label;
	
	/* array of attribute values */
	private double[] data = null;
	
	/**
	 * Constructor
	 * @param label
	 * @param data
	 */
	public Instance(double label, double[] data) {
		this.label = label;
		/* Assign the data. If required, we shall create the clone later */
		this.data = data;
	}

	public double value(int attIndex) {
		return data[attIndex];
	}
	
	public double getLabel() {
		return label;
	}

	public static Instance createInstance(Instance instance) {
		Instance local = new Instance(instance.label, instance.data);
		return local;
	}
	

}
