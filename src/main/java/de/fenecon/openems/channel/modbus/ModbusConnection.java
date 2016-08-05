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

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteCoilRequest;
import com.ghgande.j2mod.modbus.msg.WriteCoilResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.procimg.Register;

import de.fenecon.openems.device.protocol.Element;
import de.fenecon.openems.device.protocol.ElementRange;
import de.fenecon.openems.device.protocol.ModbusProtocol;
import de.fenecon.openems.device.protocol.interfaces.DoublewordElement;
import de.fenecon.openems.device.protocol.interfaces.WordElement;

public abstract class ModbusConnection implements AutoCloseable {
	private final static Logger log = LoggerFactory.getLogger(ModbusConnection.class);

	protected final int cycle; // length of a query cycle in milliseconds

	public ModbusConnection(int cycle) {
		this.cycle = cycle;
	}

	protected abstract ModbusTransaction getTransaction() throws Exception;

	public abstract void dispose();

	public int getCycle() {
		return cycle;
	}

	private synchronized Register[] singleQuery(int unitid, int ref, int count) throws Exception {
		ModbusTransaction trans = getTransaction();
		ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(ref, count);
		req.setUnitID(unitid);
		trans.setRequest(req);
		trans.execute();
		ModbusResponse res = trans.getResponse();
		if (res instanceof ReadMultipleRegistersResponse) {
			ReadMultipleRegistersResponse mres = (ReadMultipleRegistersResponse) res;
			return mres.getRegisters();
		} else {
			throw new ModbusException(res.toString());
		}
	}

	public Register[] query(int unitid, int ref, int count) throws Exception {
		try {
			return singleQuery(unitid, ref, count);
		} catch (Exception e) {
			log.info("Query-Exception: {}. Try again with new connection", e.getMessage());
			this.close();
			return singleQuery(unitid, ref, count);
		}
	}

	public void updateProtocol(int unitid, ModbusProtocol protocol) throws Exception {
		for (ElementRange elementRange : protocol.getElementRanges()) {
			Register[] registers = query(unitid, elementRange.getStartAddress(), elementRange.getTotalLength());
			int position = 0;
			for (Element<?> element : elementRange.getElements()) {
				int length = element.getLength();
				if (element instanceof WordElement) {
					((WordElement) element).update(registers[position]);
				} else if (element instanceof DoublewordElement) {
					((DoublewordElement) element).update(registers[position], registers[position + 1]);
				}
				position += length;
			}
		}
	}

	private synchronized void singleWrite(int unitid, int ref, Register reg) throws Exception {
		ModbusTransaction trans = getTransaction();
		WriteSingleRegisterRequest req = new WriteSingleRegisterRequest(ref, reg);
		req.setUnitID(unitid);
		trans.setRequest(req);
		trans.execute();
		ModbusResponse res = trans.getResponse();
		if (!(res instanceof WriteSingleRegisterResponse)) {
			throw new ModbusException(res.toString());
		}
	}

	public void write(int unitid, int ref, Register reg) throws Exception {
		try {
			singleWrite(unitid, ref, reg);
		} catch (Exception e) {
			log.info("Write-Exception: {}. Try again with new connection", e.getMessage());
			this.close();
			singleWrite(unitid, ref, reg);
		}
	}

	private synchronized void singleWrite(int unitid, int ref, Register[] regs) throws Exception {
		ModbusTransaction trans = getTransaction();
		WriteMultipleRegistersRequest req = new WriteMultipleRegistersRequest(ref, regs);
		req.setUnitID(unitid);
		trans.setRequest(req);
		trans.execute();
		ModbusResponse res = trans.getResponse();
		if (!(res instanceof WriteMultipleRegistersResponse)) {
			throw new ModbusException(res.toString());
		}
	}

	public void write(int unitid, int ref, Register[] regs) throws Exception {
		try {
			singleWrite(unitid, ref, regs);
		} catch (Exception e) {
			log.info("Write-Exception: {}. Try again with new connection", e.getMessage());
			this.close();
			singleWrite(unitid, ref, regs);
		}
	}

	public synchronized void write(int unitid, int ref, boolean value) throws Exception {
		System.out.println(ref + " --- " + value);
		ModbusTransaction trans = getTransaction();
		WriteCoilRequest req = new WriteCoilRequest(ref, value);
		req.setUnitID(unitid);
		trans.setRequest(req);
		trans.execute();
		ModbusResponse res = trans.getResponse();
		if (!(res instanceof WriteCoilResponse)) {
			throw new ModbusException(res.toString());
		}
	}

	@Override
	public abstract void close();
}
