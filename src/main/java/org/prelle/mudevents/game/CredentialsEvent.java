package org.prelle.mudevents.game;

import org.prelle.mudevents.PipeEvent;

/**
 * 
 */
public record CredentialsEvent(String user, String password) implements PipeEvent {

	//-------------------------------------------------------------------

}
