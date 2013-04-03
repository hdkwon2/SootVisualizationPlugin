package edu.illinois.hdkwon.visualizer.views;

public class LocalToObjectEdge implements SootGraphEdge{

	private final SootGraphNode source;
	private final SootGraphNode destination;
	
	public LocalToObjectEdge(SootGraphNode src, SootGraphNode des){
		this.source = src;
		this.destination = des;
	}
	
	@Override
	public SootGraphNode getSource() {
		return source;
	}

	@Override
	public SootGraphNode getDestination() {
		return destination;
	}

	@Override
	public String getName() {
		return "";
	}
	
	@Override
	public String toString(){
		return "";
	}

}
