package org.coap;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.event.listener.CoapCommandLineParser;
import org.interfaces.CoapInterface;
import org.interfaces.commonInterface;
import org.kohsuke.args4j.CmdLineException;

public class CoapProducer extends CoapServer implements commonInterface, CoapInterface {
	public static int COAP_PORT = 5684;// NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);;
										// //coap port number
	private Logger logger = Logger.getLogger(this.getClass().getName());

	// create server
	@Override
	public void connectCoAP() {
		try {
			CoapProducer server = new CoapProducer();
			server.addEndpoints();// add endpoints on all IP addresses
			server.start();
		} catch (SocketException e) {
			coapOnError();
			System.err.println("Failed to initialize server: " + e.getMessage());
		}
	}

	@Override
	public void addEndpoints() {
		for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			// only binds to IPv4 addresses and localhost
			if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}

	}

	// Constructor for a new server.
	public CoapProducer() throws SocketException {
		// provide an instance of a Hello-World resource
		add(new coapResponseReceive());

	}

	public void coapOnError() {
		// TODO Auto-generated constructor stub
		RuntimeException e = new RuntimeException("failed to intialize");
		throw e;
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
			coapProducerObj.connectCoAP();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}

	@Override
	public void coapResponseReceiveMethod() {
		// TODO Auto-generated method stub

	}
}
