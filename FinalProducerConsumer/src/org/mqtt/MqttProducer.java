package org.mqtt;

import java.io.StringWriter;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.event.listener.MqttCommandLineParser;
import org.interfaces.MqttProducerInterface;
import org.kohsuke.args4j.CmdLineException;
import org.xsd.ProtocolType;

//Create java class named “Producer�?
public class MqttProducer implements MqttProducerInterface {

	public static final int MqttVersion = 0;
	// auto generated clientId
	public static String clientId = MqttClient.generateClientId();
	public Logger logger = Logger.getLogger(this.getClass().getName());
	// Asynchronous MqttClient
	public static MqttAsyncClient mqtt;
	// default topic name
	public static String topic = "final";
	// Mqtt Connection options
	static MqttConnectOptions connOpts = new MqttConnectOptions();
	static ProtocolType emp = new ProtocolType();

	// connect method : Specify URI and default kafka URI
	@Override
	public void connect(String serverURI, String brokerList) throws MqttException {

		mqtt = new MqttAsyncClient(serverURI, clientId);

		IMqttToken token = mqtt.connect();
		connOpts.setCleanSession(true);

		Properties props = new Properties();
		props.put("metadata.broker.list", brokerList);
		props.put("serializer.class", "kafka.serializer.StringEncoder");

		token.waitForCompletion();
		logger.info("Connected to MQTT and Kafka");
	}

	// reconnect method if connection has been broken
	@Override
	public void reconnect() throws MqttException {
		IMqttToken token = mqtt.connect();
		token.waitForCompletion();
	}

	// Publish method
	@Override
	public void mqttPublish(String send_message) {

		// Create a request message and set the request payload
		Scanner src = new Scanner(System.in);
		System.out.println("Enter message to send:");
		send_message = src.nextLine();
		JAXBContext jaxbContext;
		// set Message and topic to match with XSD
		try {
			emp.setMessage(send_message);
			emp.setTopic(topic);
			jaxbContext = JAXBContext.newInstance(ProtocolType.class);

			Marshaller jaxbMarshaller;

			jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter sw = new StringWriter();

			jaxbMarshaller.marshal(emp, sw);

			// to show out put in console
			send_message = sw.toString();
			// Marshaling

			MqttMessage message = new MqttMessage(send_message.getBytes());

			/*
			 * Quality of service level: 0 - at most once delivery of message 1
			 * - at least one delivery of message 2 - exactly one delivery of
			 * message
			 */
			message.setQos(2);
			/*
			 * A retained message is a last known good value and persists at the
			 * MQTT broker for the specified topic.
			 */
			message.setRetained(false);

			mqtt.publish(topic, message);
			// mqtt.disconnect();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (

		MqttException me) {
			System.out.println("Error: " + me);
			System.out.println("Reason " + me.getReasonCode());
			System.out.println("Message " + me.getMessage());
		}

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

	// mian method
	public static void main(String args[]) {
		MqttCommandLineParser parser = null;
		PropertyConfigurator.configure("D:/javaluna/FinalProducerConsumer/bin/log4j.properties");
		Logger.getRootLogger();
		MqttProducer bridge = new MqttProducer();
		connOpts.setMqttVersion(MqttVersion);
		String send_message = null;
		try {
			parser = new MqttCommandLineParser();
			parser.parse(args);

			bridge.connect(parser.getServerURIMqtt(), parser.getBrokerList());
			bridge.mqttPublish(send_message);
			mqtt.disconnect();
		} catch (MqttException e) {
			e.printStackTrace(System.err);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
		}
	}

}
