package org.prelle.mudevents.telnet;

import org.prelle.mudevents.AMUDEvent;
import org.prelle.mudevents.MUDEvent;
import org.prelle.telnet.event.DataEvent;

/**
 * 
 */
public class MEDataEvent extends AMUDEvent implements DataEvent, MUDEvent {

	private byte[] data;
	
	//-------------------------------------------------------------------
	public MEDataEvent(MUDEventsTelnetEventFactory factory, byte[] data) {
		super(factory);
		this.data = data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.event.DataEvent#getData()
	 */
	@Override
	public byte[] getData() {
		return data;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "Telnet:DATA";
	}

}
