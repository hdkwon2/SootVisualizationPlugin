package edu.illinois.hdkwon.visualizer.views;

import java.util.ArrayList;
import java.util.List;

import soot.jimple.spark.pag.Node;

public class ObjectNode implements SootGraphNode {

	private Node node;
	private List<SootGraphNode> connections;
	
	public ObjectNode(Node node){
		this.node = node;
		connections = new ArrayList<SootGraphNode>();
	}
	
	@Override
	public String getName() {
		
		return node.getType().toString();
	}

	@Override
	public List<SootGraphNode> getConnectedTo() {
		// TODO Auto-generated method stub
		return connections;
	}

	@Override
	public String toString(){
		return getName()+"(object)";
	}
}
