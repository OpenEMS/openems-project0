package de.fenecon.openems.device.io;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.fenecon.openems.channel.modbus.WritableModbusDevice;

public abstract class IO extends WritableModbusDevice {

	public IO(String name, String channel, int unitid) throws IOException, ParserConfigurationException, SAXException {
		super(name, channel, unitid);
	}

	@Override
	public String toString() {
		return "Io [name=" + name + ", unitid=" + unitid + "]";
	}
}
