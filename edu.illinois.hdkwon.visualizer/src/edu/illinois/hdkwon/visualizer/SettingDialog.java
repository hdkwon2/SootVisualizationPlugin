package edu.illinois.hdkwon.visualizer;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.illinois.hdkwon.visualizer.handlers.SPARKHandler;

public class SettingDialog {
	public static void promptSetupShell(Shell shell, final SPARKHandler handler){
		final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setText("SPARK Setup");
		
		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth = 10;
		formLayout.marginHeight = 10;
		formLayout.spacing = 10;
		dialog.setLayout (formLayout);
		
		FormData data = new FormData ();
		
		Button cancel = new Button (dialog, SWT.PUSH);
		cancel.setText ("Cancel");
		data = new FormData ();
		data.width = 60;
		data.right = new FormAttachment (100, 0);
		data.bottom = new FormAttachment (100, 0);
		cancel.setLayoutData (data);
		cancel.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				System.out.println("User cancelled dialog");
				dialog.close ();
			}
		});
		
		Label label1 = new Label (dialog, SWT.NONE);
		label1.setText ("Classes included in the analysis:");
		data = new FormData();
		label1.setLayoutData (data);
		
		final Text text1 = new Text (dialog, SWT.BORDER);
		data = new FormData ();
		data.width = 300;
		data.left = new FormAttachment (label1, 0, SWT.DEFAULT);
		data.right = new FormAttachment (100, 0);
		data.top = new FormAttachment (label1, 0, SWT.CENTER);
		text1.setLayoutData (data);
	
		Label label2 = new Label (dialog, SWT.NONE);
		label2.setText("Main class:");
		data = new FormData();
		data.top = new FormAttachment(label1, 0, SWT.BOTTOM);
		label2.setLayoutData(data);
		
		final Text text2 = new Text (dialog, SWT.BORDER);
		data = new FormData();
		data.width = 300;
		data.left = new FormAttachment(label2, 0, SWT.DEFAULT);
		data.right = new FormAttachment (100, 0);
//		data.bottom = new FormAttachment (cancel, 0, SWT.DEFAULT);
		data.top = new FormAttachment (label2, 0, SWT.CENTER);
		text2.setLayoutData(data);
		
		Label label3 = new Label(dialog, SWT.NONE);
		label3.setText("Method to be analyzed:");
		data = new FormData();
		data.top = new FormAttachment(label2, 0, SWT.BOTTOM);
		label3.setLayoutData(data);
		
		final Text text3 = new Text(dialog, SWT.BORDER);
		data = new FormData();
		data.width = 300;
		data.left = new FormAttachment(label3, 0, SWT.DEFAULT);
		data.right = new FormAttachment (100, 0);
		data.bottom = new FormAttachment(cancel, 0, SWT.DEFAULT);
		data.top = new FormAttachment (label3, 0, SWT.CENTER);
		text3.setLayoutData(data);
		
		Button ok = new Button (dialog, SWT.PUSH);
		ok.setText ("OK");
		data = new FormData ();
		data.width = 60;
		data.right = new FormAttachment (cancel, 0, SWT.DEFAULT);
		data.bottom = new FormAttachment (100, 0);
		ok.setLayoutData (data);
		ok.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				ArrayList<String> returnedResult = new ArrayList<String>();
				returnedResult.add(text1.getText());
				returnedResult.add(text2.getText());
				returnedResult.add(text3.getText());
				dialog.close ();
				handler.okCallBack(returnedResult);
			}
		});
		
		dialog.setDefaultButton (ok);
		dialog.pack ();
		dialog.open ();
	}

}
