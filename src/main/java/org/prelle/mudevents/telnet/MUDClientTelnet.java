package org.prelle.mudevents.telnet;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import org.prelle.ansi.C0Code;
import org.prelle.mudevents.BinaryDataEvent;
import org.prelle.mudevents.MUDEvent;
import org.prelle.mudevents.MUDEventPipeline;
import org.prelle.mudevents.MUDEventProcessor;
import org.prelle.mudevents.StartEvent;
import org.prelle.telnet.event.DataEvent;
import org.prelle.telnet.event.TelnetCommand;
import org.prelle.telnet.event.TelnetEvent;
import org.prelle.telnet.option.CommunicationRole;
import org.prelle.telnet.option.TelnetOption;
import org.prelle.telnet.parser.TelnetConstants.ControlCode;
import org.prelle.telnet.parser.TelnetEncoder;
import org.prelle.telnet.protocol.TelnetOptionEvent;
import org.prelle.telnet.protocol.TelnetProtocol;
import org.prelle.telnet.protocol.TelnetProtocolListener;
import org.prelle.telnet.protocol.TelnetReturnChannel;

/**
 * 
 */
public class MUDClientTelnet implements TelnetProtocolListener, MUDEventProcessor {
	
	private final static Logger logger = System.getLogger("mud.client.telnet");
	
	private TelnetProtocol telnet;
	
	private MUDEventProcessor receiveProcessor;
	private MUDEventProcessor sendProcessor;
	
	private List<MUDEvent> telnetEvents;

	//-------------------------------------------------------------------
	public MUDClientTelnet(TelnetOption ... extensions) {
		telnetEvents = new ArrayList<>();
		telnet = TelnetProtocol.builder(CommunicationRole.CLIENT)
				.withEventFactory(MUDEventsTelnetEventFactory.INSTANCE)
				.withListener(this)
				.withOptions(extensions)
				.build();
		
		prepareReceiver();
		prepareSender();
	}

	//-------------------------------------------------------------------
	public MUDClientTelnet add(TelnetOption extension) {
		telnet.add(extension);
		return this;
	}
	
	//-------------------------------------------------------------------
	private void prepareReceiver() {
		receiveProcessor = new MUDEventProcessor() {
			@Override
			public List<MUDEvent> apply(MUDEvent event) {
				logger.log(Level.INFO, "RCV: {0}", event);
				telnetEvents.clear();
				if (event instanceof BinaryDataEvent binary) {
					telnet.process(binary.getData());
					return new ArrayList<>(telnetEvents);
				} else if (event instanceof StartEvent) {
					logger.log(Level.INFO, "Telnet protocol started.");
					telnet.initializeExtensions();
					return List.of(event);
				} else 
					logger.log(Level.ERROR, "Unexpected event type received: {0}", event.getClass().getName());
				return List.of(event);
			}
			public String getName() {
				return "Telnet";
			}
		};
	}

	//-------------------------------------------------------------------
	private void prepareSender() {
		sendProcessor = new MUDEventProcessor() {
			@Override
			public List<MUDEvent> apply(MUDEvent event) {
				logger.log(Level.INFO, "SND: {0}", event);
				if (event instanceof TelnetCommandEvent command) {
					var buf = TelnetEncoder.encodeEvent(command.getWrapped());
					return List.of(new BinaryDataEvent(this, buf));
				} else if (event instanceof BinaryDataEvent) {
					// Pass Event
					return List.of(event);
				} else
					logger.log(Level.WARNING, "Unexpected event type received in send processor: {0}", event.getClass().getName());
				return null;
			}
			public String getName() {
				return "Telnet";
			}
		};
	}

	//-------------------------------------------------------------------
	public MUDEventProcessor receiver() {
		return receiveProcessor;
	}

	//-------------------------------------------------------------------
	public MUDEventProcessor sender() {
		return sendProcessor;
	}

	public void setReversePipeline(MUDEventPipeline reversePipeline) {
		telnet.setReturnChannel(new TelnetReturnChannel() {
			@Override
			public void sendToRemote(TelnetEvent event) throws IOException {
				if (reversePipeline == null) {
					logger.log(Level.ERROR, "No reverse pipeline set, cannot send event to remote: {0}", event);
					return;
				}
				reversePipeline.publishAt(sendProcessor, new TelnetCommandEvent(this, event), false);
			}
		});
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.mudevents.MUDEventProcessor#apply(org.prelle.mudevents.MUDEvent)
	 */
	@Override
	public List<MUDEvent> apply(MUDEvent event) {
		logger.log(Level.INFO, "RCV "+event);
		if (event instanceof BinaryDataEvent binary) {
			telnet.process(binary.getData());
			return new ArrayList<>(telnetEvents);
//		} else if (event instanceof StartEvent) {
//			logger.log(Level.INFO, "Telnet protocol started.");
//			telnet.initializeExtensions();
//			return List.of(event);
		} 
		return List.of(event);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.protocol.TelnetProtocolListener#onTelnetEvent(org.prelle.telnet.event.TelnetEvent)
	 */
	@Override
	public void onTelnetEvent(TelnetEvent event) {
		logger.log(Level.INFO, "Telnet event: {0}", event);
		switch (event) {
		case DataEvent dataEvent -> {
				telnetEvents.add(new BinaryDataEvent(this, dataEvent.getData()));
			}
		case TelnetCommandEvent cmdEv  -> {
			switch (cmdEv.getWrapped()) {
			case TelnetCommand cmd when cmd.getCode()==ControlCode.GA -> {
				telnetEvents.add(new BinaryDataEvent(this, C0Code.RS.code()));
			}
			default -> {
				telnetEvents.add(cmdEv);
			}
			};
		}
		default -> {
			if (event instanceof MUDEvent) {
				telnetEvents.add((MUDEvent)event);
			} else {
				System.err.println("Unhandled Telnet event type: "+event.getClass().getName());
				telnetEvents.add(new TelnetCommandEvent(this, event));
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
		telnetEvents.add(new TelnetReadyEvent(this)); // 
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.mudevents.MUDEventProcessor#getName()
	 */
	@Override
	public String getName() {
		return "Telnet";
	}

}
