package org.prelle.mudevents;

/**
 * 
 */
public interface PipeEvent {
	
	public default byte[] asRawData() { return new byte[0]; }
}
