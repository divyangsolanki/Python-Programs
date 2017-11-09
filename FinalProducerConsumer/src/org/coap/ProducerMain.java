package org.coap;

import java.net.SocketException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.event.listener.CoapCommandLineParser;
import org.interfaces.commonInterface;
import org.kohsuke.args4j.CmdLineException;

public class ProducerMain extends CoapProducer implements commonInterface {
	public Logger logger = Logger.getLogger(this.getClass().getName());

	public ProducerMain() throws SocketException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void connect(String serverURI, String brokerList) {

		Properties props = new Properties();
		props.put("metadata.broker.list", brokerList);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		logger.info("Connected to CoAP and Kafka");
	}

	public static void main(String[] args) throws SocketException {
		// TODO Auto-generated method stub
		CoapCommandLineParser parser = null;

		try {
			PropertyConfigurator.configure("D:/javaluna/FinalProducerConsumer/bin/log4j.properties");
			Logger.getRootLogger();
			parser = new CoapCommandLineParser();
			parser.parse(args);
			ProducerMain bridge = new ProducerMain();
			bridge.connect(parser.getServerURICoap(), parser.getBrokerList());
			CoapProducer coapProducerObj = new CoapProducer();
			coapProducerObj.connect();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}
}
