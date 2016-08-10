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

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ghgande.j2mod.modbus.procimg.Register;
import com.google.gson.JsonElement;

public class UnsignedShortWordElement extends NumberElement<Integer> implements WordElement {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(UnsignedShortWordElement.class);

	public UnsignedShortWordElement(int address, int length, String name, int multiplier, int delta, String unit) {
		super(address, length, name, multiplier, delta, unit);
	}

	@Override
	public void update(Register register) {
		ByteBuffer buff = ByteBuffer.allocate(2);
		buff.put(register.toBytes());
		update(Short.toUnsignedInt((short) (buff.getShort(0) * multiplier - delta)));
	}

	@Override
	public Register[] toRegister(Integer value) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Register[] toRegister(JsonElement value) {
		Integer i = value.getAsInt();
		return toRegister(i);
	}
}
