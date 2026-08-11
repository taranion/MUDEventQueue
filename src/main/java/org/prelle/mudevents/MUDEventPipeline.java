package org.prelle.mudevents;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry point for the MUD Event Queue library.
 * <p>
 * This library provides an event-driven mechanism for reading and writing
 * Telnet and ANSI data as event streams.
 */
public final class MUDEventPipeline {

	private List<MUDEventProcessor> processors = new ArrayList<>();
	
	
    private MUDEventPipeline() {
        // Utility class
    }
    
    //-------------------------------------------------------------------
    /**
     * Add a new processor to the end of the processing chain. Processors are executed in the order they are added.
     * @param processor
     */
    public void addProcessor(MUDEventProcessor processor) {
    	if (!processors.containsAll(processors))
    		processors.add(processor);
	}
    
    //-------------------------------------------------------------------
    /**
     * @param <T>
     * @param event
     */
    public <T extends MUDEvent> void publish(T event) {
		// Send an event to all processors in the chain. Every processor can choose to 
    	// either do nothing, consume or replace the events with other events, before
    	// the next processor in the chain is called.
    	List<MUDEvent> events = new ArrayList<>();
    	events.add(event);
    	
    	nextEvent:
    	while (events.size()>0) {
    		MUDEvent current = events.removeFirst();
        	for (MUDEventProcessor processor : processors) {
    			List<MUDEvent> newEvents = processor.apply(current);
    			if (newEvents==null || newEvents.isEmpty()) {
    				continue nextEvent; // The event has been consumed, stop processing
    			} else if (newEvents.size()==1 && newEvents.get(0)==event) {
    				// The event has not been changed, continue processing
    				continue;
    			} else {
    				// The event has been replaced with other events, process them instead
    				// The first event in the list will be processed next, and the rest will be processed later
    				current = newEvents.remove(0);    				
    				events.addAll(0, newEvents);
    			}    			
    		}
		}
    	
    	// Idea: Send a Sync/Flush event to all processors at the end of the chain, so they can flush any buffered data.
	}
    
}
