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
package io.openems.controller;

import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.device.counter.Counter;
import io.openems.device.ess.Ess;
import io.openems.device.ess.EssProtocol;
import io.openems.device.protocol.BitElement;
import io.openems.device.protocol.BitsElement;

import java.util.List;
import java.util.Map;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.IeShortFloat;
import org.openmuc.j60870.InformationElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public Counter getGridCounter() {
		return gridCounter;
	}

	public Map<String, Ess> getEssDevices() {
		return essDevices;
	}

	public boolean isAllowChargeFromAC() {
		return allowChargeFromAC;
	}

	@Override
	public void init() {
		for (Ess ess : essDevices.values()) {
			BitsElement bitsElement = (BitsElement) ess.getElement(EssProtocol.SystemState.name());
			BitElement essRunning = bitsElement.getBit(EssProtocol.SystemStates.Running.name());
			if (essRunning == null) {
				log.info("No connection to ESS");
			} else {
				boolean isEssRunning = essRunning.getValue().toBoolean();
				if (isEssRunning) {
					log.info("ESS is running");
				} else {
					// Start ESS if not running
					ess.start();
					log.info("ESS is not running. Start ESS");
				}
			}
		}
	}

	private int lastSetEssActivePower = 0;
	private int lastCounterActivePower = 0;
	private int lastEssActivePower = 0;

	@Override
	public void run() {
		Ess ess = essDevices.values().iterator().next();

		int calculatedEssActivePower;

		// actual power calculation
		calculatedEssActivePower = ess.getActivePower() + gridCounter.getActivePower();

		if (calculatedEssActivePower > 0) {
			// discharge
			// Calculate discharge power with hysteresis for the minSoc
			if (ess.getMaxDischargePower() < calculatedEssActivePower) {
				calculatedEssActivePower = ess.getMaxDischargePower();
			}
		} else {
			// charge
			if (allowChargeFromAC) { // charging is allowed
				if (calculatedEssActivePower < ess.getAllowedCharge()) {
					// not allowed to charge with such high power
					calculatedEssActivePower = ess.getAllowedCharge();
				} else {
					// charge with calculated value
				}
			} else { // charging is not allowed
				calculatedEssActivePower = 0;
			}
		}

		// round to 100: ess can only be controlled with precision 100 W
		calculatedEssActivePower = calculatedEssActivePower / 100 * 100;

		ess.setActivePower(calculatedEssActivePower);

		lastSetEssActivePower = calculatedEssActivePower;
		lastEssActivePower = ess.getActivePower();
		lastCounterActivePower = gridCounter.getActivePower();

		log.info(ess.getCurrentDataAsString() + gridCounter.getCurrentDataAsString() + " SET: ["
				+ calculatedEssActivePower + "]");
	}

	@Override
	public void handleSetPoint(int function, IeShortFloat informationElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCommand(int function, InformationElement informationElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IecElementOnChangeListener> createChangeListeners(int startAddressMeassurements,
			int startAddressMessages, Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}
}
