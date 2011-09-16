package org.nirmalya;

import java.util.Random;
import java.util.Vector;

/**
 * This class will take an input and shall bootstrap the input data and shall return the
 * new variable. 
 * 
 * @author nirmalya
 *
 */
public class BootStrapping {
	

	/**
	 * Here I am not duplicating the data and assuming that synchronization will 
	 * be maintained. However, if it is not maintained, we shall duplicate the data.
	 * @param oriData
	 * @param random
	 * @return
	 */
	public static Vector<Instance> getBootStrappedData(Vector<Instance> oriData, 
														Random random) {
			
		Vector<Instance> sampledVec = new Vector<Instance>();

		int dataSize = oriData.size();
		
		for (int i = 0; i < dataSize; i++) {
			int nextIndex = random.nextInt(dataSize);
			Instance temp = oriData.get(nextIndex);
			sampledVec.add(temp);
			
		}		
		return sampledVec;
	
	}
	

}
