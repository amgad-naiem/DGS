package se.peerialism;


import java.util.ArrayList;
import java.util.TreeMap;

import se.peerialism.dgs.AssignmentEngine;
import se.peerialism.randomGenerator.GeomRandGenerator;
import se.peerialism.randomGenerator.UnifRandGenerator;



public class Test {

	
	private static String filename = "assign800.txt";
	private static boolean maximization = true;
	public static long seed = 7;
	public static ArrayList<Float> happinessTime = new ArrayList<Float>();
	
    int numberOfPersons;
    int numberOfObjects;
    float aijMatrix[][];
    float e;
	
	public static void main(String[] args) {
		Test myTest = new Test();		
		
			myTest.numberOfPersons = myTest.numberOfObjects = 10000;
			boolean Geom = true;
			int c = 1000; // max Aij in uniform & square size in geometric
			//all that needs to be edited is the previous 3 lines, and all next is adjusted accordingly
			
			if (Geom) {
				GeomRandGenerator geometric = new GeomRandGenerator(myTest.numberOfPersons, myTest.numberOfObjects, c);
				myTest.aijMatrix = geometric.generate();
			}
			else {
				UnifRandGenerator uniform = new UnifRandGenerator(myTest.numberOfPersons, myTest.numberOfObjects, c); 
				myTest.aijMatrix =  uniform.generate();
			}
//		for (int i = 0; i < myTest.numberOfPersons; i++) {
//			for (int j = 0; j < myTest.numberOfObjects; j++)
//				System.out.print(myTest.aijMatrix[i][j] + " ");
//			System.out.println("\n");
//		}

		myTest.e = 0.01f;//1.0f /((float) myTest.numberOfPersons);
		System.out.println("started " + myTest.e);
		float happiness;
		//happiness = myTest.Standard_JNI();
		//myTest.HeuristicJNI();
		//happiness = myTest.Standard_java();
		//happiness = -myTest.Standard_java_Scaling();
		float happinessHeuristic = 0;
		TreeMap<Float, Integer> frequency = new TreeMap<Float, Integer>();
		for (seed = 5; seed <= 5; seed++) {
			//System.out.println(seed);
			happinessHeuristic = myTest.Heuristic();
			//myTest.HeuristicNoDepth(); // actual greedy
			//myTest.HeuristicNoDepth2();
			//myTest.HeuristicNonGreedy();
			//myTest.HeuristicNonGreedy2();
			//myTest.HeuristicCol();
//			if (frequency.containsKey(happinessHeuristic)){
//				frequency.put(happinessHeuristic, frequency.get(happinessHeuristic)+1);
//			} else
//				frequency.put(happinessHeuristic, 1);
		}
		//float diff = happiness - happinessHeuristic;
		//System.out.println("difference in happiness = " + (diff/happiness)*100);
		System.out.println("max = " + myTest.findMax());
		//System.out.println("Frequencies : " + frequency.toString());
		//happiness = myTest.Standard_java();
		System.out.println(happinessTime);
	}

	public float findMax() {
		float max = 0f;
		float imax = 0f;
		for (int i = 0; i < numberOfPersons; i++) {
			imax = 0f; 
			for (int j = 0; j < numberOfObjects; j++) 
				if (aijMatrix[i][j] > imax) 
					imax = aijMatrix[i][j];
			max += imax;
		}
		
		return max;
	}	

	public float Heuristic() {
		AssignmentEngine myEngine = new AssignmentEngine(numberOfPersons, numberOfObjects, aijMatrix, seed);
        long start = System.currentTimeMillis();
        myEngine.randomInitialAssignment();
        System.out.println("Time initialization ==> " + (System.currentTimeMillis() - start));
        myEngine.enhanceBySwitching();
        long time = System.currentTimeMillis() - start;
        float happiness = myEngine.calculateTotalHappiness();
		System.out.println("Happiness Heuristic ==> " + happiness);
		System.out.println("Time = " + time);
		return happiness;
	}	
	
}