package io.openems.controller;

import io.openems.device.ess.Ess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO OSGi import org.openmuc.j60870.Connection;
//TODO OSGi import org.openmuc.j60870.IeDoubleCommand;
//TODO OSGi import org.openmuc.j60870.IeShortFloat;

public class MiniTest extends Controller {

	private final Map<String, Ess> essDevices;

	public MiniTest(String name, Map<String, Ess> essDevices) {
		super(name);
		this.essDevices = essDevices;
	}

	@Override
	public void run() {
		// Mini mini = (Mini) essDevices.values().iterator().next();

		Ess ess = essDevices.values().iterator().next();
		System.out.println(ess.getSOC());
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}
//
//	@Override
//	public void handleSetPoint(int function, IeShortFloat informationElement) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void handleCommand(int function, IeDoubleCommand informationElement) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public List<IecElementOnChangeListener> createChangeListeners(int startAddressMeassurements,
//			int startAddressMessages, Connection connection) {
//		// TODO Auto-generated method stub
//		return new ArrayList<IecElementOnChangeListener>();
//	}

}
