package io.openems.channel.modbus;

import java.net.InetAddress;

import com.google.gson.JsonObject;

import io.openems.channel.ChannelFactory;
import io.openems.channel.ChannelWorker;
import io.openems.config.ConfigUtil;
import io.openems.config.exception.ConfigException;

public class ModbusTcpConnectionFactory extends ChannelFactory {

	@Override
	public ChannelWorker getChannelWorker(String name, JsonObject channel) throws ConfigException {
		InetAddress inetAddress = ConfigUtil.getAsInetAddress(channel, "inetAddress");
		int cycle = ConfigUtil.getAsInt(channel, "cycle");
		return new ModbusChannelWorker(name,
				new ModbusTcpConnection(//
						inetAddress, //
						cycle));
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
