package org.prelle.mudevents;

import lombok.Getter;

/**
 * 
 */
public class BinaryDataEvent implements PipeEvent {
	
	@Getter private final byte[] data;

	//-------------------------------------------------------------------
	public BinaryDataEvent(byte[] data) {
		this.data = data;
	}

	//-------------------------------------------------------------------
	public BinaryDataEvent(int data) {
		this.data = new byte[] { (byte)data };
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.mudevents.PipeEvent#asRawData()
	 */
	@Override
	public byte[] asRawData() {
		return data;
	}

}
