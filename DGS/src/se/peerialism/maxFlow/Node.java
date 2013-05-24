package se.peerialism.maxFlow;

/**
 * @author Amgad
 * Class for nodes, contains all information about a Node
 */

public class Node 
{
	private int id;
	private int index;
	private boolean marked;
	
	public Node(int name){
		this.id = name;
		marked = false;
	}

	public int getId(){
		return id;
	}
	public void mark(){
		marked = true;
	}
	public void unmark(){
		marked = false;
	}
	public boolean ismarked(){
		return marked;
	}
	// required for Set Comparison
	public boolean equals(Object obj){
		Node n2 = (Node) obj;
		return (this.id == n2.id);
	}
	public int hashCode()
	{
		return id;
	}
	public String toString()
	{
		return "Node: " + id ;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}

