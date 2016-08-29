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

import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.channel.modbus.write.ModbusWriteRequest;
import io.openems.device.protocol.ModbusProtocol;

public abstract class WritableModbusDevice extends ModbusDevice {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(WritableModbusDevice.class);

	private ModbusProtocol writeProtocol;
	// Queue of ModbusWriteRequests, naturally ordered by address
	protected final Set<ModbusWriteRequest> writeRequestQueue = new TreeSet<>();

	public WritableModbusDevice(String name, String channel, int unitid) {
		super(name, channel, unitid);
	}

	@Override
	public void init() {
		super.init();
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

	public void addToWriteRequestQueue(ModbusWriteRequest req) {
		writeRequestQueue.add(req);
	}

	public abstract Set<String> getWriteElements();

	public void executeModbusWrite(ModbusConnection modbusConnection) throws Exception {
		for (ModbusWriteRequest req : writeRequestQueue) {
			req.write(modbusConnection, this.unitid);
		}
	}

	@Override
	public String toString() {
		return "ModbusWritableDevice [name=" + name + ", unitid=" + unitid + "]";
	}
}
