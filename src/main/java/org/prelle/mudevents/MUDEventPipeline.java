package org.prelle.mudevents;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Entry point for the MUD Event Queue library.
 * <p>
 * This library provides an event-driven mechanism for reading and writing
 * Telnet and ANSI data as event streams.
 */
public class MUDEventPipeline {

	private Logger logger;;
	private String name;
	private List<MUDEventProcessor> processors = new ArrayList<>();
	
	@Getter @Setter
	private MUDEventPipeline reversePipeline;
    
    //-------------------------------------------------------------------
	public MUDEventPipeline(String name) {
		this.name = name;
	}
    
    //-------------------------------------------------------------------
	public MUDEventPipeline(String name, Logger logger) {
		this.name = name;
		this.logger = logger;
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
    	processor.setReversePipeline(reversePipeline);
    	//if (!processors.containsAll(processors))
    		processors.add(processor);
    	return this;
	}
    
    //-------------------------------------------------------------------
    public <T extends MUDEvent> void publish(T event) {
    	publishAt(null, event, true);
    }
    
    //-------------------------------------------------------------------
    /**
     * @param <T>
     * @param event
     */
    public <T extends MUDEvent> void publishAt(MUDEventProcessor pos, T event, boolean before) {
    	if (logger!=null) {
    		logger.log(Logger.Level.INFO, "Pipeline {0} publishing event {1} at {2} (before={3})", name, event.getClass().getSimpleName(), pos==null?"start":pos.getClass().getSimpleName(), before);
    	}
		// Send an event to all processors in the chain. Every processor can choose to 
    	// either do nothing, consume or replace the events with other events, before
    	// the next processor in the chain is called.
    	if (event == null || processors.isEmpty()) {
    		return;
    	}

    	List<MUDEvent> currentEvents = new ArrayList<>();
    	currentEvents.add(event);

    	boolean startProcessing = (pos == null);
    	for (MUDEventProcessor processor : processors) {
    		if (logger!=null) {
				logger.log(Logger.Level.TRACE, "  Pipeline {0} processing event {1} at processor {2}", name, currentEvents, processor.getClass().getSimpleName());
			}
    		if (!startProcessing) {
				if (processor == pos) {
					startProcessing = true;
				}
				if (!startProcessing && !before)
					continue;
			}
    		List<MUDEvent> nextEvents = new ArrayList<>();
    		for (MUDEvent current : currentEvents) {
        		if (logger!=null) {
    				logger.log(Logger.Level.TRACE, "  Pipeline {0} sending event {1} to processor {2}", name, current.getClass().getSimpleName(), processor.getClass().getSimpleName());
    			}
    			List<MUDEvent> produced = processor.apply(current);
    			if (produced != null && !produced.isEmpty()) {
    				if (logger!=null && produced.size() == 1 && produced.get(0) == current) {
						logger.log(Logger.Level.TRACE, "Processor {0} passed {1} unchanged", processor.getClass().getSimpleName(), current.getClass().getSimpleName());
					} else if (logger!=null) {
						logger.log(Logger.Level.INFO, "Processor {0} replaced {2} with {1}", processor.getClass().getSimpleName(), produced, current.getClass().getSimpleName());
					}
    				for (MUDEvent p : produced) {
    					if (p != null) {
    						nextEvents.add(p);
    					}
    				}
    			} else {
    				// Consumed, do not pass to next processor
    				if (logger!=null && !processor.getClass().getSimpleName().isBlank()) {
    					logger.log(Logger.Level.DEBUG, "Processor {0} consumed {1}", processor.getClass().getSimpleName(), current.getClass().getSimpleName());
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
