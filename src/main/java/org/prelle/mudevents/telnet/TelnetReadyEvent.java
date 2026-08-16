package org.prelle.mudevents.telnet;

import org.prelle.mudevents.AMUDEvent;

/**
 * All protocols we support have been either rejected or confirmed.
 */
public class TelnetReadyEvent extends AMUDEvent {

	//-------------------------------------------------------------------
	public TelnetReadyEvent(Object src) {
		super(src);
	}

}
