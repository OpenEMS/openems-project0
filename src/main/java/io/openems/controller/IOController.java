package io.openems.controller;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;

import io.openems.api.iec.ConnectionListener;
import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.device.inverter.SolarLog;

public class IOController extends Controller {

	// private final HashMap<String, IO> io;
	private SolarLog sl;

	public IOController(String name) {
		super(name);
		// this.io = io;
	}

	@Override
	public void init() {
		// sl = (SolarLog) App.getConfig().getDevices().get("sl0");
		// UnsignedShortWordElement setLimitType = (UnsignedShortWordElement)
		// sl.getElement("SetLimitType");
		// sl.addToWriteQueue(setLimitType, setLimitType.toRegister(new
		// IntegerType(2)));
		// UnsignedShortWordElement setLimit = (UnsignedShortWordElement)
		// sl.getElement("SetLimit");
		// sl.addToWriteQueue(setLimit, setLimit.toRegister(new
		// IntegerType(30)));
	}

	@Override
	public void run() {
		// Wago wago = (Wago) io.values().iterator().next();
		// System.out.println(wago.getBitElement("DigitalInput_1_1").getValue());
		// System.out.println(wago.getBitElement("DigitalInput_1_2").getValue());
		// System.out.println(wago.getBitElement("DigitalOutput_1_1").getValue());
		// System.out.println(wago.getBitElement("DigitalOutput_1_2").getValue());
		// wago.addToWriteQueue(wago.getBitElement("DigitalOutput_1_1"),
		// !wago.getBitElement("DigitalOutput_1_1").getValue().toBoolean());
		// wago.addToWriteQueue(wago.getBitElement("DigitalOutput_1_2"),
		// !wago.getBitElement("DigitalOutput_1_2").getValue().toBoolean());
	}

	// public HashMap<String, IO> getIo() {
	// return io;
	// }

	@Override
	public void handleSetPoint(int function, IeShortFloat informationElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCommand(int function, IeDoubleCommand informationElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IecElementOnChangeListener> createChangeListeners(int startAddressMeassurements,
			int startAddressMessages, ConnectionListener connection, boolean negate) {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

}
