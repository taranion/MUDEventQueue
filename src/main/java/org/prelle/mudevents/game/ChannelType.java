package org.prelle.mudevents.game;

import com.google.gson.annotations.SerializedName;

/**
 * 
 */
public enum ChannelType {
	/** System channel, for system messages */
	@SerializedName(value = "system", alternate = {"SYSTEM"})
	SYSTEM,
	/** Out-of-character channel, for chatting with other players */
	@SerializedName(value = "ooc", alternate = {"OOC"})
	OOC,
	/** In-character channel, for chatting with other characters in an in-world fashion */
	@SerializedName(value = "ic", alternate = {"IC"})
	IC;

}
