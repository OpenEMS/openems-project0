package io.openems.device.ess;

import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.device.ess.EssProtocol.GridStates;
import io.openems.device.protocol.ModbusProtocol;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;

public class EssCluster extends Ess {
	public EssCluster(String name, String modbusid, int unitid, int minSoc, List<Ess> storages) {
		super(name, modbusid, unitid, minSoc);
		this.storages = storages;
	}

	private List<Ess> storages;

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
		for (Ess storage : storages) {
			if (storage.getGridState() == GridStates.OffGrid) {
				return GridStates.OffGrid;
			}
		}
		return GridStates.OnGrid;
	}

	@Override
	public void setActivePower(int power) {
		// TODO calculate with apparentPower and cos(Phi)
		if (power >= 0) {
			int useableSocSum = 0;
			int maxDischargePower = 0;
			for (Ess storage : storages) {
				if (storage.getUseableSoc() > 0) {
					useableSocSum += storage.getUseableSoc();
					maxDischargePower += storage.getAllowedDischarge();
				}
			}
			if (power > maxDischargePower) {
				power = maxDischargePower;
			}
			Collections.sort(storages, (a, b) -> a.getUseableSoc() - b.getUseableSoc());
			for (int i = 0; i < storages.size(); i++) {
				Ess e = storages.get(i);
				int minP = power;
				for (int j = i + 1; j < storages.size(); j++) {
					if (storages.get(j).getUseableSoc() > 0) {
						minP -= storages.get(j).getAllowedDischarge();
					}
				}
				if (minP < 0) {
					minP = 0;
				}
				int maxP = e.getAllowedDischarge();
				if (power < e.getAllowedDischarge()) {
					maxP = power;
				}
				double diff = maxP - minP;
				if (e.getUseableSoc() > 0) {
					int p = (int) Math.ceil((minP + diff / useableSocSum * e.getUseableSoc()) / 100) * 100;
					e.setActivePower(p);
					power -= p;
				}
			}
		} else {
			int useableSocSum = 0;
			int maxChargePower = 0;
			for (Ess storage : storages) {
				useableSocSum += (100 - storage.getUseableSoc());
				maxChargePower += storage.getAllowedCharge();
			}
			if (power < maxChargePower) {
				power = maxChargePower;
			}
			Collections.sort(storages, (a, b) -> (100 - a.getUseableSoc()) - (100 - b.getUseableSoc()));
			for (int i = 0; i < storages.size(); i++) {
				Ess e = storages.get(i);
				int minP = power;
				for (int j = i + 1; j < storages.size(); j++) {
					minP -= storages.get(j).getAllowedCharge();
				}
				if (minP > 0) {
					minP = 0;
				}
				int maxP = e.getAllowedCharge();
				if (power > e.getAllowedCharge()) {
					maxP = power;
				}
				double diff = maxP - minP;
				int p = (int) Math.floor((minP + diff / useableSocSum * (100 - e.getUseableSoc())) / 100) * 100;
				e.setActivePower(p);
				power -= p;
			}
		}
	}

	@Override
	public void setReactivePower(int power) {
		if (power >= 0) {
			int useableSocSum = 0;
			int maxDischargePower = 0;
			for (Ess storage : storages) {
				if (storage.getUseableSoc() > 0) {
					useableSocSum += storage.getUseableSoc();
					maxDischargePower += storage.getAllowedDischarge();
				}
			}
			if (power > maxDischargePower) {
				power = maxDischargePower;
			}
			Collections.sort(storages, (a, b) -> a.getUseableSoc() - b.getUseableSoc());
			for (int i = 0; i < storages.size(); i++) {
				Ess e = storages.get(i);
				int minP = power;
				for (int j = i + 1; j < storages.size(); j++) {
					if (storages.get(j).getUseableSoc() > 0) {
						minP -= storages.get(j).getAllowedDischarge();
					}
				}
				if (minP < 0) {
					minP = 0;
				}
				int maxP = e.getAllowedDischarge();
				if (power < e.getAllowedDischarge()) {
					maxP = power;
				}
				double diff = maxP - minP;
				if (e.getUseableSoc() > 0) {
					int p = (int) Math.ceil((minP + diff / useableSocSum * e.getUseableSoc()) / 100) * 100;
					e.setActivePower(p);
					power -= p;
				}
			}
		} else {
			if (power < getAllowedCharge()) {
				power = getAllowedCharge();
			}
		}
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
		int capacitanceSum = 0;
		for (Ess storage : storages) {
			capacitanceSum += storage.getCapacity();
		}
		return capacitanceSum / getMaxCapacity() * 100;
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
		for (Ess storage : storages) {
			storage.start();
		}
	}

	@Override
	public void stop() {
		for (Ess storage : storages) {
			storage.stop();
		}
	}

	@Override
	public int getReactivePower() {
		int reactivePower = 0;
		for (Ess storage : storages) {
			reactivePower += storage.getReactivePower();
		}
		return reactivePower;
	}

	@Override
	public int getApparentPower() {
		int apparentPower = 0;
		for (Ess storage : storages) {
			apparentPower += storage.getApparentPower();
		}
		return apparentPower;
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

	@Override
	public int getMaxCapacity() {
		int maxCapacitance = 0;
		for (Ess storage : storages) {
			maxCapacitance += storage.getMaxCapacity();
		}
		return maxCapacitance;
	}

	@Override
	public int getCapacity() {
		int capacitance = 0;
		for (Ess storage : storages) {
			capacitance += storage.getCapacity();
		}
		return capacitance;
	}

	@Override
	public int getUseableCapacity() {
		int useableCapacity = 0;
		for (Ess storage : storages) {
			useableCapacity += storage.getUseableCapacity();
		}
		return useableCapacity;
	}

}
