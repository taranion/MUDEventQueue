package org.prelle.mudevents.game;

import java.util.List;

import org.prelle.mudevents.PipeEvent;

public record EntityListEvent(
		EntityListScope scope,
		List<EntityItem> entities
) implements PipeEvent {
	public EntityListEvent {
		entities = entities != null ? List.copyOf(entities) : List.of();
	}

	public EntityListEvent(EntityListScope scope, EntityItem item) {
		this(scope, item != null ? List.of(item) : List.of());
	}
}
