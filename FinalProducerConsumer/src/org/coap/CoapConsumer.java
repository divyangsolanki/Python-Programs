package org.coap;

import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.interfaces.CoapInterface;

//Asynchronise response
class AsynchListener implements CoapHandler {

	@Override
	public void onLoad(CoapResponse response) {
		String content = response.getResponseText();
		System.out.println("onLoad: " + content);
	}

	@Override
	public void onError() {
		System.err.println("Error");
	}
}

public class CoapConsumer implements CoapInterface {

	public Logger logger = Logger.getLogger(this.getClass().getName());
	public static String topic = "final";

	CoapClient client;
	String uri;

	// setup client by providing uri
	@Override
	public void connectCoAP() {
		client = new CoapClient("coap://localhost:5684/" + topic); // coap://localhost:port/resource
	}

	/* To receive response from the client */
	@Override
	public void coapResponseReceiveMethod() {
		AsynchListener asynchListener = new AsynchListener();
		client.get(asynchListener);
		CoapResponse response = client.get();
		client.setTimeout(500);
		if (response != null) {

			System.out.println(response.getCode());
			System.out.println(response.getOptions());
			System.out.println(response.getResponseText());

			System.out.println("\nADVANCED\n");

			System.out.println(Utils.prettyPrint(response));// advanced response
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("No response received.");
		}
	}

	// main class
	public static void main(String[] args) throws URISyntaxException {
		CoapConsumer consumerObj = new CoapConsumer();
		consumerObj.connectCoAP();
		consumerObj.coapResponseReceiveMethod();

	}

	@Override
	public void addEndpoints() {
		// TODO Auto-generated method stub

	}

}
