package io.openems.channel.modbus;

import io.openems.device.protocol.ModbusElement;

import com.ghgande.j2mod.modbus.procimg.Register;

public class ModbusWriteRequest {
	private final ModbusElement<?> element;
	private final Register[] registers;

	public ModbusWriteRequest(ModbusElement<?> element, Register[] registers) {
		super();
		this.element = element;
		this.registers = registers;
	}

	public ModbusElement<?> getElement() {
		return element;
	}

	public Register[] getRegisters() {
		return registers;
	}

}
