package org.prelle.mudevents;

import java.util.List;

public record ResourceDefinitionEvent(
		List<ResourceDefinition> data,
		String[] keysToShow
) implements PipeEvent {
	public ResourceDefinitionEvent {
		data = data != null ? List.copyOf(data) : List.of();
	}
}
