package org.prelle.mudevents.telnet;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.prelle.ansi.C0Code;
import org.prelle.mudevents.BinaryDataEvent;
import org.prelle.mudevents.CloseClientEvent;
import org.prelle.mudevents.MUDEventPipeline;
import org.prelle.mudevents.MUDEventProcessor;
import org.prelle.mudevents.PipeEvent;
import org.prelle.mudevents.StartEvent;
import org.prelle.telnet.event.DataEvent;
import org.prelle.telnet.event.TelnetCommand;
import org.prelle.telnet.event.TelnetEvent;
import org.prelle.telnet.event.TelnetNegotiationEvent;
import org.prelle.telnet.event.TelnetSubnegotiationEvent;
import org.prelle.telnet.option.CommunicationRole;
import org.prelle.telnet.option.TelnetOption;
import org.prelle.telnet.parser.TelnetConstants.ControlCode;
import org.prelle.telnet.parser.TelnetEncoder;
import org.prelle.telnet.protocol.TelnetProtocol;
import org.prelle.telnet.protocol.TelnetProtocol.NegotiationState;
import org.prelle.telnet.protocol.TelnetProtocol.OptionState;
import org.prelle.telnet.protocol.TelnetProtocolListener;
import org.prelle.telnet.protocol.TelnetReturnChannel;

/**
 * 
 */
public class MUDServerTelnet implements TelnetProtocolListener, MUDEventProcessor {
	
	private final static Logger logger = System.getLogger("telnet");
	
	private TelnetProtocol telnet;
	
	private List<PipeEvent> telnetEvents;

	private TelnetReturnChannel returnChannel;
	
	//-------------------------------------------------------------------
	public MUDServerTelnet(TelnetOption ... extensions) {
		telnetEvents = new ArrayList<>();
		telnet = TelnetProtocol.builder(CommunicationRole.SERVER)
				.withEventFactory(MUDEventsTelnetEventFactory.INSTANCE)
				.withListener(this)
				.withOptions(extensions)
				.build();
	}

	//-------------------------------------------------------------------
	public MUDServerTelnet add(TelnetOption extension) {
		telnet.add(extension);
		return this;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.mudevents.MUDEventProcessor#onReceiveFromRemote(org.prelle.mudevents.PipeEvent)
	 */
	@Override
	public List<PipeEvent> onReceiveFromRemote(PipeEvent event) {
		logger.log(Level.INFO, "RCV: {0}", event);
		telnetEvents.clear();
		if (event instanceof BinaryDataEvent binary) {
			telnet.process(binary.getData());
			return new ArrayList<>(telnetEvents);
		} else if (event instanceof StartEvent) {
			logger.log(Level.INFO, "Telnet protocol started.");
			telnet.initializeExtensions();
			return List.of(event);
		} 
		return List.of(event);
	}

	//-------------------------------------------------------------------
	public List<PipeEvent> onSendToRemote(PipeEvent event) {
		logger.log(Level.DEBUG, "SND: {0}", event);
		if (event instanceof TelnetCommandEvent command) {
			var buf = TelnetEncoder.encodeEvent(command.getWrapped());
			return List.of(new BinaryDataEvent(buf));
		} else if (event instanceof TelnetNegotiationEvent negotiation) {
			// Change state so we don't react on the answer
			NegotiationState state = telnet.getNegotiationState(negotiation.getOption());
			if (negotiation.getType()==ControlCode.WILL || negotiation.getType()==ControlCode.DO)
				state.setState(OptionState.INACTIVE_QUERIED);
			else
				state.setState(OptionState.ACTIVE_QUERIED);				
			var buf = TelnetEncoder.encodeEvent(negotiation);
			return List.of(new BinaryDataEvent(buf));
		} else if (event instanceof TelnetSubnegotiationEvent subneg) {
			var buf = TelnetEncoder.encodeEvent(subneg);
			return List.of(new BinaryDataEvent(buf));
		} else if (event instanceof BinaryDataEvent) {
			// Pass Event
			return List.of(event);
		} else if (event instanceof CloseClientEvent) {
			return List.of(event);
		} else
			logger.log(Level.WARNING, "Unexpected event type received in send processor: {0}", event.getClass().getName());
		return null;
	}

	//-------------------------------------------------------------------
	public void setReversePipeline(MUDEventPipeline reversePipeline) {
		Objects.requireNonNull(reversePipeline);
		if (returnChannel != null) {
			return;
		}
		
		if (reversePipeline.toString().contains("RCV")) {
			throw new IllegalArgumentException("Reverse pipeline must not contain a receive processor.");
		}
		
		returnChannel = new TelnetReturnChannel() {
			@Override
			public void sendToRemote(TelnetEvent event) throws IOException {
				reversePipeline.publishAt(MUDServerTelnet.this, new TelnetCommandEvent(event), false);
			}
		};
		
		telnet.setReturnChannel(returnChannel);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.protocol.TelnetProtocolListener#onTelnetEvent(org.prelle.telnet.event.TelnetEvent)
	 */
	@Override
	public void onTelnetEvent(TelnetEvent event) {
		logger.log(Level.INFO, "Telnet event: {0} / {1}", event, event.getClass());
		switch (event) {
		case DataEvent dataEvent -> {
//			System.err.println("MUDServerTelnet: Telnet data event: "+new String(dataEvent.getData()));
			telnetEvents.add(new BinaryDataEvent(dataEvent.getData()));
		}
		case TelnetCommandEvent cmdEv  -> {
			switch (cmdEv.getWrapped()) {
			case TelnetCommand cmd when cmd.getCode()==ControlCode.GA -> {
				telnetEvents.add(new BinaryDataEvent(C0Code.RS.code()));
			}
			default -> {
				telnetEvents.add(cmdEv);
			}
			};
		}
		default -> {
			if (event instanceof PipeEvent) {
				telnetEvents.add((PipeEvent)event);
			} else {
				System.err.println("MUDServerTelnet: Unhandled Telnet event type: "+event.getClass().getName());
				telnetEvents.add(new TelnetCommandEvent(event));
			}
			}
		}
		
	}

	@Override
	public void optionStateChanged(TelnetOption extension, boolean active) {
		// TODO Auto-generated method stub
		logger.log(Level.INFO, "Telnet option {0} is now {1}", extension.getName(), active ? "active" : "inactive");
	}

	@Override
	public void telnetReady() {
		telnetEvents.add(new TelnetReadyEvent()); // 
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.mudevents.MUDEventProcessor#getName()
	 */
	@Override
	public String getName() {
		return "Telnet";
	}

	//-------------------------------------------------------------------
	public TelnetProtocol getProtocol() {
		return telnet;
	}

}
