package org.prelle.mudevents.telnet;

import org.prelle.mudevents.PipeEvent;

/**
 * 
 */
public record InputModeEvent(InputMode mode) implements PipeEvent {

	public static enum InputMode {
		LINE_MODE,
		CHARACTER_MODE
	}
}
