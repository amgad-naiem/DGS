package se.peerialism.dgs;



/**
 * 
 * This class represents the type difference calculated for each row and column... the difference between the best element and the chosen
 * element for each row and column. it contains 3 attributes: int index representing the column or row id of this difference,
 * int type which represent the type of this difference 0 is the difference was for a row and 1 if the difference was for a column, float value which 
 * represent the value of this difference. It implements Comparable so it overrides compareTo to make the differences comparable by their values.
 * 
 * 
 * @author reda
 *
 */
class Difference implements Comparable<Difference>{
	float value;
	int type; //0 for person 1 for object
	int index; //row number if type is 0 , or column number is type is 1
	int bestChange; // colum to change to if type is 0(row), or row if type is 1
	int bestToChangeCol = -1;
	int bestChangeAssigned;
	int myAssigned;
	
	Difference(int index, int type, float value){
		this.index=index;
		this.type=type;
		this.value=value;
	}
	
	Difference(int index, int changeTo, int type, float value){
		this.index=index;
		this.type=type;
		this.value=value;
		bestChange = changeTo;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Difference b) {
        if (value > b.value)
            return -1;
        else if (value < b.value)
        	return 1;
        else 
        	return (int) Math.signum(hashCode()-b.hashCode()); 
    }
	
	@Override
	public int hashCode() {
		return index * (type == 0 ? 1 : -1);
	}
	
}
