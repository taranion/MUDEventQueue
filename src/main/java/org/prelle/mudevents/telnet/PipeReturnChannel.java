package org.prelle.mudevents.telnet;

import java.io.IOException;

import org.prelle.mudevents.BinaryDataEvent;
import org.prelle.mudevents.MUDEventPipeline;
import org.prelle.telnet.event.TelnetEvent;
import org.prelle.telnet.parser.TelnetEncoder;
import org.prelle.telnet.protocol.TelnetReturnChannel;

/**
 * 
 */
public class PipeReturnChannel implements TelnetReturnChannel {
	
	private MUDEventPipeline pipeline;

	//-------------------------------------------------------------------
	/**
	 */
	public PipeReturnChannel(MUDEventPipeline pipeline) {
		this.pipeline = pipeline;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.protocol.TelnetReturnChannel#sendToRemote(org.prelle.telnet.event.TelnetEvent)
	 */
	@Override
	public void sendToRemote(TelnetEvent event) throws IOException {
		BinaryDataEvent converted = new BinaryDataEvent(null, TelnetEncoder.encodeEvent(event));
		pipeline.publish(converted);
	}

}
