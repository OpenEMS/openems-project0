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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ghgande.j2mod.modbus.procimg.Register;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.device.protocol.interfaces.DoublewordElement;
import io.openems.device.protocol.interfaces.WordElement;
import io.openems.element.InvalidValueExcecption;
import io.openems.element.type.BooleanMapType;

public class BitsElement extends ModbusElement<BooleanMapType>
		implements WordElement<BooleanMapType>, DoublewordElement {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(BitsElement.class);

	protected final Map<String, BitElement> bitElements;

	public BitsElement(int address, int length, String name, String unit, Map<String, BitElement> bitElements) {
		super(address, length, name, unit);
		this.bitElements = bitElements;
	}

	public BitElement getBit(String id) {
		return bitElements.get(id);
	}

	@Override
	public void update(Register register) {
		for (BitElement bitElement : bitElements.values()) {
			bitElement.update(register);
		}
		// TODO update();
	}

	@Override
	public void update(Register reg1, Register reg2) {
		update(reg1);
		update(reg2);
		// TODO update();
	}

	@Override
	public Register[] toRegisters(BooleanMapType value) {
		throw new UnsupportedOperationException("not implemented");
	}

	public Map<String, BitElement> getBitElements() {
		return bitElements;
	}

	@Override
	public JsonObject getAsJson() throws InvalidValueExcecption {
		JsonObject obj = new JsonObject();
		obj.addProperty("name", getName());
		JsonArray arr = new JsonArray();
		for (BitElement e : bitElements.values()) {
			arr.add(e.getAsJson());
		}
		obj.add("bits", arr);
		return obj;
	}

	@Override
	public Register[] toRegisters(JsonElement value) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Register toRegister(BooleanMapType value) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Register toRegister(JsonElement value) {
		throw new UnsupportedOperationException("not implemented");
	}
}
