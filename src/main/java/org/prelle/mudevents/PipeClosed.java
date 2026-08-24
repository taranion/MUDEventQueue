package org.prelle.mudevents;

import lombok.Getter;

/**
 * 
 */
public class PipeClosed implements PipeEvent {
	
	@Getter
	private String reason;

	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public PipeClosed(String reason) {
		this.reason = reason;
	}

}
