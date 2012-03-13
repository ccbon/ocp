package org.ocpteam.protocol.zip;

import org.ocpteam.functionality.DataModel;
import org.ocpteam.functionality.DataSource;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.misc.JLG;

public class ZipDataSource extends DataSource {
	private Context context;

	public ZipDataSource() throws Exception {
		super();
		getDesigner().add(DataModel.class, new ZipFileSystem());
	}
	@Override
	public String getProtocol() {
		return "ZIP";
	}
	
	@Override
	public void open() throws Exception {
		JLG.debug("opening datasource: " + getFile());
		ZipFileSystem fs = (ZipFileSystem) getDesigner().get(DataModel.class);
		fs.refresh();
		context = new Context(null, fs, "/");
	}
	
	@Override
	public void close() {
		context = null;
	}
	
	@Override
	public Context getContext() {
		return context;
	}
}
