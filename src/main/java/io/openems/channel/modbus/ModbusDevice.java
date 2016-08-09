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

import io.openems.device.Device;
import io.openems.device.protocol.BitElement;
import io.openems.device.protocol.BitsElement;
import io.openems.device.protocol.Element;
import io.openems.device.protocol.ElementRange;
import io.openems.device.protocol.ModbusProtocol;
import io.openems.device.protocol.interfaces.ElementUpdateListener;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public abstract class ModbusDevice extends Device {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(ModbusDevice.class);

	protected final Integer unitid;
	protected final String name;
	protected ModbusProtocol protocol;

	private ModbusProtocol initProtocol;
	private ModbusProtocol mainProtocol;
	protected ModbusProtocol remainingProtocol;

	public ModbusDevice(String name, String channel, int unitid) {
		super(channel);
		this.unitid = unitid;
		this.name = name;

	}

	@Override
	public void init() throws IOException, ParserConfigurationException, SAXException {
		// Initialize protocols
		this.protocol = getProtocol();
		Set<String> allElements = this.protocol.getElementIds();
		this.initProtocol = new ModbusProtocol(); // Init-Protocol
		Set<String> initElements = getInitElements();
		if (initElements != null) {
			for (String id : initElements) {
				initProtocol.addElementRange(protocol.getElement(id).getElementRange());
				allElements.remove(id);
			}
		}
		this.mainProtocol = new ModbusProtocol(); // Main-Protocol
		Set<String> mainElements = getMainElements();
		if (mainElements != null) {
			for (String id : mainElements) {
				mainProtocol.addElementRange(protocol.getElement(id).getElementRange());
				allElements.remove(id);
			}
		}
		this.remainingProtocol = new ModbusProtocol(); // Remaining-Protocol
		if (allElements != null) {
			for (String id : allElements) {
				remainingProtocol.addElementRange(protocol.getElement(id).getElementRange());
				// TODO: split remainingProtocol in small pieces
			}
		}
		for (ElementRange er : initProtocol.getElementRanges()) {
			for (Element<?> e : er.getElements()) {
				if (e instanceof BitsElement) {
					for (Map.Entry<String, BitElement> element : ((BitsElement) e).getBitElements().entrySet()) {
						element.getValue().addListener(new ElementUpdateListener() {

							@Override
							public void elementUpdated(String name, Object value) {
								notifyListeners(name, value);
							}
						});
					}
				} else {
					e.addListener(new ElementUpdateListener() {

						@Override
						public void elementUpdated(String name, Object value) {
							notifyListeners(name, value);
						}
					});
				}
			}
		}
		for (ElementRange er : mainProtocol.getElementRanges()) {
			for (Element<?> e : er.getElements()) {
				if (e instanceof BitsElement) {
					for (Map.Entry<String, BitElement> element : ((BitsElement) e).getBitElements().entrySet()) {
						element.getValue().addListener(new ElementUpdateListener() {

							@Override
							public void elementUpdated(String name, Object value) {
								notifyListeners(name, value);
							}
						});
					}
				} else {
					e.addListener(new ElementUpdateListener() {

						@Override
						public void elementUpdated(String name, Object value) {
							notifyListeners(name, value);
						}
					});
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public Integer getUnitid() {
		return unitid;
	}

	@Override
	public Element<?> getElement(String id) {
		return protocol.getElement(id);
	}

	public void executeInitQuery(ModbusConnection modbusConnection) throws Exception {
		modbusConnection.updateProtocol(this.unitid, this.initProtocol);
	}

	public void executeMainQuery(ModbusConnection modbusConnection) throws Exception {
		modbusConnection.updateProtocol(this.unitid, this.mainProtocol);
	}

	public void executeRemainingQuery(ModbusConnection modbusConnection) throws Exception {
		modbusConnection.updateProtocol(this.unitid, this.remainingProtocol);
	};

	protected abstract ModbusProtocol getProtocol() throws IOException, ParserConfigurationException, SAXException;

	@Override
	public String toString() {
		return "ModbusDevice [name=" + name + ", unitid=" + unitid + "]";
	}

}
