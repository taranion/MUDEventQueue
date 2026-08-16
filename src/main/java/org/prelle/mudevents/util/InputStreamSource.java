package org.prelle.mudevents.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;

import org.prelle.mudevents.BinaryDataEvent;
import org.prelle.mudevents.MUDEventPipeline;
import org.prelle.mudevents.PipeClosed;

/**
 * 
 */
public class InputStreamSource extends MUDEventPipeline {
	
	private InputStream in;

	//-------------------------------------------------------------------
	public InputStreamSource(InputStream in) {
		this(in, System.getLogger("mud.events"));
	}

	//-------------------------------------------------------------------
	public InputStreamSource(InputStream in, Logger logger) {
		super("StreamSource", logger);
		this.in = in;
		
		// Create a virtual thread to read from the input stream and publish events
		Thread.startVirtualThread(() -> readFromStream());
	}

	//-------------------------------------------------------------------
	private void readFromStream() {
		byte[] buffer = new byte[1024];
		try {
			while (true) {
				int len = in.read(buffer);
				System.err.println("InputStreamSource: Read "+len+" bytes from input stream");
				// Did we read anything?
				if (len < 0) {
					// No, the inputstream was closed
					PipeClosed event = new PipeClosed(this, "Stream closed");
					this.publish(event);
					break;
				} else if (len>0) {
					// Yes
					byte[] copied = new byte[len];
					System.arraycopy(buffer, 0, copied, 0, len);
					BinaryDataEvent event = new BinaryDataEvent(this,copied);
					this.publish(event);
				}
			}
		} catch (IOException e) {
			PipeClosed event = new PipeClosed(this, "IOError: "+e);
			this.publish(event);
		}
	}
}
