package io.openems.api.iec;

import java.util.List;

import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;

public interface IecControllable {
	public void handleSetPoint(int function, IeShortFloat informationElement);

	public void handleCommand(int function, IeDoubleCommand informationElement);

	public List<IecElementOnChangeListener> createChangeListeners(int startAddressMeassurements,
			int startAddressMessages, ConnectionListener connection);

	public String getName();
}
