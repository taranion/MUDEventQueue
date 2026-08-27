package org.prelle.mudevents.telnet;

import org.prelle.telnet.event.DataEvent;
import org.prelle.telnet.event.TelnetCommand;
import org.prelle.telnet.event.TelnetEventFactory;
import org.prelle.telnet.event.TelnetNegotiationEvent;
import org.prelle.telnet.event.TelnetSubnegotiationEvent;
import org.prelle.telnet.option.TelnetOption;
import org.prelle.telnet.parser.TelnetConstants.ControlCode;
import org.prelle.telnet.protocol.OptionStateEvent;
import org.prelle.telnet.protocol.OptionSupportEvent;

/**
 * 
 */
public class MUDEventsTelnetEventFactory implements TelnetEventFactory {
	
	public final static TelnetEventFactory INSTANCE = new MUDEventsTelnetEventFactory();
	

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.event.TelnetEventFactory#createDataEvent(byte[])
	 */
	@Override
	public DataEvent createDataEvent(byte[] data) {
		return new MEDataEvent(this, data);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.event.TelnetEventFactory#createTelnetCommand(org.prelle.telnet.parser.TelnetConstants.ControlCode)
	 */
	@Override
	public TelnetCommand createTelnetCommand(ControlCode code) {
		return new METelnetCommand(code);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.event.TelnetEventFactory#createTelnetNegotiationEvent(org.prelle.telnet.parser.TelnetConstants.ControlCode, int)
	 */
	@Override
	public TelnetNegotiationEvent createTelnetNegotiationEvent(ControlCode code, int option) {
		return new METelnetNegotiationEvent(option, code);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.event.TelnetEventFactory#createTelnetNegotiationEvent(org.prelle.telnet.event.TelnetNegotiationEvent, org.prelle.telnet.parser.TelnetConstants.ControlCode)
	 */
	@Override
	public TelnetNegotiationEvent createTelnetNegotiationEvent(TelnetNegotiationEvent request, ControlCode answer) {
		return new METelnetNegotiationEvent(request.getOption(), answer);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.event.TelnetEventFactory#createTelnetSubnegotiationEvent(int, byte[])
	 */
	@Override
	public TelnetSubnegotiationEvent createTelnetSubnegotiationEvent(int option, byte[] data) {
		return new METelnetSubnegotiationEvent(option, data);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.event.TelnetEventFactory#createOptionStateEvent(org.prelle.telnet.option.TelnetOption, boolean)
	 */
	@Override
	public OptionStateEvent createOptionStateEvent(TelnetOption option, boolean enabled) {
		return new MEOptionState(option, enabled);
	}

	@Override
	public OptionSupportEvent createOptionSupportEvent(TelnetOption option, boolean supported) {
		return new MEOptionSupport(option, supported);
	}

}
