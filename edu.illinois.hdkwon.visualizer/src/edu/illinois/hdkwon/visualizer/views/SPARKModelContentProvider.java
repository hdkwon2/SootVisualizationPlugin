package edu.illinois.hdkwon.visualizer.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soot.jimple.spark.pag.Node;

public class SPARKModelContentProvider {
	
	private final Map<String, Map<String, Set>> localPointsTo;
	private final Map<String, Map<String, Set>> fieldPointsTo;

	public SPARKModelContentProvider(Map localPointsTo, Map fieldPointsTo){
		this.localPointsTo = localPointsTo;
		this.fieldPointsTo = fieldPointsTo;
	}
	
	public List<SootGraphEdge> buildFullGraph(){
		List<SootGraphEdge> edges = new ArrayList<SootGraphEdge>();
		Map<Node, ObjectNode> graphNodeMap = new HashMap<Node, ObjectNode>();
		
		Iterator mi1 = localPointsTo.entrySet().iterator();
		while(mi1.hasNext()){
			Entry<String, Map> e1 = (Entry)mi1.next();
			String type = e1.getKey();
			Map ptm = e1.getValue();
			Iterator mi2 = ptm.entrySet().iterator();
			while (mi2.hasNext()) {
				Entry<String, Set> e2 = (Entry)mi2.next();
				String localName = e2.getKey();
				LocalNode localNode = new LocalNode(localName);
				
				Set pts = e2.getValue();
				Iterator si = pts.iterator();
				while(si.hasNext()){
					Node node = (Node) si.next();
					ObjectNode objNode;
					
					if(graphNodeMap.containsKey(node))
						objNode = graphNodeMap.get(node);
					else{
						objNode = new ObjectNode(node);
						graphNodeMap.put(node, objNode);
					}
					LocalToObjectEdge edge = new LocalToObjectEdge(localNode, objNode);
					edges.add(edge);
				}
			}
		}
		return edges;
	}
}
