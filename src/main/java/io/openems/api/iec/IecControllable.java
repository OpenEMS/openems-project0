package io.openems.api.iec;

import io.openems.element.ElementOnChangeListener;

import java.util.List;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.InformationElement;
import org.openmuc.j60870.InformationObject;

public interface IecControllable {
	public List<InformationObject> getMeassurements(int startAddress);

	public List<InformationObject> getMessages(int startAddress);

	public void handleSetPoint(int function, InformationElement informationElement);

	public void handleCommand(int function, InformationElement informationElement);

	public List<ElementOnChangeListener> createChangeListeners(int startAddress, Connection connection);
}
