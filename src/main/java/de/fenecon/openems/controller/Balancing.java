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

import de.fenecon.openems.device.counter.Counter;
import de.fenecon.openems.device.counter.Socomec;
import de.fenecon.openems.device.ess.Commercial;
import de.fenecon.openems.device.ess.Ess;
import de.fenecon.openems.device.ess.EssProtocol;
import de.fenecon.openems.device.protocol.BitElement;
import de.fenecon.openems.device.protocol.BitsElement;
import de.fenecon.openems.device.protocol.SignedIntegerDoublewordElement;
import de.fenecon.openems.device.protocol.SignedIntegerWordElement;
import de.fenecon.openems.device.protocol.UnsignedIntegerDoublewordElement;
import de.fenecon.openems.device.protocol.UnsignedShortWordElement;

public class Balancing extends Controller {
	private final static Logger log = LoggerFactory.getLogger(Balancing.class);

	private final Counter gridCounter;
	private final Map<String, Ess> essDevices;
	private final boolean allowChargeFromAC;

	public Balancing(String name, Counter gridCounter, Map<String, Ess> essDevices, boolean allowChargeFromAc) {
		super(name);
		this.gridCounter = gridCounter;
		this.essDevices = essDevices;
		this.allowChargeFromAC = allowChargeFromAc;
	}

	@Override
	public void init() {
		Commercial cess = (Commercial) essDevices.values().iterator().next();
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
			// Start CESS if not running
			cess.addToWriteQueue(cess.getSetWorkState(), cess.getSetWorkState().toRegister(64));
			log.warn("CESS is not running. Start CESS");
		}
	}

	private int lastSetCessActivePower = 0;
	private int lastCounterActivePower = 0;
	private int lastCessActivePower = 0;
	private int lastDeviationDelta = 0;

	@Override
	public void run() {
		Commercial cess = (Commercial) essDevices.values().iterator().next();
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

		System.out.println("GridState: " + cess.getGridState().name());

		// TODO: check class
		Socomec gridCounterSocomec = (Socomec) gridCounter;
		SignedIntegerDoublewordElement counterActivePower = gridCounterSocomec.getActivePower();
		int counterActivePowerValue = gridCounterSocomec.getActivePower().getValue();
		SignedIntegerDoublewordElement counterReactivePower = gridCounterSocomec.getReactivePower();
		UnsignedIntegerDoublewordElement counterApparentPower = gridCounterSocomec.getApparentPower();
		UnsignedIntegerDoublewordElement counterActivePostiveEnergy = gridCounterSocomec.getActivePositiveEnergy();
		UnsignedIntegerDoublewordElement counterActiveNegativeEnergy = gridCounterSocomec.getActiveNegativeEnergy();

		int calculatedCessActivePower;

		// actual power calculation
		calculatedCessActivePower = cessActivePower.getValue() + counterActivePowerValue;

		if (calculatedCessActivePower > 0) {
			// discharge
			// Calculate discharge power with hysteresis for the minSoc
			calculatedCessActivePower = this.calculateMinSocHyisteresis(calculatedCessActivePower, cessSoc.getValue());
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

		// round to 100: cess can only be controlled with precision 100 W
		calculatedCessActivePower = calculatedCessActivePower / 100 * 100;

		cess.addToWriteQueue(cessSetActivePower, cessSetActivePower.toRegister(calculatedCessActivePower));

		lastSetCessActivePower = calculatedCessActivePower;
		lastCessActivePower = cessActivePower.getValue();
		lastCounterActivePower = counterActivePowerValue;

		log.info("[" + cessSoc.readable() + "] PWR: [" + cessActivePower.readable() + " "
				+ cessReactivePower.readable() + " " + cessApparentPower.readable() + "] DCPV: ["
				+ cessPv1OutputPower.readable() + cessPv2OutputPower.readable() + "] COUNTER: ["
				+ counterActivePower.readable() + " " + counterReactivePower.readable() + " "
				+ counterApparentPower.readable() + " +" + counterActivePostiveEnergy.readable() + " -"
				+ counterActiveNegativeEnergy.readable() + "] SET: [" + calculatedCessActivePower + "]");
	}
}
