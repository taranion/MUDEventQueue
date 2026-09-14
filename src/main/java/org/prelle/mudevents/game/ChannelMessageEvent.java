package org.prelle.mudevents.game;

import org.prelle.mudevents.PipeEvent;

public record ChannelMessageEvent(
		String channelId,
		String sender,
		String message
) implements PipeEvent {}
