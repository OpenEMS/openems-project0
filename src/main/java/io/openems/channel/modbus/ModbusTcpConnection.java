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
package io.openems.channel.modbus;

import java.net.InetAddress;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

public class ModbusTcpConnection extends ModbusConnection {
	private final Integer port;
	private final InetAddress ip;
	private TCPMasterConnection con = null;

	public ModbusTcpConnection(InetAddress ip, int cycle) {
		super(cycle);
		this.ip = ip;
		this.port = 502;
	}

	@Override
	public void dispose() {
		if (con != null) {
			con.close();
		}
	}

	@Override
	protected ModbusTransaction getTransaction() throws Exception {
		if (con == null) {
			con = new TCPMasterConnection(this.ip);
			con.setPort(this.port);
		}
		if (!con.isConnected()) {
			con.connect();
		}
		ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
		return trans;
	}

	@Override
	public void close() {
		if (con != null && con.isConnected()) {
			con.close();
			con = null;
		}
	}

	@Override
	public String toString() {
		return "ModbusTcpConnection [port=" + port + ", ip=" + ip + "]";
	}
}
