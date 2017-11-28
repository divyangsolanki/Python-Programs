package com.m2mci.mqttKafkaBridge;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.kohsuke.args4j.CmdLineException;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

public class Bridge implements MqttCallback {

	private static String clientId = MqttClient.generateClientId();
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private MqttAsyncClient mqtt;
	private Producer<String, String> kafkaProducer;
	public static String topic="final";
	static String send_message = "raja";
	 MqttConnectOptions connOpts = new MqttConnectOptions();	
		
		
	private void connect(String serverURI, String brokerList) throws MqttException {
		
		mqtt = new MqttAsyncClient(serverURI, clientId);
		mqtt.setCallback(this);
		
		IMqttToken token = mqtt.connect();
		Properties props = new Properties();
        props.put("metadata.broker.list", brokerList);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		ProducerConfig config = new ProducerConfig(props);
		//kafkaProducer = new Producer<String, String>(config);
		token.waitForCompletion();
		logger.info("Connected to MQTT and Kafka");
	}

	private void reconnect() throws MqttException {
		IMqttToken token = mqtt.connect();
		token.waitForCompletion();
	}
	
	public void subscribe() throws MqttException {
		
		mqtt.subscribe(topic, 2);
	}
	public void mqttPublish(String send_message) 
    {
    	
        
        // Create a request message and set the request payload
        MqttMessage message = new MqttMessage(send_message.getBytes());
            //MqttMessage message = new MqttMessage(send_message.getBytes());

            /*Quality of service level: 
            0 - at most once delivery of message
            1 - at least one delivery of message
            2 - exactly one delivery of message
            */
            message.setQos(2);
            message.setRetained(false);//A retained message is a last known good value and persists at the MQTT broker for the specified topic.
           
            try {
                mqtt.publish(topic, message);
                
            } catch(MqttException me) {
                System.out.println("Error: "+me);
                System.out.println("Reason " + me.getReasonCode());
                System.out.println("Message " + me.getMessage());
                
            }   
           
        }
/*
	private void subscribe() throws MqttException {
		
		mqtt.subscribe(topic, 2);
	}
*/
	
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

	
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}

	
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		byte[] payload = message.getPayload();
		System.out.println(new String(message.getPayload()));
              //  kafkaProducer.send(new KeyedMessage<String, String>(topic, new String(payload)));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLineParser parser = null;
	
		try {
			parser = new CommandLineParser();
			parser.parse(args);
			Bridge bridge = new Bridge();
			bridge.connect(parser.getServerURI(), parser.getBrokerList());
			bridge.mqttPublish(send_message);
		bridge.subscribe();
			
		} catch (MqttException e) {
			e.printStackTrace(System.err);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
		}
	}
}
