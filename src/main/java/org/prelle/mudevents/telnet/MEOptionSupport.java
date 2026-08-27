package org.prelle.mudevents.telnet;

import org.prelle.mudevents.PipeEvent;
import org.prelle.telnet.option.TelnetOption;
import org.prelle.telnet.protocol.OptionSupportEvent;

/**
 * 
 */
public class MEOptionSupport implements PipeEvent, OptionSupportEvent {
	
	private TelnetOption option;
	private boolean support;

	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public MEOptionSupport(TelnetOption option, boolean value) {
		this.option = option;
		this.support = value;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return String.format("MEOptionState: %s is %s", option, support?"supported":"not supported");
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.protocol.TelnetOptionEvent#getOption()
	 */
	@Override
	public TelnetOption getOption() {
		return option;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.protocol.TelnetOptionEvent#setOption(org.prelle.telnet.option.TelnetOption)
	 */
	@Override
	public void setOption(TelnetOption option) {
		this.option = option;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.protocol.OptionSupportEvent#isSupported()
	 */
	@Override
	public boolean isSupported() {
		return support;
	}

}
