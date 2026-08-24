package org.prelle.mudevents.util;

import java.io.IOException;
import java.io.InputStream;

import org.prelle.mudevents.BinaryDataEvent;
import org.prelle.mudevents.MUDEventPipeline;
import org.prelle.mudevents.PipeClosed;

/**
 * 
 */
public class InputStreamPublisher {
	
	private InputStream in;
	private MUDEventPipeline pipeline;

	//-------------------------------------------------------------------
	public InputStreamPublisher(InputStream in, MUDEventPipeline pipeline) {
		this.in = in;
		this.pipeline = pipeline;
		Thread.startVirtualThread( () -> readFromStream() );
	}
	
	//-------------------------------------------------------------------
	private void readFromStream() {
		byte[] buffer = new byte[1024];
		try {
			while (true) {
				int len = in.read(buffer);
				// Did we read anything?
				if (len < 0) {
					// No, the inputstream was closed
					PipeClosed event = new PipeClosed("Stream closed");
					pipeline.publish(event);
					break;
				} else if (len>0) {
					// Yes
					byte[] copied = new byte[len];
					System.arraycopy(buffer, 0, copied, 0, len);
					BinaryDataEvent event = new BinaryDataEvent(copied);
					pipeline.publish(event);
				}
			}
		} catch (IOException e) {
			PipeClosed event = new PipeClosed("IOError: "+e);
			pipeline.publish(event);
		}
	}
}
