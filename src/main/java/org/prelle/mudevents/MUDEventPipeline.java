package org.prelle.mudevents;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

/**
 * Entry point for the MUD Event Queue library.
 * <p>
 * This pipeline provides a bidirectional event-driven processing chain for reading and writing
 * Telnet, ANSI, GMCP, and domain events.
 * <ul>
 *   <li><b>Inbound (from client / remote):</b> Traverses processors from index 0 to N (via {@link #fireReceive(PipeEvent)}).</li>
 *   <li><b>Outbound (to client / remote):</b> Traverses processors from index N down to 0 (via {@link #fireSend(PipeEvent)}).</li>
 * </ul>
 */
public class MUDEventPipeline {

	@Getter @Setter
	private boolean isSendPipe;
	private Logger logger;
	private String name;
	@Getter @Setter
	private Locale locale;
	@Getter
	private List<MUDEventProcessor> processors = new ArrayList<>();
	
	@Getter @Setter
	@Deprecated
	private MUDEventPipeline reversePipeline;
	
	private Optional<Consumer<PipeLogEntry>> logConsumer = Optional.empty();
    
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
	public void setLogger(Consumer<PipeLogEntry> logConsumer) {
		this.logConsumer = Optional.ofNullable(logConsumer);
	}
	
	//-------------------------------------------------------------------
	public String toString() {
		StringBuilder sb = new StringBuilder(name + ": ");
		List<String> processorNames = processors.stream().map(p -> p.getName()).toList();
		sb.append(String.join(" <-> ", processorNames));
		return sb.toString();
	}
   
    //-------------------------------------------------------------------
    /**
     * Add a processor to the application/game end of the processing chain (index N).
     */
    public MUDEventPipeline then(MUDEventProcessor processor) {
    	processor.setPipeline(this);
    	if (reversePipeline != null) {
			processor.setReversePipeline(reversePipeline);
		} else {
			processor.setReversePipeline(this);
		}
    	if (!processors.contains(processor))
    		processors.add(processor);
    	return this;
	}

    public MUDEventPipeline addLast(MUDEventProcessor processor) {
    	return then(processor);
    }
    
    //-------------------------------------------------------------------
    /**
     * Add a new processor directly after the given processor in the chain (towards application/high index).
     */
    public void insertAfter(MUDEventProcessor index, MUDEventProcessor processor) {
    	processor.setPipeline(this);
    	if (reversePipeline != null) {
			processor.setReversePipeline(reversePipeline);
		} else {
			processor.setReversePipeline(this);
		}
    	int pos = processors.indexOf(index);
    	if (pos >= 0 && !processors.contains(processor))
    		processors.add(pos + 1, processor);
    	else if (pos < 0 && !processors.contains(processor))
    		processors.add(processor);
	}
    
    //-------------------------------------------------------------------
    /**
     * Add a new processor directly before the given processor in the chain (towards network/low index).
     */
    public void insertBefore(MUDEventProcessor index, MUDEventProcessor processor) {
    	processor.setPipeline(this);
    	if (reversePipeline != null) {
			processor.setReversePipeline(reversePipeline);
		} else {
			processor.setReversePipeline(this);
		}
    	int pos = processors.indexOf(index);
    	if (pos >= 0 && !processors.contains(processor))
    		processors.add(pos, processor);
    	else if (pos < 0 && !processors.contains(processor))
    		processors.add(0, processor);
	}
    
    //-------------------------------------------------------------------
    /**
     * Replace an existing processor with a new processor at the exact same position in the chain.
     */
    public void replace(MUDEventProcessor toReplace, MUDEventProcessor processor) {
    	processor.setPipeline(this);
    	if (reversePipeline != null) {
			processor.setReversePipeline(reversePipeline);
		} else {
			processor.setReversePipeline(this);
		}
    	int pos = processors.indexOf(toReplace);
    	if (pos > -1) {
    		processors.remove(toReplace);
    		processors.add(pos, processor);
    	}
	}
    
    //-------------------------------------------------------------------
    /**
     * Add a processor to the network/socket end of the processing chain (index 0).
     */
    public MUDEventPipeline before(MUDEventProcessor processor) {
    	processor.setPipeline(this);
    	if (reversePipeline != null) {
			processor.setReversePipeline(reversePipeline);
		} else {
			processor.setReversePipeline(this);
		}
    	if (!processors.contains(processor))
    		processors.add(0, processor);
    	return this;
	}

    public MUDEventPipeline addFirst(MUDEventProcessor processor) {
    	return before(processor);
    }

    //-------------------------------------------------------------------
    public boolean remove(MUDEventProcessor processor) {
    	return processors.remove(processor);
    }

	//-------------------------------------------------------------------
	/**
	 * Send an event from remote (client) towards the server (inbound).
	 * Traverses processors from index 0 to N.
	 */
	public <T extends PipeEvent> void fireReceive(T event) {
		receiveFromRemote(null, event);
	}

	//-------------------------------------------------------------------
	/**
	 * Send an event from the server towards remote/client (outbound).
	 * Traverses processors from index N down to 0.
	 */
	public <T extends PipeEvent> void fireSend(T event) {
		sendToRemote(null, event);
	}

	//-------------------------------------------------------------------
	/**
	 * Inject an inbound event (from client to server) starting after the given processor.
	 * If pos == null, starts at the first processor (index 0).
	 */
	public <T extends PipeEvent> void receiveFromRemote(MUDEventProcessor pos, T event) {
		if (event == null || processors.isEmpty()) {
			return;
		}
		int startIndex = 0;
		if (pos != null) {
			int idx = processors.indexOf(pos);
			if (idx < 0) {
				if (logger != null) logger.log(Level.WARNING, "Processor {0} not in pipeline {1}", pos, name);
				return;
			}
			startIndex = idx + 1;
		}
		processInbound(startIndex, event);
	}

	//-------------------------------------------------------------------
	/**
	 * Inject an outbound event (from server to client) starting after the given processor (towards client).
	 * If pos == null, starts at the last processor (index N-1).
	 */
	public <T extends PipeEvent> void sendToRemote(MUDEventProcessor pos, T event) {
		if (event == null || processors.isEmpty()) {
			return;
		}
		int startIndex = processors.size() - 1;
		if (pos != null) {
			int idx = processors.indexOf(pos);
			if (idx < 0) {
				if (logger != null) logger.log(Level.WARNING, "Processor {0} not in pipeline {1}", pos, name);
				return;
			}
			startIndex = idx - 1;
		}
		processOutbound(startIndex, event);
	}

	//-------------------------------------------------------------------
	/**
	 * Legacy entry point. If isSendPipe is true, executes fireSend; otherwise fireReceive.
	 */
	public <T extends PipeEvent> void publish(T event) {
		System.err.println("MUDEventPipeline.publish: "+event);
		if (logger != null)
			logger.log(Level.INFO, "{2}: publish: {0} ({1})", event.getClass().getSimpleName(), event.toString(), name);
		if (isSendPipe) {
			fireSend(event);
		} else {
			fireReceive(event);
		}
	}

	//-------------------------------------------------------------------
	/**
	 * Legacy entry point with target position.
	 */
	public <T extends PipeEvent> void publishAt(MUDEventProcessor pos, T event, boolean before) {
		if (logger != null) {
			logger.log(Level.DEBUG, "Pipeline {0} publishAt {1} at {2} (before={3})", name, event.getClass().getSimpleName(), pos == null ? "end" : pos.getClass().getSimpleName(), before);
		}
		if (isSendPipe) {
			// Outbound: from N down to 0
			if (pos == null) {
				processOutbound(processors.size() - 1, event);
			} else {
				int idx = processors.indexOf(pos);
				if (idx < 0) return;
				int startIndex = before ? idx : idx - 1;
				processOutbound(startIndex, event);
			}
		} else {
			// Inbound: from 0 up to N
			if (pos == null) {
				processInbound(0, event);
			} else {
				int idx = processors.indexOf(pos);
				if (idx < 0) return;
				int startIndex = before ? idx : idx + 1;
				processInbound(startIndex, event);
			}
		}
	}

	//-------------------------------------------------------------------
	private void processInbound(int startIndex, PipeEvent event) {
		if (logger != null) {
			logger.log(Level.DEBUG, "Pipeline {0} INBOUND event {1} starting at index {2}", name, event.getClass().getSimpleName(), startIndex);
		}
		List<PipeEvent> currentEvents = new ArrayList<>();
		currentEvents.add(event);

		List<MUDEventProcessor> snapshot = new ArrayList<>(processors);
		for (int i = startIndex; i < snapshot.size(); i++) {
			MUDEventProcessor processor = snapshot.get(i);
			MUDEventProcessor previousProcessor = (i > 0) ? snapshot.get(i - 1) : null;
			List<PipeEvent> nextEvents = new ArrayList<>();
			for (PipeEvent current : currentEvents) {
				if (logConsumer.isPresent()) {
					logConsumer.get().accept(new PipeLogEntry(this, previousProcessor, processor, current.toString(), false));
				}
				List<PipeEvent> produced = processor.onReceiveFromRemote(current);
				if (produced != null && !produced.isEmpty()) {
					if (logger != null && produced.size() == 1 && produced.get(0) == current) {
						logger.log(Level.TRACE, "Processor {0} passed {1} unchanged (inbound)", processor.getName(), current.getClass().getSimpleName());
					} else if (logger != null) {
						logger.log(Level.DEBUG, "Processor {0} replaced {2} with {1} (inbound)", processor.getName(), produced, current.getClass().getSimpleName());
					}
					for (PipeEvent p : produced) {
						if (p != null) nextEvents.add(p);
					}
				} else {
					if (logger != null) {
						logger.log(Level.INFO, "{2}: Processor {0} consumed {1} (inbound)", processor.getName(), current.getClass().getSimpleName(), name);
					}
				}
			}
			currentEvents = nextEvents;
			if (currentEvents.isEmpty()) {
				break;
			}
		}
	}

	//-------------------------------------------------------------------
	private void processOutbound(int startIndex, PipeEvent event) {
		if (logger != null) {
			logger.log(Level.DEBUG, "Pipeline {0} OUTBOUND event {1} starting at index {2}", name, event.getClass().getSimpleName(), startIndex);
		}
		List<PipeEvent> currentEvents = new ArrayList<>();
		currentEvents.add(event);

		List<MUDEventProcessor> snapshot = new ArrayList<>(processors);
		for (int i = startIndex; i >= 0; i--) {
			MUDEventProcessor processor = snapshot.get(i);
			MUDEventProcessor nextProcessor = (i < snapshot.size() - 1) ? snapshot.get(i + 1) : null;
			List<PipeEvent> nextEvents = new ArrayList<>();
			for (PipeEvent current : currentEvents) {
				if (logConsumer.isPresent()) {
					logConsumer.get().accept(new PipeLogEntry(this, nextProcessor, processor, current.toString(), true));
				}
				// Avoid calling steps that are not interested in the event
//				if (!processor.processesOutgoing(current))
//					continue;
				List<PipeEvent> produced = processor.onSendToRemote(current);
				if (produced != null && !produced.isEmpty()) {
					if (logger != null && produced.size() == 1 && produced.get(0) == current) {
						logger.log(Level.TRACE, "Processor {0} passed {1} unchanged (outbound)", processor.getName(), current.getClass().getSimpleName());
					} else if (logger != null) {
						logger.log(Level.DEBUG, "Processor {0} replaced {2} with {1} (outbound)", processor.getName(), produced, current.getClass().getSimpleName());
					}
					for (PipeEvent p : produced) {
						if (p != null) nextEvents.add(p);
					}
				} else {
					if (logger != null) {
						logger.log(Level.INFO, "{2}: Processor {0} consumed {1} (outbound)", processor.getName(), current.getClass().getSimpleName(), name);
					}
				}
			}
			currentEvents = nextEvents;
			if (currentEvents.isEmpty()) {
				break;
			}
		}
	}

}
