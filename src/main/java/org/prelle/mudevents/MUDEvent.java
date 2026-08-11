package org.prelle.mudevents;

import java.time.Instant;

/**
 * 
 */
public interface MUDEvent {

	public Object getSource();
	
	public Instant getTimestamp();
	
}
