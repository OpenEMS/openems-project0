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

import io.openems.channel.modbus.write.ModbusWriteRequest;
import io.openems.device.protocol.ModbusProtocol;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WritableModbusDevice extends ModbusDevice {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(WritableModbusDevice.class);

	private ModbusProtocol writeProtocol;
	// Queue of ModbusWriteRequests, naturally ordered by address
	protected final Map<Integer, ModbusWriteRequest> writeRequestQueue = new TreeMap<>();

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
		synchronized (writeRequestQueue) {
			writeRequestQueue.put(req.getAddress(), req);
		}
	}

	public abstract Set<String> getWriteElements();

	public void executeModbusWrite(ModbusConnection modbusConnection) throws Exception {
		Exception e = null;
		synchronized (writeRequestQueue) {
			for (Entry<Integer, ModbusWriteRequest> writeRequest : writeRequestQueue.entrySet()) {
				try {
					writeRequest.getValue().write(modbusConnection, this.unitid);
				} catch (Exception ex) {
					log.error("Failed to write address {}", writeRequest.getKey());
					e = ex;
				}
			}
			writeRequestQueue.clear();
		}
		if (e != null) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public String toString() {
		return "ModbusWritableDevice [name=" + name + ", unitid=" + unitid + "]";
	}
}
