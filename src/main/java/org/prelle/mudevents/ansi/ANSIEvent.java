package org.prelle.mudevents.ansi;

import org.prelle.ansi.AParsedElement;
import org.prelle.mudevents.PipeEvent;

/**
 * 
 */
public class ANSIEvent implements PipeEvent {
	
	private AParsedElement fragment;

	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public ANSIEvent(AParsedElement fragment) {
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
	 * @see org.prelle.mudevents.PipeEvent#asRawData()
	 */
	@Override
	public byte[] asRawData() { 
		return fragment.getRaw(); 
	}
}
