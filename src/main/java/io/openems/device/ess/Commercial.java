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
package io.openems.device.ess;

import io.openems.device.protocol.BitElement;
import io.openems.device.protocol.BitsElement;
import io.openems.device.protocol.ElementBuilder;
import io.openems.device.protocol.ElementLength;
import io.openems.device.protocol.ElementRange;
import io.openems.device.protocol.ElementType;
import io.openems.device.protocol.ModbusProtocol;
import io.openems.device.protocol.SignedIntegerWordElement;
import io.openems.device.protocol.UnsignedShortWordElement;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class Commercial extends Ess {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(Commercial.class);

	public Commercial(String name, String channel, int unitid, int minSoc) throws IOException,
			ParserConfigurationException, SAXException {
		super(name, channel, unitid, minSoc);
	}

	@Override
	public String toString() {
		return "Commercial [name=" + name + ", unitid=" + unitid + "]";
	}

	@Override
	public Set<String> getInitElements() {
		return new HashSet<String>(Arrays.asList( //
				EssProtocol.SystemState.name()));
	}

	@Override
	public Set<String> getMainElements() {
		return new HashSet<String>(Arrays.asList( //
				EssProtocol.ActivePower.name(), //
				EssProtocol.ReactivePower.name(), //
				EssProtocol.ApparentPower.name(), //
				EssProtocol.AllowedCharge.name(), //
				EssProtocol.AllowedDischarge.name(), //
				EssProtocol.AllowedApparent.name(), //
				EssProtocol.GridMode.name(),//
				EssProtocol.BatteryStringSoc.name()));
	}

	@Override
	public Set<String> getWriteElements() {
		return new HashSet<String>(Arrays.asList( //
				EssProtocol.SetActivePower.name(),//
				EssProtocol.SetWorkState.name()));
	}

	@Override
	protected ModbusProtocol getProtocol() {
		ModbusProtocol protocol = new ModbusProtocol(name);
		protocol.addElementRange(new ElementRange(0x0101,
				new ElementBuilder(0x0101).device(name).name(EssProtocol.SystemState) //
						.bit(new BitElement(1, EssProtocol.SystemStates.Stop.name())) //
						.bit(new BitElement(2, EssProtocol.SystemStates.PvCharging.name())) //
						.bit(new BitElement(3, EssProtocol.SystemStates.Standby.name())) //
						.bit(new BitElement(4, EssProtocol.SystemStates.Running.name())) //
						.bit(new BitElement(5, EssProtocol.SystemStates.Fault.name())) //
						.bit(new BitElement(6, EssProtocol.SystemStates.Debug.name())).build(),//
				new ElementBuilder(0x0102).device(name).name(EssProtocol.ControlMode)//
						.bit(new BitElement(0, EssProtocol.ControlModes.Remote.name()))//
						.bit(new BitElement(1, EssProtocol.ControlModes.LocalManual.name())).build(),
				new ElementBuilder(0x0103).device(name).name(EssProtocol.WorkMode)//
						.bit(new BitElement(9, EssProtocol.RemoteDispatch.name())).build(),//
				new ElementBuilder(0x0104).device(name).name(EssProtocol.BatteryMaintananceState).build(),//
				new ElementBuilder(0x0105).device(name).name(EssProtocol.InverterState)//
						.bit(new BitElement(0, EssProtocol.InverterStates.Initial.name()))//
						.bit(new BitElement(1, EssProtocol.InverterStates.Fault.name()))//
						.bit(new BitElement(2, EssProtocol.InverterStates.Stop.name()))//
						.bit(new BitElement(3, EssProtocol.InverterStates.Standby.name()))//
						.bit(new BitElement(4, EssProtocol.InverterStates.GridMonitoring.name()))//
						.bit(new BitElement(5, EssProtocol.InverterStates.Ready.name()))//
						.bit(new BitElement(6, EssProtocol.InverterStates.Running.name()))//
						.bit(new BitElement(7, EssProtocol.InverterStates.Debug.name())).build(),//
				new ElementBuilder(0x0106).device(name).name(EssProtocol.GridMode).build(),//
				new ElementBuilder(0x0107).device(name).type(ElementType.PLACEHOLDER).intLength(0x0108 - 0x0107)
						.build(),//
				new ElementBuilder(0x0108).device(name).name(EssProtocol.ProtocolVersion).build(), //
				new ElementBuilder(0x0109).device(name).name(EssProtocol.SystemManufacturer).build(),//
				new ElementBuilder(0x010A).device(name).name(EssProtocol.SystemType).build()));
		protocol.addElementRange(new ElementRange(0x0150, new ElementBuilder(0x0150).device(name)
				.name(EssProtocol.SwitchStates) //
				.bit(new BitElement(1, EssProtocol.Switches.DCMain.name())) //
				.bit(new BitElement(2, EssProtocol.Switches.DCPrecharge.name())) //
				.bit(new BitElement(3, EssProtocol.Switches.ACBreaker.name())) //
				.bit(new BitElement(4, EssProtocol.Switches.ACMain.name())) //
				.bit(new BitElement(5, EssProtocol.Switches.ACPrecharge.name())).build()));
		protocol.addElementRange(new ElementRange(0x0200, new ElementBuilder(0x0200).device(name)
				.name(EssProtocol.DcVoltage).unit("mV").multiplier(100).build(),//
				new ElementBuilder(0x0201).device(name).name(EssProtocol.DcCurrent).unit("mA").multiplier(100).build(),//
				new ElementBuilder(0x0202).device(name).name(EssProtocol.DcPower).unit("W").multiplier(100).build(),//
				new ElementBuilder(0x0203).device(name).type(ElementType.PLACEHOLDER).intLength(0x0208 - 0x0203)
						.build(),//
				new ElementBuilder(0x0208).device(name).name(EssProtocol.ChargeEnergy).unit("Wh")
						.length(ElementLength.DOUBLEWORD).multiplier(100).build(),//
				new ElementBuilder(0x020A).device(name).name(EssProtocol.DischargeEnergy).unit("Wh").multiplier(100)
						.length(ElementLength.DOUBLEWORD).build()));
		protocol.addElementRange(new ElementRange(0x0210, new ElementBuilder(0x0210).name(EssProtocol.ActivePower)
				.multiplier(100).signed(true).unit("W").build(), new ElementBuilder(0x0211)
				.name(EssProtocol.ReactivePower).multiplier(100).signed(true).unit("Var").build(),//
				new ElementBuilder(0x0212).name(EssProtocol.ApparentPower).multiplier(100).unit("VA").build(),//
				new ElementBuilder(0x0213).name(EssProtocol.CurrentPhase1).signed(true).multiplier(100).unit("mA")
						.build(),//
				new ElementBuilder(0x0214).name(EssProtocol.CurrentPhase2).signed(true).multiplier(100).unit("mA")
						.build(),//
				new ElementBuilder(0x0215).name(EssProtocol.CurrentPhase3).signed(true).multiplier(100).unit("mA")
						.build(),//
				new ElementBuilder(0x0216).device(name).type(ElementType.PLACEHOLDER).intLength(0x219 - 0x216).build(),//
				new ElementBuilder(0x0219).name(EssProtocol.VoltagePhase1).multiplier(100).unit("mV").build(),//
				new ElementBuilder(0x021A).name(EssProtocol.VoltagePhase2).multiplier(100).unit("mV").build(),//
				new ElementBuilder(0x021B).name(EssProtocol.VoltagePhase3).multiplier(100).unit("mV").build(),//
				new ElementBuilder(0x021C).name(EssProtocol.Frequency).multiplier(100).unit("mHZ").build(),//
				new ElementBuilder(0x021D).device(name).type(ElementType.PLACEHOLDER).intLength(0x230 - 0x21D).build(),//
				new ElementBuilder(0x0230).device(name).name(EssProtocol.AllowedCharge).multiplier(100).signed(true)
						.unit("W").build(),//
				new ElementBuilder(0x0231).device(name).name(EssProtocol.AllowedDischarge).multiplier(100).unit("W")
						.build(), //
				new ElementBuilder(0x0232).device(name).name(EssProtocol.AllowedApparent).multiplier(100).unit("Var")
						.build()));
		protocol.addElementRange(new ElementRange(0x0500, new ElementBuilder(0x0500).device(name)
				.name(EssProtocol.SetWorkState) //
				.signed(true)//
				.build()));
		protocol.addElementRange(new ElementRange(0x0501, //
				new ElementBuilder(0x0501).device(name).name(EssProtocol.SetActivePower).multiplier(100).signed(true)
						.unit("W").build()));
		protocol.addElementRange(new ElementRange(0x0502, new ElementBuilder(0x0502).device(name)
				.name(EssProtocol.SetReactivePower).multiplier(100).signed(true).unit("var").build()));
		protocol.addElementRange(new ElementRange(0x1100, new ElementBuilder(0x1100).device(name)
				.name(EssProtocol.BatteryState).bit(new BitElement(0, EssProtocol.BatteryStates.Initial.name()))//
				.bit(new BitElement(1, EssProtocol.BatteryStates.Stop.name()))//
				.bit(new BitElement(2, EssProtocol.BatteryStates.StartingUp.name()))//
				.bit(new BitElement(3, EssProtocol.BatteryStates.Running.name()))//
				.bit(new BitElement(4, EssProtocol.BatteryStates.Fault.name())).build()));
		protocol.addElementRange(new ElementRange(0x1400, new ElementBuilder(0x1400).device(name)
				.name(EssProtocol.BatteryVoltage).unit("mV").multiplier(100).build(),//
				new ElementBuilder(0x1401).device(name).name(EssProtocol.BatteryAmperage).unit("mA").signed(true)
						.multiplier(100).build(),//
				new ElementBuilder(0x1402).device(name).name(EssProtocol.BatteryStringSoc).unit("%").build(),//
				new ElementBuilder(0x1403).device(name).name(EssProtocol.BatteryStringSOH).unit("%").build(),//
				new ElementBuilder(0x1404).device(name).type(ElementType.PLACEHOLDER).intLength(0x1418 - 0x1404)
						.build(),//
				new ElementBuilder(0x1418).device(name).name(EssProtocol.BatteryChargeEnergy).unit("Wh")
						.length(ElementLength.DOUBLEWORD).build(),//
				new ElementBuilder(0x141A).device(name).name(EssProtocol.BatteryDischargeEnergy).unit("Wh")
						.length(ElementLength.DOUBLEWORD).build(),//
				new ElementBuilder(0x141C).device(name).type(ElementType.PLACEHOLDER).intLength(0x1420 - 0x141C)
						.build(),//
				new ElementBuilder(0x1420).device(name).name(EssProtocol.BatteryPower).unit("W").signed(true)
						.multiplier(100).build()));
		protocol.addElementRange(new ElementRange(0xA600, new ElementBuilder(0xA600).device(name)
				.name(EssProtocol.Pv1State) //
				.bit(new BitElement(1, EssProtocol.DcStates.Initial.name())) //
				.bit(new BitElement(2, EssProtocol.DcStates.Stop.name())) //
				.bit(new BitElement(3, EssProtocol.DcStates.Ready.name())) //
				.bit(new BitElement(4, EssProtocol.DcStates.Running.name())) //
				.bit(new BitElement(5, EssProtocol.DcStates.Fault.name())) //
				.bit(new BitElement(6, EssProtocol.DcStates.Debug.name())) //
				.bit(new BitElement(7, EssProtocol.DcStates.Locked.name())).build()));
		protocol.addElementRange(new ElementRange(0xA730, new ElementBuilder(0xA730).device(name)
				.name(EssProtocol.Pv1OutputVoltage).multiplier(10).signed(true).unit("V").build(), //
				new ElementBuilder(0xA731).device(name).name(EssProtocol.Pv1OutputCurrent).multiplier(10).signed(true)
						.unit("A").build(), //
				new ElementBuilder(0xA732).device(name).name(EssProtocol.Pv1OutputPower).multiplier(100).signed(true)
						.unit("W").build(), //
				new ElementBuilder(0xA733).device(name).name(EssProtocol.Pv1InputVoltage).multiplier(10).signed(true)
						.unit("V").build(), //
				new ElementBuilder(0xA734).device(name).name(EssProtocol.Pv1InputCurrent).multiplier(10).signed(true)
						.unit("A").build(), //
				new ElementBuilder(0xA735).device(name).name(EssProtocol.Pv1InputPower).multiplier(100).signed(true)
						.unit("W").build(), //
				new ElementBuilder(0xA736).device(name).name(EssProtocol.Pv1InputEnergy).multiplier(100).signed(true)
						.unit("Wh").build(), //
				new ElementBuilder(0xA737).device(name).name(EssProtocol.Pv1OutputEnergy).multiplier(100).signed(true)
						.unit("Wh").build()));
		protocol.addElementRange(new ElementRange(0xA900, new ElementBuilder(0xA900).device(name)
				.name(EssProtocol.Pv2State) //
				.bit(new BitElement(1, EssProtocol.DcStates.Initial.name())) //
				.bit(new BitElement(2, EssProtocol.DcStates.Stop.name())) //
				.bit(new BitElement(3, EssProtocol.DcStates.Ready.name())) //
				.bit(new BitElement(4, EssProtocol.DcStates.Running.name())) //
				.bit(new BitElement(5, EssProtocol.DcStates.Fault.name())) //
				.bit(new BitElement(6, EssProtocol.DcStates.Debug.name())) //
				.bit(new BitElement(7, EssProtocol.DcStates.Locked.name())).build()));
		protocol.addElementRange(new ElementRange(0xAA30, new ElementBuilder(0xAA30).device(name)
				.name(EssProtocol.Pv2OutputVoltage).multiplier(10).signed(true).unit("V").build(), //
				new ElementBuilder(0xAA31).device(name).name(EssProtocol.Pv2OutputCurrent).multiplier(10).signed(true)
						.unit("A").build(), //
				new ElementBuilder(0xAA32).device(name).name(EssProtocol.Pv2OutputPower).multiplier(100).signed(true)
						.unit("W").build(), //
				new ElementBuilder(0xAA33).device(name).name(EssProtocol.Pv2InputVoltage).multiplier(10).signed(true)
						.unit("V").build(), //
				new ElementBuilder(0xAA34).device(name).name(EssProtocol.Pv2InputCurrent).multiplier(10).signed(true)
						.unit("A").build(), //
				new ElementBuilder(0xAA35).device(name).name(EssProtocol.Pv2InputPower).multiplier(100).signed(true)
						.unit("W").build(), //
				new ElementBuilder(0xAA36).device(name).name(EssProtocol.Pv2InputEnergy).multiplier(100).signed(true)
						.unit("Wh").build(), //
				new ElementBuilder(0xAA37).device(name).name(EssProtocol.Pv2OutputEnergy).multiplier(100).signed(true)
						.unit("Wh").build()));
		return protocol;
	}

	@Override
	public int getActivePower() {
		return ((SignedIntegerWordElement) getElement(EssProtocol.ActivePower.name())).getValue();
	}

	public SignedIntegerWordElement getReactivePower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.ReactivePower.name());
	}

	public UnsignedShortWordElement getApparentPower() {
		return (UnsignedShortWordElement) getElement(EssProtocol.ApparentPower.name());
	}

	@Override
	public int getAllowedCharge() {
		return ((SignedIntegerWordElement) getElement(EssProtocol.AllowedCharge.name())).getValue();
	}

	@Override
	public int getAllowedDischarge() {
		return ((UnsignedShortWordElement) getElement(EssProtocol.AllowedDischarge.name())).getValue();
	}

	public UnsignedShortWordElement getAllowedApparent() {
		return (UnsignedShortWordElement) getElement(EssProtocol.AllowedApparent.name());
	}

	public SignedIntegerWordElement getSetActivePower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.SetActivePower.name());
	}

	public SignedIntegerWordElement getPv1OutputPower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.Pv1OutputPower.name());
	}

	public SignedIntegerWordElement getPv1InputPower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.Pv1InputPower.name());
	}

	public SignedIntegerWordElement getPv2OutputPower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.Pv2OutputPower.name());
	}

	public SignedIntegerWordElement getPv2InputPower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.Pv2InputPower.name());
	}

	public Boolean getPv1StateInitial() {
		return ((BitsElement) getElement(EssProtocol.Pv1State.name())).getBit(EssProtocol.DcStates.Initial.name())
				.getValue();
	}

	public String getDcState(BitsElement s) {
		if (s.getBit(EssProtocol.DcStates.Initial.name()).getValue()) {
			return EssProtocol.DcStates.Initial.name();
		} else if (s.getBit(EssProtocol.DcStates.Stop.name()).getValue()) {
			return EssProtocol.DcStates.Stop.name();
		} else if (s.getBit(EssProtocol.DcStates.Ready.name()).getValue()) {
			return EssProtocol.DcStates.Ready.name();
		} else if (s.getBit(EssProtocol.DcStates.Running.name()).getValue()) {
			return EssProtocol.DcStates.Running.name();
		} else if (s.getBit(EssProtocol.DcStates.Fault.name()).getValue()) {
			return EssProtocol.DcStates.Fault.name();
		} else if (s.getBit(EssProtocol.DcStates.Debug.name()).getValue()) {
			return EssProtocol.DcStates.Debug.name();
		} else if (s.getBit(EssProtocol.DcStates.Locked.name()).getValue()) {
			return EssProtocol.DcStates.Locked.name();
		}
		return "NO STATUS";
	}

	public String getPv1State() {
		return getDcState((BitsElement) getElement(EssProtocol.Pv1State.name()));
	}

	public String getPv2State() {
		return getDcState((BitsElement) getElement(EssProtocol.Pv2State.name()));
	}

	@Override
	public EssProtocol.GridStates getGridState() {
		if (((UnsignedShortWordElement) getElement(EssProtocol.GridMode.name())).getValue() == 2) {
			return EssProtocol.GridStates.OnGrid;
		}
		return EssProtocol.GridStates.OffGrid;
	}

	private SignedIntegerWordElement getSetWorkState() {
		return ((SignedIntegerWordElement) getElement(EssProtocol.SetWorkState.name()));
	}

	@Override
	public void setActivePower(int power) {
		addToWriteQueue(getSetActivePower(), getSetActivePower().toRegister(power));
	}

	@Override
	public int getSOC() {
		return ((UnsignedShortWordElement) getElement(EssProtocol.BatteryStringSoc.name())).getValue();
	}

	@Override
	public void start() {
		addToWriteQueue(getSetWorkState(), getSetWorkState().toRegister(64));
	}

	@Override
	public void stop() {
		addToWriteQueue(getSetWorkState(), getSetWorkState().toRegister(4));
	}

	@Override
	public String getCurrentDataAsString() {
		return "[" + getElement(EssProtocol.BatteryStringSoc.name()).readable() + "] PWR: ["
				+ getElement(EssProtocol.ActivePower.name()).readable() + " "
				+ getElement(EssProtocol.ReactivePower.name()).readable() + " "
				+ getElement(EssProtocol.ApparentPower.name()).readable() + "] DCPV: ["
				+ getElement(EssProtocol.Pv1OutputPower.name()).readable()
				+ getElement(EssProtocol.Pv2OutputPower.name()).readable() + "] ";
	}
}
