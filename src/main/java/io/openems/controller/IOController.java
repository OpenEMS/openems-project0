package io.openems.controller;

import io.openems.device.io.IO;
import io.openems.device.io.Wago;

import java.util.HashMap;

public class IOController extends Controller {

	private final HashMap<String, IO> io;

	public IOController(String name, HashMap<String, IO> io) {
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
		// wago.addToWriteQueue(wago.getBitElement("DigitalOutput_1_1"),
		// !wago.getBitElement("DigitalOutput_1_1")
		// .getValue());
		// wago.addToWriteQueue(wago.getBitElement("DigitalOutput_1_2"),
		// !wago.getBitElement("DigitalOutput_1_2")
		// .getValue());
	}

	public HashMap<String, IO> getIo() {
		return io;
	}

}
