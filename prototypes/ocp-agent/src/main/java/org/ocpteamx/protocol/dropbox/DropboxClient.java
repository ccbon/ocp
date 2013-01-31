package org.ocpteamx.protocol.dropbox;

import java.util.Properties;

import org.ocpteam.component.DSContainer;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;

public class DropboxClient extends DSContainer<DropboxDataSource> implements
		IAuthenticable {
	final static private String APP_KEY = "1s2uo2miptnr9sq";
	final static private String APP_SECRET = "tk88gf7o4gi8bxx";

	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
	public DropboxAPI<WebAuthSession> mDBApi;
	public Properties properties;
	public String username;
	private String tokenSecret;
	private String tokenKey;
	private boolean bRememberMe;

	public DropboxClient() {
	}

	public void setup() {
		loadTokens();

		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		mDBApi = new DropboxAPI<WebAuthSession>(new WebAuthSession(appKeys,
				ACCESS_TYPE));
	}

	public String getURL() throws Exception {
		return getSession().getAuthInfo().url;
	}

	@Override
	public void authenticate() throws Exception {
		if (isLogged()) {
			return;
		}

		if (!bRememberMe) {
			properties.remove(username);
		}

		LOG.info("username=" + username);
		LOG.info(properties.toString());
		if (properties.containsKey(username)) {
			reAuth();
		} else {
			newAuth();
		}

		if (!isLogged()) {
			properties.remove(username);
			JLG.storeConfig(properties, ds().tokenFilename);
			setup();

			throw new Exception("Problem while log in.");
		}

		if (bRememberMe) {
			properties.setProperty(username, format(tokenKey, tokenSecret));
		}

		JLG.storeConfig(properties, ds().tokenFilename);

		IDataModel dm = ds().getComponent(IDataModel.class);
		ds().setContext(new Context(dm, "/"));
	}

	private String format(String tokenKey, String tokenSecret) {
		return tokenKey + ":" + tokenSecret;
	}

	private void loadTokens() {
		try {
			LOG.info(ds().tokenFilename);
			properties = JLG.loadConfig(ds().tokenFilename);
		} catch (Exception e) {
			LOG.info("Cant load tokens");
			properties = new Properties();
		}

		for (Object key : properties.keySet()) {
			LOG.info(key + "=" + properties.get(key));
		}
	}

	public void reAuth() {
		String value = properties.getProperty(username);
		String[] array = value.split(":");
		tokenKey = array[0];
		tokenSecret = array[1];

		LOG.info("token_key=" + tokenKey);
		LOG.info("token_secret=" + tokenSecret);
		AccessTokenPair reAuthTokens = new AccessTokenPair(tokenKey,
				tokenSecret);

		getSession().setAccessTokenPair(reAuthTokens);
	}

	public void newAuth() throws Exception {
		AccessTokenPair tokenPair = mDBApi.getSession().getAccessTokenPair();
		if (tokenPair == null) {
			throw new Exception("Tokens are null");
		}
		RequestTokenPair tokens = new RequestTokenPair(tokenPair.key,
				tokenPair.secret);
		getSession().retrieveWebAccessToken(tokens);

		tokenKey = getSession().getAccessTokenPair().key;
		tokenSecret = getSession().getAccessTokenPair().secret;

		LOG.info("tokenKey=" + tokenKey);
		LOG.info("tokenSecret=" + tokenSecret);
		username = mDBApi.accountInfo().displayName;
	}

	private WebAuthSession getSession() {
		return mDBApi.getSession();
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
		setup();
	}

	public void setRememberMe(boolean bRememberMe) {
		this.bRememberMe = bRememberMe;
	}

}
