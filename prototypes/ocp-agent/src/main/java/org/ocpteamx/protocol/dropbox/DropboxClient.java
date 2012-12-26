package org.ocpteamx.protocol.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import org.ocpteam.component.DSContainer;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.misc.JLG;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AbstractSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;

public class DropboxClient extends DSContainer<DropboxDataSource> implements
		IAuthenticable {
	public static final String TOKEN_FILE = System.getProperty("user.home")
			+ "/.dropbox_tokens";
	final static private String APP_KEY = "1s2uo2miptnr9sq";
	final static private String APP_SECRET = "tk88gf7o4gi8bxx";

	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
	public static DropboxAPI<WebAuthSession> mDBApi;
	private WebAuthSession session;

	public DropboxClient() {
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		session = new WebAuthSession(appKeys, ACCESS_TYPE);

		mDBApi = new DropboxAPI<WebAuthSession>(session);
	}

	public String getURL() throws Exception {
		return ((WebAuthSession) mDBApi.getSession()).getAuthInfo().url;
	}

	@Override
	public void authenticate() throws Exception {
		File file = new File(TOKEN_FILE);
		if (file.exists()) {
			reAuth(file);
		} else {
			newAuth(file);
		}

		if (!isLogged()) {
			throw new Exception("Problem while log in.");
		}

		IDataModel dm = ds().getComponent(IDataModel.class);
		ds().setContext(new Context(dm, "/"));
	}

	public void reAuth(File file) throws Exception {
		FileInputStream fis = null;
		try {
			Scanner tokenScanner = new Scanner(file);
			String token_key = tokenScanner.nextLine();
			String token_secret = tokenScanner.nextLine();
			tokenScanner.close();

			JLG.debug("token_key=" + token_key);
			JLG.debug("token_secret=|" + token_secret + "|");
			AccessTokenPair reAuthTokens = new AccessTokenPair(token_key,
					token_secret);

			((AbstractSession) mDBApi.getSession())
					.setAccessTokenPair(reAuthTokens);

		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	public void newAuth(File file) throws Exception {
		AccessTokenPair tokenPair = mDBApi.getSession().getAccessTokenPair();
		RequestTokenPair tokens = new RequestTokenPair(tokenPair.key,
				tokenPair.secret);
		((WebAuthSession) mDBApi.getSession()).retrieveWebAccessToken(tokens);
		PrintWriter tokenWriter = new PrintWriter(file);
		tokenWriter.println(session.getAccessTokenPair().key);
		tokenWriter.println(session.getAccessTokenPair().secret);
		tokenWriter.close();
	}

	public boolean isLogged() {
		try {
			mDBApi.accountInfo();
		} catch (DropboxException e) {
			return false;
		}
		return true;
	}

	@Override
	public void setChallenge(Object challenge) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getChallenge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unauthenticate() throws Exception {
	}

}
