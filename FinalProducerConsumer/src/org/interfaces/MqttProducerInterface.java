package org.interfaces;

import org.eclipse.paho.client.mqttv3.MqttException;

public interface MqttProducerInterface {

	public void connect(String serverURI, String brokerList)
			throws MqttException;/*
									 * connect method for connection between
									 * producer and broker
									 */

	public void reconnect() throws MqttException;// reconnection after
	// connection has been
	// broken

	/* publish method for specific topic */
	public void mqttPublish(String send_message);

	/* if connection lost has been occurred this method will use. */
	public void connectionLost(Throwable cause);

}
