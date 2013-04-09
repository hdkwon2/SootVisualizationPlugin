package edu.illinois.hdkwon.visualizer.views;

import java.util.ArrayList;
import java.util.List;

import soot.jimple.parser.node.Node;

public class LocalNode implements SootGraphNode{
	private String name;
	private List<SootGraphNode> connections;
	
	public LocalNode(String name){
		this.name = name;
		this.connections = new ArrayList<SootGraphNode>();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<SootGraphNode> getConnectedTo() {
		// TODO Auto-generated method stub
		return connections;
	}
	
	@Override
	public String toString(){
		return getName()+"(local)";
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof LocalNode){
			LocalNode node = (LocalNode) other;
			return (node.getName().equals(name) || node.name == name);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result =1;
		result = result * prime + ((name == null) ? 0 : name.hashCode());
		return result;
	}
}
