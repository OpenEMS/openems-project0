package de.fenecon.openems.controller;

import java.util.HashMap;

import de.fenecon.openems.device.io.Io;
import de.fenecon.openems.device.io.Wago;

public class IOController extends Controller {

	private final HashMap<String, Io> io;

	public IOController(String name, HashMap<String, Io> io) {
		super(name);
		this.io = io;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		Wago wago = (Wago) io.values().iterator().next();
		System.out.println(wago.getBitElement("DigitalInput_1_1").getValue());
		System.out.println(wago.getBitElement("DigitalInput_1_2").getValue());
		System.out.println(wago.getBitElement("DigitalOutput_1_1").getValue());
		System.out.println(wago.getBitElement("DigitalOutput_1_2").getValue());
		wago.addToWriteQueue(wago.getBitElement("DigitalOutput_1_2"), false);
	}
}
