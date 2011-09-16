package org.nirmalya;

import java.io.BufferedReader;
import java.io.FileReader;

public class DataReader {
	
	
	public static void readMatrix(String file, double[][] dataMatrix, double[] labels) {
		try {
			BufferedReader inFile = new BufferedReader(new FileReader(file));
			
			for (int i = 0; i < dataMatrix.length; i++) {
				String line = inFile.readLine();
				char[] chars = line.toCharArray();
				
				boolean isIn = false;
				char[] valArr = new char[30];	
				int k = 0;
				int m = 0;
				
				for (int j = 0; j < chars.length; j++) {

					if (Character.isWhitespace(chars[j])) {
						
						if (isIn) {
							/* First extract the data point stored in the array */
							String valStr = new String(valArr, 0, k);
							double valDouble = Double.parseDouble(valStr);
							if (m == 0) {
								if (valDouble != 1) {
									labels[i] = 0;
								} else {
									labels[i] = 1;
								}
								m++;
							} else {
								dataMatrix[i][m-1] = valDouble;
								m++;
							}

							/* Now reinitialize the data */
							isIn = false;
							valArr = new char[30];							
							k = 0;
						}
												
					} else {
						if (!isIn) {
							isIn = true;
						}
						valArr[k++] = chars[j];
					}
				}
				if (k > 0) {
					String valStr = new String(valArr, 0, k);
					double valDouble = Double.parseDouble(valStr);
					dataMatrix[i][m-1] = valDouble;
				}
			}
			inFile.close();
			
		} catch (Exception e) {
			//System.out.println("i: " + i + " j: " + j + " " + k);
			e.printStackTrace();
		}
	}
	

	public static void readVector(String file, double[] array) {
		try {
			
			BufferedReader inFile = new BufferedReader(new FileReader(file));
			int m = 0;
			String line = null;
			
			while (null!= (line = inFile.readLine())) {

				char[] chars = line.toCharArray();
				boolean isIn = false;
				char[] valArr = new char[30];	
				int k = 0;

				
				for (int j = 0; j < chars.length; j++) {

					if (Character.isWhitespace(chars[j])) {
						
						if (isIn) {
							/* First extract the data point stored in the array */
							String valStr = new String(valArr, 0, k);
							double valDouble = Double.parseDouble(valStr);
							array[m++] = valDouble;
							
							if (m == array.length) {
								break;
							}
							
							/* Now reinitialize the data */
							isIn = false;
							valArr = new char[30];							
							k = 0;
						}
						
					} else {
						if (!isIn) {
							isIn = true;
						}
						valArr[k++] = chars[j];
					}
				}

			}
			inFile.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}	
