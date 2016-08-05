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
package de.fenecon.openems.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.fenecon.openems.channel.ChannelWorker;
import de.fenecon.openems.channel.modbus.ModbusChannelWorker;
import de.fenecon.openems.channel.modbus.ModbusConnection;
import de.fenecon.openems.channel.modbus.ModbusRtuConnection;
import de.fenecon.openems.channel.modbus.ModbusTcpConnection;
import de.fenecon.openems.controller.Controller;
import de.fenecon.openems.controller.ControllerBuilder;
import de.fenecon.openems.controller.ControllerWorker;
import de.fenecon.openems.device.Device;
import de.fenecon.openems.device.DeviceBuilder;
import de.fenecon.openems.device.counter.Counter;
import de.fenecon.openems.device.ess.Ess;
import de.fenecon.openems.device.io.IO;
import de.fenecon.openems.monitoring.MonitoringWorker;
import de.fenecon.openems.monitoring.fenecon.FeneconMonitoringWorker;

public class Config {
	private final static Logger log = LoggerFactory.getLogger(Config.class);
	private final static File configFile = new File("/etc/openems");
	private final static File configFileDebug = new File("D:/fems/openems/openems");
	private final static File configFileDebug2 = new File("C:/Users/matthias.rossmann/Dev/openems");

	private String devicekey;
	private Map<String, JsonObject> jsonChannels = new HashMap<>();
	private Map<String, JsonObject> jsonDevices = new HashMap<>();
	private Map<String, JsonObject> jsonControllers = new HashMap<>();

	public Config(JsonObject obj) throws Exception {
		devicekey = readDevicekeyFromJson(obj.get("devicekey"));
		if (obj.has("channel")) {
			readChannelsFromJson(obj.get("channel").getAsJsonObject());
		}
		if (obj.has("device")) {
			readDevicesFromJson(obj.get("device").getAsJsonObject());
		}
		if (obj.has("controller")) {
			readControllersFromJson(obj.get("controller").getAsJsonObject());
		}
	}

	public static JsonObject readJsonFile() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		File file = getConfigFile();
		log.info("Read configuration from " + file.getAbsolutePath());
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(new FileReader(file));
		return jsonElement.getAsJsonObject();
	}

	private static File getConfigFile() {
		if (configFile.exists()) {
			return configFile;
		} else if (configFileDebug.exists()) {
			return configFileDebug;
		} else {
			return configFileDebug2;
		}
	}

	public String getDevicekey() {
		return devicekey;
	}

	/**
	 * Read all channels from json
	 * 
	 * <pre>
	 * "channel": {
	 *   "usb0": {
	 *     "type": "modbus rtu",
	 *     "serialInterface": "/dev/ttyUSB0",
	 *     "baudrate": "38400",
	 *     "databits": 8,
	 *     "parity": "even",
	 *     "stopbits": 1,
	 *     "cycle": 1000
	 *   },
	 *   "lan0": {
	 *     "type: "modbus tcp",
	 *     "inetAddress": "10.4.0.15",
	 *     "cycle": 1000
	 *   }
	 * }
	 * </pre>
	 */
	private void readChannelsFromJson(JsonElement jsonElement) {
		jsonChannels.clear();
		if (jsonElement != null && jsonElement.isJsonObject()) {
			JsonObject obj = jsonElement.getAsJsonObject();
			for (Entry<String, JsonElement> entry : obj.entrySet()) {
				jsonChannels.put(entry.getKey(), entry.getValue().getAsJsonObject());
			}
		}
	}

	/**
	 * Read all devices from json
	 * 
	 * <pre>
	 * "device": {
	 *   "ess0": {
	 *     "type": "ess",
	 *     "protocol": "FENECON Commercial",
	 *     "channel": "lan0",
	 *     "modbusUnit": 100
	 *   },
	 *   "counter0": {
	 *     "type": "counter",
	 *     "protocol": "Socomec",
	 *     "channel": "usb0",
	 *     "modbusUnit": 5
	 *   }
	 * },
	 * </pre>
	 */
	private void readDevicesFromJson(JsonElement jsonElement) {
		jsonDevices.clear();
		if (jsonElement != null && jsonElement.isJsonObject()) {
			JsonObject obj = jsonElement.getAsJsonObject();
			for (Entry<String, JsonElement> entry : obj.entrySet()) {
				jsonDevices.put(entry.getKey(), entry.getValue().getAsJsonObject());
			}
		}
	}

	/**
	 * Read all controllers from json
	 * 
	 * <pre>
	 * "controller": {
	 *   "controller0": {
	 *     "devices": {
	 *       "ess0",
	 *       "counter0"
	 *     },
	 *     "strategy": {
	 *       "implementation": "balancing",
	 *       "chargeFromAc": false,
	 *       "minSoc": 10,
	 *       "gridCounter": "counter0",
	 *       "ess": [
	 *         "ess0"
	 *       ]
	 *     }
	 *   }
	 * }
	 * </pre>
	 */
	private void readControllersFromJson(JsonElement jsonElement) {
		jsonControllers.clear();
		if (jsonElement != null && jsonElement.isJsonObject()) {
			JsonObject obj = jsonElement.getAsJsonObject();
			for (Entry<String, JsonElement> entry : obj.entrySet()) {
				jsonControllers.put(entry.getKey(), entry.getValue().getAsJsonObject());
			}
		}
	}

	private String readDevicekeyFromJson(JsonElement jsonElement) throws Exception {
		String devicekey = null;
		if (jsonElement != null && jsonElement.isJsonPrimitive()) {
			devicekey = jsonElement.getAsString();
		}
		// TODO: if devicekey is still none: read hostname from device
		if (devicekey == null) {
			throw new Exception("Devicekey is mandatory!");
		}
		return devicekey;
	}

	/**
	 * Create {@link ModbusChannelWorker}s per channel:
	 */
	public HashMap<String, ChannelWorker> getChannelWorkers() throws UnknownHostException {
		HashMap<String, ChannelWorker> channelWorkers = new HashMap<>();
		for (Entry<String, JsonObject> entry : jsonChannels.entrySet()) {
			String name = entry.getKey();
			JsonObject channel = entry.getValue();
			ModbusConnection modbusConnection = null;
			switch (channel.get("type").getAsString().toLowerCase()) {
			case "modbus rtu":
				modbusConnection = new ModbusRtuConnection( //
						channel.get("serialinterface").getAsString(), //
						channel.get("baudrate").getAsString(), //
						channel.get("databits").getAsInt(), //
						channel.get("parity").getAsString(), //
						channel.get("stopbits").getAsInt(), //
						channel.get("cycle").getAsInt());
				break;

			case "modbus tcp":
				modbusConnection = new ModbusTcpConnection(InetAddress.getByName(channel.get("inetAddress")
						.getAsString()), //
						channel.get("cycle").getAsInt());
				break;

			default:
				throw new UnsupportedOperationException("ModbusType " + channel.get("modbusType").getAsString()
						+ " is not implemented!");
			}

			channelWorkers.put(name, new ModbusChannelWorker(name, modbusConnection));
		}
		return channelWorkers;
	}

	/**
	 * Create {@link Device}s:
	 * 
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public HashMap<String, Device> getDevices() throws IOException, ParserConfigurationException, SAXException {
		HashMap<String, Device> devices = new HashMap<>();
		for (Entry<String, JsonObject> entry : jsonDevices.entrySet()) {
			String name = entry.getKey();
			JsonObject deviceJson = entry.getValue();
			DeviceBuilder devBuilder = new DeviceBuilder().name(name);
			if (deviceJson.has("type")) {
				devBuilder.type(deviceJson.get("type").getAsString());
			}
			if (deviceJson.has("protocol")) {
				devBuilder.protocol(deviceJson.get("protocol").getAsString());
			}
			if (deviceJson.has("channel")) {
				devBuilder.channel(deviceJson.get("channel").getAsString());
			}
			if (deviceJson.has("modbusUnit")) {
				devBuilder.modbusUnit(deviceJson.get("modbusUnit").getAsInt());
			}
			if (deviceJson.has("ip")) {
				devBuilder.ip(InetAddress.getByName(deviceJson.get("ip").getAsString()));
			}
			Device device = devBuilder.build();
			devices.put(name, device);
		}
		return devices;
	}

	/**
	 * Connect {@link Device}s with their {@link ChannelWorker}
	 * 
	 * @param devices
	 * @param channelWorkers
	 */
	public void registerDevicesToChannelWorkers(Map<String, Device> devices, Map<String, ChannelWorker> channelWorkers) {
		for (Device device : devices.values()) {
			channelWorkers.get(device.getChannel()).registerDevice(device);
		}
	}

	/**
	 * Create {@link ControllerWorker}s:
	 */
	public HashMap<String, ControllerWorker> getControllerWorkers(Map<String, Device> devices,
			Map<String, ChannelWorker> channelWorkers) {
		HashMap<String, ControllerWorker> controllerWorkers = new HashMap<String, ControllerWorker>();
		for (Entry<String, JsonObject> entry : jsonControllers.entrySet()) {
			String name = entry.getKey();
			JsonObject controllerJson = entry.getValue();
			ControllerBuilder controllerBuilder = new ControllerBuilder().name(name);
			if (controllerJson.has("implementation")) {
				controllerBuilder.implementation(controllerJson.get("implementation").getAsString());
			}
			if (controllerJson.has("chargeFromAc")) {
				controllerBuilder.chargeFromAc(controllerJson.get("chargeFromAc").getAsBoolean());
			}
			if (controllerJson.has("minSoc")) {
				controllerBuilder.minSoc(controllerJson.get("minSoc").getAsInt());
			}
			if (controllerJson.has("gridCounter")) {
				String gridCounter = controllerJson.get("gridCounter").getAsString();
				Device device = devices.get(gridCounter);
				if (device instanceof Counter) {
					controllerBuilder.gridCounter((Counter) device);
				}
			}
			if (controllerJson.has("ess")) {
				JsonArray essJsonArray = controllerJson.get("ess").getAsJsonArray();
				for (JsonElement essJsonElement : essJsonArray) {
					String essDevice = essJsonElement.getAsString();
					Device device = devices.get(essDevice);
					if (device instanceof Ess) {
						controllerBuilder.addEss(essDevice, (Ess) device);
					}
				}
			}
			if (controllerJson.has("io")) {
				JsonArray ioJsonArray = controllerJson.get("io").getAsJsonArray();
				for (JsonElement ioJsonElement : ioJsonArray) {
					String ioDevice = ioJsonElement.getAsString();
					Device device = devices.get(ioDevice);
					if (device instanceof IO) {
						controllerBuilder.addIo(ioDevice, (IO) device);
					}
				}
			}
			Controller controller = controllerBuilder.build();
			ControllerWorker controllerWorker = new ControllerWorker(name, channelWorkers.values(), controller);
			controllerWorkers.put(entry.getKey(), controllerWorker);
		}
		return controllerWorkers;
	}

	/**
	 * Create {@link MonitoringWorker}s:
	 */
	public HashMap<String, MonitoringWorker> getMonitoringWorkers(String devicekey, Map<String, Device> devices) {
		HashMap<String, MonitoringWorker> monitoringWorkers = new HashMap<String, MonitoringWorker>();
		// default monitoring
		FeneconMonitoringWorker feneconMonitoring = new FeneconMonitoringWorker(devicekey);
		for (Device device : devices.values()) { // add listener for all
													// elements
			for (String elementName : device.getElements()) {
				device.getElement(elementName).addListener(feneconMonitoring);
			}
		}
		monitoringWorkers.put("fenecon", feneconMonitoring);

		// TODO implement other monitorings or changes from default
		// TODO remove if fenecon monitoring is disabled in config
		/*
		 * if (jsonElement != null && jsonElement.isJsonObject()) { JsonObject
		 * jsonObject = jsonElement.getAsJsonObject(); for (Entry<String,
		 * JsonElement> entry : jsonObject.entrySet()) { JsonObject obj =
		 * entry.getValue().getAsJsonObject(); if (obj.has("enabled") &&
		 * !obj.get("enabled").getAsBoolean()) { // remove if monitoring is not
		 * enabled but already existing // in the map per default if
		 * (monitoringWorkers.containsKey(entry.getKey())) {
		 * monitoringWorkers.remove(entry.getKey()); } } else { // TODO
		 * implement other monitorings or changes from default // for fenecon }
		 * } }
		 */

		return monitoringWorkers;
	}

	/**
	 * Provides the Devices as Map of JsonObjects
	 * 
	 * @return
	 */
	public Map<String, JsonObject> getJsonDevices() {
		return jsonDevices;
	}
}