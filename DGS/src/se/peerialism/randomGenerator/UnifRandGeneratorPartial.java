package se.peerialism.randomGenerator;

import java.util.HashMap;
import java.util.Random;

public class UnifRandGeneratorPartial {

	int numberOfPersons;
	int numberOfObjects;
	int c; //parameter to set the max Aij
	
	public HashMap<Integer, Float>[] generate(float d, long randomSeed) {
		
		Random r = new Random();
		r.setSeed(randomSeed);
		Random r2 = new Random();
		r2.setSeed(randomSeed);
		HashMap<Integer, Float>[] aijMatrixPartial = new HashMap[numberOfPersons];
		for (int i = 0; i < numberOfPersons; i++) {
			aijMatrixPartial[i] = new HashMap<Integer, Float>();
			for (int j = 0; j < numberOfObjects; j++) {
				if (r.nextFloat() > d)
					aijMatrixPartial[i].put(j, (float) r2.nextInt(c));
				else 
					r2.nextInt(c);
			}
		}
		return aijMatrixPartial;
	}
	
	public UnifRandGeneratorPartial(int numberOfPersons, int numberOfObjects, int c){
		this.numberOfObjects = numberOfObjects;
		this.numberOfPersons = numberOfPersons;
		this.c = c;
	}
	
}
