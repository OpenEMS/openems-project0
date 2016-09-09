package io.openems.device.ess;

import io.openems.device.ess.EssProtocol.GridStates;
import io.openems.device.protocol.ModbusProtocol;

import java.util.List;
import java.util.Set;

//TODO OSGi import org.openmuc.j60870.Connection;
//TODO OSGi import org.openmuc.j60870.IeDoubleCommand;
//TODO OSGi import org.openmuc.j60870.IeShortFloat;

public class EssCluster extends Ess {
	public EssCluster(String name, String modbusid, int unitid, int minSoc) {
		super(name, modbusid, unitid, minSoc);
		// TODO Auto-generated constructor stub
	}

	private List<Ess> storages;
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
//		return null;
//	}

	@Override
	public GridStates getGridState() {
		for (Ess storage : storages) {
			if (storage.getGridState() == GridStates.OffGrid) {
				return GridStates.OffGrid;
			}
		}
		return GridStates.OnGrid;
	}

	@Override
	public void setActivePower(int power) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getActivePower() {
		int activePower = 0;
		for (Ess storage : storages) {
			activePower += storage.getActivePower();
		}
		return activePower;
	}

	@Override
	public int getSOC() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAllowedCharge() {
		int maxChargePower = 0;
		for (Ess storage : storages) {
			maxChargePower += storage.getAllowedCharge();
		}
		return maxChargePower;
	}

	@Override
	public int getAllowedDischarge() {
		int maxDischargePower = 0;
		for (Ess storage : storages) {
			maxDischargePower += storage.getAllowedDischarge();
		}
		return maxDischargePower;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getReactivePower() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getApparentPower() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> getWriteElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ModbusProtocol getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getInitElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getMainElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentDataAsString() {
		// TODO Auto-generated method stub
		return null;
	}

}
