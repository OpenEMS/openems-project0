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

import io.openems.device.protocol.interfaces.WordElement;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ghgande.j2mod.modbus.procimg.Register;

/**
 * This represents an Element that is only one bit long.
 * 
 * @author stefan.feilmeier
 */
public class BitElement extends Element<Boolean> implements WordElement {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(BitElement.class);

	public BitElement(int address, String name) {
		super(address, 1, name, "");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Boolean getValue() {
		return value;
	}

	/**
	 * Gets the timestamp of the last update, null if no update ever happened
	 * 
	 * @return last update timestamp
	 */
	@Override
	public DateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String toString() {
		return "Element [address=0x" + Integer.toHexString(address) + ", name=" + name + ", unit=" + unit
				+ ", lastUpdate=" + lastUpdate + ", value=" + value + "]";
	}

	@Override
	public void update(Register register) {
		int position = address % 8;
		byte curByte = register.toBytes()[1 - address / 8];
		update(new Boolean(((curByte >> position) & 1) == 1));
	}

	@Override
	public Register[] toRegister(Boolean value) {
		throw new UnsupportedOperationException("not implemented");
	}
}
