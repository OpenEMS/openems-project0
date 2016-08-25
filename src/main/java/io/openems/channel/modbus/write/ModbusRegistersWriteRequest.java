package io.openems.channel.modbus.write;

import com.ghgande.j2mod.modbus.procimg.Register;

import io.openems.channel.modbus.ModbusConnection;

public class ModbusRegistersWriteRequest extends ModbusWriteRequest {
	private final Register[] registers;

	public ModbusRegistersWriteRequest(int address, Register[] registers) {
		super(address);
		this.registers = registers;
	}

	public Register[] getRegisters() {
		return registers;
	}

	@Override
	public void write(ModbusConnection con, int unitid) throws Exception {
		con.write(unitid, this.getAddress(), this.registers);
	}
}
