package org.prelle.mudevents.util;

import java.io.OutputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import org.prelle.mudevents.BinaryDataEvent;
import org.prelle.mudevents.CloseClientEvent;
import org.prelle.mudevents.PipeEvent;
import org.prelle.mudevents.MUDEventPipeline;
import org.prelle.mudevents.MUDEventProcessor;
import org.prelle.mudevents.PipeClosed;

/**
 * 
 */
public class OutputStreamSink implements MUDEventProcessor {

	private final static Logger logger = System.getLogger("mud.events");
	
	private OutputStream out;
	private MUDEventPipeline reversePipe;

	//-------------------------------------------------------------------
	public OutputStreamSink(OutputStream out) {
		this.out = out;
	}

	//-------------------------------------------------------------------
	public OutputStreamSink(OutputStream out, MUDEventPipeline reverse) {
		this.out = out;
		this.reversePipe = reverse;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.mudevents.MUDEventProcessor#onSendToRemote(org.prelle.mudevents.PipeEvent)
	 */
	@Override
	public List<PipeEvent> onSendToRemote(PipeEvent event) {
		// Convert event into a byte buffer and send it
		if (event instanceof BinaryDataEvent bde) {
			try {
				out.write(bde.getData());
				out.flush();
			} catch (Exception e) {
				logger.log(Level.INFO, "Error writing to OutputStream: "+e);
				if (reversePipe != null) {
					reversePipe.publish(new PipeClosed("Error writing to OutputStream: "+e));
				}
			}
		} else if (event instanceof CloseClientEvent) {
			try {
				out.close();
			} catch (Exception e) {
				logger.log(Level.INFO, "Error closing OutputStream: "+e);
				if (reversePipe != null) {
					reversePipe.publish(new PipeClosed("Error closing OutputStream: "+e));
				}
			}
		} else {
			logger.log(Logger.Level.WARNING, "Received unsupported event type: "+event.getClass().getName());
		}
		
		return List.of();
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.mudevents.MUDEventProcessor#onReceiveFromRemote(org.prelle.mudevents.PipeEvent)
	 */
	@Override
	public List<PipeEvent> onReceiveFromRemote(PipeEvent event) {
		return List.of(event);
	}

}
