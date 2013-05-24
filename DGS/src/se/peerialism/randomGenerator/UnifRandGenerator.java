package se.peerialism.randomGenerator;

import java.util.ArrayList;
import java.util.Random;

public class UnifRandGenerator {

	int numberOfPersons;
	int numberOfObjects;
	float[][] aijMatrix;
	int c; //parameter to set the max Aij
	
	public float[][] generate() {
		
		Random myRand = new Random();
		myRand.setSeed(7);
		for (int i = 0 ; i <numberOfPersons ; i++){
			for (int j = 0 ; j < numberOfObjects ; j++){
				aijMatrix[i][j] =  c*myRand.nextFloat(); 
			}
		}
		return aijMatrix;
	}
	
	public UnifRandGenerator(int numberOfPersons, int numberOfObjects, int c){
		this.numberOfObjects = numberOfObjects;
		this.numberOfPersons = numberOfPersons;
		this.aijMatrix = new float[numberOfPersons][numberOfObjects];
		this.c = c;
	}
}
