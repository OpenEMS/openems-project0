package de.fenecon.openems.device;

import de.fenecon.openems.device.counter.Socomec;
import de.fenecon.openems.device.ess.Commercial;

public class DeviceBuilder {
	String name = "";
	String type = "";
	String protocol = "";
	String channel = "";
	int modbusUnit = 0;

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

	public Device build() {
		switch (type) {
		case "ess":
			switch (protocol) {
			case "fenecon commercial":
				return new Commercial(name, channel, modbusUnit);
			}
			break;

		case "counter":
			switch (protocol) {
			case "socomec":
				return new Socomec(name, channel, modbusUnit);
			}
			break;
		}
		throw new UnsupportedOperationException("DeviceBuilder: " + this.toString() + " is not implemented!");
	}
}
