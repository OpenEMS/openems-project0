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

import io.openems.channel.modbus.ModbusDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusProtocol {
	private final static Logger log = LoggerFactory.getLogger(ModbusProtocol.class);

	private final List<ElementRange> elementRanges = new ArrayList<ElementRange>();
	private final Map<String, ModbusElement<?>> elements = new HashMap<String, ModbusElement<?>>();
	/**
	 * name of the {@link ModbusDevice} the {@link ModbusElement}s in this
	 * {@link ModbusProtocol} belong to
	 */
	private final String deviceName;

	public ModbusProtocol() {
		deviceName = null;
	}

	public ModbusProtocol(String deviceName) {
		this.deviceName = deviceName;
	}

	public void addElementRange(ElementRange elementRange) {
		checkElementRange(elementRange);
		elementRanges.add(elementRange);
		for (ModbusElement<?> element : elementRange.getElements()) {
			if (!(element instanceof NoneElement)) {
				if (deviceName != null) {
					element.setDeviceName(deviceName);
				}
				elements.put(element.getName(), element);
			}
		}
	}

	public ModbusElement<?> getElement(String id) {
		return elements.get(id);
	}

	public Set<String> getElementIds() {
		return new HashSet<String>(elements.keySet());
	}

	public List<ElementRange> getElementRanges() {
		return elementRanges;
	}

	/**
	 * Checks an {@link ElementRange} for plausibility
	 * 
	 * @param elementRange
	 *            to be checked
	 */
	private void checkElementRange(ElementRange elementRange) {
		int address = elementRange.getStartAddress();
		for (ModbusElement<?> element : elementRange.getElements()) {
			if (element.address != address) {
				log.error("Start address of Element {} is wrong. Should be 0x{}", element.getName(),
						Integer.toHexString(address));
			}
			address += element.getLength();
			// TODO: check BitElements
		}
	}

	@Override
	public String toString() {
		return "ModbusProtocol [elements=" + elements + "]";
	}
}
