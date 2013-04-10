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
	private final Map<Node, ObjectNode> objectNodeMap;
	private final Map<String, LocalNode> localNodeMap;
	
	public SPARKModelContentProvider(Map localPointsTo, Map fieldPointsTo){
		this.localPointsTo = localPointsTo;
		this.fieldPointsTo = fieldPointsTo;
		this.objectNodeMap = new HashMap<Node, ObjectNode>();
		this.localNodeMap = new HashMap<String, LocalNode>();
	}
	
	/**
	 * Builds the full graph representing given local and field points-to sets. 
	 * @return Set of edges
	 */
/*	public Set<SootGraphEdge> buildFullGraph(){
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
*/
	
	
	/**
	 * Filters points-to sets by 
	 * @param methodName
	 * @param typeName
	 * @return
	 */
	public Set<SootGraphEdge> buildGraph(String className, String methodName,
			String typeName, String localName, String fieldName) {
		Set edgeSet = new HashSet();
		
		if(typeName.length() > 0){
			Map localMap = localPointsTo.get(typeName);
			buildFromTypeMap(edgeSet, typeName, localMap, localName, fieldName);
		}else{
			Iterator tmi = localPointsTo.entrySet().iterator();
			while(tmi.hasNext()){
				Entry entry = (Entry) tmi.next();
				String tpName = (String) entry.getKey();
				Map localMap = (Map) entry.getValue();
				buildFromTypeMap(edgeSet, tpName, localMap, localName, fieldName );
			}
		}
		
		return edgeSet;
	}
	
	private void buildFromTypeMap(Set edgeSet, String typeName, Map localMap,
			String localName, String fieldName) {

		if(localName.length() > 0){
			Set nodeSet = (Set) localMap.get(localName);
		
			if(fieldName.length() > 0){
				Set nodeSet2 = (Set) ((Map)((Map)fieldPointsTo.get(typeName)).get(localName)).get(fieldName);
				buildFromLocalMap(edgeSet, localName, nodeSet, fieldName, nodeSet2);
			}else{
				Map fieldMap = (Map) fieldPointsTo.get(typeName).get(localName);
				buildFromLocalMap(edgeSet, localName, nodeSet, fieldMap);
			}
		}else{
			Iterator lmi = localMap.entrySet().iterator();
			while(lmi.hasNext()){
				Entry entry = (Entry) lmi.next();
				String lcName = (String) entry.getKey();
				Set nodeSet = (Set) entry.getValue();
				
				if(fieldName.length() > 0){
					Set nodeSet2 = (Set) ((Map)((Map)fieldPointsTo.get(typeName)).get(lcName)).get(fieldName);
					buildFromLocalMap(edgeSet, localName, nodeSet, fieldName, nodeSet2);
				}else{
					Map fieldMap = (Map) fieldPointsTo.get(typeName).get(lcName);
					buildFromLocalMap(edgeSet, lcName, nodeSet, fieldMap);
				}
			}
		}
	}
	
	private void buildFromLocalMap(Set edgeSet, String localName, Set nodeSet, String fieldName, Set nodeSet2){
		LocalNode source = new LocalNode(localName);
		Iterator nsi = nodeSet.iterator();
		while(nsi.hasNext()){
			Node n = (Node) nsi.next();
			ObjectNode obj = new ObjectNode(n);
			LocalToObjectEdge e1 = new LocalToObjectEdge(source, obj);
			edgeSet.add(e1);
			
			buildFromFieldMap(edgeSet, obj, nodeSet2, fieldName);
		}
	}
	
	private void buildFromLocalMap(Set edgeSet, String localName, Set nodeSet, Map fieldMap){
		LocalNode source = new LocalNode(localName);
		Iterator nsi = nodeSet.iterator();
		while(nsi.hasNext()){
			Node n = (Node) nsi.next();
			ObjectNode obj = new ObjectNode(n);
			LocalToObjectEdge e1 = new LocalToObjectEdge(source, obj);
			edgeSet.add(e1);
			
			Iterator fmi = fieldMap.entrySet().iterator();
			while(fmi.hasNext()){
				Entry entry = (Entry) fmi.next();
				String fieldName = (String) entry.getKey();
				Set nodeSet2 = (Set) entry.getValue();
				
				buildFromFieldMap(edgeSet, obj, nodeSet2, fieldName);
			}
		}
	}
	
	private void buildFromFieldMap(Set edgeSet, ObjectNode source, Set nodeSet, String fieldName){
	
		Iterator nsi = nodeSet.iterator();
		while(nsi.hasNext()){
			Node n = (Node) nsi.next();
			ObjectNode dest = new ObjectNode(n);
			FieldToObjectEdge e = new FieldToObjectEdge(source, dest, fieldName);
			edgeSet.add(e);
		}
	}
}
