package org.prelle.mudevents.game;

import java.util.List;

import org.prelle.mudevents.PipeEvent;

public record EntityListEvent(
		EntityScope scope,
		List<EntityItem> entities
) implements PipeEvent {
	public EntityListEvent {
		entities = entities != null ? List.copyOf(entities) : List.of();
	}
}
