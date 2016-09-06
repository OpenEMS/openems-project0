package io.openems.controller;

import io.openems.OpenemsGlobal;
import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.device.ess.Ess;
import io.openems.device.inverter.SolarLog;
import io.openems.device.io.IO;
import io.openems.device.protocol.UnsignedIntegerDoublewordElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;

public class IOController extends Controller {

	private final HashMap<String, IO> io;
	private SolarLog sl;

	public IOController(String name, HashMap<String, IO> io) {
		super(name);
		this.io = io;
	}

	@Override
	public void init() {
		sl = (SolarLog) OpenemsGlobal.getConfig().getDevices().get("sl0");
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

		UnsignedIntegerDoublewordElement pac = (UnsignedIntegerDoublewordElement) sl.getElement("PAC");
		System.out.print(pac.readable());
		UnsignedIntegerDoublewordElement dailyYield = (UnsignedIntegerDoublewordElement) sl.getElement("DailyYield");
		System.out.println("\t" + dailyYield.readable());
		Ess ess = (Ess) OpenemsGlobal.getConfig().getDevices().get("ess0");
		System.out.println(ess.getActivePower());
	}

	public HashMap<String, IO> getIo() {
		return io;
	}

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
			int startAddressMessages, Connection connection) {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

}
