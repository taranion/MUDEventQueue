package org.prelle.mudevents.telnet;

import org.prelle.mudevents.PipeEvent;
import org.prelle.telnet.WellKnownTelnetOptions;
import org.prelle.telnet.event.TelnetNegotiationEvent;
import org.prelle.telnet.parser.TelnetConstants.ControlCode;

/**
 * 
 */
public class METelnetNegotiationEvent implements PipeEvent, TelnetNegotiationEvent {
	
	private int option;
	private ControlCode type;

	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public METelnetNegotiationEvent(int option, ControlCode type) {
		this.option = option;
		this.type = type;
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
	/**
	 * @see org.prelle.telnet.event.TelnetNegotiationEvent#getType()
	 */
	@Override
	public ControlCode getType() {
		return type;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "Telnet:"+type+" "+option+" ("+WellKnownTelnetOptions.valueOf(option)+")";
	}
	
}
