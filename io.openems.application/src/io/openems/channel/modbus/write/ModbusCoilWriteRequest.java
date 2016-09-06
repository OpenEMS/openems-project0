package io.openems.channel.modbus.write;

import io.openems.channel.modbus.ModbusConnection;
import io.openems.device.protocol.BitElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusCoilWriteRequest extends ModbusWriteRequest {
	private final static Logger log = LoggerFactory.getLogger(ModbusCoilWriteRequest.class);
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

	@Override
	public String toString() {
		return "Address: " + this.getAddress() + " Value: " + this.coil;
	}
}
