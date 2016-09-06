package io.openems.channel.modbus.write;

import io.openems.channel.modbus.ModbusConnection;

public abstract class ModbusWriteRequest {
	private final int address;

	public ModbusWriteRequest(int address) {
		this.address = address;
	}

	public int getAddress() {
		return address;
	}

	public abstract void write(ModbusConnection con, int unitid) throws Exception;
}
