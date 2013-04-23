package edu.illinois.hdkwon.visualizer.handlers;

import java.util.Arrays;
import java.util.HashMap;

import soot.EntryPoints;
import soot.Scene;
import soot.SootClass;
import soot.jimple.spark.SparkTransformer;

public class SPARKRunner {
	
	private static SootClass sootClass;
	
	public static void runSPARK(String classPath, String dir, String mainClass) {
		
		// Set soot options
		soot.options.Options.v().set_process_dir(Arrays.asList(new String[]{dir}));		
		soot.options.Options.v().set_soot_classpath(classPath);
		
		sootClass = loadClass(mainClass, true);
		soot.Scene.v().loadNecessaryClasses();
		soot.Scene.v().setEntryPoints(EntryPoints.v().all());

		setSparkPointsToAnalysis();
		
		
	}
	

	private static SootClass loadClass(String name, boolean main) {
		SootClass c = Scene.v().loadClassAndSupport(name);
		c.setApplicationClass();
		if (main) Scene.v().setMainClass(c);
		return c;
	}
	
	private static void setSparkPointsToAnalysis() {
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
}
