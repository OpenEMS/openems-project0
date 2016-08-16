package io.openems.channel.modbus;

import io.openems.channel.ChannelFactory;
import io.openems.channel.ChannelWorker;

import com.google.gson.JsonObject;

public class ModbusRtuConnectionFactory extends ChannelFactory {

	@Override
	public ChannelWorker getChannelWorker(String name, JsonObject channel) {
		return new ModbusChannelWorker(name, new ModbusRtuConnection(//
				channel.get("serialinterface").getAsString(), //
				channel.get("baudrate").getAsString(), //
				channel.get("databits").getAsInt(), //
				channel.get("parity").getAsString(), //
				channel.get("stopbits").getAsInt(), //
				channel.get("cycle").getAsInt()));
	}

	@Override
	public JsonObject getConfig(ChannelWorker worker) {
		if (worker instanceof ModbusChannelWorker) {
			ModbusChannelWorker mcw = (ModbusChannelWorker) worker;
			if (mcw.getModbusConnection() instanceof ModbusRtuConnection) {
				ModbusRtuConnection mrc = (ModbusRtuConnection) mcw.getModbusConnection();
				JsonObject obj = new JsonObject();
				obj.addProperty("type", mrc.getClass().getName());
				obj.addProperty("serialinterface", mrc.getSerialinterface());
				obj.addProperty("baudrate", mrc.getBaudrate());
				obj.addProperty("databits", mrc.getDatabits());
				obj.addProperty("parity", mrc.getParity());
				obj.addProperty("stopbits", mrc.getStopbits());
				obj.addProperty("cycle", mrc.getCycle());
				return obj;
			}
		}
		return null;
	}
}
