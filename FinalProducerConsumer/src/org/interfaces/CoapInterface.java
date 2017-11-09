package org.interfaces;

public interface CoapInterface {
	public void addEndpoints();// to connect using default ip address

	public void connectCoAP();

	public void coapResponseReceiveMethod();// receive response
}
