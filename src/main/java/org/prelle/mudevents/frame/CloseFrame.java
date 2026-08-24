package org.prelle.mudevents.frame;

import org.prelle.mudevents.PipeEvent;

import lombok.Data;

/**
 * 
 */
@Data
public class CloseFrame implements PipeEvent {

	private String id;
	
	//-------------------------------------------------------------------
	public CloseFrame() {
	
	}

}
