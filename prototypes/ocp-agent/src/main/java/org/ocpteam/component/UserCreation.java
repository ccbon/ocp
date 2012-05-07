package org.ocpteam.component;

import org.ocpteam.entity.Address;
import org.ocpteam.entity.AddressUser;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.ICaptcha;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IUser;
import org.ocpteam.interfaces.IUserCreation;

/**
 * Component for user creation.
 *
 */
public class UserCreation extends DSContainer<AddressDataSource> implements IUserCreation, IAuthenticable {

	private IUser user;
	private IAddressMap map;
	private String password;

	@Override
	public void init() throws Exception {
		super.init();
		map = ds().getComponent(IAddressMap.class); 
	}
	
	@Override
	public void createUser() throws Exception {
		if (user == null) {
			throw new Exception("user is null");
		}
		Address address = new Address(ds().md.hash((user.getUsername() + getPassword()).getBytes()));
		map.put(address, ds().serializer.serialize(user));
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setCaptcha(ICaptcha captcha) {
	}

	@Override
	public void setUser(String username) throws Exception {
		this.user = new AddressUser(username);
	}

	@Override
	public IUser getUser() {
		return user;
	}

	@Override
	public ICaptcha getCaptcha() {
		return null;
	}

	@Override
	public boolean needsCaptcha() {
		return false;
	}

	@Override
	public String getAnswer() {
		return null;
	}

	@Override
	public void setAnswer(String answer) {
	}

	@Override
	public void login() throws Exception {
		Authentication a = (Authentication) ds().getComponent(UserIdentification.class);
		Address address = new Address(ds().md.hash((a.getUsername() + a.getChallenge()).getBytes()));
		byte[] value = map.get(address);
		if (value == null) {
			throw new Exception("user/password do not exist.");
		}
		this.user = (IUser) ds().serializer.deserialize(value);
		IDataModel dataModel = ds().getComponent(IDataModel.class);
		ds().setContext(new Context(dataModel));
	}

	@Override
	public void logout() throws Exception {
		ds().setContext(null);
	}
	
}
