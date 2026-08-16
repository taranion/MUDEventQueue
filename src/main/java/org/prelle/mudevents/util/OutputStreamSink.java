package org.prelle.mudevents.util;

import java.io.OutputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import org.prelle.mudevents.BinaryDataEvent;
import org.prelle.mudevents.MUDEvent;
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
	 * @see org.prelle.mudevents.MUDEventProcessor#apply(org.prelle.mudevents.MUDEvent)
	 */
	@Override
	public List<MUDEvent> apply(MUDEvent event) {
		// Convert event into a byte buffer and send it
		if (event instanceof BinaryDataEvent bde) {
			try {
				out.write(bde.getData());
				out.flush();
			} catch (Exception e) {
				logger.log(Level.INFO, "Error writing to OutputStream: "+e);
				if (reversePipe != null) {
					reversePipe.publish(new PipeClosed(this, "Error writing to OutputStream: "+e));
				}
			}
		} else {
			logger.log(Logger.Level.WARNING, "Received unsupported event type: "+event.getClass().getName());
		}
		
		return List.of();
	}

}
