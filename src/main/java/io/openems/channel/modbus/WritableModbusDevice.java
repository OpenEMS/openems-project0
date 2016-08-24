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

import io.openems.device.protocol.ElementRange;
import io.openems.device.protocol.ModbusElement;
import io.openems.device.protocol.ModbusProtocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.ghgande.j2mod.modbus.procimg.Register;

public abstract class WritableModbusDevice extends ModbusDevice {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(WritableModbusDevice.class);

	private ModbusProtocol writeProtocol;
	protected final List<ModbusWriteRequest> writeRegisterQueue = new ArrayList<>();
	protected final Map<ModbusElement<?>, Boolean> writeBooleanQueue = new HashMap<ModbusElement<?>, Boolean>();

	public WritableModbusDevice(String name, String channel, int unitid) throws IOException,
			ParserConfigurationException, SAXException {
		super(name, channel, unitid);
	}

	@Override
	public void init() throws IOException, ParserConfigurationException, SAXException {
		super.init();
		// Initialize write protocol
		this.writeProtocol = new ModbusProtocol(); // Write-Protocol
		Set<String> writeElements = getWriteElements();
		if (writeElements != null) {
			// TODO writeElements should be removed from remainingElements also
			for (String id : writeElements) {
				ElementRange er = protocol.getElement(id).getElementRange();
				writeProtocol.addElementRange(er);
				remainingProtocol.removeElementRange(er);
			}
		}
	}

	public void addToWriteQueue(ModbusWriteRequest... requests) {
		for (ModbusWriteRequest req : requests) {
			writeRegisterQueue.add(req);
		}
	}

	public void addToWriteQueue(ModbusElement<?> element, Boolean value) {
		writeBooleanQueue.put(element, value);
	}

	public abstract Set<String> getWriteElements();

	public void executeModbusWrite(ModbusConnection modbusConnection) throws Exception {
		// Write multiple registers (Words) in one write combined by sequential
		// addresses
		HashMap<Integer, ModbusWriteRequest> entries = new HashMap<>();
		int firstAddress = Integer.MAX_VALUE;
		for (ModbusWriteRequest entry : writeRegisterQueue) {
			entries.put(entry.getElement().getAddress(), entry);
			if (firstAddress > entry.getElement().getAddress()) {
				firstAddress = entry.getElement().getAddress();
			}
		}
		int nextAddress = firstAddress;
		HashMap<Integer, Register[]> registerSets = new HashMap<Integer, Register[]>();
		ArrayList<Register> registers = new ArrayList<>();
		int currentStartAddress = firstAddress;
		while (entries.size() > 0) {
			if (entries.containsKey(nextAddress)) {
				ModbusWriteRequest entry = entries.get(nextAddress);
				if (registers.isEmpty()) {
					currentStartAddress = entry.getElement().getAddress();
				}
				Register[] r = entry.getRegisters();
				for (int i = 0; i < r.length; i++) {
					registers.add(r[i]);
				}
				entries.remove(nextAddress);
				nextAddress += entry.getElement().getLength();
			} else {
				// add registers to RegisterSet
				if (!registers.isEmpty()) {
					registerSets.put(currentStartAddress, registers.toArray(new Register[registers.size()]));
					registers.clear();
				} else {
					nextAddress++;
				}
			}
		}
		// Add last registerset to the registerSets collectioin
		if (!registers.isEmpty()) {
			registerSets.put(currentStartAddress, registers.toArray(new Register[registers.size()]));
		}
		// Write each Registerset to the modbusConnection
		for (Entry<Integer, Register[]> entry : registerSets.entrySet()) {
			modbusConnection.write(this.unitid, entry.getKey(), entry.getValue());
		}
		// Write booleans (Coils)
		for (Entry<ModbusElement<?>, Boolean> entry : writeBooleanQueue.entrySet()) {
			modbusConnection.write(this.unitid, entry.getKey().getAddress(), entry.getValue());
		}
	}

	@Override
	public String toString() {
		return "ModbusWritableDevice [name=" + name + ", unitid=" + unitid + "]";
	}
}
