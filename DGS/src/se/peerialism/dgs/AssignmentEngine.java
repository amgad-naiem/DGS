package se.peerialism.dgs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;


/**
 * This class is the engine of the assignment heuristic it contains two 
 * constructors and 4 methods:
 * naiveInitialAssignment, smartInitialAssignment, calculateTotalHappiness,
 * enhanceBySwitching	
 * @author reda
 *
 */
public class AssignmentEngine {

	int numberOfPersons,numberOfObjects;
	float totalHappiness;
	ArrayList<SortedRow> rows;
	ArrayList<SortedRow> columns;
	TreeSet<Difference> differences;
	Map<Integer,Difference> rowDifferencesList;
	Map<Integer,Difference> columnDifferencesList;
	int[] persons;
	int[] objects;
	float[][] aijMatrix;
	ArrayList <Integer> unChosenRows;
	ArrayList <Integer> unChosenColumns;
	long randomSeed;


	/**
	 * Constructor that takes as parameters rowsAndColumns ArrayList
	 * @param rowsAndColumns is an ArrayList of 2 elements the first element is
	 * an arrayList of elements of type Row and the second element is 
	 * an arrayList of elements of type Column
	 * The parameters are supposed to be read from a file by 
	 * the file manipulator reader
	 */
	public AssignmentEngine(int nPersons, int nObjects, float[][] aij, long seed){
		randomSeed = seed;
		numberOfObjects = nObjects;
		numberOfPersons = nPersons;
		aijMatrix = aij;
		persons = new int[numberOfPersons];
		objects = new int[numberOfObjects];
		rows = new ArrayList<SortedRow>();
		columns = new ArrayList<SortedRow>();
		unChosenRows  = new ArrayList<Integer>();
		unChosenColumns  = new ArrayList<Integer>();
		
		for (int i = 0; i < numberOfPersons; i++){
			persons[i] = -1;
			SortedRow myRow = new SortedRow(i); 
			for (int j = 0; j < numberOfObjects; j++) {
				myRow.addElement(j, aijMatrix[i][j]);
			}
			rows.add(myRow);
			unChosenRows.add(i);
		}
		for (int i = 0; i < numberOfObjects; i++) {
			objects[i] = -1;
			SortedRow myRow = new SortedRow(i);
			for (int j = 0; j < numberOfPersons; j++) {
				myRow.addElement(j, aijMatrix[j][i]);
			}
			columns.add(myRow);
			unChosenColumns.add(i);
		}

		differences  = new TreeSet<Difference>();
		columnDifferencesList = new HashMap<Integer,Difference>();
		rowDifferencesList = new HashMap<Integer,Difference>();


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
	public void randomInitialAssignment(){
		Random myR = new Random(randomSeed);
		//myR.setSeed(6); //This seed setting here is BAD
		int randomRow = myR.nextInt(numberOfPersons);
		int randomCol = myR.nextInt(numberOfObjects);

		while(!unChosenRows.isEmpty()){
			while(!unChosenRows.contains(randomRow)){
				randomRow = unChosenRows.get(myR.nextInt(unChosenRows.size()));
			}

			float maxVal = Float.NEGATIVE_INFINITY;
			for (int i : unChosenColumns)
				if (aijMatrix[randomRow][i] > maxVal) {
					randomCol = i;
					maxVal = aijMatrix[randomRow][i];
				}

			persons[randomRow] = randomCol;
			objects[randomCol] = randomRow;

			unChosenRows.remove(unChosenRows.indexOf(randomRow));
			unChosenColumns.remove(unChosenColumns.indexOf(randomCol));
		}
	}
	
	/**
	 * This method is used to initialize the assignment randomly before
	 * applying the switching method given an initial incomplete assignment
	 * 
	 */
	public void randomInitialAssignment(int[] initialAssignment){
		
		for (int i = 0; i < numberOfPersons; i++)
			if (initialAssignment[i] != -1 && unChosenColumns.contains(initialAssignment[i])) {
				persons[i] = initialAssignment[i];
				objects[initialAssignment[i]] = i;
				unChosenRows.remove(unChosenRows.indexOf(i));
				unChosenColumns.remove(unChosenColumns.indexOf(initialAssignment[i]));
			}
		
		Random myR = new Random(6);
		int randomRow = myR.nextInt(numberOfPersons);
		int randomCol = myR.nextInt(numberOfObjects);

		while(!unChosenRows.isEmpty()){
			while(!unChosenRows.contains(randomRow)){
				randomRow = myR.nextInt(numberOfPersons);
			}

			while(!unChosenColumns.contains(randomCol)){
				randomCol = myR.nextInt(numberOfObjects);
			}

			persons[randomRow] = randomCol;
			objects[randomCol] = randomRow;

			unChosenRows.remove(unChosenRows.indexOf(randomRow));
			unChosenColumns.remove(unChosenColumns.indexOf(randomCol));
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
			totalHappiness += aijMatrix[i][persons[i]];
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
		while(true){
			oldTotalHappiness = calculateTotalHappiness();
			evaluateDifferences();
			int[] switchedRows=new int[2];
			int[] switchedColumns=new int[2];

			int numSwitches = 0;
			while (!differences.isEmpty()){
				Difference myDiff=differences.first();

				/*			for(Difference d : differences){
				System.out.println("Difference " +d.index + "Type " + d.type + "value " + d.value+ " hashCOde " + d.hashCode());
			}*/
//				Difference latestDiff;
//				if(myDiff.type == 0) 
//					latestDiff = rowDifferencesList.get(myDiff.index);
//				else
//					latestDiff = columnDifferencesList.get(myDiff.index);
//				if (latestDiff.value != myDiff.value) { // unupdated difference
//					differences.remove(myDiff);
//					continue;
//				}
				
				int row1,row2,col1,col2;
				//Here I need to retrieve the 2 columns and 2 rows that will be altered due to switch ...
				//to not reUpdate all the differences in the Tree
				float diffCheck;
				if(myDiff.type == 0){ //Found in row..i.e. switching happens along columns
					row1 = myDiff.index; // index of row of the difference
					col1 = persons[row1]; //index of column of the chosen cell in the row of difference
					col2 = myDiff.bestChange; //index of column of the best cell in the row of difference
					row2 = objects[col2]; //index of  row of the chosen in the column of the best cell in the difference row
					if (col1 != myDiff.myAssigned || row2 != myDiff.bestChangeAssigned) 
						diffCheck = -1.0f;
					else if (row2 == -1)
						diffCheck = aijMatrix[row1][col2] - aijMatrix[row1][col1];
					else
						diffCheck= aijMatrix[row1][col2] + aijMatrix[row2][col1] - (aijMatrix[row1][col1] + aijMatrix[row2][col2]);
				}else{
					col1 = myDiff.index; // index of column of the difference
					row1 = objects[col1]; //index of row of the chosen cell in the column of difference
					row2 = myDiff.bestChange; // index of row of the best cell in the column of difference
					col2 = persons[row2]; //index of column of the chosen in the row of the best cell in the difference column
					if (row1 != myDiff.myAssigned || col2 != myDiff.bestChangeAssigned)
						diffCheck = -1.0f;
					else
						diffCheck= aijMatrix[row1][col2] + aijMatrix[row2][col1] - (aijMatrix[row1][col1] + aijMatrix[row2][col2]);
				}
				//We need to check that our previous calculation still holds
				//It may not due to second order effects
				//System.out.println("myDiff " + myDiff.type + " - " + myDiff.index + " DiffCheck " + diffCheck);
				if(diffCheck<=0){
					if(myDiff.type == 0) {
						rowDifferencesList.remove(myDiff.index);
						//addRowBestDifference(myDiff.index);
					}
					else {
						columnDifferencesList.remove(myDiff.index);
						//addColBestDifference(myDiff.index);
					}
					differences.remove(myDiff);
					continue;
				}
				
				//printMatrix();

				//System.out.println("Happiness before switch: "+calculateTotalHappiness());
				//So now we switch rows and columns
				persons[row1] = col2;
				if (row2 != -1)
				{
					persons[row2] = col1;
				}
				objects[col1] = row2;
				objects[col2] = row1;
				//System.out.println("Happiness after switch: "+calculateTotalHappiness());

				// Now we update the modified rows and columns
	
				switchedRows[0]=row1;
				switchedRows[1]=row2;
				switchedColumns[0]=col1;
				switchedColumns[1]=col2;
				numSwitches++;
				for(int i=0;i<2;i++){
					if(columnDifferencesList.containsKey(switchedColumns[i]))
						differences.remove(columnDifferencesList.remove(switchedColumns[i]));
					addColBestDifference(switchedColumns[i]);
					//lazyAddColDifference(switchedColumns[i]);
				}
				for(int i=0;i<2;i++){
					if (rowDifferencesList.containsKey(switchedRows[i]))
						differences.remove(rowDifferencesList.remove(switchedRows[i]));
					addRowBestDifference(switchedRows[i]);
					//lazyAddRowDifference(switchedRows[i]);
				}
			}
			//System.out.println("Total Happiness " + calculateTotalHappiness());
			newTotalHappiness = calculateTotalHappiness();
			if(newTotalHappiness == oldTotalHappiness)
				break;
			
		}

	}


	private void printMatrix() {
		for (int i = 0; i < numberOfPersons; i++) {
			for (int j = 0; j < numberOfObjects; j++) {
				if (persons[i] == j)
					System.out.print("(" + aijMatrix[i][j] + ")");
				else
					System.out.print(aijMatrix[i][j]);
				System.out.print(" ");
			}
			if (rowDifferencesList.containsKey(i))
				System.out.print(" " + rowDifferencesList.get(i).bestChange + " " + rowDifferencesList.get(i).value);
			System.out.println();
		}
		for (int i = 0 ; i < numberOfObjects; i++)
			if (columnDifferencesList.containsKey(i))
				System.out.print(columnDifferencesList.get(i).bestChange + " ");
			else
				System.out.print("NA ");
		System.out.println();
		for (int i = 0 ; i < numberOfObjects; i++)
			if (columnDifferencesList.containsKey(i))
				System.out.print(columnDifferencesList.get(i).value + " ");
			else
				System.out.print("NA ");
		System.out.println();
		System.out.println();
	}

	/**
	 * This method is responsible of evaluating the differences for rows and
	 * columns and adds the differences that improve the total objective 
	 */
	public void evaluateDifferences() {
		// TODO Auto-generated method stub
		for(int i = 0; i < numberOfObjects; i++){
			//Find the best objective improvement for this column over all other rows that can switch with the current chosen
			addColBestDifference(i);
			//lazyAddColDifference(i);
		}
		for(int i = 0; i < numberOfPersons; i++){
			//Find the best objective improvement
			addRowBestDifference(i);
			//lazyAddRowDifference(i);
		}
		
	}
	
	public void addColBestDifference(int colId){
		//SortedRow curColumn = columns.get(colId);
		//ArrayList<Integer> betterAijs = curColumn.getBetterIds(objects[colId]);
		if (objects[colId] == -1)
			return;
		float maxDiff = 0.009f;
		int bestChangeRow = -1;
		for (int i = 0; i < numberOfPersons; i++) {
			int otherRow = i;
			int myRow = objects[colId];
			int myCol = colId;
			int otherCol = persons[otherRow];
			float difference = aijMatrix[myRow][otherCol] + aijMatrix[otherRow][myCol] - (aijMatrix[myRow][myCol] + aijMatrix[otherRow][otherCol]);
			if (difference> maxDiff) {
				maxDiff = difference;
				bestChangeRow = otherRow;
			}
		}
		if(maxDiff> 0.01){
			Difference myDifference = new Difference(colId, bestChangeRow, 1, maxDiff); //Column Difference
			myDifference.myAssigned = objects[colId];
			myDifference.bestChangeAssigned = persons[bestChangeRow];
			differences.add(myDifference);
			columnDifferencesList.put(colId, myDifference);
		}
	}
	
	public void addRowBestDifference(int rowId) {
		if (rowId == -1)
			return;
//		SortedRow curRow = rows.get(rowId);
//		ArrayList<Integer> betterAijs = curRow.getBetterIds(persons[rowId]);
		float maxDiff = 0.009f;
		int bestChangeCol = -1;
		for (int i = 0; i < numberOfObjects; i++) {
			int otherCol = i;
			int myCol = persons[rowId];
			int myRow = rowId;
			int otherRow = objects[otherCol];
			float difference = -1.0f;
			if (otherRow == -1)
				difference = aijMatrix[myRow][otherCol] - aijMatrix[myRow][myCol];
			else
				difference = aijMatrix[myRow][otherCol] + aijMatrix[otherRow][myCol] - (aijMatrix[myRow][myCol] + aijMatrix[otherRow][otherCol]);
			if (difference> maxDiff) {
				maxDiff = difference;
				bestChangeCol = otherCol;
			}
		}
		if(maxDiff> 0.01){
			Difference myDifference = new Difference(rowId, bestChangeCol, 0, maxDiff); //Row Difference
			myDifference.myAssigned = persons[rowId];
			myDifference.bestChangeAssigned = objects[bestChangeCol];
			differences.add(myDifference);
			rowDifferencesList.put(rowId, myDifference);	
		}
	}

	public int[] getAssignment() {
		return persons;
	}
}
