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
package io.openems.device.ess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.channel.modbus.WritableModbusDevice;
import io.openems.element.Element;
import io.openems.element.InvalidValueExcecption;
import io.openems.element.type.IntegerType;

public abstract class Ess extends WritableModbusDevice {
	private final static Logger log = LoggerFactory.getLogger(Ess.class);

	protected Element<IntegerType> minSoc = new Element<IntegerType>("minSoc", "%");

	private static final int HYSTERESIS = 10;

	private int lowSocCounter = HYSTERESIS;
	private int lastSoc = 100;

	public Ess(String name, String modbusid, int unitid, int minSoc) {
		super(name, modbusid, unitid);
		this.minSoc.setValue(new IntegerType(minSoc));
	}

	@Override
	public String toString() {
		return "ESS [name=" + name + ", unitid=" + unitid + "]";
	}

	public abstract boolean isOnGrid() throws InvalidValueExcecption;

	public abstract boolean isRunning() throws InvalidValueExcecption;

	public abstract void setActivePower(int power) throws InvalidValueExcecption;

	public abstract int getActivePower() throws InvalidValueExcecption;

	public abstract void setReactivePower(int power) throws InvalidValueExcecption;

	public abstract int getReactivePower() throws InvalidValueExcecption;

	public abstract int getSOC() throws InvalidValueExcecption;

	public abstract int getAllowedCharge() throws InvalidValueExcecption;

	public abstract int getAllowedDischarge() throws InvalidValueExcecption;

	public abstract void start();

	public abstract void stop();

	public abstract int getApparentPower() throws InvalidValueExcecption;

	public abstract int getMaxCapacity();

	public int getMinSoc() throws InvalidValueExcecption {
		return minSoc.getValue().toInteger();
	}

	public void setMinSoc(int minSoc) {
		this.minSoc.setValue(new IntegerType(minSoc));
	}

	public int getUseableSoc() {
		try {
			return getSOC() - getMinSoc();
		} catch (InvalidValueExcecption e) {
			log.error("Soc not valid", e);
			return 0;
		}
	}

	public int getMaxDischargePower() throws InvalidValueExcecption {
		if (getSOC() >= getMinSoc()) {
			// increase the discharge Power slowly
			if (lastSoc < getMinSoc()) {
				lowSocCounter = 0;
			}
			if (lowSocCounter < HYSTERESIS) {
				lowSocCounter++;
			}
		} else {
			// decrease the discharge Power slowly
			if (lastSoc >= getMinSoc()) {
				lowSocCounter = HYSTERESIS;
			}
			if (lowSocCounter > 0) {
				lowSocCounter--;
			}
		}
		lastSoc = getSOC();
		// Calculate discharge power with hysteresis for the minSoc
		return (int) (getAllowedDischarge() / (double) HYSTERESIS * lowSocCounter);
	}

	public int getCapacity() {
		try {
			return getMaxCapacity() / 100 * getSOC();
		} catch (InvalidValueExcecption e) {
			log.error("invalid device data", e);
			return 0;
		}
	}
}
