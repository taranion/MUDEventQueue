package org.prelle.mudevents;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
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

	@Getter
	private boolean isSendPipe;
	private Logger logger;
	private String name;
	private List<MUDEventProcessor> processors = new ArrayList<>();
	
	@Getter @Setter
	private MUDEventPipeline reversePipeline;
    
    //-------------------------------------------------------------------
	public MUDEventPipeline(String name) {
		this(name, false);
	}
    
    //-------------------------------------------------------------------
	public MUDEventPipeline(String name, boolean isSend) {
		this.name = name;
		this.isSendPipe = isSend;
	}
    
    //-------------------------------------------------------------------
	public MUDEventPipeline(String name, boolean isSend, Logger logger) {
		this(name, isSend);
		this.logger = logger;
	}

	//-------------------------------------------------------------------
	public String getName() {
		return name;
	}

	//-------------------------------------------------------------------
	public String toString() {
		StringBuilder sb = new StringBuilder(name+": ");
		List<String> processorNames = processors.stream().map(p -> p.getName()).toList();
		sb.append(String.join(" -> ", processorNames));
		return sb.toString();
	}
   
    //-------------------------------------------------------------------
    /**
     * Add a new processor to the end of the processing chain. Processors are executed in the order they are added.
     * @param processor
     */
    public MUDEventPipeline then(MUDEventProcessor processor) {
    	processor.setReversePipeline(reversePipeline);
    	if (!processors.contains(processor))
    		processors.add(processor);
    	return this;
	}
    
    //-------------------------------------------------------------------
    /**
     * Add a new processor directly after the given processor in the processing chain. 
     * @param processor
     */
    public void insertAfter(MUDEventProcessor index, MUDEventProcessor processor) {
    	processor.setReversePipeline(reversePipeline);
    	int pos = processors.indexOf(index);
    	if (!processors.contains(processor))
    		processors.add(pos+1,processor);
	}
    
    //-------------------------------------------------------------------
    /**
     * Add a new processor directly after the given processor in the processing chain. 
     * @param processor
     */
    public void replace(MUDEventProcessor toReplace, MUDEventProcessor processor) {
    	processor.setReversePipeline(reversePipeline);
    	int pos = processors.indexOf(toReplace);
    	if (!processors.contains(processor) && pos>-1) {
    		processors.remove(toReplace);
    		processors.add(pos,processor);
    	}
	}
    
    //-------------------------------------------------------------------
    /**
     * Add a new processor to the begin of the processing chain. Processors are executed in the order they are added.
     * @param processor
     */
    public MUDEventPipeline before(MUDEventProcessor processor) {
    	//if (!processors.containsAll(processors))
    		processors.add(0,processor);
    	return this;
	}
    
    //-------------------------------------------------------------------
    public <T extends PipeEvent> void publish(T event) {
    	logger.log(Level.INFO, "{2}: publish: {0} to {1} processors", event.getClass(), processors.size(), name);
    	publishAt(null, event, true);
    }
    
    //-------------------------------------------------------------------
    /**
     * @param <T>
     * @param event
     */
    public <T extends PipeEvent> void publishAt(MUDEventProcessor pos, T event, boolean before) {
    	if (logger!=null) {
    		logger.log(Logger.Level.DEBUG, "Pipeline {0} publishing event {1} at {2} (before={3})", name, event.getClass().getSimpleName(), pos==null?"start":pos.getClass().getSimpleName(), before);
    	}
		// Send an event to all processors in the chain. Every processor can choose to 
    	// either do nothing, consume or replace the events with other events, before
    	// the next processor in the chain is called.
    	if (event == null || processors.isEmpty()) {
    		return;
    	}

    	List<PipeEvent> currentEvents = new ArrayList<>();
    	currentEvents.add(event);

    	boolean startProcessing = (pos == null);
    	for (MUDEventProcessor processor : new ArrayList<>(processors)) {
    		boolean isLast = (processors.indexOf(processor) == processors.size() - 1);
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
    		List<PipeEvent> nextEvents = new ArrayList<>();
    		for (PipeEvent current : currentEvents) {
        		if (logger!=null) {
    				logger.log(Logger.Level.TRACE, "  Pipeline {0} sending event {1} to processor {2}", name, current.getClass().getSimpleName(), processor.getClass().getSimpleName());
    			}
    			List<PipeEvent> produced = (isSendPipe)?processor.onSendToRemote(current):processor.onReceiveFromRemote(current);
    			if (produced != null && !produced.isEmpty()) {
    				if (logger!=null && produced.size() == 1 && produced.get(0) == current) {
						logger.log(Logger.Level.TRACE, "Processor {0} passed {1} unchanged", processor.getName(), current.getClass().getSimpleName());
					} else if (logger!=null) {
						logger.log(Logger.Level.DEBUG, "Processor {0} replaced {2} with {1}", processor.getName(), produced, current.getClass().getSimpleName());
					}
    				for (PipeEvent p : produced) {
    					if (p != null) {
    						nextEvents.add(p);
    					}
    				}
    			} else {
    				// Consumed, do not pass to next processor
    				if (logger!=null && !processor.getClass().getSimpleName().isBlank() && !isLast) {
    					logger.log(Logger.Level.WARNING, "{2}: Processor {0} consumed {1}", processor.getName(), current.getClass().getSimpleName(), name);
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
