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

	@Override
	public JsonObject getConfig(ChannelWorker worker) {
		if (worker instanceof ModbusChannelWorker) {
			ModbusChannelWorker mcw = (ModbusChannelWorker) worker;
			if (mcw.getModbusConnection() instanceof ModbusTcpConnection) {
				ModbusTcpConnection mtc = (ModbusTcpConnection) mcw.getModbusConnection();
				JsonObject obj = new JsonObject();
				obj.addProperty("type", mtc.getClass().getName());
				obj.addProperty("inetAddress", mtc.getIp().getHostAddress());
				obj.addProperty("cycle", mtc.getCycle());
				return obj;
			}
		}
		return null;
	}

}
