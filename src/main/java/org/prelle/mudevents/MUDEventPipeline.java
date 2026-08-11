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

	private String name;
	private List<MUDEventProcessor> processors = new ArrayList<>();
    
    //-------------------------------------------------------------------
	public MUDEventPipeline(String name) {
		this.name = name;
	}

	//-------------------------------------------------------------------
	public String getName() {
		return name;
	}
    
    //-------------------------------------------------------------------
    /**
     * Add a new processor to the end of the processing chain. Processors are executed in the order they are added.
     * @param processor
     */
    public MUDEventPipeline then(MUDEventProcessor processor) {
    	//if (!processors.containsAll(processors))
    		processors.add(processor);
    	return this;
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
    	if (event == null || processors.isEmpty()) {
    		return;
    	}

    	List<MUDEvent> currentEvents = new ArrayList<>();
    	currentEvents.add(event);

    	for (MUDEventProcessor processor : processors) {
    		List<MUDEvent> nextEvents = new ArrayList<>();
    		for (MUDEvent current : currentEvents) {
    			List<MUDEvent> produced = processor.apply(current);
    			if (produced != null && !produced.isEmpty()) {
    				for (MUDEvent p : produced) {
    					if (p != null) {
    						nextEvents.add(p);
    					}
    				}
    			}
    		}
    		currentEvents = nextEvents;
    		if (currentEvents.isEmpty()) {
    			break; // All events were consumed, stop pipeline early
    		}
    	}
    	
    	// Idea: Send a Sync/Flush event to all processors at the end of the chain, so they can flush any buffered data.
	}

}
