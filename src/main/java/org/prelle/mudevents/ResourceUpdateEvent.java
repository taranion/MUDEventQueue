package org.prelle.mudevents;

import java.util.Map;

public record ResourceUpdateEvent(
		Map<String, Resource> resources
) implements PipeEvent {
	public ResourceUpdateEvent {
		resources = resources != null ? Map.copyOf(resources) : Map.of();
	}
}
