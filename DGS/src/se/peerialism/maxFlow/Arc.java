package se.peerialism.maxFlow;

/**
 * @author Amgad
 * Class for arcs
 * includes arc flow & its residual arc flow 
 */

public class Arc {
	private String source;
	private String target;
	private Node sourceNode, targetNode;
	private int flow, residual_flow;
	
	public Arc(String x,String y, int z)
	{
		source = x.toLowerCase();
		target = y.toLowerCase();
		flow = z;
		residual_flow = 0;
	}
	public String getsource()
	{
		return source;
	}
	public String gettarget()
	{
		return target;
	}
	public int getflow()
	{
		return flow;
	}
	public void setflow(int flow)
	{
		this.flow = flow;
	}
	public int get_residual_flow()
	{
		return residual_flow;
	}
	public void set_residual_flow(int flow)
	{
		this.residual_flow = flow;
	}
	// used by the DefaultListModel for output in the JListBox
	public String toString()
	{
		return source + " --> " + target
				+ " ,Capacity = " + String.valueOf(flow);
	}
	public Node getSourceNode() {
		return sourceNode;
	}
	public void setSourceNode(Node sourceNode) {
		this.sourceNode = sourceNode;
	}
	public Node getTargetNode() {
		return targetNode;
	}
	public void setTargetNode(Node targetNode) {
		this.targetNode = targetNode;
	}
}

