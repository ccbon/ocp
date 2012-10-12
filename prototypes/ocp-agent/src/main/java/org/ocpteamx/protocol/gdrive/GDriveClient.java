package org.ocpteamx.protocol.gdrive;

import java.util.Arrays;

import org.ocpteam.component.DSContainer;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.JLG;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

public class GDriveClient extends DSContainer<GDriveDataSource> implements IAuthenticable {
	private static String CLIENT_ID = "244285920854.apps.googleusercontent.com";
	private static String CLIENT_SECRET = "4f3fIHLpand1dOb6Q2il09ln";
	private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
	
	private HttpTransport httpTransport;
	private JsonFactory jsonFactory;
	private GoogleAuthorizationCodeFlow flow;
	protected Drive service;
	
	public GDriveClient() {
		httpTransport = new NetHttpTransport();		
		jsonFactory = new JacksonFactory();
		flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET,
				Arrays.asList(DriveScopes.DRIVE)).setAccessType("online")
				.setApprovalPrompt("auto").build();
	}
	
	public String getURL() {
		return flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI)
				.build();
	}

	@Override
	public void login() throws Exception {
		String code = (String) ds().getComponent(IUserManagement.class).getChallenge();
		GoogleTokenResponse response = flow.newTokenRequest(code)
				.setRedirectUri(REDIRECT_URI).execute();
		GoogleCredential credential = new GoogleCredential()
				.setFromTokenResponse(response);

		// Create a new authorized API client
		service = new Drive.Builder(httpTransport, jsonFactory,
				credential).build();
		JLG.debug("GDrive logged in.");
		IDataModel dm = ds().getComponent(IDataModel.class);
		ds().setContext(new Context(dm, "/"));
	}

	@Override
	public void logout() throws Exception {
		// TODO Auto-generated method stub

	}

}
