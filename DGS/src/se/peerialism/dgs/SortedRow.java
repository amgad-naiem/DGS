package se.peerialism.dgs;


import java.util.ArrayList;

/**
 * This class represents the type row of the assignment matrix it contains 6 attributes, id of type integer, isChosen boolean variable, rowCells priority queue containing the elements
 * of this row sorted by their values, rowCellsList to get the elements of the row unordered because you cannot use the priority queue for all operations you might need the elements by their
 * actual order as for constructing the matrix in the initialization, chosen cell containing the element chosen for assigning the person of the row of the chosen cell to the object of column
 * of the chosen cell and the best cell containing the best element of best value in this row. This class contains also 3 methods getDifference, calculateBestCell, getBestCell.
 * 
 * @author reda
 *
 */
public class SortedRow {
	int id, best;
	private ArrayList<Float> values;
	private ArrayList<Integer> indeces;
	
	
	/**
	 * The class constructor to initialize the row
	 * @param id the row index of type integer.
	 */
	public SortedRow(int id){
		this.id = id;
		values =new ArrayList<Float>();
		indeces =new ArrayList<Integer>();
		best = -1;
	}

	public void addElement1(int index, float value) {
		boolean found = false;
		for (int i = 0; i < values.size(); i++) {
			if (value > values.get(i)) {
				values.add(i, value);
				indeces.add(i, index);
				found = true;
				break;
			}
		}
		if (!found){
			values.add(value);
			indeces.add(index);
		}
	}
	
	public void addElement(int index, float value) {
		//values.add(value);
		//indeces.add(index);
	}
	
	public ArrayList<Integer> getBetterIds(int currentId){
		return indeces;
	}
	
	
	/**
	 * This method assigns the best cell attribute by peeking the first element of the priority queue row cells.
	 */
	public ArrayList<Integer> getBetterIds1(int currentId){
		ArrayList<Integer> r = new ArrayList<Integer>();
		for (int i = 0; i < indeces.size(); i++) {
			if (indeces.get(i) == currentId)
				break;
			r.add(indeces.get(i));
		}
		return r;
	}

	public int getId() {
		return id;
	}

	public int getBest() {
		return best;
	}

	public void setBest(int best) {
		this.best = best;
	}


}
