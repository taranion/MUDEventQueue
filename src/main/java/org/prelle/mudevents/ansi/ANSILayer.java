package org.prelle.mudevents.ansi;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.prelle.ansi.DefaultVT500Listener;
import org.prelle.ansi.VT500Parser;
import org.prelle.mudevents.BinaryDataEvent;
import org.prelle.mudevents.MUDEvent;
import org.prelle.mudevents.MUDEventProcessor;

/**
 * 
 */
public class ANSILayer {
	
	private final static Logger logger = System.getLogger("mud.client.telnet");
	
	private VT500Parser ansi;
	
	private MUDEventProcessor receiveProcessor;
	private MUDEventProcessor sendProcessor;
	
	private DefaultVT500Listener parserListener;

	//-------------------------------------------------------------------
	public ANSILayer() {
		parserListener = new DefaultVT500Listener(StandardCharsets.UTF_8);
		ansi = new VT500Parser(parserListener);
		prepareReceiver();
		prepareSender();
	}

	//-------------------------------------------------------------------
	private void prepareReceiver() {
		receiveProcessor = new MUDEventProcessor() {
			@Override
			public List<MUDEvent> apply(MUDEvent event) {
				logger.log(Level.INFO, "RCV: {0} - {1}", event, event.getClass());
					if (event instanceof BinaryDataEvent binary) {
					for (byte b : binary.getData()) {
						ansi.parse(b & 0xff);
					}
					parserListener.releaseCollectPrintable();
					
					return parserListener.consumeFragments().stream()
							.map(frag -> (MUDEvent)new ANSIEvent(ANSILayer.this,frag))
							.toList();
				} 
				return List.of(event);
			}
			public String getName() {
				return "ANSI";
			}
		};
	}

	//-------------------------------------------------------------------
	private void prepareSender() {
		sendProcessor = new MUDEventProcessor() {
			@Override
			public List<MUDEvent> apply(MUDEvent event) {
				logger.log(Level.INFO, "handle: {0}", event);
				if (event instanceof ANSIEvent ansi) {
					return List.of(new BinaryDataEvent(this,ansi.asRawData()));
				}
				return List.of(event);
			}
			public String getName() {
				return "ANSI";
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

}
