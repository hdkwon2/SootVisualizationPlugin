package edu.illinois.hdkwon.visualizer.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.zest.core.viewers.IGraphContentProvider;

public class SootNodeContentProvider extends ArrayContentProvider implements IGraphContentProvider{

	@Override
	public Object getSource(Object rel) {
		if(rel instanceof SootGraphEdge){
			SootGraphEdge e = (SootGraphEdge) rel;
			return e.getSource();
		}
		
		throw new RuntimeException("Type not supported");
	}

	@Override
	public Object getDestination(Object rel) {
		if(rel instanceof SootGraphEdge){
			SootGraphEdge e = (SootGraphEdge) rel;
			return e.getDestination();
		}
		
		throw new RuntimeException("Type not supported");
	}


}
