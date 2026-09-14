/*
 * SPDX-FileCopyrightText: 2024 Stefan Prelle <stefan at prelle dot org>
 * SPDX-License-Identifier: MPL-2.0
 */
package org.prelle.mudevents;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 */
public class Resource {
	
	/** Matches an ID of a ResourceDefinition */
	private String ref;
	private transient ResourceDefinition definition;
	
	/** Unbuffed healthy maximum of a lifeform */
	@Getter @Setter
	private int max;
	/** Amount the maximum is reduced by status effects like illness */
	@Getter @Setter
	private int blocked;
	/** Light damage, easily recovered */
	@Getter @Setter
	private int exhausted;
	/** Severe damage, needs heal or longer recovery */
	@Getter @Setter
	private int damage;
	/** Temporary points thanks to a buff */
	@Getter @Setter
	private int temporary;

	//-------------------------------------------------------------------
	public Resource(ResourceDefinition def, int max) {
		this.ref=def.getId();
		this.definition = def;
		this.max = max;
		this.blocked = 0;
		this.exhausted = 0;
		this.damage = 0;
		this.temporary = 0;
	}

	//-------------------------------------------------------------------
	public Resource(String ref, int max, int current, int blocked, int exhausted) {
		this.ref = ref;
		this.max = max;
		this.blocked = blocked;
		this.exhausted = exhausted;
		this.damage = Math.max(0, max - blocked - exhausted - current);
		this.temporary = Math.max(0, current - (max - blocked - exhausted - this.damage));
	}

	//-------------------------------------------------------------------
	public String toString() {
		return ref+"="+damage+"/"+max;
	}
	
	//-------------------------------------------------------------------
	public Resource(Resource copy) {
		this.max = copy.max;
		this.blocked = copy.blocked;
		this.exhausted = copy.exhausted;
		this.damage = copy.damage;
		this.temporary = copy.temporary;
	}

	//-------------------------------------------------------------------
	public int getCurrent() {
		return max - blocked - damage - exhausted + temporary;
	}

	//-------------------------------------------------------------------
	public int getLifeRemaining() {
		return max - damage + temporary;
	}

	//-------------------------------------------------------------------
	public String getDefinitionKey() {
		return ref;
	}

	//-------------------------------------------------------------------
	public ResourceDefinition getDefinition() {
		return definition;
	}

	//-------------------------------------------------------------------
	public String getShortName() {
		return definition != null ? definition.getShortName() : ref;
	}

	//-------------------------------------------------------------------
	public String getName() {
		return definition != null ? definition.getName() : ref;
	}

	//-------------------------------------------------------------------
	public void setDefinition(ResourceDefinition value) {
		this.definition = value;
		if (value != null) {
			this.ref = value.getId();
		} else {
			this.ref = null;
		}
		
	}

	//-------------------------------------------------------------------
	public void increaseDamage(int value) {
		damage += value;
	}

	//-------------------------------------------------------------------
	public void healDamage(int value) {
		damage -= value;
		if (damage < 0) {
			damage = 0;
		}
	}
	
}
