package io.openems.controller;

import io.openems.device.counter.Counter;
import io.openems.device.ess.Ess;
import io.openems.device.ess.EssProtocol;
import io.openems.device.io.IO;
import io.openems.device.protocol.BitElement;
import io.openems.device.protocol.BitsElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnBAGController extends Controller {

	private final static Logger log = LoggerFactory.getLogger(EnBAGController.class);

	private final Counter gridCounter;
	private final Map<String, Ess> essDevices;
	private final boolean allowChargeFromAC;
	private final int maxGridFeedPower;
	private int lastActivePower;
	private Ess activeEss = null;
	private String pvOnGridSwitch;
	private String pvOffGridSwitch;
	private Map<String, String> essOffGridSwitches;
	private String primaryOffGridEss;
	private IO io;
	private List<Ess> aviableEss;

	public EnBAGController(String name, Counter gridCounter, Map<String, Ess> essDevices, boolean allowChargeFromAc,
			int maxGridFeedPower, String pvOnGridSwitch, String pvOffGridSwitch,
			Map<String, String> essOffGridSwitches, String primaryOffGridEss, IO io) {
		super(name);
		this.gridCounter = gridCounter;
		this.essDevices = essDevices;
		this.allowChargeFromAC = allowChargeFromAc;
		this.maxGridFeedPower = maxGridFeedPower;
		this.pvOnGridSwitch = pvOnGridSwitch;
		this.pvOffGridSwitch = pvOffGridSwitch;
		this.essOffGridSwitches = essOffGridSwitches;
		this.primaryOffGridEss = primaryOffGridEss;
		this.io = io;
	}

	@Override
	public void init() {
		for (Ess ess : essDevices.values()) {
			BitsElement bitsElement = (BitsElement) ess.getElement(EssProtocol.SystemState.name());
			BitElement essRunning = bitsElement.getBit(EssProtocol.SystemStates.Running.name());
			if (essRunning == null) {
				log.info("No connection to ESS");
			} else {
				boolean isEssRunning = essRunning.getValue();
				if (isEssRunning) {
					log.info("ESS is running");
				} else {
					// Start ESS if not running
					ess.start();
					log.warn("ESS is not running. Start ESS");
				}
			}
		}
	}

	@Override
	public void run() {
		ArrayList<Ess> allEss = new ArrayList<>(essDevices.values());
		if (isOnGrid()) {
			// TODO check if all storages switched to onGrid
			int calculatedEssActivePower = gridCounter.getActivePower();
			int allowedCharge = 0;
			int[] activePower = new int[allEss.size()];
			int allowedDischargeSum = 0;
			int sumUseableSoc = 0;
			int sumChargeableSoc = 0;

			for (Ess ess : allEss) {
				io.writeDigitalValue(essOffGridSwitches.get(ess.getName()), false);
				calculatedEssActivePower += ess.getActivePower();
				allowedCharge += ess.getAllowedCharge();
				allowedDischargeSum += ess.getMaxDischargePower();
				sumUseableSoc += ess.getUseableSoc();
				sumChargeableSoc += (100 - ess.getSOC());
			}

			if (calculatedEssActivePower > 0) {
				// discharge
				// Split ActivePower to all Ess
				if (allowedDischargeSum < calculatedEssActivePower) {
					calculatedEssActivePower = allowedDischargeSum;
				}
				// TODO check maxDischargePower of device
				for (int i = 0; i < allEss.size(); i++) {
					activePower[i] = calculatedEssActivePower / sumUseableSoc * allEss.get(i).getUseableSoc();
				}
			} else {
				// charge
				if (allowChargeFromAC) { // charging is allowed
					if (calculatedEssActivePower < allowedCharge) {
						// not allowed to charge with such high power
						calculatedEssActivePower = allowedCharge;
					}
					for (int i = 0; i < allEss.size(); i++) {
						activePower[i] = calculatedEssActivePower / sumChargeableSoc * (100 - allEss.get(i).getSOC());
					}
				} else { // charging is not allowed
					for (int i = 0; i < activePower.length; i++) {
						activePower[i] = 0;
					}
				}
			}

			// Reduce PV power
			if (gridCounter.getActivePower() - (calculatedEssActivePower - lastActivePower) >= maxGridFeedPower) {
				// TODO Reduce PV power
			} else {
				// TODO increase PV power
			}

			for (int i = 0; i < allEss.size(); i++) {
				Ess ess = allEss.get(i);
				// round to 100: ess can only be controlled with precision 100 W
				ess.setActivePower(calculatedEssActivePower / 100 * 100);
				log.info(ess.getCurrentDataAsString() + gridCounter.getCurrentDataAsString() + " SET: ["
						+ calculatedEssActivePower + "]");
			}
			lastActivePower = calculatedEssActivePower;
		} else {
			if (activeEss == null) {
				// Switch first Ess to OffGrid
				activeEss = essDevices.get(primaryOffGridEss);
				aviableEss = new LinkedList<>(essDevices.values());
				aviableEss.remove(activeEss);
			} else {
				// Check soc of activeEss
				if (activeEss.getSOC() <= 1) {
					// switch active Ess off
					io.writeDigitalValue(essOffGridSwitches.get(activeEss.getName()), false);
					// sleep 3 sec
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// switch to next Ess
					activeEss = aviableEss.iterator().next();
					if (activeEss != null) {
						aviableEss.remove(activeEss);
						io.writeDigitalValue(essOffGridSwitches.get(activeEss.getName()), true);
					}
				}
			}
		}
	}

	private boolean isOnGrid() {
		for (Ess ess : essDevices.values()) {
			if (ess.getGridState() == EssProtocol.GridStates.OffGrid) {
				return false;
			}
		}
		return true;
	}
}
