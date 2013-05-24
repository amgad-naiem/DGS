package se.peerialism.randomGenerator;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class is used for generating GEOM random aijMatrix used for the Testing the auction algorithm
 * The GEOM generated cases are supposed to be difficult problem to reach a global optimal in. The generation is made
 * by identifying the needed number of persons and objects (n) and a parameter c. Then a 2D square of size CxC is generated
 * each point in this 2D square will have a random integer number from (0 --> n) representing the dimensions of ajiMatrix
 * then the value of aij is the Euclidian distance between the the point i and point j in the 2D square.
 * @author reda
 *
 */
public class GeomRandGenerator {
	
	//int[][] square; // representing the 2D square
	int[] listOfLocXPersons; //for each element placed in the 2D square we save it's location on x (i index of the 2D square(2d array)) to not search for it again when calculating the euclidian distance...the index represent the object
	int[] listOfLocYPersons; //for each element placed in the 2D square we save it's location on y (j index of the 2D square(2d array)) to not search for it again when calculating the euclidian distance...the index represent the object
	int numberOfPersons;
	int numberOfObjects;
	int[] listOfLocXObjects;
	int[] listOfLocYObjects;
	float[][] aijMatrix;
	ArrayList<Integer> chosenIndeces;
	int c; //parameter to adjust the dimension of the 2D square used as space for the Euclidian distance between each 2 points
	/**
	 * 
	 * The method that generates the random numbers following the GEOM algorithm mentioned above 
	 * @return aijMatrix 2 dimensional array of type float
	 */
	public float[][] generate(){
		
		//ArrayList randomPoints = new ArrayList();
		Random myRand = new Random();
		myRand.setSeed(7);		//added by amgad for test purpose
		//Generate Persons
		while(chosenIndeces.size()< numberOfObjects){ //we are always concerned by the number of objects because it is the one that might be larger so that the indices
			// in the 2D square must cover all possible values of i,j of aijMatrix

			int randomIndex = myRand.nextInt(numberOfPersons);
			
			while(chosenIndeces.contains(randomIndex)){ // if it is not chosen before so we can add else we cannot add two indices with the same value in the 2D square
				randomIndex = myRand.nextInt(numberOfObjects);
			}
						
			chosenIndeces.add(randomIndex);
			
			int randomLocX  = myRand.nextInt(c); //coordinate x to place the random number generated in the 2D square
			int randomLocY = myRand.nextInt(c); //coordinate y to place the random number generated in the 2D square
						
//			while(square[randomLocX][randomLocY] != -1){ //if the place is already taken choose another one
//				randomLocX = myRand.nextInt(c);
//				randomLocX = myRand.nextInt(c);
//			}
//			
//			square[randomLocX][randomLocY] = randomIndex;
			listOfLocXPersons[randomIndex] = randomLocX;
			listOfLocYPersons[randomIndex] = randomLocY;
		}
		
		chosenIndeces.clear();
		while(chosenIndeces.size()< numberOfObjects){
			int randomIndex = myRand.nextInt(numberOfObjects);
			
			while(chosenIndeces.contains(randomIndex)){ // if it is not chosen before so we can add else we cannot add two indices with the same value in the 2D square
				randomIndex = myRand.nextInt(numberOfObjects);
			}
			
			chosenIndeces.add(randomIndex);
			int randomLocX  = myRand.nextInt(c); //coordinate x to place the random number generated in the 2D square
			int randomLocY = myRand.nextInt(c); //coordinate y to place the random number generated in the 2D square
			
			listOfLocXObjects[randomIndex] = randomLocX;
			listOfLocYObjects[randomIndex] = randomLocY;
		}
		
		// Now we have initialized the 2D Square we need to calculate the aijMatrix values by calculating the Euclidian distance between i and j in the 2D Square
		
		
		for(int i = 0 ; i < numberOfPersons ; i ++){
			for (int j = 0; j < numberOfObjects ; j++){
				aijMatrix[i][j] = Math.round(getEuclidianDistance(i,j));  // this element of aijMatrix equals the euclidian distance between the i,j in the 2D Square
			}
		}
		
		return aijMatrix;
	}
	
	
	/**
	 * 
	 * This method generates the random numbers following GEOM algorithm the difference between it and generate
	 * that the places of the 2D square are assigned from a list in random places ... but in generate they are chosen randomly
	 * and assigned in random places which takes much more time
	 * @return 2 dimensional array of float values (aijMatrix)
	 */
//	public float[][] fastGenerate(){
//		int counter = 0 ;
//		while(counter < numberOfObjects){ //we are always concerned by the number of objects because it is the one that might be larger so that the indices
//			// in the 2D square must cover all possible values of i,j of aijMatrix
//			Random myRand = new Random();
//
//			int randomLocX  = myRand.nextInt(c); //coordinate x to place the random number generated in the 2D square
//			int randomLocY = myRand.nextInt(c); //coordinate y to place the random number generated in the 2D square
//						
//			while(square[randomLocX][randomLocY] != -1){ //if the place is already taken choose another one
//				randomLocX = myRand.nextInt(c);
//				randomLocX = myRand.nextInt(c);
//			}
//			
//			square[randomLocX][randomLocY] = counter;		
//			listOfLocXPersons[counter] = randomLocX;
//			listOfLocYPersons[counter] = randomLocY;
//			counter++;
//		}
//		
//		
//		// Now we have initialized the 2D Square we need to calculate the aijMatrix values by calculating the Euclidian distance between i and j in the 2D Square
//		
//		for(int i = 0 ; i < numberOfPersons ; i ++){
//			for (int j = 0; j < numberOfObjects ; j++){
//				aijMatrix[i][j] = getEuclidianDistance(i,j);  // this element of aijMatrix equals the euclidian distance between the i,j in the 2D Square
//			}
//		}
//		return aijMatrix;
//	
//	}
	
	/**
	 * 
	 * This method is responsible of calculating the euclidian distance in the 2D Square between the two points x , y .
	 * @param x represents the first point needed to calculate the euclidian distance of type integer < numberOfObjects
	 * @param y represents the second point needed to calculate the euclidian distance of type integer < numberOfObjects
	 * @return the euclidian distance of type float
	 */
	public float getEuclidianDistance(int point1 , int point2){
		float euclidianDistance = 0;
		int point1LocX = -1; //x coordinate of first point
		int point2LocX = -1; //x coordinate of second point
		
		int point1LocY = -1; // y coordiante of first point
		int point2LocY = -1; // y coordinate of second point
		
		
		if(point1 == point2)
			return euclidianDistance = 0; // same point ..then no need to calculate euclidian distance
		
		// first we need to now the location of each point the 2D square
		
		int counter = 0; // to count the number of found squares
		
		point1LocX = listOfLocXPersons[point1];
		point1LocY = listOfLocYPersons[point1];
		
		point2LocX = listOfLocXObjects[point2];	
		point2LocY = listOfLocYObjects[point2];
		
		
		
		// then we calculate the euclidian between the 2 points locations
		euclidianDistance = (float)Math.sqrt(Math.pow(point1LocX - point2LocX,2) + Math.pow(point1LocY - point2LocY,2)); 

		return euclidianDistance;
	}
		
	
	
	
	/**
	 * The class constructor
	 * 
	 * @param numberOfPersons of type integer
	 * @param numberOfObjects of type integer
	 */
	public GeomRandGenerator(int numberOfPersons, int numberOfObjects, int c){
		this.numberOfObjects = numberOfObjects;
		this.numberOfPersons = numberOfPersons;
		this.aijMatrix = new float[numberOfPersons][numberOfObjects];
		this.chosenIndeces = new ArrayList<Integer>();
		this.listOfLocXPersons = new int[numberOfPersons];
		this.listOfLocYPersons = new int[numberOfPersons];
		this.listOfLocXObjects = new int[numberOfObjects];
		this.listOfLocYObjects = new int[numberOfObjects];
		this.c = c;
		
	}

}
