package de.fenecon.openems.controller;

import java.util.HashMap;

import de.fenecon.openems.device.counter.Counter;
import de.fenecon.openems.device.ess.Ess;
import de.fenecon.openems.device.io.IO;

public class ControllerBuilder {
	String name = "";
	String implementation = "";
	Boolean chargeFromAc = null;
	Integer minSoc = null;
	Counter gridCounter = null;
	HashMap<String, Ess> ess = new HashMap<>();
	HashMap<String, IO> io = new HashMap<>();

	public ControllerBuilder() {
	}

	public ControllerBuilder name(String name) {
		this.name = name;
		return this;
	}

	public ControllerBuilder implementation(String implementation) {
		this.implementation = implementation.toLowerCase();
		return this;
	}

	public ControllerBuilder chargeFromAc(boolean chargeFromAc) {
		this.chargeFromAc = chargeFromAc;
		return this;
	}

	public ControllerBuilder minSoc(int minSoc) {
		this.minSoc = minSoc;
		return this;
	}

	public ControllerBuilder gridCounter(Counter gridCounter) {
		this.gridCounter = gridCounter;
		return this;
	}

	public ControllerBuilder addEss(String name, Ess essDevice) {
		this.ess.put(name, essDevice);
		return this;
	}

	public void addIo(String name, IO device) {
		this.io.put(name, device);
	}

	public Controller build() {
		switch (implementation) {
		case "balancing":
			return new Balancing(name, gridCounter, ess, chargeFromAc);
		case "iotest":
			return new IOController(name, io);
		}
		throw new UnsupportedOperationException("ControllerBuilder: " + this.toString() + " is not implemented!");
	}

	@Override
	public String toString() {
		return "ControllerBuilder [name=" + name + ", implementation=" + implementation + ", chargeFromAc="
				+ chargeFromAc + ", minSoc=" + minSoc + ", gridCounter=" + gridCounter + ", ess=" + ess + "]";
	}
}
