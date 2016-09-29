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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.channel.ChannelWorker;

public class ControllerWorker extends Thread {
	private final static Logger log = LoggerFactory.getLogger(ControllerWorker.class);

	private final Collection<ChannelWorker> modbusWorkers;
	private final Controller controller;
	private final int cycle;

	public ControllerWorker(String name, Collection<ChannelWorker> modbusWorkers, Controller controller, int cycle) {
		setName(name);
		this.modbusWorkers = modbusWorkers;
		this.controller = controller;
		this.cycle = cycle;
	}

	public int getCycle() {
		return cycle;
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
		Long time;
		while (!isInterrupted()) {
			time = System.currentTimeMillis();
			controller.run();
			try {
				Long sleep = cycle - (System.currentTimeMillis() - time);
				if (sleep > 0) {
					Thread.sleep(sleep);
				} else {
					log.info("elapsed time (" + (System.currentTimeMillis() - time) + ") is larger then cycle time ("
							+ cycle + ")");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		log.info("ControllerWorker {} stopped", getName());
		for (ChannelWorker modbusWorker : modbusWorkers) {
			modbusWorker.interrupt();
		}
	}

	public Controller getController() {
		return controller;
	}

	@Override
	public String toString() {
		return "ControllerWorker [modbusWorkers=" + modbusWorkers + "]";
	}
}
