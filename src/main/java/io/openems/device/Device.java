package io.openems.device;

import io.openems.device.protocol.Element;
import io.openems.device.protocol.interfaces.ElementUpdateListener;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public abstract class Device {
	private final String channel;
	protected Set<ElementUpdateListener> listeners = new HashSet<>();

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

	public void addListener(ElementUpdateListener listener) {
		listeners.add(listener);
	}

	public void notifyListeners(String fullName, Object value) {
		for (ElementUpdateListener listener : listeners) {
			listener.elementUpdated(fullName, value);
		}
	}
}
