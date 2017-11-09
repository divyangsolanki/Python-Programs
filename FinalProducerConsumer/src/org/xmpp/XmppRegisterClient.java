package org.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

//class to register user on server
public class XmppRegisterClient {
	static ConnectionConfiguration connConfig = new ConnectionConfiguration("localhost", 5222);
	static XMPPConnection connection = new XMPPConnection(connConfig);

	// roster entry method
	public static void createEntry(String user, String name, String[] groups) throws Exception {
		System.out.println(String.format("Creating entry for buddy '%1$s' with name %2$s", user, name));
		Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
		Roster roster = connection.getRoster();
		Presence subscribe = new Presence(Presence.Type.subscribe);
		subscribe.setMode(Presence.Mode.available);
		connection.sendPacket(subscribe);
		roster.createEntry(user, name, groups);
	}

	// main method
	public static void main(String[] args) throws Exception {
		connection.connect();
		connection.login("raja", "divyang");
		AccountManager accountManager = connection.getAccountManager();

		// Specify attributes for user
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("username", "dev");
		attributes.put("password", "divyang");
		attributes.put("email", "dev@gmail.com");
		attributes.put("name", "dev solanki");
		attributes.put("groups", "einfochips");

		// Presence information of a user
		Presence presence = new Presence(Type.available);

		// give username and password for account creation
		accountManager.createAccount("dev", "divyang", attributes);

		String buddyJID = "der";
		String buddyName = "der";
		String[] groups = { "einfochips" };
		XmppRegisterClient.createEntry(buddyJID, buddyName, groups);

		connection.disconnect();

	}

}
