package org.ocpteam.protocol.zip;

import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.FileSystem;
import org.ocpteam.layer.rsp.User;

public class ZipAgent extends Agent {


	@Override
	public String getProtocolName() {
		return "ZIP";
	}

	@Override
	public String getName() {
		return "Zip client";
	}

	@Override
	public FileSystem getFileSystem(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean autoStarts() {
		return false;
	}

	@Override
	public boolean isOnlyClient() {
		return true;
	}

	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean allowsUserCreation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User login(String login, Object challenge) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logout(User user) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean requiresConfigFile() {
		return false;
	}

}
