package org.prelle.mudevents;

import lombok.Getter;

/**
 * 
 */
public class BinaryDataEvent extends AMUDEvent implements MUDEvent {
	
	@Getter private final byte[] data;

	//-------------------------------------------------------------------
	public BinaryDataEvent(Object src, byte[] data) {
		super(src);
		this.data = data;
	}

}
