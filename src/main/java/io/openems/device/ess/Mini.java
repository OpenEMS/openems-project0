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

import io.openems.device.ess.EssProtocol.GridStates;
import io.openems.device.protocol.ElementBuilder;
import io.openems.device.protocol.ElementRange;
import io.openems.device.protocol.ModbusProtocol;
import io.openems.device.protocol.UnsignedShortWordElement;

import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class Mini extends Ess {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(Mini.class);

	public Mini(String name, String channel, int unitid, int minSoc) throws IOException, ParserConfigurationException,
			SAXException {
		super(name, channel, unitid, minSoc);
	}

	@Override
	public String toString() {
		return "Mini [name=" + name + ", unitid=" + unitid + "]";
	}

	@Override
	protected ModbusProtocol getProtocol() {
		ModbusProtocol protocol = new ModbusProtocol(name);
		protocol.addElementRange(new ElementRange(4812, new ElementBuilder(4812, name)
				.name(EssProtocol.BatteryStringSoc).unit("%").build()));
		return protocol;
	}

	public UnsignedShortWordElement getSoc() {
		return (UnsignedShortWordElement) getElement(EssProtocol.BatteryStringSoc.name());
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
}
