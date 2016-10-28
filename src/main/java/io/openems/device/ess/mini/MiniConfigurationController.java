package io.openems.device.ess.mini;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;

import io.openems.api.iec.ConnectionListener;
import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.controller.Controller;

public class MiniConfigurationController extends Controller {

	private Mini mini;

	public MiniConfigurationController(String name, Mini mini) {
		super(name);
		this.mini = mini;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {

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
		return new ArrayList<>();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
