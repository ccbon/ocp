package org.ocpteamx.protocol.zip;

import java.io.File;

import org.ocpteam.component.DataSource;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.misc.LOG;

public class ZipDataSource extends DataSource {
	
	public ZipDataSource() throws Exception {
		super();
		addComponent(IDataModel.class, new ZipFileSystem());
	}
	
	@Override
	public String getProtocolName() {
		return "ZIP";
	}
	
	@Override
	public void newTemp() throws Exception {
		super.newTemp();
		ZipUtils.createEmptyFile(getFile().getAbsolutePath());
	}
	
	@Override
	public void save() throws Exception {

	}

	
	@Override
	public void saveAs(File file) throws Exception {
		LOG.debug("zip saveas");
		file.delete();
		getFile().renameTo(file);
		bIsNew = false;
		setFile(file);
	}
	
	@Override
	public void connect() throws Exception {
		super.connect();
		LOG.debug("opening datasource: " + getFile());
		ZipFileSystem fs = (ZipFileSystem) getComponent(IDataModel.class);
		fs.refresh();
		setContext(new Context(fs));
	}
	
	@Override
	public Context getContext() {
		return context;
	}
}
