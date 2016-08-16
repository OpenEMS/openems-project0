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
package io.openems.channel.modbus;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import io.openems.device.Device;
import io.openems.device.protocol.BitElement;
import io.openems.device.protocol.BitsElement;
import io.openems.device.protocol.ModbusElement;
import io.openems.device.protocol.ModbusProtocol;
import io.openems.element.ElementOnChangeListener;
import io.openems.element.ElementOnUpdateListener;
import io.openems.element.type.Type;

public abstract class ModbusDevice extends Device {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(ModbusDevice.class);

	protected final Integer unitid;
	protected final String name;
	protected ModbusProtocol protocol;

	private ModbusProtocol initProtocol;
	private ModbusProtocol mainProtocol;
	protected ModbusProtocol remainingProtocol;

	private int remainingCounter = 0;

	public ModbusDevice(String name, String channel, int unitid) {
		super(channel);
		this.unitid = unitid;
		this.name = name;

	}

	@Override
	public void init() throws IOException, ParserConfigurationException, SAXException {
		// Initialize protocols
		this.protocol = getProtocol();
		Set<String> allElements = this.protocol.getElementIds();

		// Add listeners for OnUpdate and OnChange events
		for (String id : allElements) {
			ModbusElement<?> e = this.protocol.getElement(id);
			if (e instanceof BitsElement) {
				for (Map.Entry<String, BitElement> element : ((BitsElement) e).getBitElements().entrySet()) {
					addListenersToElement(element.getValue());
				}
			} else {
				addListenersToElement(e);
			}
		}

		this.initProtocol = new ModbusProtocol(); // Init-Protocol
		Set<String> initElements = getInitElements();
		if (initElements != null) {
			for (String id : initElements) {
				initProtocol.addElementRange(protocol.getElement(id).getElementRange());
				allElements.remove(id);
			}
		}
		this.mainProtocol = new ModbusProtocol(); // Main-Protocol
		Set<String> mainElements = getMainElements();
		if (mainElements != null) {
			for (String id : mainElements) {
				mainProtocol.addElementRange(protocol.getElement(id).getElementRange());
				allElements.remove(id);
			}
		}
		this.remainingProtocol = new ModbusProtocol(); // Remaining-Protocol
		if (allElements != null) {
			for (String id : allElements) {
				remainingProtocol.addElementRange(protocol.getElement(id).getElementRange());
				// TODO: split remainingProtocol in small pieces
			}
		}
	}

	/**
	 * Add listeners to an element in order to forward OnUpdate and OnChange
	 * events
	 * 
	 * @param element
	 */
	private void addListenersToElement(ModbusElement<?> element) {
		System.out.println("addListenersToElement " + element);
		element.addOnUpdateListener(new ElementOnUpdateListener() {
			@Override
			public void elementUpdated(String name, Type newValue) {
				notifyOnUpdateListeners(name, newValue);
			}
		});
		element.addOnChangeListener(new ElementOnChangeListener() {
			@Override
			public void elementChanged(String name, Type newValue, Type oldValue) {
				notifyOnChangeListeners(name, newValue, oldValue);
			}
		});
	}

	public String getName() {
		return name;
	}

	public Integer getUnitid() {
		return unitid;
	}

	@Override
	public ModbusElement<?> getElement(String id) {
		return protocol.getElement(id);
	}

	public void executeInitQuery(ModbusConnection modbusConnection) throws Exception {
		modbusConnection.updateProtocol(this.unitid, this.initProtocol);
	}

	public void executeMainQuery(ModbusConnection modbusConnection) throws Exception {
		modbusConnection.updateProtocol(this.unitid, this.mainProtocol);
	}

	public void executeRemainingQuery(ModbusConnection modbusConnection) throws Exception {
		if (remainingProtocol != null && remainingProtocol.getElementRanges().size() > 0) {
			remainingCounter = remainingCounter % remainingProtocol.getElementRanges().size();
			modbusConnection.updateElementRange(this.unitid,
					this.remainingProtocol.getElementRanges().get(remainingCounter));
			remainingCounter++;
		}
	}

	protected abstract ModbusProtocol getProtocol() throws IOException, ParserConfigurationException, SAXException;

	@Override
	public String toString() {
		return "ModbusDevice [name=" + name + ", unitid=" + unitid + "]";
	}

	@Override
	public Set<String> getElements() {
		return protocol.getElementIds();
	}

}
