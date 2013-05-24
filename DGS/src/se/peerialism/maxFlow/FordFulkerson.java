package se.peerialism.maxFlow;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author Amgad
 * Max Flow Ford Fulkerson algorithm 
 */
public class FordFulkerson {
	private static ArrayList<Arc> arcsList;
	private static ArrayList<Node> nodesList;
	private static ArrayList<Node> path;
	
	public static ArrayList<Arc> calculate(ArrayList<Arc> arcsData)
	{
		arcsList = new ArrayList<Arc>();
		nodesList = new ArrayList<Node>();
		for (int i = 0; i < arcsData.size();i++)
		{
			Arc a = arcsData.get(i);
			Arc tobeadded = new Arc(a.getsource(),a.gettarget(),a.getflow());
			Node sourceNode = new Node(tobeadded.getsource().hashCode());
			Node targetNode = new Node(tobeadded.gettarget().hashCode());
			if (!nodesList.contains(sourceNode)) {
				sourceNode.setIndex(nodesList.size());
				nodesList.add(sourceNode);
			}
			else
				sourceNode = nodesList.get(nodesList.indexOf(sourceNode));
			if (!nodesList.contains(targetNode)) {
				targetNode.setIndex(nodesList.size());
				nodesList.add(targetNode);
			}
			else
				targetNode = nodesList.get(nodesList.indexOf(targetNode));
			tobeadded.setSourceNode(sourceNode);
			tobeadded.setTargetNode(targetNode); 
			arcsList.add(tobeadded);
		}
		
		adjacencyList = new ArrayList<LinkedList<Arc>>();
		for (Node i : nodesList) {
			LinkedList<Arc> sublist = new LinkedList<Arc>();
			adjacencyList.add(sublist);
		}		
		
		for (int j = 0; j < arcsList.size();j++)
		{
			Arc a = arcsList.get(j);
			Node i = a.getSourceNode();
			adjacencyList.get(i.getIndex()).add(a);
			i = a.getTargetNode();
			adjacencyList.get(i.getIndex()).add(a);
		}
		
		int delta;
//		 main algorithm loop	
		path = Search(arcsList);
		int z = 0;
		while (path != null)
		{
			System.out.println(z++);
			ArrayList<Integer> indeces = new ArrayList<Integer>();
			ArrayList<Integer> residual_indeces = new ArrayList<Integer>();
			delta = Capacity(path, arcsList, indeces, residual_indeces);
			// update path (arcs & residuals arcs) flow with delta
			for (int i : indeces)
			{
				arcsList.get(i).setflow(arcsList.get(i).getflow() - delta);
				arcsList.get(i).set_residual_flow(
						arcsList.get(i).get_residual_flow() + delta);
			}
			for (int i : residual_indeces)
			{
				arcsList.get(i).setflow(arcsList.get(i).getflow() + delta);
				arcsList.get(i).set_residual_flow(
						arcsList.get(i).get_residual_flow() - delta);
			}
			path = Search(arcsList);
		}
		
//		for (Arc a : arcsList)
//		{
//			System.out.println(a.getsource() + " --> " + 
//					a.gettarget() + " ,Flow = " +
//					String.valueOf(a.get_residual_flow()) + "/" +
//					String.valueOf(a.get_residual_flow() + a.getflow()));
//		}
//		System.out.println();
//		System.out.println();
		
		// arcsList is the final output !!!!!
		return arcsList;
	}
	
	private static ArrayList<LinkedList<Arc>> adjacencyList;
	private static LinkedList<Node> list;
	
	private static ArrayList<Node> Search(ArrayList<Arc> arcsData)
	{
		// Depth First Algorithm re-encoded
		ArrayList<Node> result = null ;
		LinkedList<Arc> immitingArcs;
		Node currunt_node;
		
		for (Node i : nodesList){
			i.unmark();
		}
		
		Node start = new Node("s".hashCode());
		Node target = new Node("t".hashCode());

//		 initialization
		list = new LinkedList<Node>();
		int index = nodesList.indexOf(start);
		if (index >= 0)
		{
			start = nodesList.get(index);
			start.mark();
			nodesList.set(index,start);
			list.add(start);
		}
//		 main algorithm loop
		boolean finished = false;
		boolean finished_this_node = true;
		while (! list.isEmpty())
		{
			Node i = list.getLast();
			index = i.getIndex();
			immitingArcs = adjacencyList.get(index);
			finished = false;
			finished_this_node = true;
			for (Arc a:immitingArcs)
			{
				Node j = null;
				if (a.getSourceNode().getId() == i.getId() && a.getflow() > 0)
					j = a.getTargetNode();
				else if (a.getTargetNode().getId() == i.getId() && a.get_residual_flow() > 0)
					j = a.getSourceNode();
				else
					continue;
				index = j.getIndex();
				currunt_node = nodesList.get(index);
				if (! currunt_node.ismarked())
				{
					currunt_node.mark();
					list.add(currunt_node);	
					// adds the node to the end of the list
					nodesList.set(index,currunt_node);
					finished_this_node = false;
					if (target.getId() == currunt_node.getId())
					{
						finished = true;
						result = new ArrayList<Node>(list);
					}
					break;
				}
			}
			if (finished)
				break;
			if (finished_this_node)
				list.removeLast();
		}	
		return result;
	}
	
	private static int Capacity(ArrayList<Node> p, ArrayList<Arc> y, 
		ArrayList<Integer> indeces, ArrayList<Integer> residual_indeces)
	{
		// return the flow of given Path (p)
		int minflow = 0;
		int result = 0;
		for (int i = 0; i < p.size() - 1; i++)
		{
			for (Arc a : y)
			{
				if (a.getSourceNode().getId() == p.get(i).getId() && 
					a.getTargetNode().getId() == p.get(i+1).getId()
					&& (a.getflow() > 0))
				{
					if (minflow == 0)
						minflow = a.getflow();
					if (minflow > a.getflow())
						minflow = a.getflow();
					indeces.add(y.indexOf(a));
					break;
				}
				// in case arc passed was a residual arc
				if (a.getSourceNode().getId() == p.get(i+1).getId() && 
					a.getTargetNode().getId() == p.get(i).getId() 
					&& (a.get_residual_flow() > 0))
				{
					if (minflow > a.get_residual_flow())
						minflow = a.get_residual_flow();
					residual_indeces.add(y.indexOf(a));
					break;
				}
			}
		}
		result = minflow;
		return result;
	}
}
