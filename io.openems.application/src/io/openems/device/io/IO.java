package io.openems.device.io;

import io.openems.channel.modbus.WritableModbusDevice;

public abstract class IO extends WritableModbusDevice {

	public IO(String name, String channel, int unitid) {
		super(name, channel, unitid);
	}

	@Override
	public String toString() {
		return "Io [name=" + name + ", unitid=" + unitid + "]";
	}

	public abstract void writeDigitalValue(String output, boolean value);

	public abstract boolean readDigitalValue(String channel);
}
