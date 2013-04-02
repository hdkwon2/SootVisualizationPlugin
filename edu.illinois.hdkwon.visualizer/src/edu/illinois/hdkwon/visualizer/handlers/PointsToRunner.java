package edu.illinois.hdkwon.visualizer.handlers;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soot.EntryPoints;
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
import soot.jimple.spark.SparkTransformer;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.options.Options;
import soot.tagkit.LineNumberTag;

public class PointsToRunner {
	
	
	
	static SootClass sootClass;
	
	private static SootClass loadClass(String name, boolean main) {
		SootClass c = Scene.v().loadClassAndSupport(name);
		c.setApplicationClass();
		if (main) Scene.v().setMainClass(c);
		return c;
	}
	
	public static Map runAnalysis(String classPath, String dir) {
	
		// Set soot options
		soot.options.Options.v().set_keep_line_number(true);
		soot.options.Options.v().set_whole_program(true);
		soot.options.Options.v().setPhaseOption("cg","verbose:true");
		soot.options.Options.v().set_src_prec(Options.src_prec_java);
		soot.options.Options.v().set_process_dir(Arrays.asList(new String[]{dir}));		
		soot.options.Options.v().set_soot_classpath(classPath);
				
		sootClass = loadClass("Test1", true);
		soot.Scene.v().loadNecessaryClasses();
		soot.Scene.v().setEntryPoints(EntryPoints.v().all());

		setSparkPointsToAnalysis();

		SootField f = getField("Container","item");
		return getLocals(sootClass,"go","Container");
		
	}
	
	static void setSparkPointsToAnalysis() {
		System.out.println("[spark] Starting analysis ...");
				
		HashMap opt = new HashMap();
		opt.put("enabled","true");
		opt.put("verbose","false");
		opt.put("ignore-types","false");          
		opt.put("force-gc","false");            
		opt.put("pre-jimplify","false");          
		opt.put("vta","false");                   
		opt.put("rta","false");                   
		opt.put("field-based","false");           
		opt.put("types-for-sites","false");        
		opt.put("merge-stringbuffer","true");   
		opt.put("string-constants","false");     
		opt.put("simulate-natives","true");      
		opt.put("simple-edges-bidirectional","false");
		opt.put("on-fly-cg","true");            
		opt.put("simplify-offline","false");    
		opt.put("simplify-sccs","true");        
		opt.put("ignore-types-for-sccs","false");
		opt.put("propagator","worklist");
		opt.put("set-impl","double");
		opt.put("double-set-old","hybrid");         
		opt.put("double-set-new","hybrid");
		opt.put("dump-html","false");           
		opt.put("dump-pag","false");             
		opt.put("dump-solution","false");        
		opt.put("topo-sort","false");           
		opt.put("dump-types","true");             
		opt.put("class-method-var","true");     
		opt.put("dump-answer","false");          
		opt.put("add-tags","false");             
		opt.put("set-mass","false"); 		
		
		SparkTransformer.v().transform("",opt);
		
		System.out.println("[spark] Done!");
	}
	

	public static Map getPointsToSet(Map ls){

		Map<String, Set> map = new HashMap<String, Set>();
		
		soot.PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		Iterator mi = ls.entrySet().iterator();
		
		while(mi.hasNext()){
			Map.Entry<Integer,Local> entry = (Entry) mi.next();
			final Local local = entry.getValue();
			PointsToSet pts = pta.reachingObjects(local);
			final Set<Node> set =new HashSet<Node>();
			
			((PointsToSetInternal)pts).forall(new P2SetVisitor() {
				
				@Override
				public void visit(Node n) {
					// TODO Auto-generated method stub
					set.add(n);
					System.out.println(local.getName() + ", "+n.getNumber());
				}
			});
			System.out.println(local.getName() + ": " + set.size());
			map.put(local.getName(), set);
		}
		
		
		return map;
	}
	
	public static Map getFieldPointsToSet(Map/*<Integer,Local>*/ ls, SootField f) {
		HashMap<String, Set> map = new HashMap<String, Set>();
		soot.PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		
		Iterator i1 = ls.entrySet().iterator();
		while (i1.hasNext()) {
			Map.Entry e1 = (Map.Entry)i1.next();
			int p1 = ((Integer)e1.getKey()).intValue();
			Local l1 = (Local)e1.getValue();
			PointsToSet pts = pta.reachingObjects(l1,f);
			
			final Set<Node> set =new HashSet<Node>();
			((PointsToSetInternal)pts).forall(new P2SetVisitor() {
				
				@Override
				public void visit(Node n) {
					// TODO Auto-generated method stub
					set.add(n);
				}
			});
			map.put(l1.getName(), set);
		}
		
		return map;
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
	
	public static SootField getField(String classname, String fieldname) {
		Collection app = Scene.v().getApplicationClasses();
		Iterator ci = app.iterator();
		while (ci.hasNext()) {
			SootClass sc = (SootClass)ci.next();
			if (sc.getName().equals(classname))
				return sc.getFieldByName(fieldname);
		}
		throw new RuntimeException("Field "+fieldname+" was not found in class "+classname);
	}
	
	private static Map/*<Integer,Local>*/ getLocals(SootClass sc, String methodname, String typename) {
		Map res = new HashMap();
		Iterator mi = sc.getMethods().iterator();
		int x = 0;
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
							System.out.println(v);
							if (/*v.getType().toString().equals(typename) &&*/ v instanceof Local)
								res.put(x++,v);
						}
					}					
				}
			}
		}
		
		return res;
	}
	
	public static void printLocalIntersects(Map/*<Integer,Local>*/ ls) {
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
	
	
	private static void printFieldIntersects(Map/*<Integer,Local>*/ ls, SootField f) {
		soot.PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		Iterator i1 = ls.entrySet().iterator();
		while (i1.hasNext()) {
			Map.Entry e1 = (Map.Entry)i1.next();
			int p1 = ((Integer)e1.getKey()).intValue();
			Local l1 = (Local)e1.getValue();
			PointsToSet r1 = pta.reachingObjects(l1,f);
			Iterator i2 = ls.entrySet().iterator();
			while (i2.hasNext()) {
				Map.Entry e2 = (Map.Entry)i2.next();
				int p2 = ((Integer)e2.getKey()).intValue();
				Local l2 = (Local)e2.getValue();
				PointsToSet r2 = pta.reachingObjects(l2,f);	
				if (p1<=p2)
					System.out.println("["+p1+","+p2+"]\t Container.item intersect? "+r1.hasNonEmptyIntersection(r2));
			}
		}
	}
	
}