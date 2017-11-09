package org.interfaces;

import org.jivesoftware.smack.XMPPException;

public interface XmppInterface {
	/* connect method between producer and consumer using specific url */
	public void connect(String serverURI, String brokerList);

	/* login method by giving username and password */
	public void login(String userName, String password) throws XMPPException;

	/* send message by specifying message and buddyID */
	public void sendMessage(String message, String buddyId) throws XMPPException;

	/* list of all buddies */
	public void displayBuddyList();

	/* disconnect method */
	public void disconnect();

	/* if connection has been broken this method will use */
	public void connectionLost(Throwable cause);

}
