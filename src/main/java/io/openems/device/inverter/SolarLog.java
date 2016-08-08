package io.openems.device.inverter;

import io.openems.channel.modbus.WritableModbusDevice;
import io.openems.device.protocol.ElementBuilder;
import io.openems.device.protocol.ElementLength;
import io.openems.device.protocol.ElementRange;
import io.openems.device.protocol.ModbusProtocol;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class SolarLog extends WritableModbusDevice {

	private int totalPower;

	public SolarLog(String name, String channel, int unitid, int totalPower) throws IOException,
			ParserConfigurationException, SAXException {
		super(name, channel, unitid);
		this.setTotalPower(totalPower);
	}

	@Override
	public Set<String> getWriteElements() {
		return new HashSet<String>(Arrays.asList( //
				InverterProtocol.SetLimit.name()));
	}

	@Override
	protected ModbusProtocol getProtocol() throws IOException, ParserConfigurationException, SAXException {
		ModbusProtocol protocol = new ModbusProtocol(name);
		protocol.addElementRange(new ElementRange(3502, new ElementBuilder(3502).name(InverterProtocol.PAC.name())
				.length(ElementLength.DOUBLEWORD).unit("W").build()));//
		protocol.addElementRange(new ElementRange(3502, new ElementBuilder(3504).name(InverterProtocol.PDC.name())
				.length(ElementLength.DOUBLEWORD).unit("W").build(),//
				new ElementBuilder(3506).name(InverterProtocol.UAC.name()).unit("V").build(),//
				new ElementBuilder(3507).name(InverterProtocol.UDC.name()).unit("V").build(),//
				new ElementBuilder(3508).name(InverterProtocol.DailyYield.name()).length(ElementLength.DOUBLEWORD)
						.unit("Wh").build(),//
				new ElementBuilder(3510).name(InverterProtocol.YesterdayYield.name()).length(ElementLength.DOUBLEWORD)
						.unit("Wh").build(),//
				new ElementBuilder(3512).name(InverterProtocol.MonthlyYield.name()).length(ElementLength.DOUBLEWORD)
						.unit("Wh").build(),//
				new ElementBuilder(3514).name(InverterProtocol.YearlyYield.name()).length(ElementLength.DOUBLEWORD)
						.unit("Wh").build(),//
				new ElementBuilder(3516).name(InverterProtocol.TotalYield.name()).length(ElementLength.DOUBLEWORD)
						.unit("Wh").build()));//
		protocol.addElementRange(new ElementRange(10400, new ElementBuilder(10400)
				.name(InverterProtocol.SetLimit.name()).unit("%").build()));
		protocol.addElementRange(new ElementRange(10401, new ElementBuilder(10401).name(
				InverterProtocol.SetLimitType.name()).build()));
		return protocol;
	}

	@Override
	public Set<String> getInitElements() {
		return new HashSet<String>(Arrays.asList( //
				InverterProtocol.SetLimitType.name()));
	}

	@Override
	public Set<String> getMainElements() {
		return new HashSet<String>(Arrays.asList( //
				InverterProtocol.SetLimit.name(),//
				InverterProtocol.PAC.name()));
	}

	public int getTotalPower() {
		return totalPower;
	}

	public void setTotalPower(int totalPower) {
		this.totalPower = totalPower;
	}

}
