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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import de.fenecon.openems.monitoring.MonitoringWorker;
import de.fenecon.openems.monitoring.fenecon.FeneconMonitoringWorker;

/**
 * Create an OpenEMS {@link Config} from json
 * 
 * @author Stefan Feilmeier <stefan.feilmeier.@fenecon.de>
 */
public class JsonConfigFactory {
	private static final Logger log = Logger.getLogger(JsonConfigFactory.class.getName());

	private final static File configFile = new File("/etc/openems");
	private final static File configFileDebug = new File("D:/fems/openems/openems");

	public static Config readConfigFromJsonFile() throws Exception {
		JsonObject jsonConfig = readJsonFile();
		String devicekey = getDevicekey(jsonConfig.get("devicekey"));
		HashMap<String, ChannelWorker> channelWorkers = getChannelWorkers(jsonConfig.get("channel"));
		HashMap<String, Device> devices = getDevices(jsonConfig.get("device"));
		registerDevicesToChannels(devices, channelWorkers);
		// HashMap<String, Ess> essDevices = getEssDevices(devices,
		// channelWorkers);
		// HashMap<String, Counter> counters = getCounters(devices,
		// channelWorkers);
		HashMap<String, ControllerWorker> controllers = getControllerWorkers(jsonConfig.get("controller"),
				channelWorkers, devices);
		HashMap<String, MonitoringWorker> monitorings = getMonitoringWorkers(jsonConfig.get("monitoring"), devicekey,
				devices.values());

		// return new Config(devicekey, channelWorkers, essDevices, counters,
		// controllers, monitorings);
		return new Config(devicekey, channelWorkers, devices, controllers, monitorings);
	}

	private static JsonObject readJsonFile() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		File file = getConfigFile();
		log.log(Level.FINE, "Read configuration from " + file.getAbsolutePath());
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(new FileReader(file));
		return jsonElement.getAsJsonObject();
	}

	private static File getConfigFile() {
		if (configFile.exists()) {
			return configFile;
		} else {
			return configFileDebug;
		}
	}

	/**
	 * Get unique devicekey from json config:
	 * 
	 * <pre>
	 * "devicekey": "Hhs49ZDzKuQK4ZxibFic"
	 * </pre>
	 * 
	 * @param jsonElement
	 * @return
	 * @throws Exception
	 */
	private static String getDevicekey(JsonElement jsonElement) throws Exception {
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
	 *     type: "modbus tcp",
	 *     "inetAddress": "10.4.0.15",
	 *     "cycle": 1000
	 *   }
	 * }
	 * </pre>
	 * 
	 * @param jsonElement
	 * @return
	 * @throws UnknownHostException
	 */
	private static HashMap<String, ChannelWorker> getChannelWorkers(JsonElement jsonElement)
			throws UnknownHostException {
		HashMap<String, ChannelWorker> channelWorkers = new HashMap<>();
		if (jsonElement != null && jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				JsonObject obj = entry.getValue().getAsJsonObject();
				ModbusConnection modbusConnection = null;
				switch (obj.get("type").getAsString().toLowerCase()) {
				case "modbus rtu":
					modbusConnection = new ModbusRtuConnection(obj.get("serialinterface").getAsString(),
							obj.get("baudrate").getAsString(), obj.get("databits").getAsInt(),
							obj.get("parity").getAsString(), obj.get("stopbits").getAsInt(),
							obj.get("cycle").getAsInt());
					break;

				case "modbus tcp":
					modbusConnection = new ModbusTcpConnection(
							InetAddress.getByName(obj.get("inetAddress").getAsString()), obj.get("cycle").getAsInt());
					break;

				default:
					throw new UnsupportedOperationException(
							"ModbusType " + obj.get("modbusType").getAsString() + " is not implemented!");
				}

				channelWorkers.put(entry.getKey(), new ModbusChannelWorker(entry.getKey(), modbusConnection));
			}
		}
		return channelWorkers;
	}

	/**
	 * Read {@link Device}s:
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
	 * @param jsonElement
	 * @param modbusWorkers
	 * @return
	 */
	private static HashMap<String, Device> getDevices(JsonElement jsonElement) {
		HashMap<String, Device> devices = new HashMap<>();
		if (jsonElement != null && jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				JsonObject obj = entry.getValue().getAsJsonObject();
				DeviceBuilder devBuilder = new DeviceBuilder().name(entry.getKey());
				if (obj.has("type")) {
					devBuilder.type(obj.get("type").getAsString());
				}
				if (obj.has("protocol")) {
					devBuilder.protocol(obj.get("protocol").getAsString());
				}
				if (obj.has("channel")) {
					devBuilder.channel(obj.get("channel").getAsString());
				}
				if (obj.has("modbusUnit")) {
					devBuilder.modbusUnit(obj.get("modbusUnit").getAsInt());
				}
				Device device = devBuilder.build();
				devices.put(entry.getKey(), device);
			}
		}
		return devices;
	}

	/**
	 * Connect {@link Device}s with their {@link ChannelWorker}
	 * 
	 * @param devices
	 * @param channelWorkers
	 */
	private static void registerDevicesToChannels(HashMap<String, Device> devices,
			HashMap<String, ChannelWorker> channelWorkers) {
		for (Device device : devices.values()) {
			channelWorkers.get(device.getChannel()).registerDevice(device);
		}
	}

	/**
	 * Create Map of {@link Ess}s
	 */
	private static HashMap<String, Ess> getEssDevices(HashMap<String, Device> devices,
			HashMap<String, ChannelWorker> channelWorkers) {
		HashMap<String, Ess> essDevices = new HashMap<String, Ess>();
		for (Entry<String, Device> entry : devices.entrySet()) {
			if (entry.getValue() instanceof Ess) {
				Ess essDevice = (Ess) entry.getValue();
				ChannelWorker worker = channelWorkers.get(essDevice.getChannel());
				worker.registerDevice(essDevice);
			}
		}
		return essDevices;
	}

	/**
	 * Create Map of {@link Counter}s
	 */
	private static HashMap<String, Counter> getCounters(HashMap<String, Device> devices,
			HashMap<String, ChannelWorker> channelWorkers) {
		HashMap<String, Counter> counterDevices = new HashMap<>();
		for (Entry<String, Device> entry : devices.entrySet()) {
			if (entry.getValue() instanceof Counter) {
				Counter counterDevice = (Counter) entry.getValue();
				ChannelWorker worker = channelWorkers.get(counterDevice.getChannel());
				worker.registerDevice(counterDevice);
			}
		}
		return counterDevices;
	}

	/**
	 * Create a {@link Controller} from json config:
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
	 * @return
	 */
	private static HashMap<String, ControllerWorker> getControllerWorkers(JsonElement jsonElement,
			HashMap<String, ChannelWorker> channelWorkers, HashMap<String, Device> devices) {
		HashMap<String, ControllerWorker> controllerWorkers = new HashMap<String, ControllerWorker>();
		if (jsonElement != null && jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				JsonObject obj = entry.getValue().getAsJsonObject();
				ControllerBuilder controllerBuilder = new ControllerBuilder().name(entry.getKey());
				if (obj.has("implementation")) {
					controllerBuilder.implementation(obj.get("implementation").getAsString());
				}
				if (obj.has("chargeFromAc")) {
					controllerBuilder.chargeFromAc(obj.get("chargeFromAc").getAsBoolean());
				}
				if (obj.has("minSoc")) {
					controllerBuilder.minSoc(obj.get("minSoc").getAsInt());
				}
				if (obj.has("gridCounter")) {
					String gridCounter = obj.get("gridCounter").getAsString();
					Device device = devices.get(gridCounter);
					if (device instanceof Counter) {
						controllerBuilder.gridCounter((Counter) device);
					}
				}
				if (obj.has("ess")) {
					JsonArray essJsonArray = obj.get("ess").getAsJsonArray();
					for (JsonElement essJsonElement : essJsonArray) {
						String essDevice = essJsonElement.getAsString();
						Device device = devices.get(essDevice);
						if (device instanceof Ess) {
							controllerBuilder.addEss(essDevice, (Ess) device);
						}
					}
				}
				Controller controller = controllerBuilder.build();
				ControllerWorker controllerWorker = new ControllerWorker(entry.getKey(), channelWorkers.values(),
						controller);
				controllerWorkers.put(entry.getKey(), controllerWorker);
			}
		}
		return controllerWorkers;

	}

	/**
	 * Create {@link MonitoringWorker}s from json config:
	 * 
	 * <pre>
	 * "monitoring": {
	 *   "fenecon": {
	 *     "url": "...",
	 *     "enabled": false
	 *   }
	 * },
	 * </pre>
	 * 
	 * @param jsonElement
	 * @return
	 */
	private static HashMap<String, MonitoringWorker> getMonitoringWorkers(JsonElement jsonElement, String devicekey,
			Collection<Device> devices) {
		HashMap<String, MonitoringWorker> monitoringWorkers = new HashMap<String, MonitoringWorker>();
		// default monitoring
		FeneconMonitoringWorker feneconMonitoring = new FeneconMonitoringWorker(devicekey);
		for (Device device : devices) { // add listener for all elements
			for (String elementName : device.getElements()) {
				device.getElement(elementName).addListener(feneconMonitoring);
			}
		}
		monitoringWorkers.put("fenecon", feneconMonitoring);

		if (jsonElement != null && jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				JsonObject obj = entry.getValue().getAsJsonObject();
				if (obj.has("enabled") && !obj.get("enabled").getAsBoolean()) {
					// remove if monitoring is not enabled but already existing
					// in the map per default
					if (monitoringWorkers.containsKey(entry.getKey())) {
						monitoringWorkers.remove(entry.getKey());
					}
				} else {
					// TODO implement other monitorings or changes from default
					// for fenecon
				}
			}
		}

		return monitoringWorkers;
	}
}