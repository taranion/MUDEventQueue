package org.prelle.mudevents.game;

import java.util.List;

public record EntityItem(
	String id,
	String name,
	String iconUrl,
	byte[] imgData,
	String glyph,
	Integer color16, 
	Integer color256,
	String type,
	String keyword,
	List<EntityAction> actions
) {
	
	public EntityItem {
		actions = actions != null ? List.copyOf(actions) : List.of();
	}

	public EntityItem(String id, String name, String iconUrl, byte[] img, String type, String keyword) {
		this(id, name, iconUrl, img, "?", 1, 0, type, keyword, List.of());
	}

	public EntityItem(String id, String name, String icon_url, String type, String keyword, List<EntityAction> actions2) {
		this(id, name, icon_url, null, "?", 1, 0, type, keyword, actions2);
	}
	
	//-------------------------------------------------------------------
	/**
	 * @see java.lang.Record#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object value) {
		if (value instanceof EntityItem other)
			return id.equals(other.id());
		return false;
	}
}
