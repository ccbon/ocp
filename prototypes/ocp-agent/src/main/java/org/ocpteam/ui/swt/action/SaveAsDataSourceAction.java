package org.ocpteam.ui.swt.action;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;

public class SaveAsDataSourceAction extends Action {
	private DataSourceWindow window;

	public SaveAsDataSourceAction(DataSourceWindow w) {
		window = w;
		setText("Sa&ve As@F12");
		setToolTipText("Save As File");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(DataSourceWindow.class
							.getResourceAsStream("saveas_edit.gif")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			LOG.info("Saving as datasource");
			Display display = window.getShell().getDisplay();
			Shell shell = new Shell(display);
			shell.setLayout(new FillLayout());
			FileDialog fd = new FileDialog(shell, SWT.SAVE);
			fd.setText("Save As");
			fd.setFilterPath(System.getProperty("user.home"));
			try {
				String[] filterExt = (String[]) window.ds.getResource("swt")
						.getObject("file_ext");
				fd.setFilterExtensions(filterExt);
			} catch (Exception e) {
				fd.setFilterExtensions(new String[] {"*." + window.ds.getProtocolName().toLowerCase(), "*.*"});
			}
			String selected = fd.open();
			window.getShell().setFocus();
			LOG.info(selected);
			if (selected == null) {
				return;
			}
			File file = new File(selected);
			window.ds.saveAs(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		window.refresh();
	}
}