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
package de.fenecon.openems;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.fenecon.openems.api.rest.RestWorker;
import de.fenecon.openems.channel.ChannelWorker;
import de.fenecon.openems.config.Config;
import de.fenecon.openems.controller.ControllerWorker;
import de.fenecon.openems.device.Device;
import de.fenecon.openems.monitoring.MonitoringWorker;

/**
 * Main App
 *
 */
public class App {
	private final static Logger log = LoggerFactory.getLogger(App.class);

	private static Map<String, ChannelWorker> channelWorkers = new HashMap<>();
	private static Map<String, ControllerWorker> controllerWorkers = new HashMap<>();
	private static Map<String, MonitoringWorker> monitoringWorkers = new HashMap<>();

	private static Config config = null;

	/**
	 * Main method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		config = new Config(Config.readJsonFile());
		updateConfig(config);
	}

	/**
	 * Starts all workers
	 */
	private static void startWorkers() {
		for (ControllerWorker controllerWorker : controllerWorkers.values()) {
			controllerWorker.start();
		}
		for (MonitoringWorker monitoringWorker : monitoringWorkers.values()) {
			monitoringWorker.start();
		}
		try {
			RestWorker.startWorker();
		} catch (Exception e) {
			log.warn("Unable to start REST-Api");
			e.printStackTrace();
		}
	}

	/**
	 * Stops all workers
	 */
	private static void stopWorkers() {
		for (ControllerWorker controllerWorker : controllerWorkers.values()) {
			controllerWorker.interrupt();
		}
		for (MonitoringWorker monitoringWorker : monitoringWorkers.values()) {
			monitoringWorker.interrupt();
		}
		try {
			RestWorker.stopWorker();
		} catch (Exception e) {
			log.warn("Unable to stop REST-Api");
			e.printStackTrace();
		}
	}

	/**
	 * Applies a new configuration. Restarts all workers.
	 * 
	 * @param config
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public static void updateConfig(Config config) throws IOException, ParserConfigurationException, SAXException {
		stopWorkers();
		channelWorkers = config.getChannelWorkers();
		HashMap<String, Device> devices = config.getDevices();
		config.registerDevicesToChannelWorkers(devices, channelWorkers);
		controllerWorkers = config.getControllerWorkers();
		monitoringWorkers = config.getMonitoringWorkers();
		startWorkers();
	}

	/**
	 * Provides the configuration object
	 * 
	 * @return
	 */
	public static Config getConfig() {
		return config;
	}
}
