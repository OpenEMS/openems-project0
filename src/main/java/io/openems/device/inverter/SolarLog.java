package io.openems.device.inverter;

import io.openems.channel.modbus.WritableModbusDevice;
import io.openems.device.protocol.ElementBuilder;
import io.openems.device.protocol.ElementLength;
import io.openems.device.protocol.ElementRange;
import io.openems.device.protocol.ModbusProtocol;
import io.openems.device.protocol.WordOrder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.openmuc.j60870.InformationElement;
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
				InverterProtocol.SetLimit.name(), InverterProtocol.SetLimitType.name()));
	}

	@Override
	protected ModbusProtocol getProtocol() throws IOException, ParserConfigurationException, SAXException {
		ModbusProtocol protocol = new ModbusProtocol(name);
		protocol.addElementRange(new ElementRange(3502, new ElementBuilder(3502, name)
				.name(InverterProtocol.PAC.name()).length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW)
				.unit("W").build()));//
		protocol.addElementRange(new ElementRange(3504, new ElementBuilder(3504, name)
				.name(InverterProtocol.PDC.name()).length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW)
				.unit("W").build(),//
				new ElementBuilder(3506, name).name(InverterProtocol.UAC.name()).unit("V").build(),//
				new ElementBuilder(3507, name).name(InverterProtocol.UDC.name()).unit("V").build(),//
				new ElementBuilder(3508, name).name(InverterProtocol.DailyYield.name())
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).unit("Wh").build(),//
				new ElementBuilder(3510, name).name(InverterProtocol.YesterdayYield.name())
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).unit("Wh").build(),//
				new ElementBuilder(3512, name).name(InverterProtocol.MonthlyYield.name())
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).unit("Wh").build(),//
				new ElementBuilder(3514, name).name(InverterProtocol.YearlyYield.name())
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).unit("Wh").build(),//
				new ElementBuilder(3516, name).name(InverterProtocol.TotalYield.name())
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).unit("Wh").build()));//
		protocol.addElementRange(new ElementRange(10400, new ElementBuilder(10400, name)
				.name(InverterProtocol.SetLimitType.name()).unit("%").build()));
		protocol.addElementRange(new ElementRange(10401, new ElementBuilder(10401, name).name(
				InverterProtocol.SetLimit.name()).build()));
		protocol.addElementRange(new ElementRange(10402, new ElementBuilder(10402, name)
				.name(InverterProtocol.Placeholder.name()).length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW)
				.build()));
		protocol.addElementRange(new ElementRange(10404, new ElementBuilder(10404, name)
				.name(InverterProtocol.WatchDog.name()).length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW)
				.build()));
		return protocol;
	}

	@Override
	public Set<String> getInitElements() {
		return new HashSet<String>(
		// Arrays.asList( //
		// InverterProtocol.SetLimitType.name())
		);
	}

	@Override
	public Set<String> getMainElements() {
		return new HashSet<String>(Arrays.asList( //
				// InverterProtocol.SetLimit.name(),//
				InverterProtocol.PAC.name()));
	}

	public int getTotalPower() {
		return totalPower;
	}

	public void setTotalPower(int totalPower) {
		this.totalPower = totalPower;
	}

	@Override
	public String getCurrentDataAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InformationElement[][] getIecValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
