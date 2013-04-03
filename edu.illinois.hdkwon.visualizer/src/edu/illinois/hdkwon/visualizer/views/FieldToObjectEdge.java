package edu.illinois.hdkwon.visualizer.views;

public class FieldToObjectEdge implements SootGraphEdge{

	private final SootGraphNode source;
	private final SootGraphNode destination;
	private final String name;
	
	public FieldToObjectEdge(SootGraphNode src, SootGraphNode des, String name){
		this.source = src;
		this.destination = des;
		this.name = name;
	}
	
	@Override
	public SootGraphNode getSource() {
		return source;
	}

	@Override
	public SootGraphNode getDestination() {
		// TODO Auto-generated method stub
		return destination;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}
}
