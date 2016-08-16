package io.openems.device;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import io.openems.device.protocol.Element;
import io.openems.device.protocol.interfaces.ElementOnChangeListener;
import io.openems.device.protocol.interfaces.ElementOnUpdateListener;

public abstract class Device {
	private final String channel;
	protected Set<ElementOnUpdateListener> onUpdateListeners = new HashSet<>();
	protected Set<ElementOnChangeListener> onChangeListeners = new HashSet<>();

	public Device(String channel) {
		this.channel = channel;
	}

	public String getChannel() {
		return this.channel;
	}

	public abstract Set<String> getElements();

	public abstract Element<?> getElement(String id);

	public abstract Set<String> getInitElements();

	public abstract Set<String> getMainElements();

	public abstract void init() throws IOException, ParserConfigurationException, SAXException;

	/**
	 * Add a new listener for OnUpdate events.
	 * 
	 * @param listener
	 */
	public void addOnUpdateListener(ElementOnUpdateListener listener) {
		onUpdateListeners.add(listener);
	}

	/**
	 * Add a new listener for OnChange events.
	 * 
	 * @param listener
	 */
	public void addOnChangeListener(ElementOnChangeListener listener) {
		onChangeListeners.add(listener);
	}

	/**
	 * Notify all onUpdateListeners about updated value
	 */
	public void notifyOnUpdateListeners(String fullName, Object newValue) {
		for (ElementOnUpdateListener listener : onUpdateListeners) {
			listener.elementUpdated(fullName, newValue);
		}
	}

	/**
	 * Notify all onChangeListeners about changed value
	 */
	public void notifyOnChangeListeners(String fullName, Object newValue, Object oldValue) {
		for (ElementOnChangeListener listener : onChangeListeners) {
			listener.elementChanged(fullName, newValue, oldValue);
		}
	}

	public abstract String getCurrentDataAsString();
}
