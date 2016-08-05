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
package io.openems.channel.modbus;

import io.openems.channel.ChannelWorker;
import io.openems.device.Device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ModbusWorker handles all modbus communication on one channel like
 * /dev/ttyUSB0, /dev/ttyUSB1, eth0-ip0, eth0-ip1,...
 * 
 * @author Stefan Feilmeier
 */
public class ModbusChannelWorker extends ChannelWorker {
	private final static Logger log = LoggerFactory.getLogger(ModbusChannelWorker.class);

	private final ModbusConnection modbusConnection;

	public ModbusChannelWorker(String name, ModbusConnection modbusConnection) {
		this.setName(name);
		this.modbusConnection = modbusConnection;
	}

	public final ModbusConnection getModbusConnection() {
		return modbusConnection;
	}

	long beforeRun = 0;

	@Override
	public synchronized void run() {
		log.info("ModbusWorker {} started: {}", getName(), toString());
		for (Device device : devices) {
			if (device instanceof ModbusDevice) { // TODO: fix polymorphism
				try {
					((ModbusDevice) device).executeInitQuery(modbusConnection);
				} catch (Exception e) {
					log.error("Error while executing modbus query: {}", e.getMessage());
				}
			}
		}

		initQueryFinished.release();

		while (!isInterrupted()) {
			beforeRun = System.nanoTime();
			// Execute Modbus Main Queries
			boolean error = false;
			for (Device device : devices) {
				if (device instanceof ModbusDevice) { // TODO: fix polymorphism
					try {
						((ModbusDevice) device).executeMainQuery(modbusConnection);
					} catch (Exception e) {
						log.error("Query-Exception: {}", e.getMessage());
						error = true;
					}
				}
			}
			if (!error) {
				mainQueryFinished.release();
			}

			// // Execute Next Modbus Queries
			// for (Device device : devices) {
			// if (device instanceof ModbusDevice) { // TODO: fix polymorphism
			// try {
			// ((ModbusDevice) device).executeRemainingQuery(modbusConnection);
			// } catch (Exception e) {
			// log.error("Query-Exception: {}", e.getMessage());
			// e.printStackTrace();
			// }
			// }
			// }

			// Execute Modbus Writes
			// TODO start write only after controller finished (maybe via write
			// queue)
			for (Device device : devices) {
				if (device instanceof WritableModbusDevice) {
					try {
						((WritableModbusDevice) device).executeModbusWrite(modbusConnection);
					} catch (Exception e) {
						log.error("Write-Exception: {}", e.getMessage());
					}
				}
			}

			// Sleep till next cycle
			long elapsedTime = (System.nanoTime() - beforeRun) / 1000000;
			if (elapsedTime > this.modbusConnection.getCycle()) {
				log.info(String.format("elapsed time (%d) is larger then cycle time (%d)", elapsedTime,
						this.modbusConnection.getCycle()));
			} else {
				try {
					Thread.sleep(this.modbusConnection.getCycle() - elapsedTime);
				} catch (InterruptedException e) {
					interrupt();
				}
			}
		}
		log.info("ModbusWorker {} stopped", getName());
	}

	@Override
	public String toString() {
		return "ModbusWorker [modbusConnection=" + modbusConnection + ", devices=" + devices + "]";
	}
}