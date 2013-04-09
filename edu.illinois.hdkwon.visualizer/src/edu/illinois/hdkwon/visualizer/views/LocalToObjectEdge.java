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
	
	@Override
	public boolean equals(Object other){
		if(other instanceof LocalToObjectEdge){
			LocalToObjectEdge edge = (LocalToObjectEdge) other;
			return (edge.getSource().equals(source) || edge.source == source)
					&& (edge.getDestination().equals(destination) || edge.destination == destination);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result =1;
		result = result * prime + ((source == null) ? 0 : source.hashCode());
		result = result * prime + ((destination == null) ? 0 : destination.hashCode());
		return result;
	}
}
