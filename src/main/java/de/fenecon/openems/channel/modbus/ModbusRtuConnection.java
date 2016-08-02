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
package de.fenecon.openems.channel.modbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class ModbusRtuConnection extends ModbusConnection {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(ModbusRtuConnection.class);

	private final String serialinterface;
	private final String baudrate;
	private final int databits;
	private final String parity;
	private final int stopbits;
	private SerialConnection con = null;

	public ModbusRtuConnection(String serialinterface, String baudrate, int databits, String parity, int stopbits,
			int cycle) {
		super(cycle);
		this.serialinterface = serialinterface;
		this.baudrate = baudrate;
		this.databits = databits;
		this.parity = parity;
		this.stopbits = stopbits;
	}

	@Override
	public void dispose() {
		if (con != null && con.isOpen()) {
			con.close();
			con = null;
		}
	}

	@Override
	protected ModbusTransaction getTransaction() throws Exception {
		if (con == null) {
			SerialParameters params = new SerialParameters();
			params.setPortName(this.serialinterface);
			params.setBaudRate(this.baudrate);
			params.setDatabits(this.databits);
			params.setParity(this.parity);
			params.setStopbits(this.stopbits);
			params.setEncoding(Modbus.SERIAL_ENCODING_RTU);
			params.setEcho(false);
			con = new SerialConnection(params);
		}
		if (!con.isOpen()) {
			con.open();
		}
		ModbusSerialTransaction trans = new ModbusSerialTransaction(con);
		return trans;
	}

	@Override
	public void close() {
		if (con == null) {
			if (con.isOpen()) {
				con.close();
			}
		}
	}

	@Override
	public String toString() {
		return "ModbusRtuConnection [serialinterface=" + serialinterface + ", baudrate=" + baudrate + ", databits="
				+ databits + ", parity=" + parity + ", stopbits=" + stopbits + "]";
	}
}
