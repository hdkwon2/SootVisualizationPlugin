package edu.illinois.hdkwon.visualizer.handlers;


import java.io.File;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.illinois.hdkwon.visualizer.views.View;

public class SPARKHandler extends AbstractHandler{

	private ExecutionEvent myEvent;
	private Shell shell;
	private String absolutePathToWorkspace;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		myEvent = event;
		shell = HandlerUtil.getActiveShell(event);
		// get the absolute path of the workspace	
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		absolutePathToWorkspace = workspace.getRoot().getLocation().toFile().getAbsolutePath();
		
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;
		
		Object firstElement = selection.getFirstElement();
		/* Selection must be the main class that is to be analyzed */
		if(firstElement instanceof ICompilationUnit){
			ICompilationUnit jClass = (ICompilationUnit) firstElement;
			IJavaProject jProject = jClass.getJavaProject();
		
			String classPath = buildClassPath(jProject);

			Map localSet = PointsToRunner.runAnalysis(classPath, jProject.getPath().toString());
			PointsToRunner.printLocalIntersects(localSet);
			setView(PointsToRunner.getPointsToSet(localSet),
					PointsToRunner.getFieldPointsToSet(localSet,
							PointsToRunner.getField("Container", "item")));
		}
		
		return null;
	}
	
	private String buildClassPath(IJavaProject jProject){
		String classPath = ""; 
		try {
			// add the bin folder
			classPath = absolutePathToWorkspace + jProject.getOutputLocation().toString();
			// add the class path of the target project
			for(IClasspathEntry entry : jProject.getRawClasspath()){
				classPath += File.pathSeparator;
				if(entry.getEntryKind() == IClasspathEntry.CPE_SOURCE)
					classPath += absolutePathToWorkspace + entry.getPath().toString();
				else 
					classPath += entry.getPath().toString();
			}
			//add jre libraries
			for(IClasspathEntry entry: PreferenceConstants.getDefaultJRELibrary()){
				IClasspathContainer container= JavaCore.getClasspathContainer(entry.getPath(), jProject);
				for(IClasspathEntry e :container.getClasspathEntries()){
					classPath += File.pathSeparator;
					classPath += e.getPath().toString();
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		return classPath;
	}

	
	private void setView(Map lps, Map fps){
		IViewPart findView = HandlerUtil.getActiveWorkbenchWindow(myEvent)
		        .getActivePage().findView("edu.illinois.hdkwon.visualizer.views.spark");

		View view = (View) findView;
	
		view.setView(lps, fps);
		
		/* Loads up the view programatically */
		try {
			HandlerUtil.getActiveWorkbenchWindow(myEvent).getActivePage().showView("edu.illinois.hdkwon.visualizer.views.spark");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
