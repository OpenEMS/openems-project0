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
package de.fenecon.openems.modbus;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fenecon.openems.modbus.device.ModbusDevice;
import de.fenecon.openems.modbus.device.WritableModbusDevice;
import de.fenecon.openems.utils.Mutex;

/**
 * ModbusWorker handles all modbus communication on one channel like
 * /dev/ttyUSB0, /dev/ttyUSB1, eth0-ip0, eth0-ip1,...
 * 
 * @author Stefan Feilmeier
 */
public class ModbusWorker extends Thread {
	private final static Logger log = LoggerFactory.getLogger(ModbusWorker.class);

	private final List<ModbusDevice> devices = new ArrayList<ModbusDevice>();
	private final ModbusConnection modbusConnection;
	private final Mutex initQueryFinished = new Mutex(false);
	private final Mutex mainQueryFinished = new Mutex(false);

	public ModbusWorker(String name, ModbusConnection modbusConnection) {
		this.setName(name);
		this.modbusConnection = modbusConnection;
	}

	public final ModbusConnection getModbusConnection() {
		return modbusConnection;
	}

	public void waitForInit() throws InterruptedException {
		initQueryFinished.await();
	}

	public void waitForMain() throws InterruptedException {
		mainQueryFinished.await();
	}

	/**
	 * Register a new modbus device to this worker
	 * 
	 * @param device
	 */
	public synchronized void registerDevice(ModbusDevice device) {
		synchronized (devices) {
			devices.add(device);
		}
	}

	@Override
	public synchronized void run() {
		log.info("ModbusWorker {} started", getName());
		for (ModbusDevice device : devices) {
			try {
				device.executeInitQuery(modbusConnection);
			} catch (Exception e) {
				log.error("Error while executing modbus query: {}", e.getMessage());
			}
		}

		initQueryFinished.release();

		while (!isInterrupted()) {
			// Execute Modbus Main Queries
			boolean error = false;
			for (ModbusDevice device : devices) {
				try {
					device.executeMainQuery(modbusConnection);
				} catch (Exception e) {
					log.error("Query-Exception: {}", e.getMessage());
					error = true;
				}
			}
			if (!error) {
				mainQueryFinished.release();
			}

			// Execute Modbus Writes
			for (ModbusDevice device : devices) {
				if (device instanceof WritableModbusDevice) {
					try {
						((WritableModbusDevice) device).executeModbusWrite(modbusConnection);
					} catch (Exception e) {
						log.error("Write-Exception: {}", e.getMessage());
					}
				}
			}

			// Execute Next Modbus Queries
			for (ModbusDevice device : devices) {
				try {
					device.executeRemainingQuery(modbusConnection);
				} catch (Exception e) {
					log.error("Query-Exception: {}", e.getMessage());
					e.printStackTrace();
				}
			}

			// Sleep till next cycle
			try {
				Thread.sleep(this.modbusConnection.getCycle());
				// TODO: calculate the difference from method start till now and
				// wait only remaining time
			} catch (InterruptedException e) {
				interrupt();
			}
		}
		log.info("ModbusWorker {} stopped", getName());
	}

	@Override
	public String toString() {
		return "ModbusWorker [modbusConnection=" + modbusConnection + ", devices=" + devices + "]";
	}
}