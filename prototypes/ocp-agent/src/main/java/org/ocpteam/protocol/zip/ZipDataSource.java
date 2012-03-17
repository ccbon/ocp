package org.ocpteam.protocol.zip;

import java.io.File;

import org.ocpteam.component.IDataModel;
import org.ocpteam.component.DataSource;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.misc.JLG;

public class ZipDataSource extends DataSource {
	
	public ZipDataSource() throws Exception {
		super();
		getDesigner().add(IDataModel.class, new ZipFileSystem());
	}
	
	@Override
	public String getProtocol() {
		return "ZIP";
	}
	
	@Override
	public void newTemp() throws Exception {
		super.newTemp();
		ZipUtils.createEmptyFile(getFile().getAbsolutePath());
	}
	
	@Override
	public void saveAs(File file) throws Exception {
		JLG.debug("zip saveas");
		file.delete();
		getFile().renameTo(file);
		bIsNew = false;
	}
	
	@Override
	public void connect() throws Exception {
		JLG.debug("opening datasource: " + getFile());
		ZipFileSystem fs = (ZipFileSystem) getDesigner().get(IDataModel.class);
		fs.refresh();
		context = new Context(fs, "/");
	}
	
	@Override
	public void disconnect() {
		context = null;
	}
	
	@Override
	public Context getContext() {
		return context;
	}
}
