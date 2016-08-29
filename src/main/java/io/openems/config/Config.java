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
package io.openems.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.openems.channel.ChannelFactory;
import io.openems.channel.ChannelWorker;
import io.openems.channel.modbus.ModbusChannelWorker;
import io.openems.config.exception.ConfigException;
import io.openems.config.exception.ConfigInstantiateException;
import io.openems.controller.ControllerFactory;
import io.openems.controller.ControllerWorker;
import io.openems.device.Device;
import io.openems.device.DeviceFactory;
import io.openems.monitoring.MonitorFactory;
import io.openems.monitoring.MonitoringWorker;

public class Config {
	private final static Logger log = LoggerFactory.getLogger(Config.class);
	private final static File configFile = new File("/etc/openems");
	private final static File configFileDebug = new File("D:/fems/openems/openems");
	private final static File configFileDebug2 = new File("C:/Users/matthias.rossmann/Dev/git/openems/openems");

	private HashMap<String, ChannelWorker> channels = new HashMap<>();
	private HashMap<String, Device> devices = new HashMap<>();
	private HashMap<String, ControllerWorker> controllers = new HashMap<>();
	private HashMap<String, MonitoringWorker> monitors = new HashMap<>();

	private HashMap<String, ChannelFactory> channelFactories = new HashMap<>();
	private HashMap<String, DeviceFactory> deviceFactories = new HashMap<>();
	private HashMap<String, ControllerFactory> controllerFactories = new HashMap<>();
	private HashMap<String, MonitorFactory> monitorFactories = new HashMap<>();

	public Config(JsonObject obj) throws ConfigException {
		if (obj.has("channel")) {
			readChannelsFromJson(ConfigUtil.getAsJsonElement(obj, "channel"));
		}
		if (obj.has("device")) {
			readDevicesFromJson(ConfigUtil.getAsJsonElement(obj, "device"));
		}
		if (obj.has("controller")) {
			readControllersFromJson(ConfigUtil.getAsJsonElement(obj, "controller"));
		}
		if (obj.has("monitor")) {
			readMonitorFromJson(ConfigUtil.getAsJsonElement(obj, "monitor"));
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

	/**
	 * Calls the Factory for all channels from json and saves the generated
	 * channel into a hashmap
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
	 * 
	 * @throws ConfigException
	 */
	private void readChannelsFromJson(JsonElement jsonElement) throws ConfigException {
		if (jsonElement != null && jsonElement.isJsonObject()) {
			JsonObject obj = jsonElement.getAsJsonObject();
			for (Entry<String, JsonElement> entry : obj.entrySet()) {
				JsonObject channel = entry.getValue().getAsJsonObject();
				ChannelFactory cf = null;
				String type = ConfigUtil.getAsString(channel, "type");
				if (channelFactories.containsKey(type)) {
					cf = channelFactories.get(type);
				} else {
					String factoryClass = type + "Factory";
					try {
						@SuppressWarnings("unchecked")
						Class<ChannelFactory> factory = (Class<ChannelFactory>) Class.forName(factoryClass);
						cf = factory.newInstance();
						channelFactories.put(type, cf);
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| ClassCastException e) {
						throw new ConfigInstantiateException(factoryClass, jsonElement);
					}
				}
				channels.put(entry.getKey(), cf.getChannelWorker(entry.getKey(), channel));
			}
		}
	}

	/**
	 * Calls the Factory for all monitors from json and saves the generated
	 * monitor into a hashmap
	 * 
	 * @param jsonElement
	 * @throws ConfigException
	 */
	private void readMonitorFromJson(JsonElement jsonElement) throws ConfigException {
		if (jsonElement != null && jsonElement.isJsonObject()) {
			JsonObject obj = jsonElement.getAsJsonObject();
			for (Entry<String, JsonElement> entry : obj.entrySet()) {
				JsonObject monitor = entry.getValue().getAsJsonObject();
				MonitorFactory mf = null;
				String type = ConfigUtil.getAsString(monitor, "type");
				if (monitorFactories.containsKey(type)) {
					mf = monitorFactories.get(type);
				} else {
					String factoryClass = type + "Factory";
					try {
						@SuppressWarnings("unchecked")
						Class<MonitorFactory> factory = (Class<MonitorFactory>) Class.forName(factoryClass);
						mf = factory.newInstance();
						monitorFactories.put(type, mf);
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| ClassCastException e) {
						throw new ConfigInstantiateException(factoryClass, jsonElement);
					}
				}
				monitors.put(entry.getKey(), mf.getMonitoringWorker(entry.getKey(), monitor, devices));
			}
		}
	}

	/**
	 * Calls the Factory for all devices from json and saves the generated
	 * device into a hashmap
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
	 * 
	 * @throws ConfigException
	 */
	private void readDevicesFromJson(JsonElement jsonElement) throws ConfigException {
		if (jsonElement != null && jsonElement.isJsonObject()) {
			JsonObject obj = jsonElement.getAsJsonObject();
			for (Entry<String, JsonElement> entry : obj.entrySet()) {
				JsonObject device = entry.getValue().getAsJsonObject();
				DeviceFactory df = null;
				String type = ConfigUtil.getAsString(device, "type");
				if (deviceFactories.containsKey(type)) {
					df = deviceFactories.get(type);
				} else {
					String factoryClass = type + "Factory";
					try {
						@SuppressWarnings("unchecked")
						Class<DeviceFactory> factory = (Class<DeviceFactory>) Class.forName(factoryClass);
						df = factory.newInstance();
						deviceFactories.put(type, df);
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| ClassCastException e) {
						throw new ConfigInstantiateException(factoryClass, jsonElement);
					}
				}
				devices.put(entry.getKey(), df.getDevice(entry.getKey(), device, channels));
			}
		}
	}

	/**
	 * Calls the Factory for all controllers from json and saves the generated
	 * controller into a hashmap
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
	 * 
	 * @throws ConfigException
	 * 
	 * @throws Exception
	 */
	private void readControllersFromJson(JsonElement jsonElement) throws ConfigException {
		if (jsonElement != null && jsonElement.isJsonObject()) {
			JsonObject obj = jsonElement.getAsJsonObject();
			for (Entry<String, JsonElement> entry : obj.entrySet()) {
				JsonObject controller = entry.getValue().getAsJsonObject();
				ControllerFactory cf = null;
				String type = ConfigUtil.getAsString(controller, "type");
				if (controllerFactories.containsKey(type)) {
					cf = controllerFactories.get(type);
				} else {
					String factoryClass = type + "Factory";
					try {
						@SuppressWarnings("unchecked")
						Class<ControllerFactory> factory = (Class<ControllerFactory>) Class.forName(factoryClass);
						cf = factory.newInstance();
						controllerFactories.put(type, cf);
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| ClassCastException e) {
						throw new ConfigInstantiateException(factoryClass, jsonElement);
					}
				}
				controllers.put(entry.getKey(), cf.getControllerWorker(entry.getKey(), controller, devices, channels));
			}
		}
	}

	/**
	 * returns {@link ModbusChannelWorker}s per channel:
	 */
	public HashMap<String, ChannelWorker> getChannelWorkers() {
		return channels;
	}

	/**
	 * returns {@link Device}s:
	 * 
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public HashMap<String, Device> getDevices() {
		return devices;
	}

	/**
	 * returns {@link Device}s with their {@link ChannelWorker}
	 * 
	 * @param devices
	 * @param channelWorkers
	 */
	public void registerDevicesToChannelWorkers(Map<String, Device> devices,
			Map<String, ChannelWorker> channelWorkers) {
		for (Device device : devices.values()) {
			channelWorkers.get(device.getChannel()).registerDevice(device);
		}
	}

	/**
	 * returns {@link ControllerWorker}s:
	 */
	public Map<String, ControllerWorker> getControllerWorkers() {
		return controllers;
	}

	/**
	 * returns {@link MonitoringWorker}s:
	 */
	public HashMap<String, MonitoringWorker> getMonitoringWorkers() {
		return monitors;
	}

	/**
	 * Provides the Devices as Map of JsonObjects
	 * 
	 * @return
	 */
	public Map<String, JsonObject> getJsonDevices() {
		HashMap<String, JsonObject> jsonDevices = new HashMap<String, JsonObject>();
		for (Map.Entry<String, Device> entry : devices.entrySet()) {
			jsonDevices.put(entry.getKey(),
					deviceFactories.get(entry.getValue().getClass().getName()).getConfig(entry.getValue()));
		}
		return jsonDevices;
	}

	/**
	 * Provides the Controllers as Map of JsonObjects
	 * 
	 * @return
	 */
	public Map<String, JsonObject> getJsonControllers() {
		HashMap<String, JsonObject> jsonDevices = new HashMap<String, JsonObject>();
		for (Map.Entry<String, ControllerWorker> entry : controllers.entrySet()) {
			ControllerFactory cf = controllerFactories.get(entry.getValue().getController().getClass().getName());
			JsonObject obj = cf.getConfig(entry.getValue());
			jsonDevices.put(entry.getKey(), obj);
		}
		return jsonDevices;
	}

	/**
	 * Provides the Channels as Map of JsonObjects
	 * 
	 * @return
	 */
	public Map<String, JsonObject> getJsonCannels() {
		HashMap<String, JsonObject> jsonDevices = new HashMap<String, JsonObject>();
		for (Map.Entry<String, ChannelWorker> entry : channels.entrySet()) {
			ChannelFactory cf = channelFactories
					.get(((ModbusChannelWorker) entry.getValue()).getModbusConnection().getClass().getName());
			JsonObject obj = cf.getConfig(entry.getValue());
			jsonDevices.put(entry.getKey(), obj);
		}
		return jsonDevices;
	}

	/**
	 * Provides the Monitors as Map of JsonObjects
	 * 
	 * @return
	 */
	public Map<String, JsonObject> getJsonMonitors() {
		HashMap<String, JsonObject> jsonDevices = new HashMap<String, JsonObject>();
		for (Map.Entry<String, MonitoringWorker> entry : monitors.entrySet()) {
			MonitorFactory mf = monitorFactories.get(entry.getValue().getClass().getName());
			JsonObject obj = mf.getConfig(entry.getValue());
			jsonDevices.put(entry.getKey(), obj);
		}
		return jsonDevices;
	}

	public JsonObject getConfigAsJson() {
		JsonObject devices = new JsonObject();
		for (Entry<String, JsonObject> entry : getJsonDevices().entrySet()) {
			devices.add(entry.getKey(), entry.getValue());
		}
		JsonObject controllers = new JsonObject();
		for (Entry<String, JsonObject> entry : getJsonControllers().entrySet()) {
			controllers.add(entry.getKey(), entry.getValue());
		}
		JsonObject channels = new JsonObject();
		for (Entry<String, JsonObject> entry : getJsonCannels().entrySet()) {
			channels.add(entry.getKey(), entry.getValue());
		}
		JsonObject monitors = new JsonObject();
		for (Entry<String, JsonObject> entry : getJsonMonitors().entrySet()) {
			monitors.add(entry.getKey(), entry.getValue());
		}
		JsonObject obj = new JsonObject();
		obj.add("channel", channels);
		obj.add("device", devices);
		obj.add("controller", controllers);
		obj.add("monitor", monitors);
		return obj;
	}
}