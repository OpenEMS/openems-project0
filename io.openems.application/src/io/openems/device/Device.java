package io.openems.device;

import io.openems.api.iec.IecControllable;
import io.openems.device.protocol.ModbusElement;
import io.openems.element.ElementOnChangeListener;
import io.openems.element.ElementOnUpdateListener;
import io.openems.element.type.Type;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public abstract class Device implements IecControllable {
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

	public abstract ModbusElement<?> getElement(String id);

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
	public void notifyOnUpdateListeners(String fullName, Type newValue) {
		for (ElementOnUpdateListener listener : onUpdateListeners) {
			listener.elementUpdated(fullName, newValue);
		}
	}

	/**
	 * Notify all onChangeListeners about changed value
	 */
	public void notifyOnChangeListeners(String fullName, Type newValue, Type oldValue) {
		for (ElementOnChangeListener listener : onChangeListeners) {
			listener.elementChanged(fullName, newValue, oldValue);
		}
	}

	public abstract String getCurrentDataAsString();

}
