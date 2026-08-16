package org.prelle.mudevents.ansi;

import org.prelle.ansi.AParsedElement;
import org.prelle.mudevents.AMUDEvent;

/**
 * 
 */
public class ANSIEvent extends AMUDEvent {
	
	private AParsedElement fragment;

	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public ANSIEvent(Object src, AParsedElement fragment) {
		super(src);
		this.fragment = fragment;
	}

	//-------------------------------------------------------------------
	public AParsedElement getFragment() {
		return fragment;
	}

	//-------------------------------------------------------------------
	public String toString() {
		return "ANSIEvent("+String.valueOf(fragment)+")";
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.mudevents.MUDEvent#asRawData()
	 */
	@Override
	public byte[] asRawData() { 
		return fragment.getRaw(); 
	}
}
