package org.mqtt;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.event.listener.MqttCommandLineParser;
import org.interfaces.MqttConsumerInterface;
import org.kohsuke.args4j.CmdLineException;
import org.xml.sax.SAXException;

/*Consumer main Class*/
public class MqttConsumer implements MqttCallback, MqttConsumerInterface {

	// auto generated clientId
	public static String clientId = MqttClient.generateClientId();
	private Logger logger = Logger.getLogger(this.getClass().getName());
	// Asynchronous MqttClient
	private static MqttAsyncClient mqtt;
	// default topic name
	public static String topic = "final";
	// Mqtt Connection options
	MqttConnectOptions connOpts = new MqttConnectOptions();

	// connect method : Specify URI and default kafka URI
	@Override
	public void connect(String serverURI, String brokerList) throws MqttException {

		mqtt = new MqttAsyncClient(serverURI, clientId);
		// calling callback method
		mqtt.setCallback(this);
		IMqttToken token = mqtt.connect();
		connOpts.setCleanSession(true);

		token.waitForCompletion();
		logger.info("Connected to MQTT and Kafka");
	}

	// reconnect method if connection has been broken
	@Override
	public void reconnect() throws MqttException {
		IMqttToken token = mqtt.connect();
		token.waitForCompletion();

	}

	// subscribe method : Parameters are topic name and QoS
	@Override
	public void mqttSubscribe() throws MqttException {

		mqtt.subscribe(topic, 2);
	}

	@Override
	public void connectionLost(Throwable cause) {
		logger.warn("Lost connection to MQTT server", cause);
		while (true) {
			try {
				logger.info("Attempting to reconnect to MQTT server");
				reconnect();
				logger.info("Reconnected to MQTT server, resuming");
				return;
			} catch (MqttException e) {
				logger.warn("Reconnect failed, retrying in 10 seconds", e);
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub

	}

	// Message Display method and along with XSD validation
	@Override
	public void messageArrived(String topic, MqttMessage message) throws SAXException, MqttException, Exception {

		String msg = new String(message.getPayload());

		SchemaFactory factory1 = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// XSD file
		File schemaFile = new File("D:/javaluna/FinalProducerConsumer/src/org/xsd/schemaEinfochips.xsd");
		Schema schema = factory1.newSchema(schemaFile);
		Validator validator = schema.newValidator();

		// create a source from a string '
		Source source = new StreamSource(new StringReader(msg));
		// validation
		try {
			validator.validate(source);

			validator.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Message:" + new String(message.getPayload()));

		//
		String Msg = null;
		String Id = null;
		Pattern r = Pattern.compile("<message>([^<>]+)</message>");
		Matcher m = r.matcher(msg);
		while (m.find()) {
			if (m.group(1).length() != 0) {
				Msg = m.group(1);

			}
		}
		Pattern r1 = Pattern.compile("<clientid>([^<>]+)</clientid>");
		Matcher m1 = r1.matcher(msg);
		while (m1.find()) {
			if (m1.group(1).length() != 0) {
				Id = m1.group(1);

			}
		}
		// database connection
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/eInfochips", "root", "divyang");

			// here eInfochips is database name, root is username and password
			/*
			 * Statement stmt = con.createStatement(); stmt.
			 * execute("insert into protocol_fields (message_id,message,client_id) values(Id,Msg,'')"
			 * ); // ResultSet rs =
			 * stmt.executeQuery("select * from mqtt_fields");
			 */
			// the mysql insert statement
			String query = " insert into protocol_fields (message_id, message, client_id)" + " values (?, ?, ?)";

			// create the mysql insert preparedstatement
			java.sql.PreparedStatement preparedStmt = con.prepareStatement(query);
			preparedStmt.setString(1, Id);
			preparedStmt.setString(2, Msg);
			preparedStmt.setString(3, "");

			// execute the preparedstatement
			preparedStmt.execute();

			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// main method
	public static void main(String[] args) {
		MqttCommandLineParser parser = null;
		try {
			parser = new MqttCommandLineParser();

			parser.parse(args);

			MqttConsumer bridge = new MqttConsumer();

			bridge.connect(parser.getServerURIMqtt(), parser.getBrokerList());
			bridge.mqttSubscribe();

		} catch (CmdLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace(System.err);
		}
	}

}
