package de.fenecon.femscore.femscore;

import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.device.ess.Ess;
import io.openems.device.ess.EssProtocol.GridStates;
import io.openems.device.protocol.ModbusProtocol;

import java.util.List;
import java.util.Set;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;

public class TestEss extends Ess {

	private int capacity = 0;
	private int activePower = 0;
	private int reactivePower = 0;
	private int soc = 0;
	private int maxCapacity = 0;
	private int allowedDischarge = 0;
	private int allowedCharge = 0;

	public TestEss(int minSoc, int capacity, int soc, int maxCapacity, int allowedDischarge, int allowedCharge) {
		super("", "", 0, minSoc);
		this.capacity = capacity;
		this.soc = soc;
		this.maxCapacity = maxCapacity;
		this.allowedDischarge = allowedDischarge;
		this.allowedCharge = allowedCharge;
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
			int startAddressMessages, Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GridStates getGridState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActivePower(int power) {
		this.activePower = power;
	}

	@Override
	public int getActivePower() {
		return this.activePower;
	}

	@Override
	public void setReactivePower(int power) {
		this.reactivePower = power;
	}

	@Override
	public int getReactivePower() {
		return this.reactivePower;
	}

	@Override
	public int getSOC() {
		return this.soc;
	}

	@Override
	public int getAllowedCharge() {
		return this.allowedCharge;
	}

	@Override
	public int getAllowedDischarge() {
		return this.allowedDischarge;
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
	public int getApparentPower() {
		return getReactivePower() + getActivePower();
	}

	@Override
	public int getMaxCapacity() {
		return this.maxCapacity;
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
