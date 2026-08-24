package org.prelle.mudevents.telnet;

import org.prelle.mudevents.BinaryDataEvent;
import org.prelle.mudevents.MUDEventPipeline;
import org.prelle.telnet.event.TelnetEvent;
import org.prelle.telnet.parser.TelnetEncoder;
import org.prelle.telnet.protocol.TelnetReturnChannel;

/**
 * 
 */
public class MUDEventPipeTelnetSink implements TelnetReturnChannel {
	
	private MUDClientTelnet parent;
	private MUDEventPipeline pipe;

	//-------------------------------------------------------------------
	/**
	 */
	public MUDEventPipeTelnetSink(MUDClientTelnet parent, MUDEventPipeline pipe) {
		this.parent = parent;
		this.pipe = pipe;
	}

	//-------------------------------------------------------------------
	@Override
	public void sendToRemote(TelnetEvent event) {
		// Encode the event into an byte array
		var buffer = TelnetEncoder.encodeEvent(event);
		pipe.publishAt(parent, new BinaryDataEvent(buffer), false);
	}

}