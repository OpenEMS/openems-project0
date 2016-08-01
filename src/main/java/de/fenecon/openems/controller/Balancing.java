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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fenecon.openems.modbus.device.counter.Counter;
import de.fenecon.openems.modbus.device.counter.Socomec;
import de.fenecon.openems.modbus.device.ess.Commercial;
import de.fenecon.openems.modbus.device.ess.Ess;
import de.fenecon.openems.modbus.device.ess.EssProtocol;
import de.fenecon.openems.modbus.protocol.BitElement;
import de.fenecon.openems.modbus.protocol.BitsElement;
import de.fenecon.openems.modbus.protocol.SignedIntegerDoublewordElement;
import de.fenecon.openems.modbus.protocol.SignedIntegerWordElement;
import de.fenecon.openems.modbus.protocol.UnsignedIntegerDoublewordElement;
import de.fenecon.openems.modbus.protocol.UnsignedShortWordElement;

public abstract class Balancing extends Controller {
	private final static Logger log = LoggerFactory.getLogger(Balancing.class);

	private final boolean allowChargeFromAC;
	private final boolean invertedCounter;

	private int minSoc = 10;

	public Balancing(String name, Map<String, Ess> essDevices, Map<String, Counter> counterDevices,
			boolean allowChargeFromAc) {
		super(name, essDevices, counterDevices);
		this.allowChargeFromAC = allowChargeFromAc;
		this.invertedCounter = false;
	}

	// TODO: remove this
	public Balancing(String name, Map<String, Ess> essDevices, Map<String, Counter> counterDevices,
			boolean allowChargeFromAc, boolean invertedCounter) {
		super(name, essDevices, counterDevices);
		this.allowChargeFromAC = allowChargeFromAc;
		this.invertedCounter = invertedCounter;
	}

	public void setMinSoc(int minSoc) {
		this.minSoc = minSoc;
	}

	public int getMinSoc() {
		return minSoc;
	}

	@Override
	public void init() {
		Commercial cess = (Commercial) esss.values().iterator().next();
		// TODO: check class
		// TODO: take all ESS, not only first
		BitsElement bitsElement = (BitsElement) cess.getElement(EssProtocol.SystemState.name());
		BitElement cessRunning = bitsElement.getBit(EssProtocol.SystemStates.Running.name());
		Boolean isCessRunning = cessRunning.getValue();
		if (isCessRunning == null) {
			log.info("No connection to CESS");
		} else if (isCessRunning) {
			log.info("CESS is running");
		} else {
			log.warn("CESS is NOT running!");
			// TODO: activate it
		}
	}

	private int lastSetCessActivePower = 0;
	private int lastCounterActivePower = 0;
	private int lastCessActivePower = 0;
	private int lastDeviationDelta = 0;
	private int lowSocCounter = 0;

	@Override
	public void run() {
		Commercial cess = (Commercial) esss.values().iterator().next();
		// TODO: check class
		// TODO: take all ESS, not only first
		UnsignedShortWordElement cessSoc = cess.getSoc();
		SignedIntegerWordElement cessActivePower = cess.getActivePower();
		SignedIntegerWordElement cessReactivePower = cess.getReactivePower();
		UnsignedShortWordElement cessApparentPower = cess.getApparentPower();
		SignedIntegerWordElement cessAllowedCharge = cess.getAllowedCharge();
		UnsignedShortWordElement cessAllowedDischarge = cess.getAllowedDischarge();
		// UnsignedShortWordElement cessAllowedApparent =
		// cess.getAllowedApparent();
		SignedIntegerWordElement cessSetActivePower = cess.getSetActivePower();
		SignedIntegerWordElement cessPv1OutputPower = cess.getPv1OutputPower();
		SignedIntegerWordElement cessPv2OutputPower = cess.getPv2OutputPower();

		Socomec counter = (Socomec) (counters.get("grid"));
		SignedIntegerDoublewordElement counterActivePower = counter.getActivePower();
		int counterActivePowerValue = counter.getActivePower().getValue();
		if (invertedCounter) {
			counterActivePowerValue *= -1;
			log.info("INVERTED! counterActivePowerValue: " + counterActivePowerValue);
		}
		SignedIntegerDoublewordElement counterReactivePower = counter.getReactivePower();
		UnsignedIntegerDoublewordElement counterApparentPower = counter.getApparentPower();
		UnsignedIntegerDoublewordElement counterActivePostiveEnergy = counter.getActivePositiveEnergy();
		UnsignedIntegerDoublewordElement counterActiveNegativeEnergy = counter.getActiveNegativeEnergy();

		// ess set active power deviation:
		// lastCalculatedCessActivePower = lastCalculatedCessActivePower
		// + (lastCalculatedCessActivePower - cessActivePower.getValue()) / 2;

		int calculatedCessActivePower;

		// } else {
		// normal operation
		// calculatedCessActivePower = (lastCalculatedCessActivePower +
		// counterActivePower.getValue()) / 100 * 100;

		// TODO: removed deviationDelta because offgrid-power is counted in
		// cessActivePower
		// change in ActivePower => deviationDelta
		// int diffCounterActivePower = lastCounterActivePower -
		// counterActivePowerValue;
		// int diffCessActivePower = lastCessActivePower -
		// cessActivePower.getValue();
		int deviationDelta = 0;
		/*
		 * if (counterActivePowerValue < 0) { deviationDelta =
		 * lastDeviationDelta - 100; } else if (counterActivePowerValue < 100) {
		 * deviationDelta = lastDeviationDelta; } else if
		 * (Math.abs(diffCounterActivePower - diffCessActivePower) <= 200) {
		 * deviationDelta = lastDeviationDelta + 100; }
		 */
		lastDeviationDelta = deviationDelta;

		// low SOC hysteresis
		if (cessSoc.getValue() < this.getMinSoc()) {
			if (lowSocCounter < 0) {
				lowSocCounter = 1;
			} else if (lowSocCounter > 3) {
				lowSocCounter = 4;
			} else {
				lowSocCounter++;
			}
		} else {
			if (lowSocCounter > 0) {
				lowSocCounter = -1;
			} else if (lowSocCounter < -3) {
				lowSocCounter = -4;
			} else {
				lowSocCounter--;
			}
		}

		// actual power calculation
		calculatedCessActivePower = (cessActivePower.getValue() + counterActivePowerValue + deviationDelta);

		if (calculatedCessActivePower > 0) {
			// discharge
			if (lowSocCounter > 3) {
				// low soc
				calculatedCessActivePower = 0;
			} else if (lowSocCounter < 3) {
				// normal operation
				if (calculatedCessActivePower > cessAllowedDischarge.getValue()) {
					// not allowed to discharge with such high power
					calculatedCessActivePower = cessAllowedDischarge.getValue();
				} else {
					// discharge with calculated value
				}
			} else {
				System.out.println("vorher: " + calculatedCessActivePower + "; lowSocCounter: " + lowSocCounter);
				calculatedCessActivePower = (int) (calculatedCessActivePower * Math.abs(lowSocCounter) / 3.);
				System.out.println("nachher: " + calculatedCessActivePower);
			}

		} else {
			// charge
			if (allowChargeFromAC) { // charging is allowed
				if (calculatedCessActivePower < cessAllowedCharge.getValue()) {
					// not allowed to charge with such high power
					calculatedCessActivePower = cessAllowedCharge.getValue();
				} else {
					// charge with calculated value
				}
			} else { // charging is not allowed
				calculatedCessActivePower = 0;
			}
		}

		// TODO: safety - remove

		/*
		 * if (calculatedCessActivePower > 30000) { log.info(
		 * "calculatedCessActivePower > 30000: " + calculatedCessActivePower);
		 * calculatedCessActivePower = 30000; } else if
		 * (calculatedCessActivePower < -30000) { log.info(
		 * "calculatedCessActivePower < -30000: " + calculatedCessActivePower);
		 * calculatedCessActivePower = -30000; }
		 */

		// round to 100: cess can only be controlled with precision 100 W
		calculatedCessActivePower = calculatedCessActivePower / 100 * 100;

		cess.addToWriteQueue(cessSetActivePower, cessSetActivePower.toRegister(calculatedCessActivePower));

		lastSetCessActivePower = calculatedCessActivePower;
		lastCessActivePower = cessActivePower.getValue();
		lastCounterActivePower = counterActivePowerValue;

		/*
		 * log.info("[" + cessSoc.readable() + "] PWR: [" +
		 * cessActivePower.readable() + " " + cessReactivePower.readable() + " "
		 * + cessApparentPower.readable() + "] ALLOWED: [" +
		 * cessAllowedCharge.readable() + " " + cessAllowedDischarge.readable()
		 * + " " + cessAllowedApparent.readable() + "] COUNTER: [" +
		 * counterActivePower.readable() + " " + counterReactivePower.readable()
		 * + " " + counterApparentPower.readable() + "] SET: [" +
		 * calculatedCessActivePower + "]");
		 */
		log.info("[" + cessSoc.readable() + "] PWR: [" + cessActivePower.readable() + " " + cessReactivePower.readable()
				+ " " + cessApparentPower.readable() + "] DCPV: [" + cessPv1OutputPower.readable()
				+ cessPv2OutputPower.readable() + "] COUNTER: [" + counterActivePower.readable() + " "
				+ counterReactivePower.readable() + " " + counterApparentPower.readable() + " +"
				+ counterActivePostiveEnergy.readable() + " -" + counterActiveNegativeEnergy.readable() + "] SET: ["
				+ calculatedCessActivePower + "]");
	}

	private int roundTo100(int value) {
		return ((value + 99) / 100) * 100;
	}
}
