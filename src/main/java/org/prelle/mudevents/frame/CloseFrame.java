package org.prelle.mudevents.frame;

import org.prelle.mudevents.AMUDEvent;

import lombok.Data;

/**
 * 
 */
@Data
public class CloseFrame extends AMUDEvent {

	private String id;
	
	//-------------------------------------------------------------------
	public CloseFrame() {
		super(null);
	}

}
