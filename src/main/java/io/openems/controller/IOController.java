package io.openems.controller;

import io.openems.App;
import io.openems.device.inverter.SolarLog;
import io.openems.device.io.IO;
import io.openems.device.protocol.UnsignedIntegerDoublewordElement;
import io.openems.device.protocol.UnsignedShortWordElement;
import io.openems.element.type.IntegerType;
import io.openems.element.type.LongType;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class IOController extends Controller {

	private final HashMap<String, IO> io;
	private SolarLog sl;
	private long count = 0;

	public IOController(String name, HashMap<String, IO> io) {
		super(name);
		this.io = io;
	}

	@Override
	public void init() {
		try {
			sl = (SolarLog) App.getConfig().getDevices().get("sl0");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UnsignedShortWordElement setLimitType = (UnsignedShortWordElement) sl.getElement("SetLimitType");
		sl.addToWriteQueue(setLimitType, setLimitType.toRegister(new IntegerType(2)));
		UnsignedShortWordElement setLimit = (UnsignedShortWordElement) sl.getElement("SetLimit");
		sl.addToWriteQueue(setLimit, setLimit.toRegister(new IntegerType(35)));
		UnsignedIntegerDoublewordElement placeholder = (UnsignedIntegerDoublewordElement) sl.getElement("Placeholder");
		sl.addToWriteQueue(placeholder, placeholder.toRegister(new LongType(0)));
		UnsignedIntegerDoublewordElement watchdog = (UnsignedIntegerDoublewordElement) sl.getElement("WatchDog");
		sl.addToWriteQueue(watchdog, watchdog.toRegister(new LongType(System.currentTimeMillis())));
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

		// UnsignedShortWordElement setLimit = (UnsignedShortWordElement)
		// sl.getElement("SetLimit");
		// sl.addToWriteQueue(setLimit, setLimit.toRegister(new
		// IntegerType((int) (25 + count))));
		// UnsignedIntegerDoublewordElement watchdog =
		// (UnsignedIntegerDoublewordElement) sl.getElement("WatchDog");
		// sl.addToWriteQueue(watchdog, watchdog.toRegister(new
		// LongType(count)));
		// count++;
	}

	public HashMap<String, IO> getIo() {
		return io;
	}

}
