/*
 * SPDX-FileCopyrightText: 2024 Stefan Prelle <stefan at prelle dot org>
 * SPDX-License-Identifier: MPL-2.0
 */
package org.prelle.mudevents;

import java.util.Locale;

import de.rpgframework.MultiLanguageResourceBundle;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 */
public class ResourceDefinition {
	
	private MultiLanguageResourceBundle RES;
	@Getter @Setter
	private String id;
	
	/** Value from 0 to 15 for the filled color in a 16 color environment */
	@Getter @Setter
	private int color16Full;
	/** Value from 0 to 15 for the empty color  in a 16 color environment */
	@Getter @Setter
	private int color16Empty;
	/** Value from 0 to 255 for the filled color  in a 256 color environment */
	@Getter @Setter
	private int color256Full;
	/** Value from 0 to 255 for the empty color in a 256 color environment */
	@Getter @Setter
	private int color256Empty;
	/** Value from 0 to 255 for the empty color in a 256 color environment */
	@Getter @Setter
	private String rgb;

	@Getter @Setter
	private String name;
	@Getter @Setter
	private String shortName;

	//-------------------------------------------------------------------
	public ResourceDefinition(String id, MultiLanguageResourceBundle res, int col) {
		this.id = id;
		this.RES = res;
		this.color16Full = col;
		this.color16Empty = (col<8)? (col+8) : (col-8);
		this.color256Full = color16Full;
		this.color256Empty = color16Empty;
		this.rgb = "#800000"; // Default black
	}

	//-------------------------------------------------------------------
	public ResourceDefinition(String id, MultiLanguageResourceBundle res, int col, String rgb) {
		this.id = id;
		this.RES = res;
		this.color16Full = col;
		this.color16Empty = (col<8)? (col+8) : (col-8);
		this.color256Full = color16Full;
		this.color256Empty = color16Empty;
		this.rgb = rgb;
	}

	//-------------------------------------------------------------------
	public ResourceDefinition(String id, String name, String shortName, String rgb) {
		this.id = id;
		this.name = name;
		this.shortName = shortName;
		this.rgb = rgb != null ? rgb : "#800000";
		this.color16Full = 7;
		this.color16Empty = 0;
		this.color256Full = 7;
		this.color256Empty = 0;
	}

	//-------------------------------------------------------------------
	public String getShortName() {
		if (RES != null) return RES.getString("resource."+id + ".short");
		return shortName != null ? shortName : id;
	}

	//-------------------------------------------------------------------
	public String getShortName(Locale loc) {
		if (RES != null) return RES.getString("resource."+id + ".short", loc);
		return shortName != null ? shortName : id;
	}

	//-------------------------------------------------------------------
	public String getName() {
		if (RES != null) return RES.getString("resource."+id);
		return name != null ? name : id;
	}

	//-------------------------------------------------------------------
	public String getName(Locale loc) {
		if (RES != null) return RES.getString("resource."+id, loc);
		return name != null ? name : id;
	}

}
