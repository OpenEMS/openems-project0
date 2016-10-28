package io.openems.device.inverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;

import io.openems.api.iec.ConnectionListener;
import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.api.iec.MessageType;
import io.openems.channel.modbus.WritableModbusDevice;
import io.openems.channel.modbus.write.ModbusRegistersWriteRequest;
import io.openems.channel.modbus.write.ModbusSingleRegisterWriteRequest;
import io.openems.device.protocol.ElementBuilder;
import io.openems.device.protocol.ElementLength;
import io.openems.device.protocol.ElementRange;
import io.openems.device.protocol.ModbusProtocol;
import io.openems.device.protocol.UnsignedIntegerDoublewordElement;
import io.openems.device.protocol.UnsignedShortWordElement;
import io.openems.device.protocol.WordOrder;
import io.openems.element.Element;
import io.openems.element.InvalidValueExcecption;
import io.openems.element.type.IntegerType;
import io.openems.element.type.LongType;

public class SolarLog extends WritableModbusDevice {

	private int totalPower;

	public SolarLog(String name, String channel, int unitid, int totalPower) {
		super(name, channel, unitid);
		this.setTotalPower(totalPower);
	}

	@Override
	public Set<String> getWriteElements() {
		return new HashSet<String>(Arrays.asList( //
				InverterProtocol.SetLimit.name(), InverterProtocol.SetLimitType.name()));
	}

	@Override
	public void init() {
		super.init();
		// Set SolarLog PLimit_Type to remote PV limitation
		UnsignedShortWordElement setLimitType = (UnsignedShortWordElement) getElement("SetLimitType");
		UnsignedIntegerDoublewordElement watchdog = (UnsignedIntegerDoublewordElement) getElement("WatchDog");
		addToWriteRequestQueue(new ModbusSingleRegisterWriteRequest(setLimitType.getAddress(),
				setLimitType.toRegister(new IntegerType(2))));
		addToWriteRequestQueue(new ModbusRegistersWriteRequest(watchdog.getAddress(),
				watchdog.toRegisters(new LongType(System.currentTimeMillis()))));
	}

	@Override
	protected ModbusProtocol getProtocol() {
		ModbusProtocol protocol = new ModbusProtocol(name);
		protocol.addElementRange(new ElementRange(3502, new ElementBuilder(3502, name).name(InverterProtocol.PAC.name())
				.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).unit("W").build()));//
		protocol.addElementRange(new ElementRange(3504,
				new ElementBuilder(3504, name).name(InverterProtocol.PDC.name()).length(ElementLength.DOUBLEWORD)
						.wordOrder(WordOrder.LSWMSW).unit("W").build(), //
				new ElementBuilder(3506, name).name(InverterProtocol.UAC.name()).unit("V").build(), //
				new ElementBuilder(3507, name).name(InverterProtocol.UDC.name()).unit("V").build(), //
				new ElementBuilder(3508, name).name(InverterProtocol.DailyYield.name()).length(ElementLength.DOUBLEWORD)
						.wordOrder(WordOrder.LSWMSW).unit("Wh").build(), //
				new ElementBuilder(3510, name).name(InverterProtocol.YesterdayYield.name())
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).unit("Wh").build(), //
				new ElementBuilder(3512, name).name(InverterProtocol.MonthlyYield.name())
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).unit("Wh").build(), //
				new ElementBuilder(3514, name).name(InverterProtocol.YearlyYield.name())
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).unit("Wh").build(), //
				new ElementBuilder(3516, name).name(InverterProtocol.TotalYield.name()).length(ElementLength.DOUBLEWORD)
						.wordOrder(WordOrder.LSWMSW).unit("Wh").build()));//
		protocol.addElementRange(new ElementRange(10400,
				new ElementBuilder(10400, name).name(InverterProtocol.SetLimitType.name()).unit("%").build()));
		protocol.addElementRange(new ElementRange(10401,
				new ElementBuilder(10401, name).name(InverterProtocol.SetLimit.name()).build()));
		protocol.addElementRange(
				new ElementRange(10404, new ElementBuilder(10404, name).name(InverterProtocol.WatchDog.name())
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build()));
		protocol.addElementRange(
				new ElementRange(10900, new ElementBuilder(10900, name).name(InverterProtocol.Status.name()).build(),
						new ElementBuilder(10901, name).name(InverterProtocol.GetLimit.name()).unit("%").build()));
		return protocol;
	}

	@Override
	public Set<String> getInitElements() {
		return new HashSet<String>();
	}

	@Override
	public Set<String> getMainElements() {
		return new HashSet<String>(Arrays.asList( //
				InverterProtocol.PAC.name()));
	}

	public int getTotalPower() {
		return totalPower;
	}

	public void setTotalPower(int totalPower) {
		this.totalPower = totalPower;
	}

	public long getActivePower() throws InvalidValueExcecption {
		return ((UnsignedIntegerDoublewordElement) getElement(InverterProtocol.PAC.name())).getValue().toLong();
	}

	@Override
	public String getCurrentDataAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPVLimit(int power) {
		int limitPercent = (int) ((double) power / (double) totalPower * 100.0);
		if (limitPercent > 100) {
			limitPercent = 100;
		}
		if (limitPercent < 0) {
			limitPercent = 0;
		}
		UnsignedShortWordElement setLimit = (UnsignedShortWordElement) getElement("SetLimit");
		UnsignedIntegerDoublewordElement placeholder = (UnsignedIntegerDoublewordElement) getElement("Placeholder");
		UnsignedIntegerDoublewordElement watchdog = (UnsignedIntegerDoublewordElement) getElement("WatchDog");
		addToWriteRequestQueue(new ModbusSingleRegisterWriteRequest(setLimit.getAddress(),
				setLimit.toRegister(new IntegerType(limitPercent))));
		addToWriteRequestQueue(new ModbusRegistersWriteRequest(watchdog.getAddress(),
				watchdog.toRegisters(new LongType(System.currentTimeMillis()))));
	}

	public int getPVLimit() throws InvalidValueExcecption {
		return totalPower / 100
				* ((UnsignedShortWordElement) getElement(InverterProtocol.GetLimit.name())).getValue().toInteger();
	}

	@Override
	public void handleSetPoint(int function, IeShortFloat informationElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCommand(int function, IeDoubleCommand informationElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IecElementOnChangeListener> createChangeListeners(int startAddressMeassurements,
			int startAddressMessages, ConnectionListener connection, boolean negate) {
		ArrayList<IecElementOnChangeListener> eventListener = new ArrayList<>();
		/* Meassurements */
		float multiplier = 1;
		if (negate) {
			multiplier = -1;
		}
		eventListener.add(createMeassurementListener(InverterProtocol.PAC.name(), startAddressMeassurements + 0,
				0.001f * multiplier, connection));
		return eventListener;
	}

	private IecElementOnChangeListener createMeassurementListener(String elementName, int address, float multiplier,
			ConnectionListener connection) {
		Element<?> element = getElement(elementName);
		IecElementOnChangeListener ieocl = new IecElementOnChangeListener(element, connection, address, multiplier,
				MessageType.MEASSUREMENT, false);
		element.addOnChangeListener(ieocl);
		return ieocl;
	}

}
