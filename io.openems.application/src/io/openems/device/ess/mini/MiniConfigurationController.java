package io.openems.device.ess.mini;

import io.openems.controller.Controller;

import java.util.ArrayList;
import java.util.List;

//TODO OSGi import org.openmuc.j60870.Connection;
//TODO OSGi import org.openmuc.j60870.IeDoubleCommand;
//TODO OSGi import org.openmuc.j60870.IeShortFloat;

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
//		return new ArrayList<>();
//	}

}
