package org.xmpp;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.xml.sax.SAXException;

//Message Listener class for both Producer and consumer
public class XMPPMessageListener implements MessageListener {

	String packetID;
	String to;
	String stringToXml;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public String messageFrom;
	public String messageBody;

	// Message display if it is validated by XSD
	@Override
	public void processMessage(Chat chat, Message message) {
		this.messageFrom = message.getFrom();
		this.messageBody = message.getBody();
		packetID = message.getPacketID();
		to = message.getTo();
		SchemaFactory factory1 = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// XSD file
		File schemaFile = new File("D:/javaluna/FinalProducerConsumer/src/org/xsd/schemaEinfochips.xsd");
		Schema schema = null;

		try {
			schema = factory1.newSchema(schemaFile);
			Validator validator = schema.newValidator();

			// create a source from a string '
			Source source = new StreamSource(new StringReader(messageBody));
			validator.validate(source);
			System.out.println("Successful");
			validator.reset();

		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			logger.warn("Exception");
			System.exit(0);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Received message: " + messageBody + "  from " + messageFrom);
	}

}
