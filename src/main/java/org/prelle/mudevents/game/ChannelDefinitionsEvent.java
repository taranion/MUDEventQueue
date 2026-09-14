package org.prelle.mudevents.game;

import java.util.List;

import org.prelle.mudevents.PipeEvent;

public record ChannelDefinitionsEvent(
		List<ChannelDefinition> channels
) implements PipeEvent {
	public ChannelDefinitionsEvent {
		channels = channels != null ? List.copyOf(channels) : List.of();
	}
}
