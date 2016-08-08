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

import io.openems.channel.modbus.WritableModbusDevice;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public abstract class Ess extends WritableModbusDevice {
	public Ess(String name, String modbusid, int unitid) throws IOException, ParserConfigurationException, SAXException {
		super(name, modbusid, unitid);
	}

	@Override
	public String toString() {
		return "ESS [name=" + name + ", unitid=" + unitid + "]";
	}
}