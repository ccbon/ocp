package org.ocpteam.layer.rsp;



public abstract class Agent {

	private boolean bIsConnected = false;

	public DataSource ds;

	protected Context context;

	public Agent(DataSource ds) {
		this.ds = ds;
	}
	
	public void connect() throws Exception {
		if (bIsConnected == true) {
			throw new Exception("Already connected");
		}
		onConnect();
		bIsConnected = true;
	}

	public void disconnect() throws Exception {
		if (bIsConnected == false) {
			throw new Exception("Already disconnected");
		}
		onDisconnect();
		bIsConnected = false;
	}

	protected abstract void onConnect() throws Exception;

	protected abstract void onDisconnect();

	public boolean isConnected() {
		return bIsConnected;
	}
	

	public abstract FileSystem getFileSystem(User user);

	public Context getContext() {
		return context;
	}
}
