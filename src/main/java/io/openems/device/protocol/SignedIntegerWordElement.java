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
import java.nio.ByteOrder;

import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;

public class SignedIntegerWordElement extends NumberElement<Integer> implements WordElement {
	final ByteOrder byteOrder;

	public SignedIntegerWordElement(int address, int length, String name, int multiplier, int delta, String unit,
			ByteOrder byteOrder) {
		super(address, length, name, multiplier, delta, unit);
		this.byteOrder = byteOrder;
	}

	@Override
	public void update(Register register) {
		ByteBuffer buff = ByteBuffer.allocate(2).order(byteOrder);
		buff.put(register.toBytes());
		update((buff.order(byteOrder).getShort(0) * multiplier - delta));
	}

	@Override
	public Register[] toRegister(Integer value) {
		byte[] b = ByteBuffer.allocate(2).order(byteOrder)
				.putShort(new Integer((value - delta) / multiplier).shortValue()).array();
		return new Register[] { new SimpleRegister(b[0], b[1]) };
	}
}