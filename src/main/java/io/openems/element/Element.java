package io.openems.element;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joda.time.DateTime;
import org.joda.time.Period;

import com.google.gson.JsonObject;

import io.openems.element.type.Type;

public class Element<T extends Type> {
	private List<ElementOnUpdateListener> listenersOnUpdate = new CopyOnWriteArrayList();
	private List<ElementOnChangeListener> listenersOnChange = new CopyOnWriteArrayList();

	private String deviceName;
	private T value = null;
	private final String name;
	private DateTime lastUpdate = null;
	private final String unit;
	private final Period validPeriod;
	private boolean isValid = true;

	public Element(String name, String unit) {
		this.name = name;
		this.unit = unit;
		this.validPeriod = new Period(Period.minutes(1));
	}

	public String getName() {
		return name;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getFullName() {
		return deviceName + "." + name;
	}

	public String getUnit() {
		return unit;
	}

	public String readable() throws InvalidValueExcecption {
		if (getValue() == null) {
			return "<empty>";
		}
		return getValue().readable() + " " + unit;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public T getValue() throws InvalidValueExcecption {
		if (isValid()) {
			return value;
		} else {
			throw new InvalidValueExcecption("Element " + name + " from device " + deviceName + " is invalid!");
		}
	}

	/**
	 * Gets the timestamp of the last update, null if no update ever happened
	 * 
	 * @return last update timestamp
	 */
	public DateTime getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Returns the raw value, without checking if it is still valid
	 * 
	 * @return unchecked, raw value
	 */
	public T getRawValue() {
		return value;
	}

	/**
	 * Updates the lastUpdate timestamp. Always call this method with any
	 * "update" method
	 * 
	 */
	public void setValue(T newValue) {
		lastUpdate = DateTime.now();
		T oldValue = this.value;
		this.value = newValue;
		if (oldValue == null || !oldValue.isEqual(newValue)) {
			notifyOnChangeListeners(oldValue);
		}
		notifyOnUpdateListeners();
	};

	/**
	 * Add a new listener for OnUpdate events.
	 * 
	 * @param listener
	 */
	public void addOnUpdateListener(ElementOnUpdateListener listener) {
		listenersOnUpdate.add(listener);
	}

	/**
	 * Notify {@link ElementOnUpdateListener}s about updated value
	 */
	private void notifyOnUpdateListeners() {
		for (ElementOnUpdateListener listener : listenersOnUpdate) {
			listener.elementUpdated(this.getFullName(), this.value);
		}
	}

	/**
	 * Add a new listener for OnChange events.
	 * 
	 * @param listener
	 */
	public void addOnChangeListener(ElementOnChangeListener listener) {
		synchronized (listenersOnChange) {
			listenersOnChange.add(listener);
		}
	}

	/**
	 * Remove a added listener for OnChange events.
	 * 
	 * @param listener
	 */
	public void removeOnChangeListener(ElementOnChangeListener listener) {
		synchronized (listenersOnChange) {
			listenersOnChange.remove(listener);
		}
	}

	/**
	 * Notify {@link ElementOnChangeListener}s about changed value
	 */
	private void notifyOnChangeListeners(T oldValue) {
		synchronized (listenersOnChange) {
			for (ElementOnChangeListener listener : listenersOnChange) {
				listener.elementChanged(this.getFullName(), this.value, oldValue);
			}
		}
	}

	public JsonObject getAsJson() throws InvalidValueExcecption {
		JsonObject obj = new JsonObject();
		obj.addProperty("name", this.name);
		obj.addProperty("unit", this.unit);
		obj.addProperty("lastUpdate", lastUpdate.toString());
		obj.addProperty("value", getValue().readable());
		return obj;
	}
}
