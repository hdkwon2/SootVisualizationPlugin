package edu.illinois.hdkwon.visualizer.handlers;


import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.illinois.hdkwon.visualizer.views.View;

public class SPARKHandler extends AbstractHandler{

	ExecutionEvent myEvent;
	Shell shell;
	String path;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		myEvent = event;
		shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;
		
		Object firstElement = selection.getFirstElement();
		if(firstElement instanceof IJavaElement){
			IJavaElement jElement = (IJavaElement) firstElement;
			IJavaProject jProject = jElement.getJavaProject();
			// get the absolute path of the workspace
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			String absolute = workspace.getRoot().getLocation().toFile().getAbsolutePath();
			
			String classPath = ""; 
			try {
				// add the bin folder
				classPath = absolute+ jProject.getOutputLocation().toString();
				// add the class path of the target project
				for(IClasspathEntry entry : jProject.getRawClasspath()){
					classPath += File.pathSeparator;
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
			
			SootRunner.runSoot(classPath);
		}
//		SettingDialog.promptSetupShell(shell, this);
		
		return null;
	}

	/* Called when the OK button on setting dialog is clicked */
	public void okCallBack(ArrayList<String> settings){
		try{
			System.out.println(System.getProperty("java.class.path"));
			PointsToRunner.runAnalysis(settings);
			Map map = PointsToRunner.getPointsToSet(settings.get(2), "Container");
			setView(map);
		}catch(Exception ex){
//			MessageDialog.openError(shell, "Error", "Error occured");
			ex.printStackTrace();
		}
	}
	

	
	private void setView(Map map){
		IViewPart findView = HandlerUtil.getActiveWorkbenchWindow(myEvent)
		        .getActivePage().findView("edu.illinois.hdkwon.visualizer.views.spark");

		View view = (View) findView;
	
		view.setView(map);
		
		/* Loads up the view programatically */
		try {
			HandlerUtil.getActiveWorkbenchWindow(myEvent).getActivePage().showView("edu.illinois.hdkwon.visualizer.views.spark");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
