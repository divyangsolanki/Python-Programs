package org.interfaces;

import org.eclipse.paho.client.mqttv3.MqttException;

public interface MqttConsumerInterface {
	public void connect(String serverURI, String brokerList)
			throws MqttException;/*
									 * connect method for connection between
									 * consumer and broker
									 */

	public void reconnect() throws MqttException;// reconnection after
													// connection has been
													// broken

	/* subscribe method for specific topic */
	public void mqttSubscribe() throws MqttException;

	/* if connection lost has been occurred this method will use. */
	public void connectionLost(Throwable cause);
}
