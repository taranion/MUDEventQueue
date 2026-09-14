package org.prelle.mudevents.game;

import org.prelle.mudevents.PipeEvent;

public record EntityListRemoveEvent(
		EntityScope scope,
		String id,
		String name
) implements PipeEvent {}
