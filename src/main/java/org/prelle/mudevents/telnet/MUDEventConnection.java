package org.prelle.mudevents.telnet;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.InetAddress;
import java.util.List;

import org.prelle.mudevents.MUDEventPipeline;
import org.prelle.mudevents.MUDEventProcessor;
import org.prelle.mudevents.PipeEvent;
import org.prelle.telnet.TelnetSocket;
import org.prelle.telnet.TelnetSocketListener;
import org.prelle.telnet.event.TelnetEvent;
import org.prelle.telnet.option.TelnetOption;

/**
 * 
 */
public class MUDEventConnection implements TelnetSocketListener, MUDEventProcessor {
	
	private final static Logger logger = System.getLogger("mudevents");
	
	private TelnetSocket socket;
	private MUDEventPipeline rcvPipeline;
	private MUDEventPipeline sndPipeline;

	//-------------------------------------------------------------------
	/**
	 */
	public MUDEventConnection(TelnetSocket socket) {
		this.socket = socket;
		socket.getStack().setEventFactory(MUDEventsTelnetEventFactory.INSTANCE);
		rcvPipeline = new MUDEventPipeline(socket.getInetAddress().getHostAddress());
		sndPipeline = new MUDEventPipeline("SND");
		sndPipeline.then(this);
		
		// Install all telnet options
		socket.getStack().getExtensions().forEach( ext -> {
			if (ext instanceof MUDEventProcessor proc) {
				rcvPipeline.then(proc);
				proc.setReversePipeline(sndPipeline);
			}
		});
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.TelnetSocketListener#onTelnetEvent(org.prelle.telnet.event.TelnetEvent)
	 */
	@Override
	public void onTelnetEvent(TelnetEvent event) {
		// TODO Auto-generated method stub
		logger.log(Level.INFO, "onTelnetEvent: {0}", event);
		
		PipeEvent event2 = (PipeEvent)event;
		rcvPipeline.publish(event2);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.TelnetSocketListener#optionStateChanged(org.prelle.telnet.option.TelnetOption, boolean)
	 */
	@Override
	public void optionStateChanged(TelnetOption extension, boolean active) {
		// TODO Auto-generated method stub
		logger.log(Level.INFO, "optionStateChanged: {0} is now {1}", extension.getName(), active ? "active" : "inactive");
		MEOptionState state = new MEOptionState(extension, active);
		rcvPipeline.publish(state);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.telnet.TelnetSocketListener#telnetReady()
	 */
	@Override
	public void telnetReady() {
		// TODO Auto-generated method stub
		logger.log(Level.INFO, "telnetReady");
	}

	//-------------------------------------------------------------------
	/**
	 * 
	 * @see org.prelle.mudevents.MUDEventProcessor#apply(org.prelle.mudevents.PipeEvent)
	 */
	@Override
	public List<PipeEvent> onReceiveFromRemote(PipeEvent event) {
		// TODO Auto-generated method stub
		logger.log(Level.INFO, "apply: {0}", event);
		return null;
	}

	//-------------------------------------------------------------------
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	//-------------------------------------------------------------------
	public int getPort() {
		return socket.getPort();
	}

	//-------------------------------------------------------------------
	public void start() {
		socket.start();

	}

	//-------------------------------------------------------------------
	public MUDEventPipeline getRcvPipeline() {
		return rcvPipeline;
	}

	//-------------------------------------------------------------------
	public MUDEventPipeline getSndPipeline() {
		return sndPipeline;
	}

	@Override
	public List<PipeEvent> onSendToRemote(PipeEvent event) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
