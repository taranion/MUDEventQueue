package org.prelle.mudevents.telnet;

import org.prelle.mudevents.PipeEvent;
import org.prelle.telnet.event.DataEvent;

/**
 * 
 */
public class MEDataEvent implements DataEvent, PipeEvent {

	private byte[] data;
	
	//-------------------------------------------------------------------
	public MEDataEvent(MUDEventsTelnetEventFactory factory, byte[] data) {
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
