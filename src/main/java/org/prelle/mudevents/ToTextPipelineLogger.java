package org.prelle.mudevents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 
 */
public class ToTextPipelineLogger implements Consumer<PipeLogEntry> {
	
	private List<MUDEventProcessor> drawingOrder;

	//-------------------------------------------------------------------
	public ToTextPipelineLogger(MUDEventPipeline pipeline) {
		pipeline.setLogger(this);
		drawingOrder = new ArrayList<>(pipeline.getProcessors());
	}

	//-------------------------------------------------------------------
	public ToTextPipelineLogger(MUDEventPipeline rcv, MUDEventPipeline snd) {
		rcv.setLogger(this);
		snd.setLogger(this);
		
		drawingOrder = new ArrayList<>(rcv.getProcessors());
		Collections.reverse(drawingOrder);
		// Sort the send pipeline processors in between the receive pipeline processors
		int nextInsertAt = 0;
		for (MUDEventProcessor p : snd.getProcessors()) {
			if (drawingOrder.contains(p)) {
				if (drawingOrder.indexOf(p) < nextInsertAt) {
					continue;
				}
				nextInsertAt = drawingOrder.indexOf(p) + 1;
				continue; // Already in the list
			}
			drawingOrder.add(nextInsertAt, p);
			nextInsertAt++;
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see java.util.function.Consumer#accept(java.lang.Object)
	 */
	@Override
	public void accept(PipeLogEntry entry) {
		int pos = (entry.getFrom() != null) ? drawingOrder.indexOf(entry.getFrom()) : (entry.isOutbound() ? drawingOrder.size() : -1);
		if (pos == -1 && entry.getFrom() != null) {
			System.err.println("ToTextPipelineLogger: Unknown processor " + entry.getFrom().getName());
			return;
		}
		int prefix = Math.max(0, (pos + 1) * 12);
		String logLine = " ".repeat(prefix) + "|" + entry.toString() + "|";
		System.err.println(logLine);
	}

}
