package io.openems.controller;

import java.util.HashMap;

import io.openems.device.io.IO;
import io.openems.device.io.Wago;

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
		wago.addToWriteQueue(wago.getBitElement("DigitalOutput_1_1"),
				!wago.getBitElement("DigitalOutput_1_1").getValue().toBoolean());
		wago.addToWriteQueue(wago.getBitElement("DigitalOutput_1_2"),
				!wago.getBitElement("DigitalOutput_1_2").getValue().toBoolean());
		// try {
		// SolarLog sl = (SolarLog) App.getConfig().getDevices().get("sl0");
		// UnsignedIntegerDoublewordElement pac =
		// (UnsignedIntegerDoublewordElement) sl.getElement("PAC");
		// System.out.println(pac.getValue());
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ParserConfigurationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (SAXException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	public HashMap<String, IO> getIo() {
		return io;
	}

}
