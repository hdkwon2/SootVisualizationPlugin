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
}
