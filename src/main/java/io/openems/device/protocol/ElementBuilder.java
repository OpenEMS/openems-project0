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

import io.openems.device.counter.CounterProtocol;
import io.openems.device.ess.EssProtocol;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public class ElementBuilder {
	final int address;
	String deviceName = "";
	String name = "";
	ElementType type = ElementType.INTEGER;
	ElementLength length = ElementLength.WORD;
	int intLength = 1;
	int multiplier = 1;
	int delta = 0;
	String unit = "";
	boolean signed = false;
	ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
	WordOrder wordOrder = WordOrder.MSWLSW;
	boolean writable = false;
	Map<String, BitElement> bitElements = new HashMap<>();

	public ElementBuilder(int address) {
		this.address = address;
	}

	public ElementBuilder device(String deviceName) {
		this.deviceName = deviceName;
		return this;
	}

	public ElementBuilder name(String name) {
		this.name = name;
		return this;
	}

	public ElementBuilder name(EssProtocol name) {
		this.name = name.name();
		return this;
	}

	public ElementBuilder name(CounterProtocol name) {
		this.name = name.name();
		return this;
	}

	public ElementBuilder length(ElementLength length) {
		this.length = length;
		if (length == ElementLength.WORD) {
			this.intLength = 1;
		} else if (length == ElementLength.DOUBLEWORD) {
			this.intLength = 2;
		} else {
			this.intLength = 0;
		}
		return this;
	}

	public ElementBuilder intLength(int length) {
		if (intLength == 1) {
			this.length = ElementLength.WORD;
		} else if (intLength == 2) {
			this.length = ElementLength.DOUBLEWORD;
		} else {
			this.length = ElementLength.OTHER;
		}
		this.intLength = length;
		return this;
	}

	public ElementBuilder type(ElementType type) {
		this.type = type;
		return this;
	}

	public ElementBuilder multiplier(int multiplier) {
		this.multiplier = multiplier;
		return this;
	}

	public ElementBuilder delta(int delta) {
		this.delta = delta;
		return this;
	}

	public ElementBuilder unit(String unit) {
		this.unit = unit;
		return this;
	}

	public ElementBuilder signed(boolean signed) {
		this.signed = signed;
		return this;
	}

	public ElementBuilder byteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
		return this;
	}

	public ElementBuilder bit(BitElement bitElement) {
		this.bitElements.put(bitElement.getName(), bitElement);
		return this;
	}

	public ElementBuilder wordOrder(WordOrder wordOrder) {
		this.wordOrder = wordOrder;
		return this;
	}

	public ModbusElement<?> build() {
		ModbusElement<?> element = null;
		if (bitElements.size() > 0) {
			element = new BitsElement(address, intLength, name, unit, bitElements);
		} else if (type == ElementType.INTEGER) {
			if (signed) {
				if (length == ElementLength.WORD) {
					element = new SignedIntegerWordElement(address, intLength, name, multiplier, delta, unit, byteOrder);
				} else if (length == ElementLength.DOUBLEWORD) {
					element = new SignedIntegerDoublewordElement(address, intLength, name, multiplier, delta, unit,
							byteOrder, wordOrder);
				}
			} else {
				if (length == ElementLength.WORD) {
					element = new UnsignedShortWordElement(address, intLength, name, multiplier, delta, unit, byteOrder);
				} else if (length == ElementLength.DOUBLEWORD) {
					element = new UnsignedIntegerDoublewordElement(address, intLength, name, multiplier, delta, unit,
							byteOrder, wordOrder);
				}
			}
			// } else if (type == ElementType.DOUBLE) {
			// return new DoubleElement(address, name, multiplier, delta, unit);
		} else if (type == ElementType.TEXT) {
			throw new UnsupportedOperationException("TEXT is not implemented!");
		} else if (type == ElementType.PLACEHOLDER) {
			element = new NoneElement(address, intLength, name);
		}
		if (element != null) {
			return element;
		} else {
			throw new UnsupportedOperationException("ElementBuilder build for " + type + " is not implemented!");
		}
	}
}
