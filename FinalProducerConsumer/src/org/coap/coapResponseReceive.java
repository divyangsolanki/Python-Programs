package org.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

//Definition of the Resource
class coapResponseReceive extends CoapResource 
{
 
 public coapResponseReceive()
 {
		super("final");
		 getAttributes().setTitle("Resource");
		// TODO Auto-generated constructor stub
	}

 public void handleGET(CoapExchange exchange)
 {
     // respond to the request
     exchange.respond("divyang");//message
     
 }
}