package org.prelle.mudevents;

/**
 * 
 */
public interface MUDEvent {
	
	public default byte[] asRawData() { return new byte[0]; }
}
