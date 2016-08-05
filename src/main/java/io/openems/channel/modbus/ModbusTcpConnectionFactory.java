package io.openems.channel.modbus;

import io.openems.channel.ChannelFactory;
import io.openems.channel.ChannelWorker;

import java.net.InetAddress;

import com.google.gson.JsonObject;

public class ModbusTcpConnectionFactory extends ChannelFactory {

	@Override
	public ChannelWorker getChannelWorker(String name, JsonObject channel) throws Exception {
		return new ModbusChannelWorker(name, new ModbusTcpConnection(//
				InetAddress.getByName(channel.get("inetAddress").getAsString()), //
				channel.get("cycle").getAsInt()));
	}

}
