package de.fenecon.openems.device;

import java.io.IOException;
import java.net.InetAddress;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.fenecon.openems.device.counter.Socomec;
import de.fenecon.openems.device.ess.Commercial;
import de.fenecon.openems.device.io.Wago;

public class DeviceBuilder {
	String name = "";
	String type = "";
	String protocol = "";
	String channel = "";
	int modbusUnit = 0;
	InetAddress ip;

	public DeviceBuilder() {
	}

	public DeviceBuilder name(String name) {
		this.name = name;
		return this;
	}

	public DeviceBuilder type(String type) {
		this.type = type.toLowerCase();
		return this;
	}

	public DeviceBuilder protocol(String protocol) {
		this.protocol = protocol.toLowerCase();
		return this;
	}

	public DeviceBuilder channel(String channel) {
		this.channel = channel.toLowerCase();
		return this;
	}

	public DeviceBuilder modbusUnit(int modbusUnit) {
		this.modbusUnit = modbusUnit;
		return this;
	}

	public DeviceBuilder ip(InetAddress ip) {
		this.ip = ip;
		return this;
	}

	public Device build() throws IOException, ParserConfigurationException, SAXException {
		Device device = null;
		switch (type) {
		case "ess":
			switch (protocol) {
			case "fenecon commercial":
				device = new Commercial(name, channel, modbusUnit);
			}
			break;

		case "counter":
			switch (protocol) {
			case "socomec":
				device = new Socomec(name, channel, modbusUnit);
			}
			break;
		case "io":
			switch (protocol) {
			case "wago":
				device = new Wago(name, channel, modbusUnit, ip);
			}
			break;
		}
		if (device == null) {
			throw new UnsupportedOperationException("DeviceBuilder: " + this.toString() + " is not implemented!");
		}
		device.init();
		return device;
	}
}
