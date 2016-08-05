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

import io.openems.channel.ChannelWorker;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerWorker extends Thread {
	private final static Logger log = LoggerFactory.getLogger(ControllerWorker.class);

	private final Collection<ChannelWorker> modbusWorkers;
	private final Controller controller;

	public ControllerWorker(String name, Collection<ChannelWorker> modbusWorkers, Controller controller) {
		setName(name);
		this.modbusWorkers = modbusWorkers;
		this.controller = controller;
	}

	@Override
	public void run() {
		log.info("ControllerWorker {} started", getName());
		// Initialize ModbusWorkers
		for (ChannelWorker modbusWorker : modbusWorkers) {
			modbusWorker.start();
		}
		for (ChannelWorker modbusWorker : modbusWorkers) {
			try {
				modbusWorker.waitForInit();
			} catch (InterruptedException e) {
				interrupt();
			}
		}
		controller.init();

		while (!isInterrupted()) {
			try {
				for (ChannelWorker modbusWorker : modbusWorkers) {
					modbusWorker.waitForMain();
				}
				controller.run();
			} catch (InterruptedException e) {
				interrupt();
			}
		}
		log.info("ControllerWorker {} stopped", getName());
	}

	@Override
	public String toString() {
		return "ControllerWorker [modbusWorkers=" + modbusWorkers + "]";
	}
}
