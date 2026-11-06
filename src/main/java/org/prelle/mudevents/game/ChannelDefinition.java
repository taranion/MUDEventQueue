package org.prelle.mudevents.game;

public record ChannelDefinition(
		String id,
		String name,
		ChannelType type,
		String colorRGB,
		Integer colorANSI
) {
	public ChannelDefinition(String id, String name, String colorRGB, Integer colorANSI) {
		this(id, name, ChannelType.SYSTEM, colorRGB, colorANSI);
	}

	public ChannelDefinition(String id, String name, String colorRGB, Integer colorANSI, ChannelType type) {
		this(id, name, type, colorRGB, colorANSI);
	}
}
