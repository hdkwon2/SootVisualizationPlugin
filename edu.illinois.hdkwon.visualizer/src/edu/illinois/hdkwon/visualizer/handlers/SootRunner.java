package edu.illinois.hdkwon.visualizer.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.Local;
import soot.PointsToSet;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.ValueBox;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.tagkit.LineNumberTag;

public class SootRunner {

	public static void runSoot(String classPath){

		List<String> sootArgs = new LinkedList<String>();
		
		sootArgs.add("-cp");
		sootArgs.add(classPath);
		
		// enable whole program mode
		sootArgs.add("-w");
		
		// enable points-to analysis
		sootArgs.add("-p");
		sootArgs.add("cg");
		sootArgs.add("enabled:true");

		// enable Spark
		sootArgs.add("-p");
		sootArgs.add("cg.spark");
		sootArgs.add("enabled:true");
		
		sootArgs.add("Container");
		sootArgs.add("Item");
		sootArgs.add("Test1");
		soot.Main.main(sootArgs.toArray(new String[0]));
		
		SootClass sc = Scene.v().getMainClass();

		Map map = getLocals(sc, "go", "Container");
//		printLocalIntersects(map);
	}
	
	private static int getLineNumber(Stmt s) {
		Iterator ti = s.getTags().iterator();
		while (ti.hasNext()) {
			Object o = ti.next();
			if (o instanceof LineNumberTag) 
				return Integer.parseInt(o.toString());
		}
		return -1;
	}
	
	private static Map/*<Integer,Local>*/ getLocals(SootClass sc, String methodname, String typename) {
		Map res = new HashMap();
		Iterator mi = sc.getMethods().iterator();
		while (mi.hasNext()) {
			SootMethod sm = (SootMethod)mi.next();
			System.err.println(sm.getName());
			if (true && sm.getName().equals(methodname) && sm.isConcrete()) {
				JimpleBody jb = (JimpleBody)sm.retrieveActiveBody();
				Iterator ui = jb.getUnits().iterator();
				while (ui.hasNext()) {
					Stmt s = (Stmt)ui.next();						
					int line = getLineNumber(s);
					// find definitions
					Iterator bi = s.getDefBoxes().iterator();
					while (bi.hasNext()) {
						Object o = bi.next();
						if (o instanceof ValueBox) {
							Value v = ((ValueBox)o).getValue();
							if (/*v.getType().toString().equals(typename) &&*/ v instanceof Local)
								res.put(new Integer(line),v);
						}
					}					
				}
			}
		}
		
		return res;
	}
	
	private static void printLocalIntersects(Map/*<Integer,Local>*/ ls) {
		soot.PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		Iterator i1 = ls.entrySet().iterator();
		while (i1.hasNext()) {
			Map.Entry e1 = (Map.Entry)i1.next();
			int p1 = ((Integer)e1.getKey()).intValue();
			Local l1 = (Local)e1.getValue();
			PointsToSet r1 = pta.reachingObjects(l1);
			Iterator i2 = ls.entrySet().iterator();
			while (i2.hasNext()) {
				Map.Entry e2 = (Map.Entry)i2.next();
				int p2 = ((Integer)e2.getKey()).intValue();
				Local l2 = (Local)e2.getValue();
				PointsToSet r2 = pta.reachingObjects(l2);
				if (p1<=p2)
					System.out.println("["+l1.getName()+","+l2.getName()+"]\t Container intersect? "+r1.hasNonEmptyIntersection(r2));
			}
		}
	}
}
