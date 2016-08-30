package io.openems.controller;

import io.openems.device.counter.Counter;
import io.openems.device.ess.Ess;
import io.openems.device.ess.EssProtocol;
import io.openems.device.inverter.SolarLog;
import io.openems.device.io.IO;
import io.openems.device.protocol.BitElement;
import io.openems.device.protocol.BitsElement;
import io.openems.element.Element;
import io.openems.element.type.IntegerType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnBAGController extends Controller {

	private final static Logger log = LoggerFactory.getLogger(EnBAGController.class);

	private final Counter gridCounter;
	private final Map<String, Ess> essDevices;
	private final boolean allowChargeFromAC;
	private final Element<IntegerType> maxGridFeedPower;
	private int lastActivePower;
	private Ess activeEss = null;
	private String pvOnGridSwitch;
	private String pvOffGridSwitch;
	private Map<String, String> essOffGridSwitches;
	private String primaryOffGridEss;
	private IO io;
	private List<Ess> availableEss;
	private SolarLog solarLog;

	public EnBAGController(String name, Counter gridCounter, Map<String, Ess> essDevices, boolean allowChargeFromAc,
			int maxGridFeedPower, String pvOnGridSwitch, String pvOffGridSwitch,
			Map<String, String> essOffGridSwitches, String primaryOffGridEss, IO io, SolarLog solarLog) {
		super(name);
		this.gridCounter = gridCounter;
		this.essDevices = essDevices;
		this.allowChargeFromAC = allowChargeFromAc;
		this.maxGridFeedPower = new Element<IntegerType>("maxGridFeedPower", "W");
		this.maxGridFeedPower.setValue(new IntegerType(maxGridFeedPower));
		this.pvOnGridSwitch = pvOnGridSwitch;
		this.pvOffGridSwitch = pvOffGridSwitch;
		this.essOffGridSwitches = essOffGridSwitches;
		this.primaryOffGridEss = primaryOffGridEss;
		this.io = io;
		this.solarLog = solarLog;
	}

	public int getMaxGridFeedPower() {
		return maxGridFeedPower.getValue().toInteger();
	}

	public void setMaxGridFeedPower(int value) {
		maxGridFeedPower.setValue(new IntegerType(value));
	}

	public Counter getGridCounter() {
		return gridCounter;
	}

	public Map<String, Ess> getEssDevices() {
		return essDevices;
	}

	public boolean isAllowChargeFromAC() {
		return allowChargeFromAC;
	}

	public String getPvOnGridSwitch() {
		return pvOnGridSwitch;
	}

	public String getPvOffGridSwitch() {
		return pvOffGridSwitch;
	}

	public Map<String, String> getEssOffGridSwitches() {
		return essOffGridSwitches;
	}

	public String getPrimaryOffGridEss() {
		return primaryOffGridEss;
	}

	public IO getIo() {
		return io;
	}

	public SolarLog getSolarLog() {
		return solarLog;
	}

	@Override
	public void init() {
		for (Ess ess : essDevices.values()) {
			BitsElement bitsElement = (BitsElement) ess.getElement(EssProtocol.SystemState.name());
			BitElement essRunning = bitsElement.getBit(EssProtocol.SystemStates.Running.name());
			if (essRunning == null) {
				log.info("No connection to ESS");
			} else {
				boolean isEssRunning = essRunning.getValue().toBoolean();
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

	private long time = 0;
	private long time2 = 0;
	private boolean isOffGrid = false;

	@Override
	public void run() {
		ArrayList<Ess> allEss = new ArrayList<>(essDevices.values());
		if (isOnGrid()) {
			// OnGrid
			// switch all ESS and PV to onGrid
			if (activeEss != null) {
				if (areAllEssOff() && !io.readDigitalValue(pvOffGridSwitch)) {
					if (time + 3000 <= System.currentTimeMillis()) {
						// switch primary Ess On
						io.writeDigitalValue(essOffGridSwitches.get(primaryOffGridEss), false);
						// SetSolarLog to max power
						solarLog.setPVLimit(solarLog.getTotalPower());
						// OnGridSwitch is inverted
						io.writeDigitalValue(pvOnGridSwitch, false);
						activeEss = null;
						isOffGrid = false;
					}
				} else {
					// Switch all Ess off
					for (Ess ess : allEss) {
						if (ess.getName() != primaryOffGridEss) {
							io.writeDigitalValue(essOffGridSwitches.get(ess.getName()), false);
						}
					}
					io.writeDigitalValue(essOffGridSwitches.get(primaryOffGridEss), true);
					// Disconnect PV Off-Grid connection
					io.writeDigitalValue(pvOffGridSwitch, false);
					time = System.currentTimeMillis();
				}
			}
			int calculatedEssActivePower = gridCounter.getActivePower();
			int allowedCharge = 0;
			int[] activePower = new int[allEss.size()];
			int allowedDischargeSum = 0;
			int sumUseableSoc = 0;
			int sumChargeableSoc = 0;
			int soc = 0;

			// Collect data of all Ess devices
			for (Ess ess : allEss) {
				io.writeDigitalValue(essOffGridSwitches.get(ess.getName()), false);
				calculatedEssActivePower += ess.getActivePower();
				allowedCharge += ess.getAllowedCharge();
				allowedDischargeSum += ess.getMaxDischargePower();
				sumUseableSoc += ess.getUseableSoc();
				sumChargeableSoc += (100 - ess.getSOC());
				soc += ess.getSOC();
			}
			soc /= allEss.size();

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
					int reservedSoc = 20;
					// Reserve storage capacity for the Pv peak at midday
					if (new DateTime().getHourOfDay() <= 11 && soc > 100 - reservedSoc
							&& calculatedEssActivePower < getMaxGridFeedPower()) {
						calculatedEssActivePower = calculatedEssActivePower / (reservedSoc * 2)
								* (reservedSoc - (soc - (100 - reservedSoc)));
					} else {
						if (calculatedEssActivePower < allowedCharge) {
							// not allowed to charge with such high power
							calculatedEssActivePower = allowedCharge;
						}
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
			int toGridPower = gridCounter.getActivePower() - (calculatedEssActivePower - lastActivePower);
			if (toGridPower >= getMaxGridFeedPower() || solarLog.getPVLimit() < solarLog.getTotalPower()) {
				// set PV power
				int pvlimit = solarLog.getPVLimit() - (toGridPower - getMaxGridFeedPower());
				solarLog.setPVLimit(pvlimit);
			}
			// Write new calculated ActivePower to Ess device
			for (int i = 0; i < allEss.size(); i++) {
				Ess ess = allEss.get(i);
				// round to 100: ess can only be controlled with precision 100 W
				ess.setActivePower(calculatedEssActivePower / 100 * 100);
				log.info(ess.getCurrentDataAsString() + gridCounter.getCurrentDataAsString() + " SET: ["
						+ calculatedEssActivePower + "]");
			}
			lastActivePower = calculatedEssActivePower;
		} else {
			// OffGrid
			if (isOffGrid) {
				// Check soc of activeEss
				if (activeEss.getSOC() <= 2) {
					if (areAllEssOff()) {
						if (time2 + 3000 <= System.currentTimeMillis()) {
							// switch to next Ess
							try {
								activeEss = availableEss.iterator().next();
								availableEss.remove(activeEss);
								io.writeDigitalValue(essOffGridSwitches.get(activeEss.getName()), true);
							} catch (NoSuchElementException ex) {
								log.debug("Off-Grid: All Storages are empty!");
							}
						}
					} else {
						// switch primary Ess off (is seperately needed because
						// primary Ess output is inverted)
						io.writeDigitalValue(essOffGridSwitches.get(primaryOffGridEss), true);
						// switch active Ess off
						io.writeDigitalValue(essOffGridSwitches.get(activeEss.getName()), false);
						time2 = System.currentTimeMillis();
					}
				}
			} else {
				if (areAllEssOff() && io.readDigitalValue(pvOnGridSwitch)) {
					if (time2 + 3000 <= System.currentTimeMillis()) {
						// switch primary Ess On
						io.writeDigitalValue(essOffGridSwitches.get(primaryOffGridEss), false);
						// Switch Solar to OffGrid
						io.writeDigitalValue(pvOffGridSwitch, true);
						activeEss = essDevices.get(primaryOffGridEss);
						availableEss = new LinkedList<>(essDevices.values());
						availableEss.remove(activeEss);
						isOffGrid = true;
					}
				} else {
					// Switch all Ess off
					for (Ess ess : allEss) {
						if (ess.getName() != primaryOffGridEss) {
							io.writeDigitalValue(essOffGridSwitches.get(ess.getName()), false);
						}
					}
					io.writeDigitalValue(essOffGridSwitches.get(primaryOffGridEss), true);
					// Disconnect PV On-Grid connection
					io.writeDigitalValue(pvOnGridSwitch, true);
					// Set SolarLog max power to 35kW
					solarLog.setPVLimit(35000);
					time2 = System.currentTimeMillis();
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

	private boolean areAllEssOff() {
		for (Ess ess : essDevices.values()) {
			if (primaryOffGridEss.equals(ess.getName())) {
				if (!io.readDigitalValue(essOffGridSwitches.get(ess.getName()))) {
					return false;
				}
			} else {
				if (io.readDigitalValue(essOffGridSwitches.get(ess.getName()))) {
					return false;
				}
			}
		}
		return true;
	}
}
