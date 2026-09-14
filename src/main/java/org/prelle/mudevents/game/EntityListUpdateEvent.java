package org.prelle.mudevents.game;

import org.prelle.mudevents.PipeEvent;

public record EntityListUpdateEvent(
		EntityScope scope,
		EntityItem item
) implements PipeEvent {}
