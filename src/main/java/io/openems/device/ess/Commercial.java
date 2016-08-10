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

	public Commercial(String name, String channel, int unitid) throws IOException, ParserConfigurationException,
			SAXException {
		super(name, channel, unitid);
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
		protocol.addElementRange(new ElementRange(0x0210, new ElementBuilder(0x0210).name(EssProtocol.ActivePower)
				.multiplier(100).signed(true).unit("W").build(), new ElementBuilder(0x0211)
				.name(EssProtocol.ReactivePower).multiplier(100).signed(true).unit("VA").build(),//
				new ElementBuilder(0x0212).name(EssProtocol.ApparentPower).multiplier(100).unit("Var").build(),//
				new ElementBuilder(0x0213).device(name).type(ElementType.PLACEHOLDER).intLength(0x230 - 0x213).build(),//
				new ElementBuilder(0x0230).device(name).name(EssProtocol.AllowedCharge).multiplier(100).signed(true)
						.unit("W").build(),//
				new ElementBuilder(0x0231).device(name).name(EssProtocol.AllowedDischarge).multiplier(100).unit("W")
						.build(), //
				new ElementBuilder(0x0232).device(name).name(EssProtocol.AllowedApparent).multiplier(100).unit("Var")
						.build()));
		protocol.addElementRange(new ElementRange(0x0101, new ElementBuilder(0x0101).device(name)
				.name(EssProtocol.SystemState) //
				.bit(new BitElement(1, EssProtocol.SystemStates.Stop.name())) //
				.bit(new BitElement(2, EssProtocol.SystemStates.PvCharging.name())) //
				.bit(new BitElement(3, EssProtocol.SystemStates.Standby.name())) //
				.bit(new BitElement(4, EssProtocol.SystemStates.Running.name())) //
				.bit(new BitElement(5, EssProtocol.SystemStates.Fault.name())) //
				.bit(new BitElement(6, EssProtocol.SystemStates.Debug.name())).build()));
		protocol.addElementRange(new ElementRange(0x0106, new ElementBuilder(0X0106).device(name)
				.name(EssProtocol.GridMode).build()));
		protocol.addElementRange(new ElementRange(0x0501, //
				new ElementBuilder(0x0501).device(name).name(EssProtocol.SetActivePower).multiplier(100).signed(true)
						.unit("W").build()));
		protocol.addElementRange(new ElementRange(0x1402, new ElementBuilder(0x1402).device(name)
				.name(EssProtocol.BatteryStringSoc).unit("%").build()));
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
		protocol.addElementRange(new ElementRange(0x0150, new ElementBuilder(0x0150).device(name)
				.name(EssProtocol.SwitchStates) //
				.bit(new BitElement(1, EssProtocol.Switches.DCMain.name())) //
				.bit(new BitElement(2, EssProtocol.Switches.DCPrecharge.name())) //
				.bit(new BitElement(3, EssProtocol.Switches.ACBreaker.name())) //
				.bit(new BitElement(4, EssProtocol.Switches.ACMain.name())) //
				.bit(new BitElement(5, EssProtocol.Switches.ACPrecharge.name())).build()));
		protocol.addElementRange(new ElementRange(0x0500, new ElementBuilder(0x0500).device(name)
				.name(EssProtocol.SetWorkState) //
				.signed(true)//
				.build()));
		protocol.addElementRange(new ElementRange(0x1100, new ElementBuilder(0x1100).device(name)
				.name(EssProtocol.BatteryState).bit(new BitElement(0, EssProtocol.BatteryStates.Initial.name()))//
				.bit(new BitElement(1, EssProtocol.BatteryStates.Stop.name()))//
				.bit(new BitElement(2, EssProtocol.BatteryStates.StartingUp.name()))//
				.bit(new BitElement(3, EssProtocol.BatteryStates.Running.name()))//
				.bit(new BitElement(4, EssProtocol.BatteryStates.Fault.name())).build()));
		protocol.addElementRange(new ElementRange(0x0105, new ElementBuilder(0x0105).device(name)
				.name(EssProtocol.InverterState).bit(new BitElement(0, EssProtocol.InverterStates.Initial.name()))
				.bit(new BitElement(1, EssProtocol.InverterStates.Fault.name()))//
				.bit(new BitElement(2, EssProtocol.InverterStates.Stop.name()))//
				.bit(new BitElement(3, EssProtocol.InverterStates.Standby.name()))//
				.bit(new BitElement(4, EssProtocol.InverterStates.GridMonitoring.name()))//
				.bit(new BitElement(5, EssProtocol.InverterStates.Ready.name()))//
				.bit(new BitElement(6, EssProtocol.InverterStates.Running.name()))//
				.bit(new BitElement(7, EssProtocol.InverterStates.Debug.name())).build()));
		protocol.addElementRange(new ElementRange(0x0208, new ElementBuilder(0x0208).device(name)
				.name(EssProtocol.ChargeEnergy).unit("Wh").length(ElementLength.DOUBLEWORD).multiplier(100).build(),
				new ElementBuilder(0x020A).device(name).name(EssProtocol.DischargeEnergy).unit("Wh").multiplier(100)
						.length(ElementLength.DOUBLEWORD).build()));
		protocol.addElementRange(new ElementRange(0x1418, new ElementBuilder(0x1418).device(name)
				.name(EssProtocol.BatteryChargeEnergy).unit("Wh").length(ElementLength.DOUBLEWORD).build(),
				new ElementBuilder(0x141A).device(name).name(name).unit("Wh").length(ElementLength.DOUBLEWORD).build()));
		return protocol;
	}

	public UnsignedShortWordElement getSoc() {
		return (UnsignedShortWordElement) getElement(EssProtocol.BatteryStringSoc.name());
	}

	public SignedIntegerWordElement getActivePower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.ActivePower.name());
	}

	public SignedIntegerWordElement getReactivePower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.ReactivePower.name());
	}

	public UnsignedShortWordElement getApparentPower() {
		return (UnsignedShortWordElement) getElement(EssProtocol.ApparentPower.name());
	}

	public SignedIntegerWordElement getAllowedCharge() {
		return (SignedIntegerWordElement) getElement(EssProtocol.AllowedCharge.name());
	}

	public UnsignedShortWordElement getAllowedDischarge() {
		return (UnsignedShortWordElement) getElement(EssProtocol.AllowedDischarge.name());
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

	public EssProtocol.GridStates getGridState() {
		if (((UnsignedShortWordElement) getElement(EssProtocol.GridMode.name())).getValue() == 2) {
			return EssProtocol.GridStates.OnGrid;
		}
		return EssProtocol.GridStates.OffGrid;
	}

	public SignedIntegerWordElement getSetWorkState() {
		return ((SignedIntegerWordElement) getElement(EssProtocol.SetWorkState.name()));
	}
}
