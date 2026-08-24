package org.prelle.mudevents.ansi;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.prelle.ansi.DefaultVT500Listener;
import org.prelle.ansi.VT500Parser;
import org.prelle.mudevents.BinaryDataEvent;
import org.prelle.mudevents.PipeEvent;
import org.prelle.mudevents.MUDEventProcessor;

/**
 * 
 */
public class ANSILayer implements MUDEventProcessor {
	
	private final static Logger logger = System.getLogger("mud.events.telnet");
	
	private VT500Parser ansi;
	
	private DefaultVT500Listener parserListener;

	//-------------------------------------------------------------------
	public ANSILayer() {
		parserListener = new DefaultVT500Listener(StandardCharsets.UTF_8);
		ansi = new VT500Parser(parserListener);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.mudevents.MUDEventProcessor#onReceiveFromRemote(org.prelle.mudevents.PipeEvent)
	 */
	@Override
	public List<PipeEvent> onReceiveFromRemote(PipeEvent event) {
		if (event instanceof BinaryDataEvent binary) {
		for (byte b : binary.getData()) {
			ansi.parse(b & 0xff);
		}
		parserListener.releaseCollectPrintable();
		
		return parserListener.consumeFragments().stream()
				.map(frag -> (PipeEvent)new ANSIEvent(frag))
				.toList();
		} 
		return List.of(event);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.mudevents.MUDEventProcessor#onSendToRemote(org.prelle.mudevents.PipeEvent)
	 */
	@Override
	public List<PipeEvent> onSendToRemote(PipeEvent event) {
		logger.log(Level.INFO, "handle: {0}", event);
		if (event instanceof ANSIEvent ansi) {
			return List.of(new BinaryDataEvent(ansi.asRawData()));
		}
		return List.of(event);
	}

}
