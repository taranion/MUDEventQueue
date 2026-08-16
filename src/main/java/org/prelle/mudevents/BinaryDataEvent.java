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

	//-------------------------------------------------------------------
	public BinaryDataEvent(Object src, int data) {
		super(src);
		this.data = new byte[] { (byte)data };
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.mudevents.MUDEvent#asRawData()
	 */
	@Override
	public byte[] asRawData() {
		return data;
	}

}
