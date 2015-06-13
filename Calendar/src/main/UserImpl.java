package main;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

public class UserImpl implements User {
	private PeerSemanticTag user;

	public UserImpl(String name, String email) {
		
	}
	
	public UserImpl(PeerSemanticTag user) {
		this.user = user;
	}
	
	@Override
	public String getName() {
		return user.getName();
	}

	@Override
	public String getEmailAddress() {
		//TODO: return complete array?
		return user.getAddresses()[0];
	}

	@Override
	public String getTcpAddress() {
		return user.getAddresses()[0];
	}

	@Override
	public String getNameEmailString() {
		return getName() + " : " + getEmailAddress();
	}

}
