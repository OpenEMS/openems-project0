package io.openems.device.ess.mini;

import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.controller.Controller;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.InformationElement;

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
	public void handleSetPoint(int function, InformationElement informationElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCommand(int function, InformationElement informationElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IecElementOnChangeListener> createChangeListeners(int startAddressMeassurements,
			int startAddressMessages, Connection connection) {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

}
