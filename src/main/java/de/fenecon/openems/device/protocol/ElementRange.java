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
package de.fenecon.openems.device.protocol;

import java.util.Arrays;

import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.net.SerialConnection;

public class ElementRange {
	private int startAddress;
	private Element<?>[] elements;

	public ElementRange(int startAddress, Element<?>... elements) {
		this.startAddress = startAddress;
		this.elements = elements;
		for (Element<?> element : elements) {
			element.setElementRange(this);
		}
	}

	/*
	 * Returns the total number of words (lengths) of all elements
	 */
	public int getTotalLength() {
		int length = 0;
		for (Element<?> element : elements) {
			length += element.getLength();
		}
		return length;
	}

	public ModbusSerialTransaction getModbusSerialTransaction(SerialConnection serialConnection, int unitid) {
		ModbusSerialTransaction modbusSerialTransaction = null;
		ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(startAddress, getTotalLength());
		req.setUnitID(unitid);
		req.setHeadless();
		modbusSerialTransaction = new ModbusSerialTransaction(serialConnection);
		modbusSerialTransaction.setRequest(req);
		return modbusSerialTransaction;
	}

	public void dispose() {
		// nothing to dispose for now...
	}

	@Override
	public String toString() {
		return "ModbusElementRange [startAddress=" + startAddress + ", words=" + Arrays.toString(elements) + "]";
	}

	public Element<?>[] getElements() {
		return elements;
	}

	public int getStartAddress() {
		return startAddress;
	}
}