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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ghgande.j2mod.modbus.procimg.Register;
import com.google.gson.JsonElement;

import io.openems.element.Element;
import io.openems.element.type.Type;

public abstract class ModbusElement<T extends Type> extends Element<T> {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(ModbusElement.class);

	protected final int address;
	protected final int length;
	protected ElementRange elementRange = null;

	public ModbusElement(int address, int length, String name, String unit) {
		super(name, unit);
		this.address = address;
		this.length = length;
	}

	public abstract Register[] toRegisters(T value);

	public abstract Register[] toRegisters(JsonElement value);

	public int getAddress() {
		return address;
	}

	public int getLength() {
		return length;
	}

	public void setElementRange(ElementRange elementRange) {
		this.elementRange = elementRange;
	}

	public ElementRange getElementRange() {
		return elementRange;
	}

	@Override
	public String toString() {
		//TODO OSGi 
		return "Element [address=0x" + Integer.toHexString(address) + ", name=" + getName() + ", unit=" + getUnit()
				+ /*", lastUpdate=" + getLastUpdate() + */", value=" + getValue() + "]";
	}
}
