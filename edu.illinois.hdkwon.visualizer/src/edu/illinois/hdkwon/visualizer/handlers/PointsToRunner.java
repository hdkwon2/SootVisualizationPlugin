package edu.illinois.hdkwon.visualizer.handlers;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soot.ArrayType;
import soot.Local;
import soot.PointsToSet;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Value;
import soot.ValueBox;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.options.Options;

//type, local, field
public class PointsToRunner {
	
	static{
		soot.options.Options.v().set_keep_line_number(true);
		soot.options.Options.v().set_whole_program(true);
//		soot.options.Options.v().setPhaseOption("cg","verbose:false");
		soot.options.Options.v().set_src_prec(Options.src_prec_java);
	}
	
	private static Map localPointsTo;
	private static Map fieldPointsTo;
	
	public static void runAnalysis(String classPath, String dir, String mainClass) {
	
		SPARKRunner.runSPARK(classPath, dir, mainClass);
		Map ls = getLocals();
		buildPointsTo(ls);
	}
	
	public static Map getLocalPointsTo(){
		return localPointsTo;
	}
	
	public static Map getFieldPointsTo(){
		return fieldPointsTo;
	}	
	
	private static void buildPointsTo(Map ls){
		fieldPointsTo = new HashMap();
		localPointsTo = new HashMap();
		Iterator lsi = ls.entrySet().iterator();
		while(lsi.hasNext()){
			Entry entry1 = (Entry) lsi.next();
			String className = (String) entry1.getKey();
			Map fieldMethodMap = new HashMap();
			Map localMethodMap = new HashMap();
			Map methodMap = (Map) entry1.getValue();
			Iterator mmi = methodMap.entrySet().iterator();
			while(mmi.hasNext()){
				Entry entry2 = (Entry) mmi.next();
				String methodName = (String) entry2.getKey();
				Map typeMap = (Map) entry2.getValue();
				Map fieldTypeMap = getFieldPointsToSet(typeMap);
				fieldMethodMap.put(methodName, fieldTypeMap);
				localMethodMap.put(methodName, getLocalPointsToSet(typeMap, fieldTypeMap));
			}
			fieldPointsTo.put(className, fieldMethodMap);
			localPointsTo.put(className, localMethodMap);
		}
	}
	
	private static Map getLocalPointsToSet(Map ls, Map fieldTypeMap){
		Map res = new HashMap();
		Iterator lsi = ls.entrySet().iterator();
		while(lsi.hasNext()){
			Entry entry = (Entry) lsi.next();
			String typeName = (String) entry.getKey();
			Set localSet = (Set) entry.getValue();
			Map localMap = new HashMap();
			Iterator lsi2 = localSet.iterator();
			while(lsi2.hasNext()){
				Local l = (Local) lsi2.next();
				Set nodeSet = localToNodeSet(l, fieldTypeMap);
				localMap.put(l.getName(), nodeSet);
			}
			res.put(typeName, localMap);
		}
		
		return res;
	}
	
	private static SootClass getClassByName(String name){
		Collection app = Scene.v().getApplicationClasses();
		Iterator appi = app.iterator();
		while(appi.hasNext()){
			SootClass sc = (SootClass) appi.next();
			if(sc.getName().equals(name)) return sc;
		}
		
		return null;
	}
	
	private static Map/* <className, Map<localName, Map<fieldName, Set<Node>>>>*/getFieldPointsToSet(
			Map/* <String, Set> */ls) {
		
		HashMap<String, Set> map = new HashMap<String, Set>();
		soot.PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		
		Collection app = Scene.v().getClasses();
		
		Iterator ci = app.iterator();

		Map typeToLocal = new HashMap();
		while (ci.hasNext()) {
			SootClass sc = (SootClass)ci.next();
			String className = sc.getName();
			Set s1 = (Set)ls.get(className);
			if(s1 != null){
				Map localToField = new HashMap();
				Iterator s1i = s1.iterator();
				while(s1i.hasNext()){
					Local local = (Local) s1i.next();
					Map fieldToNode = new HashMap();
					Iterator fi = sc.getFields().iterator();
					while(fi.hasNext()){
						SootField field = (SootField) fi.next();
						PointsToSet pts;
						try{
						pts = pta.reachingObjects(local, field);
						}catch(Exception e){
							System.out.println(field.getName());
							continue;
						}
						final Set nodeSet = new HashSet();
						((PointsToSetInternal) pts).forall(new P2SetVisitor(){

							@Override
							public void visit(Node n) {
								nodeSet.add(n);
							}
							
						});
						fieldToNode.put(field.getName(), nodeSet);
					}
					localToField.put(local.getName(), fieldToNode);
				}
				typeToLocal.put(className, localToField);
			}
		}
		return typeToLocal;
	}
	
/*	private static int getLineNumber(Stmt s) {
		Iterator ti = s.getTags().iterator();
		while (ti.hasNext()) {
			Object o = ti.next();
			if (o instanceof LineNumberTag) 
				return Integer.parseInt(o.toString());
		}
		return -1;
	}*/
	
	private static SootField getField(String classname, String fieldname) {
		Collection app = Scene.v().getApplicationClasses();
		Iterator ci = app.iterator();
		while (ci.hasNext()) {
			SootClass sc = (SootClass)ci.next();
			if (sc.getName().equals(classname))
				return sc.getFieldByName(fieldname);
		}
		throw new RuntimeException("Field "+fieldname+" was not found in class "+classname);
	}
	

	private static Map/*<methodName, Map<typeName, Set<Local>>>*/ getLocals() {
		Map classMap = new HashMap();
		Collection classes = Scene.v().getApplicationClasses();
		Iterator ci = classes.iterator();
		
		while (ci.hasNext()) {
			SootClass sc = (SootClass) ci.next();
			Map<String, Map> methodMap = new HashMap<String, Map>();
			Iterator mi = sc.getMethods().iterator();

			while (mi.hasNext()) {
				SootMethod sm = (SootMethod) mi.next();
				Map<String, Set<Local>> typeMap = new HashMap();
				if (sm.isConcrete()) {
					JimpleBody jb = (JimpleBody) sm.retrieveActiveBody();
					Iterator ui = jb.getUnits().iterator();
					while (ui.hasNext()) {
						Stmt s = (Stmt) ui.next();
						Iterator bi = s.getDefBoxes().iterator();
						while (bi.hasNext()) {
							Object o = bi.next();
							if (o instanceof ValueBox) {
								Value v = ((ValueBox) o).getValue();
								if (v instanceof Local) {
									Local l = (Local) v;
									String type = l.getType().toString();
									if (typeMap.containsKey(type)) {
										typeMap.get(type).add(l);
									} else {
										Set set = new HashSet();
										set.add(l);
										typeMap.put(type, set);
									}
								}
							}
						}
					}
				}
				methodMap.put(sm.getName(), typeMap);
			}
			classMap.put(sc.getName(), methodMap);
		}
		return classMap;
	}

	private static Set localToNodeSet(final Local local, final Map fieldTypeMap){
		final soot.PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		final PointsToSet pts = pta.reachingObjects(local);
		final Set<Node> set =new HashSet<Node>();
		
		((PointsToSetInternal)pts).forall(new P2SetVisitor() {
			
			@Override
			public void visit(Node n) {
				if(n.getType() instanceof ArrayType){
					PointsToSet pts2 = pta.reachingObjectsOfArrayElement(pts);
					final Set<Node> set2 = new HashSet();
					((PointsToSetInternal)pts2).forall(new P2SetVisitor(){
						@Override
						public void visit(Node n) {

							set2.add(n);
						}
						
					});
					Map localMap = (Map) fieldTypeMap.get(n.getType().toString());
					if(localMap == null) localMap = new HashMap();
					Map fieldMap = new HashMap();
					fieldMap.put("element", set2);
					localMap.put(local.getName(), fieldMap);
					fieldTypeMap.put(n.getType().toString(), localMap);
				}
				set.add(n);
			}
		});

		return set;
	}
}