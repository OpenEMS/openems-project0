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
package de.fenecon.openems.modbus.device;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ghgande.j2mod.modbus.procimg.Register;

import de.fenecon.openems.modbus.ModbusConnection;
import de.fenecon.openems.modbus.protocol.Element;
import de.fenecon.openems.modbus.protocol.ModbusProtocol;

public abstract class WritableModbusDevice extends ModbusDevice {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(WritableModbusDevice.class);

	private final ModbusProtocol writeProtocol;
	protected final Map<Element<?>, Register[]> writeQueue = new HashMap<Element<?>, Register[]>();

	public WritableModbusDevice(String name, String modbusid, int unitid) {
		super(name, modbusid, unitid);

		// Initialize write protocol
		this.writeProtocol = new ModbusProtocol(); // Write-Protocol
		Set<String> writeElements = getWriteElements();
		if (writeElements != null) {
			// TODO writeElements should be removed from remainingElements also
			for (String id : writeElements) {
				writeProtocol.addElementRange(protocol.getElement(id).getElementRange());
			}
		}
	}

	public void addToWriteQueue(Element<?> element, Register[] registers) {
		writeQueue.put(element, registers);
	}

	public void addToWriteQueue(String id, Register[] registers) {
		Element<?> element = writeProtocol.getElement(id);
		if (element != null) {
			writeQueue.put(element, registers);
		}
	}

	public abstract Set<String> getWriteElements();

	public void executeModbusWrite(ModbusConnection modbusConnection) throws Exception {
		for (Entry<Element<?>, Register[]> entry : writeQueue.entrySet()) {
			// TODO: combine writes to one write
			if (entry.getValue().length > 1) {
				/*
				 * log.info("Writing Multiple " + entry.getKey().getName() +
				 * ", 0x" + Integer.toHexString(entry.getKey().getAddress()) +
				 * ", " + entry.getValue()[0].getValue() + ", " +
				 * entry.getValue()[1].getValue());
				 */
				modbusConnection.write(this.unitid, entry.getKey().getAddress(), entry.getValue());
			} else {
				/*
				 * log.info("Writing Single " + entry.getKey().getName() +
				 * ", 0x" + Integer.toHexString(entry.getKey().getAddress()) +
				 * ", " + entry.getValue()[0].getValue());
				 */
				modbusConnection.write(this.unitid, entry.getKey().getAddress(), entry.getValue()[0]);
			}
		}
	}

	@Override
	public String toString() {
		return "ModbusWritableDevice [name=" + name + ", unitid=" + unitid + "]";
	}
}
