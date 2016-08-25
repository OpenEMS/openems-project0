package io.openems.channel.modbus.write;

import com.ghgande.j2mod.modbus.procimg.Register;

import io.openems.channel.modbus.ModbusConnection;
import io.openems.device.protocol.interfaces.WordElement;
import io.openems.element.type.IntegerType;

public class ModbusSingleRegisterWriteRequest extends ModbusWriteRequest {
	private final Register register;

	public ModbusSingleRegisterWriteRequest(int address, Register register) {
		super(address);
		this.register = register;
	}

	public ModbusSingleRegisterWriteRequest(WordElement<?> element, Register register) {
		this(element.getAddress(), register);
	}

	public ModbusSingleRegisterWriteRequest(WordElement<IntegerType> element, IntegerType value) {
		this(element, element.toRegister(value));
	}

	public ModbusSingleRegisterWriteRequest(WordElement<IntegerType> element, int value) {
		this(element, new IntegerType(value));
	}

	public Register getRegister() {
		return register;
	}

	@Override
	public void write(ModbusConnection con, int unitid) throws Exception {
		con.write(unitid, this.getAddress(), this.register);
	}
}
