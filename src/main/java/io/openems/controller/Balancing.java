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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.api.iec.ConnectionListener;
import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.device.counter.Counter;
import io.openems.device.ess.Ess;
import io.openems.device.ess.EssCluster;
import io.openems.element.InvalidValueExcecption;

public class Balancing extends Controller {
	private final static Logger log = LoggerFactory.getLogger(Balancing.class);

	private final Counter gridCounter;
	private final Map<String, Ess> essDevices;
	private final EssCluster cluster;
	private final boolean allowChargeFromAC;

	public Balancing(String name, Counter gridCounter, Map<String, Ess> essDevices, boolean allowChargeFromAc) {
		super(name);
		this.gridCounter = gridCounter;
		this.essDevices = essDevices;
		this.allowChargeFromAC = allowChargeFromAc;
		this.cluster = new EssCluster("", "", 0, 0, new ArrayList<>(essDevices.values()));
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
		try {
			if (cluster.isRunning()) {
				log.info("ESS is running");
			} else {
				cluster.start();
				log.info("ESS is not running. Start ESS");
			}
		} catch (InvalidValueExcecption e) {
			log.error("Failed to run the storage.", e);
		}
	}

	private int lastSetEssActivePower = 0;
	private int lastCounterActivePower = 0;
	private int lastEssActivePower = 0;

	@Override
	public void run() {
		try {

			int calculatedEssActivePower;

			// actual power calculation
			calculatedEssActivePower = cluster.getActivePower() + gridCounter.getActivePower() / 2;

			if (calculatedEssActivePower > 0) {
				// discharge
				try {
					for (Ess ess : essDevices.values()) {
						if (!ess.isRunning() && ess.getSOC() > ess.getMinSoc() + 2) {
							log.warn("ESS is not running. Start ESS");
							ess.start();
						}
					}
				} catch (InvalidValueExcecption e) {
					log.error("can't start some ess", e);
				}
				// check max dischargepower
				if (cluster.getAllowedDischarge() < calculatedEssActivePower) {
					calculatedEssActivePower = cluster.getAllowedDischarge();
				}
			} else {
				// charge
				if (allowChargeFromAC) { // charging is allowed
					// Runn all ess by charging
					try {
						if (!cluster.isRunning()) {
							log.warn("ESS is not running. Start ESS");
							cluster.start();
						}
					} catch (InvalidValueExcecption e) {
						log.error("can't start some ess", e);
					}
					if (calculatedEssActivePower < cluster.getAllowedCharge()) {
						// not allowed to charge with such high power
						calculatedEssActivePower = cluster.getAllowedCharge();
					}
				} else { // charging is not allowed
					calculatedEssActivePower = 0;
				}
			}

			// round to 100: ess can only be controlled with precision 100 W
			calculatedEssActivePower = calculatedEssActivePower / 100 * 100;

			cluster.setActivePower(calculatedEssActivePower);

			lastSetEssActivePower = calculatedEssActivePower;
			lastEssActivePower = cluster.getActivePower();
			lastCounterActivePower = gridCounter.getActivePower();

			log.info(cluster.getCurrentDataAsString() + gridCounter.getCurrentDataAsString() + " SET: ["
					+ calculatedEssActivePower + "]");
		} catch (InvalidValueExcecption e) {
			log.error("The system encountered some invalid values. Set Storage power to zero.", e);
			try {
				cluster.setActivePower(0);
			} catch (InvalidValueExcecption e1) {
				log.error("Error on stoping the storage", e1);
			}
		}
	}

	@Override
	public void handleSetPoint(int function, IeShortFloat informationElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCommand(int function, IeDoubleCommand informationElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IecElementOnChangeListener> createChangeListeners(int startAddressMeassurements,
			int startAddressMessages, ConnectionListener connection) {
		// TODO Auto-generated method stub
		return null;
	}
}
