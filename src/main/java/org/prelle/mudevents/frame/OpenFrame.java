package org.prelle.mudevents.frame;

import org.prelle.mudevents.AMUDEvent;

import lombok.Data;

/**
 * 
 */
@Data
public class OpenFrame extends AMUDEvent {

	private String parent;
	private String id;
	private String label;
	private int size;
	private SizeUnit unit;
	/** Where to split of the frame. */
	private Position position;
	private FrameType type;
	private FrameContent content;
	/** For webview frames */
	private String url;
	
	//-------------------------------------------------------------------
	/**
	 * @param src
	 */
	public OpenFrame() {
		super(null);
	}

}
