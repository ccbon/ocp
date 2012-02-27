package org.ocpteam.ui.swt;

import java.io.File;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.JLG;

public class DefineDataSourceWizard extends Wizard {

	private DataSource ds;
	private DefineDataSourceWizardPage p1;

	public DefineDataSourceWizard(DataSource ds) {
		this.ds = ds;
		setWindowTitle("Define Data Source Wizard");
	}

	@Override
	public void addPages() {
		JLG.debug("addPages");
		p1 = new DefineDataSourceWizardPage();
		addPage(p1);
	}

	@Override
	public boolean performFinish() {
		try {
			if (p1.mode == DefineDataSourceWizardPage.FILE_MODE) {
				ds.setFile(new File(p1.filename));
			} else {
				JLG.debug("selection=" + p1.selection);
				ds.setProtocol(p1.selection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean canFinish() {
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++) {
			if (!pages[i].isPageComplete()) {
				JLG.debug("page not completed: " + i);
				return false;
			}
		}
		JLG.debug("all pages completed.");
		return true;
	}

}
