package org.prelle.mudevents;

import java.time.Instant;

import lombok.Getter;

/**
 * 
 */
public abstract class AMUDEvent implements MUDEvent {

	@Getter
	private final Object source;

	@Getter
	private final Instant timestamp;

	public AMUDEvent(Object src) {
		this.source = src;
		this.timestamp = Instant.now();
	}

}
