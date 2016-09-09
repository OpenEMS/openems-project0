/*
 * OpenEMS - Open Source Energy Management System
 * Copyright (c) 2016 FENECON GmbH & Co. KG
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.openems.device.ess.mini;

import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.channel.modbus.write.ModbusSingleRegisterWriteRequest;
import io.openems.device.ess.Ess;
import io.openems.device.ess.EssProtocol;
import io.openems.device.ess.EssProtocol.GridStates;
import io.openems.device.protocol.ElementBuilder;
import io.openems.device.protocol.ElementRange;
import io.openems.device.protocol.ElementType;
import io.openems.device.protocol.ModbusProtocol;
import io.openems.device.protocol.UnsignedShortWordElement;
import io.openems.device.protocol.interfaces.WordElement;
import io.openems.element.state.AllowedState;
import io.openems.element.state.EnabledState;
import io.openems.element.type.IntegerType;

import java.util.List;
import java.util.Set;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mini extends Ess {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(Mini.class);

	public Mini(String name, String channel, int unitid, int minSoc) {
		super(name, channel, unitid, minSoc);
	}

	@Override
	public String toString() {
		return "Mini [name=" + name + ", unitid=" + unitid + "]";
	}

	@Override
	protected ModbusProtocol getProtocol() {
		ModbusProtocol protocol = new ModbusProtocol(name);
		protocol.addElementRange(new ElementRange(4812, //
				new ElementBuilder(4812, name).name(EssProtocol.BatteryStringSoc).unit("%").build()));

		protocol.addElementRange(new ElementRange(30502, //
				new ElementBuilder(30102, name).name(MiniProtocol.OffGridInverterAllowLoad) //
						.map(0, AllowedState.ALLOWED) //
						.map(1, AllowedState.NOT_ALLOWED).build(), //
				new ElementBuilder(30103, name).name(MiniProtocol.InverterAllowGridConnect) //
						.map(0, AllowedState.NOT_ALLOWED) //
						.map(1, AllowedState.ALLOWED).build(), //
				new ElementBuilder(30104, name).name(MiniProtocol.ChargeMode) //
						.map(0, "Match charge") //
						.map(1, "Maximum power charge").build(), //
				new ElementBuilder(30105, name).name(MiniProtocol.DischargeMode) //
						.map(0, "Only grid matched load") //
						.map(1, "Grid matched load priority") //
						.map(2, "Only grid electricity sold") //
						.map(3, "Off-grid load") //
						.map(4, "Meter matched load balancing").build(), //
				new ElementBuilder(30106, name).name(MiniProtocol.InverterAllowGridCharge) //
						.map(0, AllowedState.NOT_ALLOWED) //
						.map(1, AllowedState.ALLOWED).build(), //
				new ElementBuilder(30107, name).name(MiniProtocol.InverterAllowGridDischarge) //
						.map(0, AllowedState.NOT_ALLOWED) //
						.map(1, AllowedState.ALLOWED).build(), //
				new ElementBuilder(30108, name).name(MiniProtocol.InverterChargeDischargeTimeMode) //
						.map(0, "No charge/discharge time") //
						.map(1, "Charge/discharge at set time").build(), //
				new ElementBuilder(30109, name).name(MiniProtocol.BatteryVoltageLowLimit).multiplier(100).unit("mV")
						.build(), //
				new ElementBuilder(30110, name).name(MiniProtocol.BatteryVoltageHighLimit).multiplier(100).unit("mV")
						.build(), //
				new ElementBuilder(30111, name).name(MiniProtocol.GridVoltageLowLimit).multiplier(100).unit("mV")
						.build(), //
				new ElementBuilder(30112, name).name(MiniProtocol.GridFrequencyLowLimit).multiplier(100).unit("mHz")
						.build(), //
				new ElementBuilder(30113, name).name(MiniProtocol.GridVoltageHighLimit).multiplier(100).unit("mV")
						.build(), //
				new ElementBuilder(30114, name).name(MiniProtocol.GridFrequencyHighLimit).multiplier(100).unit("mHz")
						.build(), //
				new ElementBuilder(30115, name).name(MiniProtocol.InverterCertificationStandard) //
						.map(0, "VDE4105 and off-grid") //
						.map(1, "AS4777 and off-grid") //
						.map(2, "CEI021 and off-grid") //
						.map(3, "ET and off-grid") //
						.map(4, "VDE4105 and grid") //
						.map(5, "AS4777 and grid") //
						.map(6, "CEI021 and grid") //
						.map(7, "JET and grid").build(), //
				new ElementBuilder(30116, name).name(MiniProtocol.EnableSmoothPV) //
						.map(0, EnabledState.ENABLED) //
						.map(1, EnabledState.DISABLED).build(), //
				new ElementBuilder(30117, name).name(MiniProtocol.SmoothSpeedPV).unit("s").build(), //
				new ElementBuilder(30118, name).type(ElementType.PLACEHOLDER).intLength(30118 - 30124).build(), //
				new ElementBuilder(30125, name).name(MiniProtocol.GridMaxOutputCurrent).multiplier(100).unit("mA")
						.build(), //
				new ElementBuilder(30126, name).name(MiniProtocol.InverterMaxChargeCurrent).multiplier(100).unit("mA")
						.build(), //
				new ElementBuilder(30127, name).name(MiniProtocol.InverterMaxDischargeCurrent).multiplier(100)
						.unit("mA").build(), //
				new ElementBuilder(30128, name).name(MiniProtocol.GridMaxInputCurrent).multiplier(100).unit("mA")
						.build(), //
				new ElementBuilder(30129, name).name(MiniProtocol.TimeModeStartChargeMinute).unit("min").build(), //
				new ElementBuilder(30130, name).name(MiniProtocol.TimeModeStartChargeHour).unit("h").build(), //
				new ElementBuilder(30131, name).name(MiniProtocol.TimeModeStopChargeMinute).unit("min").build(), //
				new ElementBuilder(30132, name).name(MiniProtocol.TimeModeStopChargeHour).unit("h").build(), //
				new ElementBuilder(30133, name).name(MiniProtocol.TimeModeStartDischargeMinute).unit("min").build(), //
				new ElementBuilder(30134, name).name(MiniProtocol.TimeModeStartDischargeHour).unit("h").build(), //
				new ElementBuilder(30135, name).name(MiniProtocol.TimeModeStopDischargeMinute).unit("min").build(), //
				new ElementBuilder(30136, name).name(MiniProtocol.TimeModeStopDischargeHour).unit("h").build(), //
				new ElementBuilder(30137, name).name(MiniProtocol.SystemMaxSOC).unit("%").build(), //
				new ElementBuilder(30138, name).name(MiniProtocol.SystemMinSOC).unit("%").build(), //
				new ElementBuilder(30139, name).name(MiniProtocol.SystemChargeFromGridSOC).unit("%").build(), //
				new ElementBuilder(30140, name).name(MiniProtocol.GridSlowVoltageLowLimit).multiplier(100).unit("mV")
						.build(), //
				new ElementBuilder(30141, name).name(MiniProtocol.GridSlowVoltageHighLimit).multiplier(100).unit("mV")
						.build(), //
				new ElementBuilder(30142, name).name(MiniProtocol.GridSlowVoltageLowTimeLimit).unit("ms").build(), //
				new ElementBuilder(30143, name).name(MiniProtocol.GridSlowVoltageHighTimeLimit).unit("ms").build(), //
				new ElementBuilder(30144, name).name(MiniProtocol.GridFastVoltageLowTimeLimit).unit("ms").build(), //
				new ElementBuilder(30145, name).name(MiniProtocol.GridFastVoltageHighTimeLimit).unit("ms").build(), //
				new ElementBuilder(30146, name).name(MiniProtocol.GridFrequencyLowTimeLimit).unit("ms").build(), //
				new ElementBuilder(30147, name).name(MiniProtocol.GridFrequencyHighTimeLimit).unit("ms").build(), //
				new ElementBuilder(30148, name).name(MiniProtocol.GridFrequencyReconnectLowLimit).multiplier(10)
						.unit("mHz").build(), //
				new ElementBuilder(30149, name).name(MiniProtocol.GridFrequencyReconnectHighLimit).multiplier(10)
						.unit("mHz").build(), //
				new ElementBuilder(30150, name).name(MiniProtocol.GridFrequencyReconnectTimeLimit).unit("ms").build(), //
				new ElementBuilder(30151, name).name(MiniProtocol.GridVoltageStartPowerFactorAdjustmentLimit)
						.multiplier(100).unit("V").build(), //
				new ElementBuilder(30152, name).name(MiniProtocol.GridVoltageStartPowerFactorAdjustmentPercentageLimit)
						.unit("%").build(), //
				new ElementBuilder(30153, name).name(MiniProtocol.GridVoltageStopPowerFactorAdjustmentLimit)
						.multiplier(100).unit("V").build(), //
				new ElementBuilder(30154, name).name(MiniProtocol.GridVoltageReconnectLowLimit).multiplier(100)
						.unit("V").build(), //
				new ElementBuilder(30155, name).name(MiniProtocol.GridVoltageReconnectHighLimit).multiplier(100)
						.unit("V").build(), //
				new ElementBuilder(30156, name).name(MiniProtocol.GridReconnectPowerRisingSlope).build(), //
				new ElementBuilder(30157, name).name(MiniProtocol.ParameterSetting) //
						.map(0, EnabledState.DISABLED) //
						.map(1, EnabledState.ENABLED).build(), //
				new ElementBuilder(30157, name).name(MiniProtocol.PcsMode) //
						.map(0, "Emergency Mode") //
						.map(1, "Consumers peak pattern") //
						.map(2, "Economic model") //
						.map(3, "Eco mode") //
						.map(4, "Debug mode") //
						.map(5, "Smooth PV") //
						.map(6, "Remote scheduling") //
						.map(8, "Timing mode").build(), //
				new ElementBuilder(30158, name).name(MiniProtocol.PowerMeterLimit).unit("W").build())); //
		protocol.addElementRange(new ElementRange(
				30502, // //
				new ElementBuilder(30502, name).name(MiniProtocol.SetOffGridInverterAllowLoad).writable() // //
						.map(0, AllowedState.ALLOWED) // //
						.map(1, AllowedState.NOT_ALLOWED).build(), //
				new ElementBuilder(30503, name).name(MiniProtocol.SetInverterAllowGridConnect).writable() //
						.map(0, AllowedState.NOT_ALLOWED) //
						.map(1, AllowedState.ALLOWED).build(), //
				new ElementBuilder(30504, name).name(MiniProtocol.SetChargeMode).writable() //
						.map(0, "Match charge") //
						.map(1, "Maximum power charge").build(), //
				new ElementBuilder(30505, name).name(MiniProtocol.SetDischargeMode).writable() //
						.map(0, "Only grid matched load") //
						.map(1, "Grid matched load priority") //
						.map(2, "Only grid electricity sold") //
						.map(3, "Off-grid load") //
						.map(4, "Meter matched load balancing").build(), //
				new ElementBuilder(30506, name).name(MiniProtocol.SetInverterAllowGridCharge).writable() //
						.map(0, AllowedState.NOT_ALLOWED) //
						.map(1, AllowedState.ALLOWED).build(), //
				new ElementBuilder(30507, name).name(MiniProtocol.SetInverterAllowGridDischarge).writable() //
						.map(0, AllowedState.NOT_ALLOWED) //
						.map(1, AllowedState.ALLOWED).build(), //
				new ElementBuilder(30508, name).name(MiniProtocol.SetInverterChargeDischargeTimeMode).writable() //
						.map(0, "No charge/discharge time") //
						.map(1, "Charge/discharge at set time").build(), //
				new ElementBuilder(30509, name).name(MiniProtocol.SetBatteryVoltageLowLimit).multiplier(100).unit("mV")
						.writable().build(), //
				new ElementBuilder(30510, name).name(MiniProtocol.SetBatteryVoltageHighLimit).multiplier(100)
						.unit("mV").writable().build(), //
				new ElementBuilder(30511, name).name(MiniProtocol.SetGridVoltageLowLimit).multiplier(100).unit("mV")
						.writable().build(), //
				new ElementBuilder(30512, name).name(MiniProtocol.SetGridFrequencyLowLimit).multiplier(100).unit("mHz")
						.writable().build(), //
				new ElementBuilder(30513, name).name(MiniProtocol.SetGridVoltageHighLimit).multiplier(100).unit("mV")
						.writable().build(), //
				new ElementBuilder(30514, name).name(MiniProtocol.SetGridFrequencyHighLimit).multiplier(100)
						.unit("mHz").writable().build(), //
				new ElementBuilder(30515, name).name(MiniProtocol.SetInverterCertificationStandard).writable() //
						.map(0, "VDE4105 and off-grid") //
						.map(1, "AS4777 and off-grid") //
						.map(2, "CEI021 and off-grid") //
						.map(3, "ET and off-grid") //
						.map(4, "VDE4105 and grid") //
						.map(5, "AS4777 and grid") //
						.map(6, "CEI021 and grid") //
						.map(7, "JET and grid").build(), //
				new ElementBuilder(30516, name).name(MiniProtocol.SetEnableSmoothPV).writable() //
						.map(0, EnabledState.ENABLED) // //
						.map(1, EnabledState.DISABLED).build(), //
				new ElementBuilder(30517, name).name(MiniProtocol.SetSmoothSpeedPV).unit("s").writable().build(), //
				new ElementBuilder(30518, name).type(ElementType.PLACEHOLDER).intLength(30118 - 30124).writable()
						.build(), //
				new ElementBuilder(30525, name).name(MiniProtocol.SetGridMaxOutputCurrent).multiplier(100).unit("mA")
						.writable().build(), //
				new ElementBuilder(30526, name).name(MiniProtocol.SetInverterMaxChargeCurrent).multiplier(100)
						.unit("mA").writable().build(), //
				new ElementBuilder(30527, name).name(MiniProtocol.SetInverterMaxDischargeCurrent).multiplier(100)
						.unit("mA").writable().build(), //
				new ElementBuilder(30528, name).name(MiniProtocol.SetGridMaxInputCurrent).multiplier(100).unit("mA")
						.writable().build(), //
				new ElementBuilder(30529, name).name(MiniProtocol.SetTimeModeStartChargeMinute).unit("min").writable()
						.build(), //
				new ElementBuilder(30530, name).name(MiniProtocol.SetTimeModeStartChargeHour).unit("h").writable()
						.build(), //
				new ElementBuilder(30531, name).name(MiniProtocol.SetTimeModeStopChargeMinute).unit("min").writable()
						.build(), //
				new ElementBuilder(30532, name).name(MiniProtocol.SetTimeModeStopChargeHour).unit("h").writable()
						.build(), //
				new ElementBuilder(30533, name).name(MiniProtocol.SetTimeModeStartDischargeMinute).unit("min")
						.writable().build(), //
				new ElementBuilder(30534, name).name(MiniProtocol.SetTimeModeStartDischargeHour).unit("h").writable()
						.build(), //
				new ElementBuilder(30535, name).name(MiniProtocol.SetTimeModeStopDischargeMinute).unit("min")
						.writable().build(), //
				new ElementBuilder(30536, name).name(MiniProtocol.SetTimeModeStopDischargeHour).unit("h").writable()
						.build(), //
				new ElementBuilder(30537, name).name(MiniProtocol.SetSystemMaxSOC).unit("%").writable().build(), //
				new ElementBuilder(30538, name).name(MiniProtocol.SetSystemMinSOC).unit("%").writable().build(), //
				new ElementBuilder(30539, name).name(MiniProtocol.SetSystemChargeFromGridSOC).unit("%").writable()
						.build(), //
				new ElementBuilder(30540, name).name(MiniProtocol.SetGridSlowVoltageLowLimit).multiplier(100)
						.unit("mV").writable().build(), //
				new ElementBuilder(30541, name).name(MiniProtocol.SetGridSlowVoltageHighLimit).multiplier(100)
						.unit("mV").writable().build(), //
				new ElementBuilder(30542, name).name(MiniProtocol.SetGridSlowVoltageLowTimeLimit).unit("ms").writable()
						.build(), //
				new ElementBuilder(30543, name).name(MiniProtocol.SetGridSlowVoltageHighTimeLimit).unit("ms")
						.writable().build(), //
				new ElementBuilder(30544, name).name(MiniProtocol.SetGridFastVoltageLowTimeLimit).unit("ms").writable()
						.build(), //
				new ElementBuilder(30545, name).name(MiniProtocol.SetGridFastVoltageHighTimeLimit).unit("ms")
						.writable().build(), //
				new ElementBuilder(30546, name).name(MiniProtocol.SetGridFrequencyLowTimeLimit).unit("ms").writable()
						.build(), //
				new ElementBuilder(30547, name).name(MiniProtocol.SetGridFrequencyHighTimeLimit).unit("ms").writable()
						.build(), //
				new ElementBuilder(30548, name).name(MiniProtocol.SetGridFrequencyReconnectLowLimit).multiplier(10)
						.unit("mHz").writable().build(), //
				new ElementBuilder(30549, name).name(MiniProtocol.SetGridFrequencyReconnectHighLimit).multiplier(10)
						.unit("mHz").writable().build(), //
				new ElementBuilder(30550, name).name(MiniProtocol.SetGridFrequencyReconnectTimeLimit).unit("ms")
						.writable().build(), //
				new ElementBuilder(30551, name).name(MiniProtocol.SetGridVoltageStartPowerFactorAdjustmentLimit)
						.multiplier(100).unit("V").writable().build(), //
				new ElementBuilder(30552, name)
						.name(MiniProtocol.SetGridVoltageStartPowerFactorAdjustmentPercentageLimit).unit("%")
						.writable().build(), //
				new ElementBuilder(30553, name).name(MiniProtocol.SetGridVoltageStopPowerFactorAdjustmentLimit)
						.multiplier(100).unit("V").writable().build(), //
				new ElementBuilder(30554, name).name(MiniProtocol.SetGridVoltageReconnectLowLimit).multiplier(100)
						.unit("V").writable().build(), //
				new ElementBuilder(30555, name).name(MiniProtocol.SetGridVoltageReconnectHighLimit).multiplier(100)
						.unit("V").writable().build(), //
				new ElementBuilder(30556, name).name(MiniProtocol.SetGridReconnectPowerRisingSlope).writable().build(), //
				new ElementBuilder(30557, name).name(MiniProtocol.SetLocalRemoteMode).writable() //
						.map(0, "Local") //
						.map(1, "Remote").build(), //
				new ElementBuilder(30558, name).name(MiniProtocol.SetParameterSetting).writable() //
						.map(0, EnabledState.DISABLED) //
						.map(1, EnabledState.ENABLED).build(), //
				new ElementBuilder(30559, name).name(MiniProtocol.SetPcsMode).writable() //
						.map(0, "Emergency Mode") //
						.map(1, "Consumers peak pattern") //
						.map(2, "Economic model") //
						.map(3, "Eco mode") //
						.map(4, "Debug mode") //
						.map(5, "Smooth PV") //
						.map(6, "Remote scheduling") //
						.map(8, "Timing mode").build(), //
				new ElementBuilder(30560, name).name(MiniProtocol.SetPowerMeterLimit).unit("W").writable().build(),
				new ElementBuilder(30561, name).name(MiniProtocol.SetInverterNetworking).writable() //
						.map(0, "Emergency Mode") //
						.map(0, "DC bus without Networking") //
						.map(1, "DC bus in parallel host") //
						.map(2, "DC bus in parallel from 1") //
						.map(3, "DC bus in parallel from 2") //
						.map(4, "parallel host bus exchange") //
						.map(5, "AC bus in parallel from 1") //
						.map(6, "AC bus in parallel from 2") //
						.map(7, "dual FireWire L1 DC Bus") //
						.map(8, "DC bus L2 dual FireWire") //
						.map(9, "AC bus two FireWire L1") //
						.map(10, "AC bus two FireWire L2") //
						.map(11, "A phase three-phase DC bus") //
						.map(12, "Three-phase B-phase DC bus") //
						.map(13, "Three-phase C-phase DC bus") //
						.map(14, "A phase three-phase AC bus") //
						.map(15, "Three-phase B-phase AC bus") //
						.map(16, "Three-phase C-phase AC bus") //
						.map(17, "AC bus without networking").build()

		));

		protocol.addElementRange(new ElementRange(30558, //
				new ElementBuilder(30558, name).name(MiniProtocol.SetParameterSetting).build()));
		return protocol;
	}

	public UnsignedShortWordElement getSoc() {
		return (UnsignedShortWordElement) getElement(EssProtocol.BatteryStringSoc.name());
	}

	@SuppressWarnings("unchecked")
	private WordElement<IntegerType> getSetParameterSetting() {
		return (WordElement<IntegerType>) getElement(MiniProtocol.SetParameterSetting.name());
	}

	@Override
	public Set<String> getWriteElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getInitElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getMainElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GridStates getGridState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActivePower(int power) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getActivePower() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSOC() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAllowedCharge() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAllowedDischarge() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCurrentDataAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void startParameterSettingMode() {
		int START = 1;
		addToWriteRequestQueue(new ModbusSingleRegisterWriteRequest(getSetParameterSetting(), START));
	}

	public void stopParameterSettingMode() {
		int STOP = 0;
		addToWriteRequestQueue(new ModbusSingleRegisterWriteRequest(getSetParameterSetting(), STOP));
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
			int startAddressMessages, Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getReactivePower() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getApparentPower() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setReactivePower(int power) {
		// TODO Auto-generated method stub

	}

}
