package org.prelle.mudevents.game;

import java.net.URL;
import java.nio.file.Path;

import org.prelle.mudevents.PipeEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpeakerImageEvent implements PipeEvent {
	private URL external;
	private byte[] data;
	private String filename;
	private String name;
	private String info;
	private transient Path origFile;
	
	public SpeakerImageEvent(URL external, byte[] data, String filename) {
		this.external = external;
		this.data = data;
		this.filename = filename;
	}
 }
