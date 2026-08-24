package org.prelle.mudevents.telnet;

import org.prelle.mudevents.PipeEvent;
import org.prelle.telnet.WellKnownTelnetOptions;
import org.prelle.telnet.event.TelnetSubnegotiationEvent;

/**
 * 
 */
public class METelnetSubnegotiationEvent implements PipeEvent, TelnetSubnegotiationEvent {
	
	private int option;
	private byte[] data;

	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public METelnetSubnegotiationEvent(int option, byte[] data) {
		this.option = option;
		this.data = data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.event.TelnetNegotiationEvent#getOption()
	 */
	@Override
	public int getOption() {
		return option;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "Telnet:SB "+option+" ("+WellKnownTelnetOptions.valueOf(option)+") SE";
	}

	@Override
	public byte[] getData() {
		return data;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.event.TelnetSubnegotiationEvent#getAsIntArray()
	 */
	@Override
	public int[] getAsIntArray() {
		int[] result = new int[data.length];
		for (int i=0; i<data.length; i++) {
			result[i] = data[i] & 0xFF;
		}
		return result;
	}
	
}
