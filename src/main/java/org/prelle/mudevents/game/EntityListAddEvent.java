package org.prelle.mudevents.game;

import org.prelle.mudevents.PipeEvent;

public record EntityListAddEvent(
		EntityScope scope,
		EntityItem item
) implements PipeEvent {}
