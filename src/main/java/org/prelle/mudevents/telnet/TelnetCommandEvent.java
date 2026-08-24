package org.prelle.mudevents.telnet;

import org.prelle.mudevents.PipeEvent;
import org.prelle.telnet.event.TelnetEvent;

import lombok.Getter;

public class TelnetCommandEvent implements PipeEvent {
	
	@Getter private TelnetEvent wrapped;

	//-------------------------------------------------------------------
	public TelnetCommandEvent( TelnetEvent data) {
		this.wrapped = data;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "Telnet:"+wrapped.toString();
	}
	
}