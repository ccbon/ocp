package org.ocpteam.component;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;

import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;
import org.ocpteam.core.TopContainer;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IConnect;
import org.ocpteam.interfaces.IDocument;
import org.ocpteam.interfaces.IListener;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Contact;

public abstract class DataSource extends TopContainer implements IComponent,
		IDocument, IConnect {

	protected IContainer parent;

	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
	}

	@Override
	public IContainer getParent() {
		return parent;
	}

	private URI uri;
	private File file;

	protected boolean bIsNew = true;

	protected Context context;
	private boolean bIsConnected;
	private String name;
	protected Class<? extends Contact> contactClass;

	@Override
	public void init() throws Exception {
		super.init();
		contactClass = Contact.class;
	}

	public abstract String getProtocolName();

	/**
	 * When information is stored under properties, this function read the
	 * properties.
	 * 
	 * @throws Exception
	 */
	public void readConfig() throws Exception {
		if (getConfig().containsKey("name")) {
			setName(getProperty("name"));
		}
	}

	@Override
	public void setConfig(Properties p) throws Exception {
		super.setConfig(p);
		readConfig();
	}

	@Override
	public void open(File file) throws Exception {
		bIsNew = false;
		this.file = file;
		try {
			// try to load properties if the format is ok...
			Properties p = JLG.loadConfig(this.getFile().getAbsolutePath());
			setConfig(p);
		} catch (Exception e) {
		}
	}

	@Override
	public void newTemp() throws Exception {
		setConfig(new Properties());
		bIsNew = true;

		file = File.createTempFile("temp" + System.currentTimeMillis(), ".tmp");
		file.delete();
		file.deleteOnExit();
	}

	@Override
	public boolean isNew() {
		return bIsNew;
	}

	@Override
	public void save() throws Exception {
		LOG.debug("saving ds");
		if (bIsNew) {
			throw new Exception("Need a filename to save a new datasource");
		}

		if (this.getFile() != null) {
			JLG.saveProperties(this.getFile(), this.getConfig());
		} else {
			throw new Exception("cannot save: file not set.");
		}
	}

	@Override
	public void saveAs(File file) throws Exception {
		this.file = file;
		bIsNew = false;
		LOG.debug("save");
		save();
	}

	@Override
	public void close() throws Exception {

	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public URI getURI() throws Exception {
		return this.uri;
	}

	public void setURI(URI uri) {
		this.uri = uri;
		setProperty("uri", uri.toString());
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public synchronized void connect() throws Exception {
		if (bIsConnected == true) {
			throw new Exception("already connected.");
		}
		if (usesComponent(Client.class)) {
			getComponent(Client.class).start();
		}
		bIsConnected = true;
	}

	@Override
	public synchronized void disconnect() throws Exception {
		if (bIsConnected == false) {
			throw new Exception("already disconnected.");
		}
		if (usesComponent(Client.class)) {
			getComponent(Client.class).stop();
		}
		bIsConnected = false;
		context = null;
	}

	public ResourceBundle getResource(String subpackage) throws Exception {
		String packageString = this.getClass().getPackage().getName() + "."
				+ subpackage.toLowerCase();
		String resourceClassString = packageString + "."
				+ getProtocolName().toUpperCase() + subpackage.toUpperCase()
				+ "Resource";
		LOG.debug("class=" + resourceClassString);
		try {
			ResourceBundle result = (ResourceBundle) Class.forName(
					resourceClassString).newInstance();
			return result;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public boolean isConnected() {
		return bIsConnected;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Contact toContact() throws Exception {
		// convert the agent public information into a contact
		Contact c = contactClass.newInstance();
		c.setName(getName());
		c.setHost("localhost");
		// add the listener url and node id information
		if (usesComponent(Server.class)) {
			Iterator<IListener> it = getComponent(Server.class).getListeners()
					.iterator();
			while (it.hasNext()) {
				IListener l = it.next();
				if (l instanceof TCPListener) {
					int port = l.getUrl().getPort();
					c.setTcpPort(port);
				}
				if (l instanceof UDPListener) {
					int port = l.getUrl().getPort();
					c.setUdpPort(port);
				}
			}
		}

		return c;
	}

	/**
	 * This method is called just after a contact is detached. By default it
	 * does nothing but you can overwrite it.
	 * 
	 * @param contact
	 */
	public void onDetach(Contact contact) {
	}

}
