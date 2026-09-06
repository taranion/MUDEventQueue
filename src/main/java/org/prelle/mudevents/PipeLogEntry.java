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
		if (pipe.isSendPipe()) {
			return "-"+tmp+"->";
		} else {
			return "<-"+tmp+"-";
		}
	}

}
