package org.prelle.mudevents.game;

import org.prelle.mudevents.PipeEvent;

public record EntityListRemoveEvent(
		EntityListScope scope,
		EntityItem item
) implements PipeEvent {

	public EntityListRemoveEvent(EntityListScope scope, String id, String name) {
		this(scope, new EntityItem(id, name,null, (byte[])null, null, null));
	}
	
	public String name() {return item.name(); }
	public String id() { return item.id(); }
}
