package de.fenecon.openems.channel.modbus;

import java.net.InetAddress;

import com.google.gson.JsonObject;

import de.fenecon.openems.channel.ChannelFactory;
import de.fenecon.openems.channel.ChannelWorker;

public class ModbusTcpConnectionFactory extends ChannelFactory {

	@Override
	public ChannelWorker getChannelWorker(String name, JsonObject channel) throws Exception {
		return new ModbusChannelWorker(name, new ModbusTcpConnection(//
				InetAddress.getByName(channel.get("inetAddress").getAsString()), //
				channel.get("cycle").getAsInt()));
	}

}
