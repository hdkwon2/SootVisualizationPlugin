package edu.illinois.hdkwon.visualizer.views;

import java.util.List;

public interface SootGraphNode {

	public String getName();
	
	public List<SootGraphNode> getConnectedTo();
}
