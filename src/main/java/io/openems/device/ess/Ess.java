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

import io.openems.channel.modbus.WritableModbusDevice;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public abstract class Ess extends WritableModbusDevice {

	protected int minSoc = 10;

	private static final int HYSTERESIS = 10;

	private int lowSocCounter = HYSTERESIS;
	private int lastSoc = 100;

	public Ess(String name, String modbusid, int unitid, int minSoc) throws IOException, ParserConfigurationException,
			SAXException {
		super(name, modbusid, unitid);
		this.minSoc = minSoc;
	}

	@Override
	public String toString() {
		return "ESS [name=" + name + ", unitid=" + unitid + "]";
	}

	public abstract EssProtocol.GridStates getGridState();

	public abstract void setActivePower(int power);

	public abstract int getActivePower();

	public abstract int getSOC();

	public abstract int getAllowedCharge();

	public abstract int getAllowedDischarge();

	public abstract void start();

	public abstract void stop();

	public int getMinSoc() {
		return minSoc;
	}

	public void setMinSoc(int minSoc) {
		this.minSoc = minSoc;
	}

	public int getUseableSoc() {
		return getSOC() - getMinSoc();
	}

	public int getMaxDischargePower() {
		if (getSOC() >= minSoc) {
			// increase the discharge Power slowly
			if (lastSoc < minSoc) {
				lowSocCounter = 0;
			}
			if (lowSocCounter < HYSTERESIS) {
				lowSocCounter++;
			}
		} else {
			// decrease the discharge Power slowly
			if (lastSoc >= minSoc) {
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
}
