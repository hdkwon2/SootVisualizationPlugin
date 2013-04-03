package edu.illinois.hdkwon.visualizer.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import soot.jimple.spark.pag.Node;

public class View extends ViewPart {
	public static final String ID = "de.vogella.zest.first.view";
	private Graph graph;
	private int layout = 1;
	private boolean show = false;
	private HashMap<Node, GraphNode> graphMap;

	public void createPartControl(Composite parent) {

		// Graph will hold all other objects
		graph = new Graph(parent, SWT.NONE);
	}

	public void setLayoutManager() {
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

	}

	public void setView(Map localPointsTo, Map fieldPointsTo){

		GraphViewer viewer = new GraphViewer(graph.getParent(), SWT.BORDER);
		viewer.setContentProvider(new SootNodeContentProvider());
		viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		SPARKModelContentProvider model = new SPARKModelContentProvider(localPointsTo, fieldPointsTo);
		viewer.setInput(model.buildFullGraph());
		LayoutAlgorithm layout = new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		viewer.setLayoutAlgorithm(layout, true);
		viewer.applyLayout();
		
//					// Lets have a directed connection
//					new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, node1,
//							node2);
//					// Lets have a dotted graph connection
//					new GraphConnection(graph, ZestStyles.CONNECTIONS_DOT, node2, node3);
//					// Standard connection
//					new GraphConnection(graph, SWT.NONE, node3, node1);
//					// Change line color and line width
//					GraphConnection graphConnection = new GraphConnection(graph,
//							SWT.NONE, node1, node4);
//					graphConnection.changeLineColor(graph.getParent().getDisplay().getSystemColor(
//							SWT.COLOR_GREEN));
//					// Also set a text
//					graphConnection.setText("This is a text");
//					graphConnection.setHighlightColor(graph.getParent().getDisplay()
//							.getSystemColor(SWT.COLOR_RED));
//					graphConnection.setLineWidth(3);

/*					graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(
							LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
					// Selection listener on graphConnect or GraphNode is not supported
					// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=236528
					graph.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							System.out.println(e);
						}

					}); */
	  }

	private GraphNode getGraphNode(Node node) {
		GraphNode obj;
		if (graphMap.containsKey(node)) {
			obj = graphMap.get(node);
		} else {
			obj = new GraphNode(graph, SWT.NONE, node.getNumber() + ", "
					+ node.getType().toString());
			graphMap.put(node, obj);
		}

		return obj;
	}

	/** * Passing the focus request to the viewer's control. */

	public void setFocus() {
	}
}