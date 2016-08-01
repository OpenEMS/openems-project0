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
package de.fenecon.openems.controller;

import java.util.HashMap;
import java.util.Map;

import de.fenecon.openems.modbus.device.counter.Counter;
import de.fenecon.openems.modbus.device.ess.Ess;

public abstract class Controller {
	private static final int HYSTERESIS = 10;
	private final String name;
	protected final Map<String, Ess> esss;
	protected final Map<String, Counter> counters;
	private int lowSocCounter = HYSTERESIS;
	private int lastSoc = 100;
	private int minSoc = 10;

	public Controller(String name, Map<String, Ess> esss, Map<String, Counter> counters) {
		this.name = name;
		if (esss == null) {
			this.esss = new HashMap<String, Ess>();
		} else {
			this.esss = esss;
		}
		if (counters == null) {
			this.counters = new HashMap<String, Counter>();
		} else {
			this.counters = counters;
		}
	}

	public void setMinSoc(int minSoc) {
		this.minSoc = minSoc;
	}

	public int getMinSoc() {
		return minSoc;
	}

	public abstract void init();

	public abstract void run();

	public int calculateMinSocHyisteresis(int calculatedPower, int currentSoc) {
		if (currentSoc >= minSoc) {
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
		lastSoc = currentSoc;
		System.out.println("vorher: " + calculatedPower + "; lowSocCounter: " + lowSocCounter);
		// Calculate discharge power with hysteresis for the minSoc
		calculatedPower = (int) (calculatedPower / (double) HYSTERESIS * lowSocCounter);
		System.out.println("nachher: " + calculatedPower);
		return calculatedPower;
	}
}
