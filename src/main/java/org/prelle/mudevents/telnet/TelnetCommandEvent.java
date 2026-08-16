package org.prelle.mudevents.telnet;

import org.prelle.mudevents.AMUDEvent;
import org.prelle.telnet.event.TelnetEvent;

import lombok.Getter;

public class TelnetCommandEvent extends AMUDEvent {
	
	@Getter private TelnetEvent wrapped;

	//-------------------------------------------------------------------
	public TelnetCommandEvent(Object src, TelnetEvent data) {
		super(src);
		this.wrapped = data;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "Telnet:"+wrapped.toString();
	}
	
}