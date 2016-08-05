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
}
