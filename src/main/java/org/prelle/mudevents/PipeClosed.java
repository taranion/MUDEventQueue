package org.prelle.mudevents;

import lombok.Getter;

/**
 * 
 */
public class PipeClosed extends AMUDEvent {
	
	@Getter
	private String reason;

	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public PipeClosed(Object src, String reason) {
		super(src);
		this.reason = reason;
	}

}
