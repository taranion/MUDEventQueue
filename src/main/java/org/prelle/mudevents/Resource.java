package org.prelle.mudevents;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Resource {
	private String definitionKey;
	private int max;
	private int current;
	private int blocked;
	private int exhausted;
	private int damage;
	private ResourceDefinition definition;

	public Resource() {
	}

	public Resource(String definitionKey, int max, int current, int blocked, int exhausted) {
		this.definitionKey = definitionKey;
		this.max = max;
		this.current = current;
		this.blocked = blocked;
		this.exhausted = exhausted;
		this.damage = Math.max(0, max - current - blocked - exhausted);
	}

	public Resource(ResourceDefinition def, int max) {
		this.definition = def;
		this.definitionKey = def != null ? def.getId() : null;
		this.max = max;
		this.current = max;
		this.blocked = 0;
		this.exhausted = 0;
		this.damage = 0;
	}

	public Resource(Resource other) {
		if (other != null) {
			this.definitionKey = other.definitionKey;
			this.max = other.max;
			this.current = other.current;
			this.blocked = other.blocked;
			this.exhausted = other.exhausted;
			this.damage = other.damage;
			this.definition = other.definition;
		}
	}

	public String getName() {
		return definition != null ? definition.getName() : definitionKey;
	}

	public String getShortName() {
		return definition != null ? definition.getShortName() : definitionKey;
	}

	public int getLifeRemaining() {
		return current;
	}
}
