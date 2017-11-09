package org.event.listener;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.jivesoftware.smack.XMPPException;
import org.kohsuke.args4j.CmdLineException;
import org.mqtt.MqttConsumer;

import kafka.producer.ProducerConfig;

public class EventListener {

	public Logger logger = Logger.getLogger(this.getClass().getName());
	static MqttConnectOptions connOpts = new MqttConnectOptions();

	// Connect method for kafka server
	public void connect(String serverURI, String brokerList) {

		Properties props = new Properties();
		props.put("metadata.broker.list", brokerList);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		ProducerConfig config = new ProducerConfig(props);

		logger.info("Connected to XMPP and Kafka");
	}

	public void connectionLost(Throwable cause) {
		logger.warn("Lost connection to  server", cause);
		while (true) {
			logger.info("Attempting to reconnect to  server");

			logger.info("Reconnected to  server, resuming");
			return;
		}
	}

	public static void main(String[] args)
			throws XMPPException, IOException, CmdLineException, URISyntaxException, JAXBException {

		PropertyConfigurator.configure("D:/javaluna/FinalProducerConsumer/bin/log4j.properties");
		Logger.getRootLogger();
		EventListener bridge = new EventListener();

		MqttCommandLineParser mqttParser = null;
		mqttParser = new MqttCommandLineParser();
		mqttParser.parse(args);

		XmppCommandLineParser xmppParser = null;
		xmppParser = new XmppCommandLineParser();
		xmppParser.parse(args);

		CoapCommandLineParser coapParser = null;
		coapParser = new CoapCommandLineParser();
		coapParser.parse(args);

		bridge.connect(mqttParser.getServerURIMqtt(), mqttParser.getBrokerList());
		MqttConsumer.main(args);

		/*
		 * bridge.connect(xmppParser.getServerURIXmpp(),
		 * xmppParser.getBrokerList()); XmppConsumer.main(args);
		 * 
		 * bridge.connect(coapParser.getServerURICoap(),
		 * coapParser.getBrokerList()); CoapConsumer.main(args);
		 */
	}
}
