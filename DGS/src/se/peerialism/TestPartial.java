package se.peerialism;


import java.util.ArrayList;
import java.util.HashMap;

import se.peerialism.dgs.AssignmentEnginePartialAij;


public class TestPartial {

	
	private static String filename = "assignp5000.txt";
	private static boolean maximization = false;
	private static long seed = 3;
	
    int numberOfPersons;
    int numberOfObjects;
    HashMap<Integer, Float>[] aijMatrix;
    float e;
	
	public static void main(String[] args) {
		TestPartial myTest = new TestPartial();
		float happinesHeuristic = myTest.Heuristic_Partial();
		System.out.println("difference in happiness = " + (((happinesHeuristic-2239f)/79999f)));
	}
	
	public float Heuristic_Partial() {
		AssignmentEnginePartialAij myEngine = new AssignmentEnginePartialAij(numberOfPersons, numberOfObjects, aijMatrix, seed);
        long start = System.currentTimeMillis();
        myEngine.smartInitialAssignment();
        System.out.println("Time initialization ==> " + (System.currentTimeMillis() - start));
        myEngine.enhanceBySwitching();
        long time = System.currentTimeMillis() - start;
        float happiness = myEngine.calculateTotalHappiness();
		System.out.println("Happiness Heuristic ==> " + happiness);
		System.out.println("Time = " + time);
		myEngine.checkAllAsigned();
		return happiness;
	}	
	
}