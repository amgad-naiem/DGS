package se.peerialism.dgs;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import se.peerialism.maxFlow.Arc;
import se.peerialism.maxFlow.FordFulkerson;

/**
 * This class is the engine of the assignment heuristic it contains two 
 * constructors and 4 methods:
 * naiveInitialAssignment, smartInitialAssignment, calculateTotalHappiness,
 * enhanceBySwitching	
 * @author amgad
 *
 */
public class AssignmentEnginePartialAij {
	
	int numberOfPersons,numberOfObjects;
	float totalHappiness;
	TreeSet<Difference> differences;
	Map<Integer,Difference> rowDifferencesList;
	Map<Integer,Difference> columnDifferencesList;
	int[] persons;
	int[] objects;
	private HashMap<Integer, Float>[] aijMatrix;
	private ArrayList<Integer>[] bannedSwitches;
	private boolean clearedBannedSwitched[];
	private long randomSeed;

	/**
	 * Constructor that takes as parameters rowsAndColumns ArrayList
	 * @param rowsAndColumns is an ArrayList of 2 elements the first element is
	 * an arrayList of elements of type Row and the second element is 
	 * an arrayList of elements of type Column
	 * The parameters are supposed to be read from a file by 
	 * the file manipulator reader
	 */
	public AssignmentEnginePartialAij(int nPersons, int nObjects, HashMap<Integer, Float>[] aij, long seed){
		randomSeed = seed;
		numberOfObjects = nObjects;
		numberOfPersons = nPersons;
		bannedSwitches = new ArrayList[numberOfPersons];
		clearedBannedSwitched = new boolean[numberOfPersons];
		aijMatrix = aij;
		persons = new int[numberOfPersons];
		objects = new int[2*numberOfObjects];
		
		for (int i = 0; i < numberOfPersons; i++){
			bannedSwitches[i] = new ArrayList<Integer>();
			clearedBannedSwitched[i] = false;
			persons[i] = -1;
		}
		for (int i = 0; i < 2*numberOfObjects; i++) {
			objects[i] = -1;
		}

		this.differences  = new TreeSet<Difference>();
		this.columnDifferencesList = new HashMap<Integer,Difference>();
		this.rowDifferencesList = new HashMap<Integer,Difference>();


	}

	/**
	 * This method is used to initialize the assignment randomly before
	 * applying the switching method.It is not smarter but it might produce
	 * good enough solutions as smartInitial assignment takes a long time.
	 * The method generates a random integer that has to be in the set of 
	 * unassigned rows and generates another random integer that has to be
	 * in the set of unassigned columns, then we take the two generated 
	 * integers and we assign the person of random row generated to the object
	 * of random column generated, then we remove them from the set of
	 * unassigned rows and columns.
	 * 
	 */
	@Deprecated
	public void randomInitialAssignment(){	
		ArrayList<Arc> arcsList = new ArrayList<Arc>();
		for (int i = 0; i < numberOfObjects; i++) {
			Arc a = new Arc("s", i+"o",1);
			arcsList.add(a);
		}
		
		for (int i = 0; i < numberOfPersons; i++) {
			for (int j : aijMatrix[i].keySet()) {
				Arc a = new Arc(j+"o", i+"p",1);
				arcsList.add(a);
			}
			Arc a = new Arc(i+"p","t", 1);
			arcsList.add(a);
		}
		
		arcsList = FordFulkerson.calculate(arcsList);
		for (Arc a : arcsList) {
			if (a.getsource().endsWith("o") && a.getflow() == 0) {
				int person = Integer.valueOf(a.gettarget().split("p")[0]);
				int object = Integer.valueOf(a.getsource().split("o")[0]);
				persons[person] = object;
				objects[object] = person;
			}
		}
	}
	
	public void smartInitialAssignment(){	
		int row1, curRow, col2;
		ArrayList<Integer> indeces = new ArrayList<Integer>();
		for (int i = 0; i < numberOfPersons; i++) 
			indeces.add(i);
		Random rnd = new Random();
		rnd.setSeed(randomSeed);
		Collections.shuffle(indeces, rnd);
		for (int i : indeces){
			
			curRow = i;
			while (curRow != -1) {
				Difference myDiff = getRowBestDifference(curRow);
				if (myDiff != null) {
					row1 = myDiff.index; // index of row of the difference
					col2 = myDiff.bestChange; //index of column of the best cell in the row of difference
					curRow = objects[col2]; //index of  row of the chosen in the column of the best cell in the difference row
					persons[row1] = col2;
					objects[col2] = row1;
					if (curRow != -1) {
						persons[curRow] = -1;
						bannedSwitches[row1].add(curRow);
					}
				}
			}
		}
	}

	/**
	 * This method is responsible of calculating the total happiness and 
	 * this is done by passing over the rows,and add up the value of each 
	 * chosen element in the matrix
	 *
	 */
	public float calculateTotalHappiness(){
		totalHappiness = 0;
		for(int i = 0 ; i < numberOfPersons ; i ++){
			if (persons[i] != -1)
				totalHappiness += aijMatrix[i].get(persons[i]);
		}
		return totalHappiness;
	}

	/**
	 * This method tries to enhance the quality of final solution which is the 
	 * total happiness, so it tries to increase it by making switches over rows
	 * and columns. The switches is done by the following: first we calculate 
	 * the differences over all rows and columns.
	 * 
	 * The difference is between the best and chosen element and this is done 
	 * for each row and column. After calculating the differences we choose 
	 * the maximum difference of all rows and columns. Then we enter the row 
	 * or column of the maximum difference then we calculate the impact of 
	 * the switch over the total happiness if the it increases the total 
	 * happiness then it is added to differences (as an array to maintain 
	 * all differences where switching should enhance the total happiness). 
	 * 
	 * Each cell in the differences array is then taken and its switch is 
	 * deployed. the switch is done by the following: if the difference is 
	 * of type row then we will the chosen element for this row will be
	 * the best of this row (so we will change only the column of the chosen 
	 * element) But we have added another element for the same column as this
	 * column did contain a chosen element so the chosen element of this 
	 * column will be the chosen element of the row just assigned and the 
	 * chosen element of the row of the old chosen element of this column will
	 * be the element of the column of the best element of the row chosen from
	 * differences. if the chosen difference was of type column the same but 
	 * from the column perspective.
	 * 
	 *   
	 */
	public void enhanceBySwitching() {
		float newTotalHappiness,oldTotalHappiness;
//		int counter = 1;
		while(true){
//			System.out.println(counter++);
			oldTotalHappiness = calculateTotalHappiness();
			evaluateDifferences();
			int[] switchedRows=new int[2];
			int[] switchedColumns=new int[2];
//			System.out.println(counter + ", diff: " + differences.size());
			while (!differences.isEmpty()){
				Difference myDiff=differences.first();
				
				int row1,row2,col1,col2;
				//Here I need to retrieve the 2 columns and 2 rows that will be altered due to switch ...
				//to not reUpdate all the differences in the Tree
				float diffCheck; 
				if(myDiff.type == 0){ //Found in row..i.e. switching happens along columns
					row1 = myDiff.index; // index of row of the difference
					col1 = persons[row1]; //index of column of the chosen cell in the row of difference
					col2 = myDiff.bestChange; //index of column of the best cell in the row of difference
					row2 = objects[col2]; //index of  row of the chosen in the column of the best cell in the difference row
					if (col1 != myDiff.myAssigned || row2 != myDiff.bestChangeAssigned) {
						diffCheck = -1.0f;
					} else if (row2 == -1) {
						diffCheck = aijMatrix[row1].get(col2) - aijMatrix[row1].get(col1);
					} else {
						diffCheck= aijMatrix[row1].get(col2) + aijMatrix[row2].get(col1) - (aijMatrix[row1].get(col1) + aijMatrix[row2].get(col2));
					}
				}else{
					col1 = myDiff.index; // index of column of the difference
					row1 = objects[col1]; //index of row of the chosen cell in the column of difference
					row2 = myDiff.bestChange; // index of row of the best cell in the column of difference
					col2 = persons[row2]; //index of column of the chosen in the row of the best cell in the difference column
					if (row1 != myDiff.myAssigned || col2 != myDiff.bestChangeAssigned)
						diffCheck = -1.0f;
					else
						diffCheck= aijMatrix[row1].get(col2) + aijMatrix[row2].get(col1) - (aijMatrix[row1].get(col1) + aijMatrix[row2].get(col2));
				}
				//We need to check that our previous calculation still holds
				//It may not due to second order effects
				if(diffCheck<=0){
					if(myDiff.type == 0) 
						rowDifferencesList.remove(myDiff.index);
					else
						columnDifferencesList.remove(myDiff.index);
					differences.remove(myDiff);
					continue;
				}

				//System.out.println("Happiness before switch: "+calculateTotalHappiness());
				//So now we switch rows and columns
				persons[row1] = col2;
				if (row2 != -1)
				{
//					if (col1 == -1)
//						bannedSwitches[row1].add(row2);
					persons[row2] = col1;
				}
//				if (col1 != -1)
					objects[col1] = row2;
				objects[col2] = row1;
//				if (col1 == -1 && row2 == -1)
//					return;
					
				//System.out.println("Happiness after switch: "+calculateTotalHappiness());

				// Now we update the modified rows and columns
				switchedRows[0]=row1;
				switchedRows[1]=row2;
				switchedColumns[0]=col1;
				switchedColumns[1]=col2;
				for(int i=0;i<2;i++){
					if(columnDifferencesList.containsKey(switchedColumns[i]))
						differences.remove(columnDifferencesList.remove(switchedColumns[i]));
					//addColBestDifference(switchedColumns[i]);
				}
				for(int i=0;i<2;i++){
					if (rowDifferencesList.containsKey(switchedRows[i]))
						differences.remove(rowDifferencesList.remove(switchedRows[i]));
					addRowBestDifference(switchedRows[i]);
				}
			}
			//System.out.println("Total Happiness " + calculateTotalHappiness());
			newTotalHappiness = calculateTotalHappiness();
			if(newTotalHappiness == oldTotalHappiness)
				break;
			
		}

	}


	/**
	 * This method is responsible of evaluating the differences for rows and
	 * columns and adds the differences that improve the total objective 
	 */
	public void evaluateDifferences() {
//		System.out.println("before Columns");
//		for(int i = 0; i < numberOfObjects; i++){
//			//Find the best objective improvement for this column over all other rows that can switch with the current chosen
//			addColBestDifference(i);
//		}
//		System.out.println("before Rows");
		for(int i = 0; i < numberOfPersons; i++){
			//Find the best objective improvement
			addRowBestDifference(i);
		}
		
	}
	
	public void addColBestDifference(int colId){
		if (colId == -1 || objects[colId] == -1)
			return;
		float maxDiff = 0.009f;
		int bestChangeRow = -1;
		int myRow = objects[colId];
		int myCol = colId;
		for (int otherRow = 0; otherRow < numberOfPersons; otherRow++) {
			int otherCol = persons[otherRow];
			if (aijMatrix[otherRow].containsKey(myCol) && aijMatrix[myRow].containsKey(otherCol)) {
				float difference = aijMatrix[myRow].get(otherCol) + aijMatrix[otherRow].get(myCol) - (aijMatrix[myRow].get(myCol) + aijMatrix[otherRow].get(otherCol));
				if (difference> maxDiff) {
					maxDiff = difference;
					bestChangeRow = otherRow;
				}
			}
		}
		if(maxDiff> 0.1){
			Difference myDifference = new Difference(colId, bestChangeRow, 1, maxDiff); //Column Difference
			myDifference.myAssigned = objects[colId];
			myDifference.bestChangeAssigned = persons[bestChangeRow];
			differences.add(myDifference);
			columnDifferencesList.put(colId, myDifference);
		}
	}
	
	public void addRowBestDifference(int rowId) {
		Difference myDifference = getRowBestDifference(rowId);
		if (myDifference != null) {
			myDifference.myAssigned = persons[rowId];
			myDifference.bestChangeAssigned = objects[myDifference.bestChange];
			differences.add(myDifference);
			rowDifferencesList.put(rowId, myDifference);	
		}
	}

	public Difference getRowBestDifference(int rowId) {
		if (rowId == -1)
			return null;
		float maxDiff = 0.009f;
		int bestChangeCol = -1;
		int myCol = persons[rowId];
		if (myCol == -1)
			maxDiff = Float.NEGATIVE_INFINITY;
		int myRow = rowId;
		boolean foundFreeObject = false;
		for (int otherCol : aijMatrix[rowId].keySet()) {
			int otherRow = objects[otherCol];
			float difference = Float.NEGATIVE_INFINITY;
			if (myCol == -1) {
				if (otherRow == -1) {
					difference = aijMatrix[myRow].get(otherCol);
					if (!foundFreeObject) {
						maxDiff = difference;
						bestChangeCol = otherCol;
					}
					foundFreeObject = true;
				}
				else if (!foundFreeObject && !bannedSwitches[myRow].contains(otherRow))
					difference = aijMatrix[myRow].get(otherCol) - aijMatrix[otherRow].get(otherCol);
			}
			else if (otherRow == -1)
				difference = aijMatrix[myRow].get(otherCol) - aijMatrix[myRow].get(myCol);
			else if (aijMatrix[otherRow].containsKey(myCol)) {
				difference = aijMatrix[myRow].get(otherCol) + aijMatrix[otherRow].get(myCol) - (aijMatrix[myRow].get(myCol) + aijMatrix[otherRow].get(otherCol));
			}
			if (difference > maxDiff) {
				maxDiff = difference;
				bestChangeCol = otherCol;
			}
		}
		maxDiff = Math.abs(maxDiff);
		if(maxDiff> 0.1 || myCol == -1) {
			if (bestChangeCol == -1) {
				if (clearedBannedSwitched[myRow]){
					numberOfObjects++;
					aijMatrix[myRow].put(numberOfObjects-1, Float.NEGATIVE_INFINITY);
					return new Difference(rowId, numberOfObjects-1, 0, 1000);
				}
				clearedBannedSwitched[myRow] = true;
				bannedSwitches[myRow].clear();
				return getRowBestDifference(rowId);
			}
			if (myCol == -1)
				maxDiff = maxDiff*1000;
			return new Difference(rowId, bestChangeCol, 0, maxDiff); //Row Difference
		}
		return null;
	}

	public int[] getAssignment() {
		return persons;
	}

	public void checkAllAsigned() {
		for (int i = 0 ; i < numberOfPersons; i++)
			if (persons[i] == -1)
				System.out.println("not all are assigned");
	}

}
