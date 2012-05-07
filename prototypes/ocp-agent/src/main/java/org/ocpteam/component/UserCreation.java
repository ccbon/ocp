package org.ocpteam.component;

import org.ocpteam.entity.Address;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.ICaptcha;
import org.ocpteam.interfaces.IUser;
import org.ocpteam.interfaces.IUserCreation;

/**
 * Component for user creation.
 *
 */
public class UserCreation extends DSContainer<AddressDataSource> implements IUserCreation {

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
	public void setUser(IUser user) {
		this.user = user;
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
	
}
