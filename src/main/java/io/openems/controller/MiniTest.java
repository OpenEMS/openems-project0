package io.openems.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;

import io.openems.api.iec.ConnectionListener;
import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.device.ess.Ess;
import io.openems.element.InvalidValueExcecption;

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
		try {
			System.out.println(ess.getSOC());
		} catch (InvalidValueExcecption e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

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
			int startAddressMessages, ConnectionListener connection, boolean negate) {
		// TODO Auto-generated method stub
		return new ArrayList<IecElementOnChangeListener>();
	}

}
