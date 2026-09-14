package org.prelle.mudevents.game;

public record EntityAction(
		String name,
		String command,
		String emoji,
		String color
) {
	public EntityAction(String name, String command, String emoji) {
		this(name, command, emoji, null);
	}
}
