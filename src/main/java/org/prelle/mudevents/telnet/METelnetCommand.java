package org.prelle.mudevents.telnet;

import org.prelle.mudevents.PipeEvent;
import org.prelle.telnet.event.TelnetCommand;
import org.prelle.telnet.parser.TelnetConstants.ControlCode;

/**
 * 
 */
public class METelnetCommand implements PipeEvent, TelnetCommand {

	private ControlCode code;
	
	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public METelnetCommand(ControlCode code) {
		this.code = code;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.event.TelnetCommand#getCode()
	 */
	@Override
	public ControlCode getCode() {
		return code;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "Telnet:"+code;
	}
	
}
