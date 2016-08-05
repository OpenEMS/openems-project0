package de.fenecon.openems.channel.modbus;

import com.google.gson.JsonObject;

import de.fenecon.openems.channel.ChannelFactory;
import de.fenecon.openems.channel.ChannelWorker;

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
