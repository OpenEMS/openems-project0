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
package io.openems.device.counter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.api.iec.ConnectionListener;
import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.device.protocol.ElementBuilder;
import io.openems.device.protocol.ElementLength;
import io.openems.device.protocol.ElementRange;
import io.openems.device.protocol.ModbusProtocol;
import io.openems.device.protocol.SignedIntegerDoublewordElement;
import io.openems.device.protocol.UnsignedIntegerDoublewordElement;
import io.openems.element.InvalidValueExcecption;

public class Socomec extends Counter {

	private final static Logger log = LoggerFactory.getLogger(Socomec.class);

	public Socomec(String name, String channel, int unitid) {
		super(name, channel, unitid);
	}

	@Override
	public String toString() {
		return "Socomec [name=" + name + ", unitid=" + unitid + "]";
	}

	@Override
	protected ModbusProtocol getProtocol() {
		ModbusProtocol protocol = new ModbusProtocol(name);
		protocol.addElementRange(new ElementRange(0xc568,
				new ElementBuilder(0xc568, name).name(CounterProtocol.ActivePower).multiplier(10).signed(true)
						.length(ElementLength.DOUBLEWORD).unit("W").build(),
				new ElementBuilder(0xc56a, name).name(CounterProtocol.ReactivePower).multiplier(10).signed(true)
						.length(ElementLength.DOUBLEWORD).unit("VA").build(),
				new ElementBuilder(0xc56c, name).name(CounterProtocol.ApparentPower).multiplier(10)
						.length(ElementLength.DOUBLEWORD).unit("Var").build()));
		protocol.addElementRange(new ElementRange(0xc652,
				new ElementBuilder(0xc652, name).name(CounterProtocol.ActivePositiveEnergy)
						.length(ElementLength.DOUBLEWORD).unit("kWh").build(),
				new ElementBuilder(0xc654, name).name(CounterProtocol.ReactivePositiveEnergy)
						.length(ElementLength.DOUBLEWORD).unit("kvarh").build(),
				new ElementBuilder(0xc656, name).name(CounterProtocol.ApparentEnergy).length(ElementLength.DOUBLEWORD)
						.unit("kVAh").build(),
				new ElementBuilder(0xc658, name).name(CounterProtocol.ActiveNegativeEnergy)
						.length(ElementLength.DOUBLEWORD).unit("kWh").build(),
				new ElementBuilder(0xc65a, name).name(CounterProtocol.ReactiveNegativeEnergy)
						.length(ElementLength.DOUBLEWORD).unit("kvarh").build()));
		return protocol;
	}

	@Override
	public Set<String> getInitElements() {
		return null;
	}

	@Override
	public Set<String> getMainElements() {
		return new HashSet<String>(Arrays.asList( //
				CounterProtocol.ActivePower.name(), //
				CounterProtocol.ReactivePower.name(), //
				CounterProtocol.ApparentPower.name()));
	}

	@Override
	public int getActivePower() throws InvalidValueExcecption {
		return ((SignedIntegerDoublewordElement) getElement(CounterProtocol.ActivePower.name())).getValue().toInteger();
	}

	public SignedIntegerDoublewordElement getReactivePower() {
		return (SignedIntegerDoublewordElement) getElement(CounterProtocol.ReactivePower.name());
	}

	public UnsignedIntegerDoublewordElement getApparentPower() {
		return (UnsignedIntegerDoublewordElement) getElement(CounterProtocol.ApparentPower.name());
	}

	public UnsignedIntegerDoublewordElement getActivePositiveEnergy() {
		return (UnsignedIntegerDoublewordElement) getElement(CounterProtocol.ActivePositiveEnergy.name());
	}

	public UnsignedIntegerDoublewordElement getActiveNegativeEnergy() {
		return (UnsignedIntegerDoublewordElement) getElement(CounterProtocol.ActiveNegativeEnergy.name());
	}

	@Override
	public String getCurrentDataAsString() {
		try {
			return "COUNTER: [" + getActivePower() + "W " + getElement(CounterProtocol.ReactivePower.name()).readable()
					+ " " + getElement(CounterProtocol.ApparentPower.name()).readable() + " +"
					+ getElement(CounterProtocol.ActivePositiveEnergy.name()).readable() + " -"
					+ getElement(CounterProtocol.ActiveNegativeEnergy.name()).readable() + "] ";
		} catch (InvalidValueExcecption e) {
			log.error("Invalid Value", e);
			return "COUNTER " + getName() + " has invalid Values ";
		}
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
			int startAddressMessages, ConnectionListener connection) {
		// TODO Auto-generated method stub
		return null;
	}
}
