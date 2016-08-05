package io.openems.device;

import io.openems.device.protocol.Element;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public abstract class Device {
	private final String channel;

	public Device(String channel) {
		this.channel = channel;
	}

	public String getChannel() {
		return this.channel;
	}

	public Set<String> getElements() {
		Set<String> elements = new HashSet<>();
		Set<String> initElements = getInitElements();
		if (initElements != null) {
			elements.addAll(initElements);
		}
		Set<String> mainElements = getMainElements();
		if (mainElements != null) {
			elements.addAll(mainElements);
		}
		return elements;
	}

	public abstract Element<?> getElement(String id);

	public abstract Set<String> getInitElements();

	public abstract Set<String> getMainElements();

	public abstract void init() throws IOException, ParserConfigurationException, SAXException;
}
