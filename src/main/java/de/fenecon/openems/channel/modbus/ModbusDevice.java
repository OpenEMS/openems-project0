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
package de.fenecon.openems.channel.modbus;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fenecon.openems.device.Device;
import de.fenecon.openems.device.protocol.Element;
import de.fenecon.openems.device.protocol.ModbusProtocol;

public abstract class ModbusDevice extends Device {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(ModbusDevice.class);

	protected final Integer unitid;
	protected final String name;
	protected final ModbusProtocol protocol;

	private final ModbusProtocol initProtocol;
	private final ModbusProtocol mainProtocol;
	protected final ModbusProtocol remainingProtocol;

	public ModbusDevice(String name, String channel, int unitid) {
		super(channel);
		this.unitid = unitid;
		this.name = name;

		// Initialize protocols
		this.protocol = getProtocol();
		Set<String> allElements = this.protocol.getElementIds();
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

	public String getName() {
		return name;
	}

	@Override
	public Element<?> getElement(String id) {
		return protocol.getElement(id);
	}

	public void executeInitQuery(ModbusConnection modbusConnection) throws Exception {
		modbusConnection.updateProtocol(this.unitid, this.initProtocol);
	}

	public void executeMainQuery(ModbusConnection modbusConnection) throws Exception {
		modbusConnection.updateProtocol(this.unitid, this.mainProtocol);
	}

	public void executeRemainingQuery(ModbusConnection modbusConnection) throws Exception {
		modbusConnection.updateProtocol(this.unitid, this.remainingProtocol);
	};

	protected abstract ModbusProtocol getProtocol();

	@Override
	public String toString() {
		return "ModbusDevice [name=" + name + ", unitid=" + unitid + "]";
	}
}
