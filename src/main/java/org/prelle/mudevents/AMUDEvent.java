package org.prelle.mudevents;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 */
public abstract class AMUDEvent implements MUDEvent {

	@Getter @Setter
	protected Object source;

	@Getter
	private final Instant timestamp;

	public AMUDEvent(Object src) {
		this.source = src;
		this.timestamp = Instant.now();
	}

}
