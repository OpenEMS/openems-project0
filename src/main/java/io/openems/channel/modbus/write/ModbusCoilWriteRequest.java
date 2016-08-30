package io.openems.channel.modbus.write;

import io.openems.channel.modbus.ModbusConnection;
import io.openems.device.protocol.BitElement;

public class ModbusCoilWriteRequest extends ModbusWriteRequest {
	private final boolean coil;

	public ModbusCoilWriteRequest(int address, boolean coil) {
		super(address);
		this.coil = coil;
	}

	public ModbusCoilWriteRequest(BitElement element, boolean value) {
		this(element.getAddress(), value);
	}

	public boolean getCoil() {
		return coil;
	}

	@Override
	public void write(ModbusConnection con, int unitid) throws Exception {
		con.write(unitid, this.getAddress(), this.coil);
	}
}