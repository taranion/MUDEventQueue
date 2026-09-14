package org.prelle.mudevents.telnet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;

import org.prelle.ansi.C0Code;
import org.prelle.mudevents.BinaryDataEvent;
import org.prelle.mudevents.PipeEvent;
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
import org.prelle.telnet.protocol.TelnetProtocol;
import org.prelle.telnet.protocol.TelnetProtocolListener;
import org.prelle.telnet.protocol.TelnetReturnChannel;

/**
 * 
 */
public class MUDClientTelnet implements TelnetProtocolListener, MUDEventProcessor {
	
	private final static Logger logger = System.getLogger("mud.client.telnet");
	
	private TelnetProtocol telnet;
	
	private List<PipeEvent> telnetEvents;

	private MUDEventPipeline pipeline;

	//-------------------------------------------------------------------
	public MUDClientTelnet(TelnetOption ... extensions) {
		telnetEvents = new ArrayList<>();
		telnet = TelnetProtocol.builder(CommunicationRole.CLIENT)
				.withEventFactory(MUDEventsTelnetEventFactory.INSTANCE)
				.withListener(this)
				.withOptions(extensions)
				.build();
	}

	//-------------------------------------------------------------------
	public MUDClientTelnet add(TelnetOption extension) {
		telnet.add(extension);
		return this;
	}

	//-------------------------------------------------------------------
	@Override
	public void setPipeline(MUDEventPipeline pipeline) {
		this.pipeline = pipeline;
		telnet.setReturnChannel(new TelnetReturnChannel() {
			@Override
			public void sendToRemote(TelnetEvent event) throws IOException {
				if (pipeline == null) {
					logger.log(Level.ERROR, "No pipeline set, cannot send event to remote: {0}", event);
					return;
				}
				List<byte[]> buffers = telnet.encode(event);
				for (byte[] encoded : buffers) {
					pipeline.sendToRemote(MUDClientTelnet.this, new BinaryDataEvent(encoded));
				}
			}
		});
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
		} else 
			logger.log(Level.ERROR, "Unexpected event type received: {0}", event.getClass().getName());
		return List.of(event);
	}

	//-------------------------------------------------------------------
	@Override
	public List<PipeEvent> onSendToRemote(PipeEvent event) {
		logger.log(Level.DEBUG, "SND: {0}", event);
		if (event instanceof TelnetCommandEvent command) {
			List<byte[]> buffers = telnet.encode(command.getWrapped());
			List<PipeEvent> result = new ArrayList<>();
			for (byte[] buf : buffers) {
				result.add(new BinaryDataEvent(buf));
			}
			return result;
		} else if (event instanceof BinaryDataEvent binary) {
			// If binary data contains 0xFF (IAC), Telnet requires escaping it as 0xFF 0xFF if not a command
			byte[] raw = binary.getData();
			boolean needsEscape = false;
			for (byte b : raw) {
				if ((b & 0xFF) == 0xFF) {
					needsEscape = true;
					break;
				}
			}
			if (!needsEscape) {
				return List.of(event);
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			for (byte b : raw) {
				baos.write(b);
				if ((b & 0xFF) == 0xFF) {
					baos.write(0xFF);
				}
			}
			return List.of(new BinaryDataEvent(baos.toByteArray()));
		} else
			logger.log(Level.WARNING, "Unexpected event type received in send processor: {0}", event.getClass().getName());
		return null;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.protocol.TelnetProtocolListener#onTelnetEvent(org.prelle.telnet.event.TelnetEvent)
	 */
	@Override
	public void onTelnetEvent(TelnetEvent event) {
		switch (event) {
		case DataEvent dataEvent -> {
				telnetEvents.add(new BinaryDataEvent(dataEvent.getData()));
			}
		case TelnetCommandEvent cmdEv  -> {
			switch (cmdEv.getWrapped()) {
			case TelnetCommand cmd when cmd.getCode()==ControlCode.GA -> {
				telnetEvents.add(new BinaryDataEvent(C0Code.RS.code()));
			}
			default -> {
				logger.log(Level.DEBUG, "Telnet event: {0}", event);
				telnetEvents.add(cmdEv);
			}
			};
		}
		default -> {
			if (event instanceof PipeEvent) {
				telnetEvents.add((PipeEvent)event);
			} else {
				logger.log(Level.DEBUG, "Telnet event: {0}", event);
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

}
