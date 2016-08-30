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

import io.openems.device.protocol.interfaces.DoublewordElement;
import io.openems.element.type.LongType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.ghgande.j2mod.modbus.procimg.Register;
import com.google.gson.JsonElement;

public class UnsignedIntegerDoublewordElement extends NumberElement<LongType> implements DoublewordElement {
	final ByteOrder byteOrder;
	final WordOrder wordOrder;

	public UnsignedIntegerDoublewordElement(int address, int length, String name, int multiplier, int delta,
			String unit, ByteOrder byteOrder, WordOrder wordOrder) {
		super(address, length, name, multiplier, delta, unit);
		this.byteOrder = byteOrder;
		this.wordOrder = wordOrder;
	}

	@Override
	public void update(Register reg1, Register reg2) {
		ByteBuffer buff = ByteBuffer.allocate(4).order(byteOrder);
		if (wordOrder == WordOrder.MSWLSW) {
			buff.put(reg1.toBytes());
			buff.put(reg2.toBytes());
		} else {
			buff.put(reg2.toBytes());
			buff.put(reg1.toBytes());
		}
		setValue(new LongType(Integer.toUnsignedLong(buff.getInt(0)) * multiplier - delta));
	}

	@Override
	public Register[] toRegisters(LongType value) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Register[] toRegisters(JsonElement value) {
		LongType l = new LongType(value.getAsLong());
		return toRegisters(l);
	}
}
