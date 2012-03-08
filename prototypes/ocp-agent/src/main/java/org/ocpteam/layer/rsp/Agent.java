package org.ocpteam.layer.rsp;


public abstract class Agent {

	private boolean bIsConnected = false;

	protected DataSource ds;

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
	
	

	public abstract void login(Authentication a) throws Exception;

	public abstract void logout(Authentication a) throws Exception;

	public abstract FileSystem getFileSystem(User user);

	public abstract boolean isOnlyClient();

	

	public Context getContext() {
		return context;
	}
}
