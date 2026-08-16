package org.prelle.mudevents.telnet;

import org.prelle.mudevents.AMUDEvent;
import org.prelle.telnet.option.TelnetOption;
import org.prelle.telnet.protocol.OptionStateEvent;

/**
 * 
 */
public class MEOptionState extends AMUDEvent implements OptionStateEvent {
	
	private TelnetOption option;
	private boolean active;

	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public MEOptionState(Object src, TelnetOption option, boolean active) {
		super(src);
		this.option = option;
		this.active = active;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return String.format("MEOptionState: %s is %s", option, active?"active":"inactive");
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
	 * @see org.prelle.telnet.protocol.OptionStateEvent#isActive()
	 */
	@Override
	public boolean isActive() {
		return active;
	}

}
