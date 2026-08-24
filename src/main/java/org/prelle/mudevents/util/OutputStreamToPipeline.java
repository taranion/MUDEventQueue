package org.prelle.mudevents.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import org.prelle.mudevents.BinaryDataEvent;
import org.prelle.mudevents.MUDEventPipeline;
import org.prelle.mudevents.MUDEventProcessor;

/**
 * 
 */
public class OutputStreamToPipeline extends OutputStream {
	
	private MUDEventPipeline pipeline;
	private MUDEventProcessor publishAt;

	//-------------------------------------------------------------------
	public OutputStreamToPipeline(MUDEventPipeline pipeline) {
		this.pipeline = pipeline;
	}
	
	//-------------------------------------------------------------------
	public OutputStreamToPipeline(MUDEventPipeline pipeline, MUDEventProcessor publishAt) {
		this.pipeline = pipeline;
		this.publishAt = publishAt;
	}
	
	//-------------------------------------------------------------------
	public String toString() {
		return "OS->"+pipeline.getName();
	}

	@Override
	public void write(int b) throws IOException {
		BinaryDataEvent event = new BinaryDataEvent(new byte[] {(byte)b});
		if (publishAt != null) {
			pipeline.publishAt(publishAt, event, false);
		} else {
			pipeline.publish(event);
		}
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		BinaryDataEvent event = new BinaryDataEvent(b);
		if (publishAt != null) {
			pipeline.publishAt(publishAt, event, false);
		} else {
			pipeline.publish(event);
		}
	}
	
	@Override
   public void write(byte[] b, int off, int len) throws IOException {
		Objects.checkFromIndexSize(off, len, b.length);
		byte[] copied = new byte[len];
		BinaryDataEvent event = new BinaryDataEvent(copied);
		if (publishAt != null) {
			pipeline.publishAt(publishAt, event, false);
		} else {
			pipeline.publish(event);
		}
    }


}
