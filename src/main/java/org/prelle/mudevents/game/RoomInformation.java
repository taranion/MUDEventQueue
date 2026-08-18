package org.prelle.mudevents.game;

import java.util.List;
import java.util.Optional;

import org.prelle.mudansi.TextWithMarkup;
import org.prelle.mudevents.MUDEvent;

/**
 * 
 */
public interface RoomInformation extends MUDEvent {
	
	public static record ExitInfo(String directionLabel, String command, String title) {}
	public static record EntityInfo(String command, TextWithMarkup title) {}

    //---------------------------------------------------------------
	public Optional<String> getTitle();

    //---------------------------------------------------------------
	public List<ExitInfo> getExits();

    //---------------------------------------------------------------
    /**
     * Returns a description that is associated with this surrounding.
     *
     * @return String with description - may be NULL
     */
    public Optional<TextWithMarkup> getDescription();

    List<TextWithMarkup> getPlayerCharacterLines();
    List<TextWithMarkup> getOtherMobileCharacterLines();
    List<TextWithMarkup> getItemLines();

}
