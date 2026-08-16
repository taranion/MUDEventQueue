package org.prelle.mudevents;

import java.util.List;

/**
 * This interface allows a processing step for an event to replace one event with 0..n other events - or simply return the original one.
 */
public interface MUDEventProcessor {

	public List<MUDEvent> apply(MUDEvent event);
	
	public default void setReversePipeline(MUDEventPipeline reversePipeline) {}
	
}
