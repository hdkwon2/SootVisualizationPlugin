package edu.illinois.hdkwon.visualizer.views;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class FilterMenu {
	
	final Composite composite;

	final Combo classDropDown;
	final Combo methodDropDown;
	final Combo typeDropDown;
	final Combo localDropDown;
	final Combo fieldDropDown;
	final Button graphButton;
	
	final Map localPointsTo;
	final Map fieldPointsTo;
	final View view;
	
	public FilterMenu(View v,  Map lpt, Map fpt) {
		init();
		this.view = v;
		this.localPointsTo = lpt;
		this.fieldPointsTo = fpt;
		
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 5;
		rowLayout.marginWidth = 15;
		rowLayout.marginHeight = 5;

		composite = new Composite(view.shell, SWT.BAR);
		composite.setLayout(rowLayout);		
	
		classDropDown = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER);
		methodDropDown = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER);
		typeDropDown = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER);
		localDropDown = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER);
		fieldDropDown = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER);
		
		graphButton = new Button(composite, SWT.PUSH);
		graphButton.setText("Graph");
		graphButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				String className = classDropDown.getText();
				String methodName = methodDropDown.getText();
				String typeName = typeDropDown.getText();
				String localName = localDropDown.getText();
				String fieldName = fieldDropDown.getText();
				
				view.buildGraph(className, methodName, typeName, localName, fieldName);
			}
		});
		
		for (int i = 0; i < 3; i++) {
			classDropDown.add("item " + i);
		}

		classDropDown.addSelectionListener(new DropDownSelectionEventListener(
				this, DropDownSelectionEventListener.CLASS_DROP_DOWN));
		methodDropDown.addSelectionListener(new DropDownSelectionEventListener(
				this, DropDownSelectionEventListener.METHOD_DROP_DOWN));
		typeDropDown.addSelectionListener(new DropDownSelectionEventListener(
				this, DropDownSelectionEventListener.TYPE_DROP_DOWN));
		localDropDown.addSelectionListener(new DropDownSelectionEventListener(
				this, DropDownSelectionEventListener.LOCAL_DROP_DOWN));
		fieldDropDown.addSelectionListener(new DropDownSelectionEventListener(
				this, DropDownSelectionEventListener.FIELD_DROP_DOWN));

		Set types = localPointsTo.keySet();
		Iterator ti = types.iterator();
		while(ti.hasNext()){
			String type = (String) ti.next();
			typeDropDown.add(type);
		}
	}

	private void init() {

	}

	static class DropDownSelectionEventListener implements SelectionListener{

		public static final int CLASS_DROP_DOWN = 0;
		public static final int METHOD_DROP_DOWN = 1;
		public static final int TYPE_DROP_DOWN = 2;
		public static final int LOCAL_DROP_DOWN = 3;
		public static final int FIELD_DROP_DOWN = 4;
		
		FilterMenu menu;
		int combo;
		
		DropDownSelectionEventListener(FilterMenu menu, int combo){
			this.menu = menu;
			this.combo = combo;
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			String fieldName = null;
			String localName = null;
			String typeName = null;
			String methodName = null;
			String className = null;
			
			switch(combo){
			case FIELD_DROP_DOWN:
				fieldName = menu.fieldDropDown.getText();
			case LOCAL_DROP_DOWN:
				localName = menu.localDropDown.getText();
			case TYPE_DROP_DOWN:
				typeName = menu.typeDropDown.getText();
			case METHOD_DROP_DOWN:
				methodName = menu.methodDropDown.getText();
				
			case CLASS_DROP_DOWN:
				className = menu.classDropDown.getText();
			}
			
			switch(combo){
			case FIELD_DROP_DOWN:
				break;
				
			case LOCAL_DROP_DOWN:
				break;
				
			case TYPE_DROP_DOWN:
				menu.localDropDown.removeAll();
				menu.fieldDropDown.removeAll();
				Set locals = ((Map)menu.localPointsTo.get(typeName)).keySet();
				Iterator li = locals.iterator();
				while(li.hasNext()){
					String local = (String) li.next();
					menu.localDropDown.add(local);
				}
				
				Set fields = new HashSet();
				try{
					Iterator fpi = ((Map)menu.fieldPointsTo.get(typeName)).entrySet().iterator();
					while(fpi.hasNext()){
						Entry entry = (Entry) fpi.next();
						Map fieldMap = (Map) entry.getValue();
						fields.addAll(fieldMap.keySet());
					}
				}catch(NullPointerException exception){
					break;
				}
				Iterator fi = fields.iterator();
				while(fi.hasNext()){
					String field = (String) fi.next();
					menu.fieldDropDown.add(field);
				}
				break;
				
			case METHOD_DROP_DOWN:
				methodName = menu.methodDropDown.getText();
				break;
				
			case CLASS_DROP_DOWN:
				className = menu.classDropDown.getText();
			}		
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
