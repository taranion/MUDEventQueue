package org.prelle.mudevents.game;

import java.util.List;

public record EntityItem(
	String id,
	String name,
	String iconUrl,
	String type,
	List<EntityAction> actions
) {
	public EntityItem {
		actions = actions != null ? List.copyOf(actions) : List.of();
	}

	public EntityItem(String id, String name, String iconUrl, String type) {
		this(id, name, iconUrl, type, List.of());
	}
}
