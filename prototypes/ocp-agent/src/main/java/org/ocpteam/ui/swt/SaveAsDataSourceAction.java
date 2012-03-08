package org.ocpteam.ui.swt;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.misc.JLG;


public class SaveAsDataSourceAction extends Action {
	private DataSourceWindow window;

	public SaveAsDataSourceAction(DataSourceWindow w) {
		window = w;
		setText("Sa&ve As@F12");
		setToolTipText("Save As File");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(ExitAction.class
							.getResourceAsStream("saveas_edit.gif")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			JLG.debug("Saving as datasource");
			Display display = window.getShell().getDisplay();
			Shell shell = new Shell(display);
			shell.setLayout(new FillLayout());
			FileDialog fd = new FileDialog(shell, SWT.SAVE);
			fd.setText("Save As");
			fd.setFilterPath(System.getProperty("user.home"));
			String[] filterExt = (String[]) window.ds.getResource("swt").getObject(
					"file_ext");
			fd.setFilterExtensions(filterExt);
			String selected = fd.open();
			window.getShell().setFocus();
			JLG.debug(selected);
			if (selected == null) {
				return;
			}
			File file = new File(selected);
			window.ds.setFile(file);
			window.ds.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}