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

import io.openems.channel.modbus.ModbusWriteRequest;
import io.openems.element.type.NoneType;

import com.ghgande.j2mod.modbus.procimg.Register;
import com.google.gson.JsonElement;

public class NoneElement extends ModbusElement<NoneType> implements PlaceholderElement {
	public NoneElement(int address, int length, String name) {
		super(address, length, name, "");
	}

	@Override
	public Register[] toRegister(NoneType value) {
		return null;
	}

	@Override
	public ModbusWriteRequest createWriteRequest(NoneType value) {
		return new ModbusWriteRequest(this, toRegister(value));
	}

	@Override
	public ModbusWriteRequest createWriteRequest(JsonElement value) {
		throw new UnsupportedOperationException("not implemented");
	}
}
