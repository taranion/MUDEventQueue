package org.prelle.mudevents;

import java.util.List;

/**
 * This interface allows a processing step for an event to replace one event with 0..n other events - or simply return the original one.
 */
public interface MUDEventProcessor {
	
	public default String getName() { return getClass().getSimpleName(); }

	public List<PipeEvent> onReceiveFromRemote(PipeEvent event);

	public List<PipeEvent> onSendToRemote(PipeEvent event);
	
	public default void setReversePipeline(MUDEventPipeline reversePipeline) {}
	
}
