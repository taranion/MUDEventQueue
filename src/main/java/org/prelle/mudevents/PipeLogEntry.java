package org.prelle.mudevents;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 */
@AllArgsConstructor
@Getter
public class PipeLogEntry {
	
	private MUDEventPipeline pipe;
	private MUDEventProcessor from;
	private MUDEventProcessor to;
	private String name;
	private boolean outbound;
	
	public PipeLogEntry(MUDEventPipeline pipe, MUDEventProcessor from, MUDEventProcessor to, String name) {
		this(pipe, from, to, name, pipe != null && pipe.isSendPipe());
	}
	
	public String toString() {
		// Limit name to 8 characters
		String tmp = name;
		if (tmp.indexOf(":")>0) {
			tmp = tmp.substring(tmp.indexOf(":")+1, tmp.length());
		}
		if (tmp.length() > 8) {
			tmp = tmp.substring(0, 8);
		} else if (tmp.length() < 8) {
			// Fill with "-" to 8 characters
			tmp = "-".repeat(8 - tmp.length())+tmp;
		}
		if (outbound) {
			return "-"+tmp+"->";
		} else {
			return "<-"+tmp+"-";
		}
	}

}
