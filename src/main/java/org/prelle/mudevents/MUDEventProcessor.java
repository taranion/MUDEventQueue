package org.prelle.mudevents;

import java.util.List;
import java.util.function.Function;

/**
 * This interface allows a processing step for an event to replace one event with 0..n other events - or simply return the original one.
 */
public interface MUDEventProcessor extends Function<MUDEvent, List<MUDEvent>> {

	
}
