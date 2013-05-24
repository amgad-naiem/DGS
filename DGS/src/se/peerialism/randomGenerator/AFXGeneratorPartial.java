package se.peerialism.randomGenerator;

import java.util.HashMap;
import java.util.Random;

public class AFXGeneratorPartial {

	int numberOfPersons;
	int numberOfObjects;
	int c; //parameter to set the max Aij
	
	public HashMap<Integer, Float>[] generate(long seed) {
		
		Random r = new Random(seed);
		HashMap<Integer, Float>[] aijMatrixPartial = new HashMap[numberOfPersons];
		for (int i = 0; i < numberOfPersons; i++) {
			aijMatrixPartial[i] = new HashMap<Integer, Float>();
			int d = numberOfObjects/16;
			for (int j = 0; j < numberOfObjects - d; j++) {
				int x = r.nextInt(numberOfObjects);
				while (aijMatrixPartial[i].containsKey(x))
					x = r.nextInt(numberOfObjects);
				aijMatrixPartial[i].put(x, (float) -100*(i+1)*(x+1));
			}
		}
		return aijMatrixPartial;
	}
	
	public AFXGeneratorPartial(int n){
		this.numberOfObjects = n;
		this.numberOfPersons = n;
	}
	
}
