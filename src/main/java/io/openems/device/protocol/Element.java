/*
 * OpenEMS - Open Source Energy Management System
 * Copyright (c) 2016 FENECON GmbH & Co. KG
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.openems.device.protocol;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ghgande.j2mod.modbus.procimg.Register;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.channel.modbus.ModbusDevice;
import io.openems.device.protocol.interfaces.ElementOnChangeListener;
import io.openems.device.protocol.interfaces.ElementOnUpdateListener;

public abstract class Element<T> {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(Element.class);

	protected final int address;
	protected final int length;
	protected final String name;
	protected final String unit;
	protected final Period validPeriod;
	protected Set<ElementOnUpdateListener> listenersOnUpdate = new HashSet<>();
	protected Set<ElementOnChangeListener> listenersOnChange = new HashSet<>();

	protected DateTime lastUpdate = null;
	protected T value = null;
	protected ElementRange elementRange = null;

	/**
	 * Name of the {@link ModbusDevice}; filled by {@link ModbusProtocol}
	 * .addElementRange
	 */
	private String deviceName;

	public Element(int address, int length, String name, String unit) {
		this.address = address;
		this.length = length;
		this.name = name;
		this.unit = unit;
		this.validPeriod = new Period(Period.minutes(1));
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getAddress() {
		return address;
	}

	public int getLength() {
		return length;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return deviceName + "." + name;
	}

	public String getUnit() {
		return unit;
	}

	public T getValue() {
		// TODO: check if valid is still valid
		return value;
	}

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
		listenersOnChange.add(listener);
	}

	/**
	 * Notify {@link ElementOnChangeListener}s about changed value
	 */
	private void notifyOnChangeListeners(T oldValue) {
		for (ElementOnChangeListener listener : listenersOnChange) {
			listener.elementChanged(this.getFullName(), this.value, oldValue);
		}
	}

	/**
	 * Returns the raw value, without checking if it is still valid
	 * 
	 * @return unchecked, raw value
	 */
	public T getRawValue() {
		return value;
	}

	public void setElementRange(ElementRange elementRange) {
		this.elementRange = elementRange;
	}

	public ElementRange getElementRange() {
		return elementRange;
	}

	/**
	 * Gets the timestamp of the last update, null if no update ever happened
	 * 
	 * @return last update timestamp
	 */
	public DateTime getLastUpdate() {
		return lastUpdate;
	}

	public abstract Register[] toRegister(T value);

	/**
	 * Updates the lastUpdate timestamp. Always call this method with any
	 * "update" method
	 * 
	 */
	protected void update(T newValue) {
		lastUpdate = DateTime.now();
		T oldValue = newValue;
		this.value = newValue;
		if (oldValue != newValue) {
			// TODO should be a smarter comparison
			notifyOnChangeListeners(oldValue);
		}
		notifyOnUpdateListeners();
	};

	@Override
	public String toString() {
		return "Element [address=0x" + Integer.toHexString(address) + ", name=" + name + ", unit=" + unit
				+ ", lastUpdate=" + lastUpdate + ", value=" + value + "]";
	}

	public String readable() {
		return String.format("%5d %s", value, unit);
	}

	public JsonObject getAsJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("name", this.name);
		obj.addProperty("unit", this.unit);
		obj.addProperty("lastUpdate", lastUpdate.toString());
		obj.addProperty("value", getValue().toString());
		return obj;
	}

	public abstract Register[] toRegister(JsonElement value);
}
