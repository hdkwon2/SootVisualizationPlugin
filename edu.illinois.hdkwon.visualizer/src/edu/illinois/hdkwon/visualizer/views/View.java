package edu.illinois.hdkwon.visualizer.views;

import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

public class View extends ViewPart {

	public Shell shell;
	private SPARKModelContentProvider model;
	private GraphViewer viewer;
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		shell = new Shell(parent.getShell());
	}


	public void setView(Map localPointsTo, Map fieldPointsTo){

		shell.setLayout(new FormLayout());
		FilterMenu menu = new FilterMenu(this, localPointsTo, fieldPointsTo);
		
		final Composite comp = new Composite(shell, SWT.NONE);
		comp.setLayout(new FillLayout());
		FormData data = new FormData();
		data.width = shell.getSize().x;
		data.height = 730;
		data.top = new FormAttachment(menu.composite, 0, SWT.DEFAULT);
		data.bottom = new FormAttachment(100, 0);
		comp.setLayoutData(data);

		viewer = new GraphViewer(comp , SWT.BORDER);

		viewer.setContentProvider(new SootNodeContentProvider());
		viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		model = new SPARKModelContentProvider(localPointsTo, fieldPointsTo);
		viewer.setInput(model.buildFullGraph());
		LayoutAlgorithm layout = new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		viewer.setLayoutAlgorithm(layout, true);
		viewer.applyLayout();

		shell.addListener(SWT.RESIZE, new Listener(){

			@Override
			public void handleEvent(Event event) {
				FormData data = new FormData();
				data.width = shell.getSize().x;
				data.height = shell.getSize().y - 50;
				comp.setLayoutData(data);
				viewer.applyLayout();
				System.out.println("REsized");
			}
			
		});
//		shell.setSize(800, 800);
		shell.pack();
		shell.open();
	  }

	public void buildGraph(String className, String methodName, String typeName, String localName, String fieldName){
		Set edges = model.filterGraph(methodName, typeName, localName, fieldName);	
		viewer.setInput(edges);
		viewer.refresh();
	}

	/*public void setLayoutManager() {
	switch (layout) {
	case 1:
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		layout++;
		break;
	case 2:
		graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		layout = 1;
		break;

	}

}*/

	/** * Passing the focus request to the viewer's control. */

	public void setFocus() {
	}
}