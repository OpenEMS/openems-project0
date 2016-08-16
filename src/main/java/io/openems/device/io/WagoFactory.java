package io.openems.device.io;

import io.openems.channel.ChannelWorker;
import io.openems.channel.modbus.ModbusChannelWorker;
import io.openems.channel.modbus.ModbusTcpConnection;
import io.openems.device.Device;
import io.openems.device.DeviceFactory;

import java.net.InetAddress;
import java.util.HashMap;

import com.google.gson.JsonObject;

public class WagoFactory extends DeviceFactory {

	@Override
	public Device getDevice(String name, JsonObject device, HashMap<String, ChannelWorker> channels) throws Exception {
		ModbusChannelWorker cw = (ModbusChannelWorker) channels.get(device.get("channel").getAsString());
		InetAddress ip = ((ModbusTcpConnection) cw.getModbusConnection()).getIp();
		Wago wago = new Wago(name, device.get("channel").getAsString(), device.get("modbusUnit").getAsInt(), ip);
		return wago;
	}

	@Override
	public JsonObject getConfig(Device d) {
		if (d instanceof Wago) {
			JsonObject jo = new JsonObject();
			Wago w = (Wago) d;
			jo.addProperty("type", w.getClass().getName());
			jo.addProperty("channel", w.getChannel());
			jo.addProperty("modbusUnit", w.getUnitid());
			return jo;
		}
		return null;
	}

}
