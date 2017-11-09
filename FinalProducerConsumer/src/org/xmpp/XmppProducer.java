package org.xmpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.event.listener.XmppCommandLineParser;
import org.interfaces.XmppInterface;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.kohsuke.args4j.CmdLineException;
import org.xsd.ProtocolType;

//Producer class
public class XmppProducer extends XMPPMessageListener implements XmppInterface {

	XMPPConnection connection;
	// default username of Producer
	static String username = "dev";
	// default password of Producer
	static String password = "divyang";
	// default domain name
	public static String domainProducer = "@ahmcpu2129.eic.einfochips.com";
	public Logger logger = Logger.getLogger(this.getClass().getName());
	XMPPMessageListener messageListener = new XMPPMessageListener();

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;

	}

	public XmppProducer() {
	}

	// connect with kafka
	@Override
	public void connect(String serverURI, String brokerList) {

		Properties props = new Properties();
		props.put("metadata.broker.list", brokerList);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		logger.info("Connected to XMPP and Kafka");
	}

	// login method
	@Override
	public void login(String userName, String password) throws XMPPException {

		ConnectionConfiguration config = new ConnectionConfiguration("localhost", 5222);
		connection = new XMPPConnection(config);
		Presence presence = new Presence(Type.available);
		config.setSendPresence(true);
		config.setDebuggerEnabled(true);
		connection.connect();
		connection.login(userName, password);
	}

	// send message by specifying message and buddID
	@Override
	public void sendMessage(String message, String buddyId) throws XMPPException {

		buddyId += domainProducer;
		ChatManager chatmanager = connection.getChatManager();
		Chat chatObject = chatmanager.createChat(buddyId, messageListener);
		chatObject.sendMessage(message);
	}

	// to display buddy list
	@Override
	public void displayBuddyList() {
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		System.out.println("\n\n" + entries.size() + " Buddies:");

		for (RosterEntry r : entries) {
			System.out.println(r.getName());
		}
	}

	// disconnect from server
	@Override
	public void disconnect() {
		connection.disconnect();
	}

	// connection lost method
	@Override
	public void connectionLost(Throwable cause) {
		logger.warn("Lost connection to server", cause);

		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}

	// main method
	public static void main(String args[]) throws XMPPException, IOException, JAXBException, CmdLineException {

		PropertyConfigurator.configure("D:/javaluna/FinalProducerConsumer/bin/log4j.properties");
		Logger.getRootLogger();
		// declare variables
		XmppProducer xmppObject = new XmppProducer();
		BufferedReader brObj = new BufferedReader(new InputStreamReader(System.in));
		String message;
		ProtocolType p = new ProtocolType();

		XmppCommandLineParser parser = null;
		XmppProducer bridge = new XmppProducer();

		parser = new XmppCommandLineParser();
		parser.parse(args);

		bridge.connect(parser.getServerURIXmpp(), parser.getBrokerList());

		// turn on the enhanced debugger

		XMPPConnection.DEBUG_ENABLED = true;

		// Enter your login information here
		xmppObject.login(username, password);

		xmppObject.displayBuddyList();

		System.out.println("-----");
		System.out.println("Type bye to terminate the session");
		System.out.println("Who do you want to talk to?");
		String talkTo = brObj.readLine();

		System.out.println("-----");
		System.out.println("All messages will be sent to " + talkTo);
		System.out.println("Enter your message :");

		while (!(message = brObj.readLine()).equals("bye")) {
			p.setMessage(message);
			p.setTopic("Final");
			JAXBContext jaxbContext = JAXBContext.newInstance(ProtocolType.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// Marshaling
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(p, sw);

			// to show out put in console
			message = sw.toString();

			xmppObject.sendMessage(message, talkTo);
		}

		xmppObject.disconnect();
	}

}
