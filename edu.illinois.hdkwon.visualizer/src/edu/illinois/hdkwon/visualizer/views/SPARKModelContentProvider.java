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
	 * Filters points-to sets by 
	 * @param methodName
	 * @param typeName
	 * @return
	 */
	public Set<SootGraphEdge> buildGraph(String className, String methodName,
			String typeName, String localName, String fieldName) {
		Set edgeSet = new HashSet();
		
		if(className.length() > 0){
			Map methodMap = localPointsTo.get(className);
			Map fieldMethodMap = fieldPointsTo.get(className);
			buildFromClassMap(edgeSet, methodMap, fieldMethodMap, methodName, typeName, localName, fieldName);
		}else{
			Iterator lpti = localPointsTo.entrySet().iterator();
			while(lpti.hasNext()){
				Entry entry = (Entry) lpti.next();
				String clsName = (String) entry.getKey();
				Map methodMap = (Map) entry.getValue();
				Map fieldMethodMap = (Map) fieldPointsTo.get(clsName);
				buildFromClassMap(edgeSet, methodMap, fieldMethodMap, methodName, typeName, localName, fieldName );
			}
		}
		
		return edgeSet;
	}

	private void buildFromClassMap(Set edgeSet,
			Map methodMap, Map fieldMethodMap, String methodName, String typeName,
			String localName, String fieldName) {

		if(methodName.length() > 0){
			Map typeMap = (Map) methodMap.get(methodName);
			Map fieldTypeMap = (Map) fieldMethodMap.get(methodName);
			buildFromMethodMap(edgeSet, typeMap, fieldTypeMap, typeName, localName, fieldName);
		}else{
			Iterator mmi = methodMap.entrySet().iterator();
			while(mmi.hasNext()){
				Entry entry = (Entry) mmi.next();
				String mtdName = (String) entry.getKey();
				Map typeMap = (Map) entry.getValue();
				Map fieldTypeMap = (Map) fieldMethodMap.get(mtdName);
				buildFromMethodMap(edgeSet, typeMap, fieldTypeMap, typeName, localName, fieldName);
			}
		}
	}
	
	private void buildFromMethodMap(Set edgeSet, Map typeMap, Map fieldTypeMap,
			String typeName, String localName, String fieldName) {
		
		if(typeName.length() > 0){
			Map localMap = (Map) typeMap.get(typeName);
			Map fieldLocalMap = (Map) fieldTypeMap.get(typeName);
			if(fieldLocalMap == null) fieldLocalMap = new HashMap();
			buildFromTypeMap(edgeSet, localMap, fieldLocalMap, localName, fieldName);
		}else{
			Iterator tmi = typeMap.entrySet().iterator();
			while(tmi.hasNext()){
				Entry entry = (Entry) tmi.next();
				String tpName = (String) entry.getKey();
				Map localMap = (Map) entry.getValue();
				Map fieldLocalMap = (Map) fieldTypeMap.get(tpName);
				if(fieldLocalMap == null) fieldLocalMap = new HashMap();
				buildFromTypeMap(edgeSet, localMap, fieldLocalMap,localName, fieldName );
			}
		}
	}
	
	private void buildFromTypeMap(Set edgeSet, Map localMap, Map fieldLocalMap,
			String localName, String fieldName) {

		if(localName.length() > 0){
			Set nodeSet = (Set) localMap.get(localName);
		
			if(fieldName.length() > 0){
				Set nodeSet2 = (Set) ((Map)fieldLocalMap.get(localName)).get(fieldName);
				buildFromLocalMap(edgeSet, localName, nodeSet, fieldName, nodeSet2);
			}else{
				Map fieldMap = (Map) fieldLocalMap.get(localName);
				if(fieldMap == null) fieldMap = new HashMap();
				buildFromLocalMap(edgeSet, localName, nodeSet, fieldMap);
			}
		}else{
			Iterator lmi = localMap.entrySet().iterator();
			while(lmi.hasNext()){
				Entry entry = (Entry) lmi.next();
				String lcName = (String) entry.getKey();
				Set nodeSet = (Set) entry.getValue();
				
				if(fieldName.length() > 0){
					Set nodeSet2 = (Set) ((Map)fieldLocalMap.get(lcName)).get(fieldName);
					buildFromLocalMap(edgeSet, localName, nodeSet, fieldName, nodeSet2);
				}else{
					Map fieldMap = (Map) fieldLocalMap.get(lcName);
					if(fieldMap == null) fieldMap = new HashMap();
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
