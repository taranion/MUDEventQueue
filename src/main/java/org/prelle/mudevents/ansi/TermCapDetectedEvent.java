package org.prelle.mudevents.ansi;

import org.prelle.ansi.TerminalCapabilities;
import org.prelle.mudevents.PipeEvent;

import lombok.Getter;

/**
 * 
 */
public class TermCapDetectedEvent implements PipeEvent {
	
	@Getter
	private TerminalCapabilities capabilities;

	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public TermCapDetectedEvent(TerminalCapabilities capabilities) {
		this.capabilities = capabilities;
	}

}
