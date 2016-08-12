package io.openems.device.io;

import io.openems.channel.modbus.WritableModbusDevice;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public abstract class IO extends WritableModbusDevice {

	public IO(String name, String channel, int unitid) throws IOException, ParserConfigurationException, SAXException {
		super(name, channel, unitid);
	}

	@Override
	public String toString() {
		return "Io [name=" + name + ", unitid=" + unitid + "]";
	}

	public abstract void writeDigitalValue(String output, boolean value);

	public abstract boolean readDigitalValue(String channel);
}
