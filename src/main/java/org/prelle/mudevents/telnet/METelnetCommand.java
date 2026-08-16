package org.prelle.mudevents.telnet;

import org.prelle.mudevents.AMUDEvent;
import org.prelle.telnet.event.TelnetCommand;
import org.prelle.telnet.parser.TelnetConstants.ControlCode;

/**
 * 
 */
public class METelnetCommand extends AMUDEvent implements TelnetCommand {

	private ControlCode code;
	
	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public METelnetCommand(Object src, ControlCode code) {
		super(src);
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
