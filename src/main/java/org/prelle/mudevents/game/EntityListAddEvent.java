package org.prelle.mudevents.game;

import org.prelle.mudevents.PipeEvent;

public record EntityListAddEvent(
		EntityListScope scope,
		EntityItem item
) implements PipeEvent {}
