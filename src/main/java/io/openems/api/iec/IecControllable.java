package io.openems.api.iec;

import java.util.List;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.InformationElement;

public interface IecControllable {
	public void handleSetPoint(int function, InformationElement informationElement);

	public void handleCommand(int function, InformationElement informationElement);

	public List<IecElementOnChangeListener> createChangeListeners(int startAddressMeassurements,
			int startAddressMessages, Connection connection);
}
