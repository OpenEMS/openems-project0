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

import io.openems.element.type.Type;

public abstract class NumberElement<T extends Type> extends ModbusElement<T> {
	protected final int multiplier;
	protected final int delta;

	public NumberElement(int address, int length, String name, int multiplier, int delta, String unit) {
		super(address, length, name, unit);
		this.multiplier = multiplier;
		this.delta = delta;
	}
}
