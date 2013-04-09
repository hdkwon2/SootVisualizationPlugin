package edu.illinois.hdkwon.visualizer.views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	
	/**
	 * Builds the full graph representing given local and field points-to sets. 
	 * @return Set of edges
	 */
	public Set<SootGraphEdge> buildFullGraph(){
		Set<SootGraphEdge> edges = new HashSet<SootGraphEdge>();
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
				Map fpm = fieldPointsTo.get(type);
				if(fpm != null){
					fpm = (Map) fpm.get(localName);
				}
				Set pts = e2.getValue();
				Iterator si = pts.iterator();
				while(si.hasNext()){
					Node node = (Node) si.next();
					ObjectNode objNode = (ObjectNode) getGraphNode(graphNodeMap, node);
		
					LocalToObjectEdge edge = new LocalToObjectEdge(localNode, objNode);
					edges.add(edge);
					
					if(fpm != null){
						Iterator fpmi = fpm.entrySet().iterator();
						while(fpmi.hasNext()){
							Entry e3 = (Entry) fpmi.next();
							String fieldName = (String) e3.getKey();
							Set nodeSet = (Set)e3.getValue();
							Iterator nodeSeti = nodeSet.iterator();
							while(nodeSeti.hasNext()){
								Node n = (Node) nodeSeti.next();
								ObjectNode objNode2 = (ObjectNode) getGraphNode(graphNodeMap, n);
								
								FieldToObjectEdge edge2 = new FieldToObjectEdge(objNode, objNode2, fieldName);
								edges.add(edge2);
							}
						}
					}
				}	
			}
		}
		return edges;
	}
	
	
	/**
	 * Filters points-to sets by 
	 * @param methodName
	 * @param typeName
	 * @return
	 */
	public Set<SootGraphEdge> filterGraph(String methodName, String typeName, String localName, String fieldName){
		Set res = new HashSet();
		Map graphNodeMap = new HashMap();
		Map typeMap = localPointsTo.get(typeName);
		if(typeMap == null) return res;
		
		Set localSet = (Set) typeMap.get(localName);
		if(localSet == null) return res;
		
		typeMap = fieldPointsTo.get(typeName);
		if(typeMap == null) return res;
		
		Map localMap = (Map) typeMap.get(localName);
		if(localMap == null) return res;
		
		Set fieldSet = (Set) localMap.get(fieldName);
		if(fieldSet == null) return res;
		
		LocalNode source = new LocalNode(localName);
		Iterator lsi = localSet.iterator();
		while(lsi.hasNext()){
			Node n = (Node) lsi.next();
			ObjectNode destination = (ObjectNode) getGraphNode(graphNodeMap, n);
			LocalToObjectEdge e1 = new LocalToObjectEdge(source, destination);
			res.add(e1);
			Iterator fsi = fieldSet.iterator();
			while(fsi.hasNext()){
				Node n2 = (Node) fsi.next();
				ObjectNode fieldDestination = (ObjectNode) getGraphNode(graphNodeMap, n2);
				FieldToObjectEdge e2 = new FieldToObjectEdge(destination, fieldDestination, fieldName);
				res.add(e2);
			}
		}
		return res;
	}
	
	private SootGraphNode getGraphNode(Map graphNodeMap, Node node){
		SootGraphNode res;
		
		if(graphNodeMap.containsKey(node))
			res = (SootGraphNode) graphNodeMap.get(node);
		else{
			res = new ObjectNode(node);
			graphNodeMap.put(node, res);
		}
		return res;
	}
}
