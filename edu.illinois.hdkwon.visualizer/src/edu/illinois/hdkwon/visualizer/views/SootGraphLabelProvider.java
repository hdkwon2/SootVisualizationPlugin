package edu.illinois.hdkwon.visualizer.views;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;

public class SootGraphLabelProvider extends LabelProvider implements IEntityStyleProvider{

	@Override
	public Color getNodeHighlightColor(Object entity) {
		// TODO Auto-generated method stub
		if(entity instanceof SootGraphEdge){
			return Display.getDefault().getSystemColor(SWT.COLOR_RED);
		}
		return null;
	}

	@Override
	public Color getBorderColor(Object entity) {
		if(entity instanceof SootGraphEdge){
			return Display.getDefault().getSystemColor(SWT.COLOR_RED);
		}
		return null;
	}

	@Override
	public Color getBorderHighlightColor(Object entity) {
		if(entity instanceof SootGraphEdge){
			return Display.getDefault().getSystemColor(SWT.COLOR_RED);
		}
		return null;
	}

	@Override
	public int getBorderWidth(Object entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Color getBackgroundColour(Object entity) {
		// node background color
		if(entity instanceof LocalNode){
			return Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
		}
		else if(entity instanceof ObjectNode){
			return Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		}
		if(entity instanceof SootGraphEdge){
			return Display.getDefault().getSystemColor(SWT.COLOR_RED);
		}
		return null;
	}

	@Override
	public Color getForegroundColour(Object entity) {
		// node text color
		if(entity instanceof ObjectNode){
			return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		}
		if(entity instanceof SootGraphEdge){
			return Display.getDefault().getSystemColor(SWT.COLOR_RED);
		}
		return null;
	}

	@Override
	public IFigure getTooltip(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean fisheyeNode(Object entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
