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
	
	@Override
	public boolean equals(Object other){
		if(other instanceof FieldToObjectEdge){
			FieldToObjectEdge edge = (FieldToObjectEdge) other;
			return (edge.getSource().equals(source) || edge.source == source)
					&& (edge.getDestination().equals(destination) || edge.destination == destination)
					&& (edge.getName().equals(name) || edge.name == name);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result =1;
		result = result * prime + ((source == null) ? 0 : source.hashCode());
		result = result * prime + ((destination == null) ? 0 : destination.hashCode());
		result = result * prime + ((name == null) ? 0 : name.hashCode());
		return 1;
	}
}
