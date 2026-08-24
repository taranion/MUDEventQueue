package org.prelle.mudevents.game;

import org.prelle.mudevents.PipeEvent;

import lombok.Getter;

/**
 * 
 */
public class UserTypedEvent implements PipeEvent {

	@Getter
	private String text;
	
	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public UserTypedEvent() {
	}

	//-------------------------------------------------------------------
	/**
	 * @return
	 */
	public String toString() {
		return "TYPED:"+text;
	}
	
}
