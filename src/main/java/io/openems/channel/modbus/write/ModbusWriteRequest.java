package io.openems.channel.modbus.write;

import io.openems.channel.modbus.ModbusConnection;

public abstract class ModbusWriteRequest implements Comparable<ModbusWriteRequest> {
	private final int address;

	public ModbusWriteRequest(int address) {
		this.address = address;
	}

	public int getAddress() {
		return address;
	}

	public abstract void write(ModbusConnection con, int unitid) throws Exception;

	@Override
	public int compareTo(ModbusWriteRequest o) {
		return new Integer(address).compareTo(o.getAddress());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ModbusWriteRequest) {
			ModbusWriteRequest o = (ModbusWriteRequest) obj;
			return o.getAddress() == this.getAddress();
		}
		return super.equals(obj);
	}
}
