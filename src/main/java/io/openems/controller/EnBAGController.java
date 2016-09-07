package io.openems.controller;

import io.openems.api.iec.IecElementOnChangeListener;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.joda.time.DateTime;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;
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
	private String pvOnGridSwitchName;
	private String pvOffGridSwitchName;
	private Map<String, String> essOffGridSwitchMapping;
	private String primaryOffGridEss;
	private IO io;
	private List<Ess> availableEss;
	private SolarLog solarLog;
	private Element<IntegerType> totalActivePower;
	private Element<IntegerType> totalReactivePower;
	private Element<IntegerType> totalApparentPower;
	private boolean gridFeedLimitation = true;
	private boolean isStopped = false;
	private boolean isRemoteControlled = false;
	private int remoteActivePower = 0;
	private long time = 0;
	private long time2 = 0;
	private boolean isOffGrid = true;
	private int pvLimit = 100;
	private boolean pvOnGridSwitch = false;
	private boolean pvOffGridSwitch = false;
	private Map<String, Boolean> essOffGridSwitches;

	public EnBAGController(String name, Counter gridCounter, Map<String, Ess> essDevices, boolean allowChargeFromAc,
			int maxGridFeedPower, String pvOnGridSwitch, String pvOffGridSwitch,
			Map<String, String> essOffGridSwitchMapping, String primaryOffGridEss, IO io, SolarLog solarLog) {
		super(name);
		this.gridCounter = gridCounter;
		this.essDevices = essDevices;
		this.allowChargeFromAC = allowChargeFromAc;
		this.maxGridFeedPower = new Element<IntegerType>("maxGridFeedPower", "W");
		this.maxGridFeedPower.setValue(new IntegerType(maxGridFeedPower));
		this.pvOnGridSwitchName = pvOnGridSwitch;
		this.pvOffGridSwitchName = pvOffGridSwitch;
		this.essOffGridSwitchMapping = essOffGridSwitchMapping;
		this.primaryOffGridEss = primaryOffGridEss;
		this.io = io;
		this.solarLog = solarLog;
		totalActivePower = new Element<IntegerType>("totalActivePower", "W");
		totalReactivePower = new Element<IntegerType>("totalReactivePower", "W");
		totalApparentPower = new Element<IntegerType>("totalApparentPower", "W");
		this.essOffGridSwitches = new HashMap<String, Boolean>();
		for (Entry<String, String> value : essOffGridSwitchMapping.entrySet()) {
			this.essOffGridSwitches.put(value.getValue(), false);
		}
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
		return pvOnGridSwitchName;
	}

	public String getPvOffGridSwitch() {
		return pvOffGridSwitchName;
	}

	public Map<String, String> getEssOffGridSwitches() {
		return essOffGridSwitchMapping;
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
		if (isEssOnGrid()) {
			isOffGrid = true;
		} else {
			isOffGrid = false;
		}
	}

	@Override
	public void run() {
		if (!isStopped) {
			ArrayList<Ess> allEss = new ArrayList<>(essDevices.values());
			int totalActivePower = 0;
			int totalReactivePower = 0;
			int totalApparentPower = 0;
			for (Ess ess : allEss) {
				totalActivePower += ess.getActivePower();
				totalReactivePower += ess.getReactivePower();
				totalApparentPower += ess.getApparentPower();
			}
			this.totalActivePower.setValue(new IntegerType(totalActivePower));
			this.totalReactivePower.setValue(new IntegerType(totalReactivePower));
			this.totalApparentPower.setValue(new IntegerType(totalApparentPower));
			if (isEssOnGrid()) {
				// OnGrid
				// switch all ESS and PV to onGrid
				if (isOffGrid) {
					log.info("Switch to On-Grid");
					if (areAllEssDisconnected() && !io.readDigitalValue(pvOffGridSwitchName)
							&& !io.readDigitalValue(pvOnGridSwitchName)) {
						if (time + 3000 <= System.currentTimeMillis()) {
							// switch primary Ess On
							essOffGridSwitches.put(essOffGridSwitchMapping.get(primaryOffGridEss), false);
							// SetSolarLog to max power
							pvLimit = solarLog.getTotalPower();
							// OnGridSwitch is inverted
							pvOnGridSwitch = true;
							activeEss = null;
							isOffGrid = false;
						}
					} else {
						// Switch all Ess off
						for (Ess ess : allEss) {
							essOffGridSwitches.put(essOffGridSwitchMapping.get(ess.getName()), false);
						}
						essOffGridSwitches.put(essOffGridSwitchMapping.get(primaryOffGridEss), true);
						// Disconnect PV Off-Grid connection
						pvOffGridSwitch = false;
						pvOnGridSwitch = false;
						time = System.currentTimeMillis();
					}
				} else {
					int calculatedEssActivePower = gridCounter.getActivePower();
					int allowedCharge = 0;
					int[] activePower = new int[allEss.size()];
					int allowedDischargeSum = 0;
					int sumUseableSoc = 0;
					int sumChargeableSoc = 0;
					int soc = 0;

					// Collect data of all Ess devices
					for (Ess ess : allEss) {
						allowedCharge += ess.getAllowedCharge();
						allowedDischargeSum += ess.getMaxDischargePower();
						sumUseableSoc += ess.getUseableSoc();
						sumChargeableSoc += (100 - ess.getSOC());
						soc += ess.getSOC();
						calculatedEssActivePower += ess.getActivePower();
					}
					soc /= allEss.size();
					// overwrite ActivePower by Remote value
					if (isRemoteControlled) {
						calculatedEssActivePower = remoteActivePower;
					}
					if (calculatedEssActivePower > 0) {
						// discharge
						// Split ActivePower to all Ess
						if (allowedDischargeSum < calculatedEssActivePower) {
							calculatedEssActivePower = allowedDischargeSum;
						}
						// TODO check maxDischargePower of device
						for (int i = 0; i < allEss.size(); i++) {
							activePower[i] = (int) ((double) calculatedEssActivePower / sumUseableSoc * allEss.get(i)
									.getUseableSoc());
						}
					} else {
						// charge
						if (allowChargeFromAC) { // charging is allowed
							int reservedSoc = 20;
							// Reserve storage capacity for the Pv peak at
							// midday
							if (new DateTime().getHourOfDay() <= 11 && soc > 100 - reservedSoc
									&& calculatedEssActivePower < getMaxGridFeedPower()) {
								calculatedEssActivePower = calculatedEssActivePower / (reservedSoc * 2)
										* (reservedSoc - (soc - (100 - reservedSoc)));
							} else {
								if (calculatedEssActivePower < allowedCharge) {
									// not allowed to charge with such high
									// power
									calculatedEssActivePower = allowedCharge;
								}
							}
							// TODO Durch aufteilung der Leistung auf mehrere
							// Speicher kann Leistung verloren gehen, da
							// abgerundet
							// wird
							for (int i = 0; i < allEss.size(); i++) {
								activePower[i] = calculatedEssActivePower / sumChargeableSoc
										* (100 - allEss.get(i).getSOC());
							}
						} else { // charging is not allowed
							for (int i = 0; i < activePower.length; i++) {
								activePower[i] = 0;
							}
						}
					}

					// Reduce PV power
					int toGridPower = gridCounter.getActivePower() * -1;
					if (gridFeedLimitation && toGridPower >= getMaxGridFeedPower()
							|| solarLog.getPVLimit() < solarLog.getTotalPower()) {
						// set PV power
						int pvlimit = toGridPower - getMaxGridFeedPower();
						pvLimit = pvlimit;
					}
					// Write new calculated ActivePower to Ess device
					for (int i = 0; i < allEss.size(); i++) {
						Ess ess = allEss.get(i);
						ess.setActivePower(activePower[i]);
						log.info(ess.getCurrentDataAsString() + gridCounter.getCurrentDataAsString() + " SET: ["
								+ activePower[i] + "]");
					}
					lastActivePower = calculatedEssActivePower;
				}
			} else {
				// OffGrid
				if (isOffGrid) {
					// Check soc of activeEss
					if (activeEss.getSOC() <= 2) {
						if (areAllEssDisconnected()) {
							if (time2 + 3000 <= System.currentTimeMillis()) {
								// switch to next Ess
								try {
									activeEss = availableEss.iterator().next();
									availableEss.remove(activeEss);
									essOffGridSwitches.put(essOffGridSwitchMapping.get(activeEss.getName()), true);
								} catch (NoSuchElementException ex) {
									log.info("Off-Grid: All Storages are empty!");
								}
							}
						} else {
							// switch primary Ess off (is seperately needed
							// because
							// primary Ess output is inverted)
							essOffGridSwitches.put(essOffGridSwitchMapping.get(primaryOffGridEss), true);
							// switch active Ess off
							essOffGridSwitches.put(essOffGridSwitchMapping.get(activeEss.getName()), false);
							time2 = System.currentTimeMillis();
						}
					} else if (activeEss.getSOC() >= 95) {
						pvOffGridSwitch = false;
					}
				} else {
					log.info("Switch to Off-Grid");
					if (areAllEssDisconnected() && !io.readDigitalValue(pvOffGridSwitchName)
							&& !io.readDigitalValue(pvOnGridSwitchName)) {
						if (time2 + 3000 <= System.currentTimeMillis()) {
							// switch primary Ess On
							essOffGridSwitches.put(essOffGridSwitchMapping.get(primaryOffGridEss), false);
							activeEss = essDevices.get(primaryOffGridEss);
							System.out.println("ActiveEss :" + activeEss);
							availableEss = new LinkedList<>(essDevices.values());
							availableEss.remove(activeEss);
							// Switch Solar to OffGrid
							if (activeEss.getSOC() < 95) {
								pvOffGridSwitch = true;
							}
							isOffGrid = true;
						}
					} else {
						// Switch all Ess off
						for (Ess ess : allEss) {
							if (ess.getName() != primaryOffGridEss) {
								essOffGridSwitches.put(essOffGridSwitchMapping.get(ess.getName()), false);
							}
						}
						essOffGridSwitches.put(essOffGridSwitchMapping.get(primaryOffGridEss), true);
						// Disconnect PV connections
						pvOnGridSwitch = false;
						pvOffGridSwitch = false;
						// Set SolarLog max power to 35kW
						pvLimit = 35000;
						time2 = System.currentTimeMillis();
					}
				}
			}
			solarLog.setPVLimit(pvLimit);
			io.writeDigitalValue(pvOnGridSwitchName, pvOnGridSwitch);
			io.writeDigitalValue(pvOffGridSwitchName, pvOffGridSwitch);
			for (Entry<String, Boolean> value : essOffGridSwitches.entrySet()) {
				io.writeDigitalValue(value.getKey(), value.getValue());
			}
		}
	}

	private boolean isEssOnGrid() {
		for (Ess ess : essDevices.values()) {
			if (ess.getGridState() == EssProtocol.GridStates.OffGrid) {
				return false;

			}
		}
		return true;
	}

	private boolean areAllEssDisconnected() {
		for (Ess ess : essDevices.values()) {
			if (primaryOffGridEss.equals(ess.getName())) {
				if (!io.readDigitalValue(essOffGridSwitchMapping.get(ess.getName()))) {
					return false;
				}
			} else {
				if (io.readDigitalValue(essOffGridSwitchMapping.get(ess.getName()))) {
					return false;
				}
			}
		}
		return true;
	}

	public void start() {
		for (Ess ess : essDevices.values()) {
			ess.start();
		}
		isStopped = false;
	}

	public void stop() {
		for (Ess ess : essDevices.values()) {
			ess.stop();
		}
		isStopped = true;
	}

	@Override
	public void handleSetPoint(int function, IeShortFloat informationElement) {
		switch (function) {
		case 0:
			remoteActivePower = (int) (informationElement.getValue() * 100);
			break;
		case 1:
			maxGridFeedPower.setValue(new IntegerType((int) (informationElement.getValue() * 100)));
			break;
		default:
			break;

		}
	}

	@Override
	public void handleCommand(int function, IeDoubleCommand informationElement) {
		switch (function) {
		case 0:
			switch (informationElement.getCommandState()) {
			default:
			case ON:
				start();
				break;
			case OFF:
				stop();
				break;
			}
			break;
		case 1:
			switch (informationElement.getCommandState()) {
			default:
			case ON:
				gridFeedLimitation = true;
				break;
			case OFF:
				gridFeedLimitation = false;
				break;
			}
			break;
		case 2:
			switch (informationElement.getCommandState()) {
			default:
			case ON:
				isRemoteControlled = true;
				break;
			case OFF:
				isRemoteControlled = false;
				break;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public List<IecElementOnChangeListener> createChangeListeners(int startAddressMeassurements,
			int startAddressMessages, Connection connection) {
		ArrayList<IecElementOnChangeListener> eventListener = new ArrayList<>();
		IecElementOnChangeListener totalActivePowerListener = new IecElementOnChangeListener(totalActivePower,
				connection, startAddressMeassurements + 0, 0.001f);
		totalActivePower.addOnChangeListener(totalActivePowerListener);
		eventListener.add(totalActivePowerListener);
		IecElementOnChangeListener totalReactivePowerListener = new IecElementOnChangeListener(totalReactivePower,
				connection, startAddressMeassurements + 1, 0.001f);
		totalReactivePower.addOnChangeListener(totalReactivePowerListener);
		eventListener.add(totalReactivePowerListener);
		IecElementOnChangeListener totalApparentPowerListener = new IecElementOnChangeListener(totalApparentPower,
				connection, startAddressMeassurements + 2, 0.001f);
		totalApparentPower.addOnChangeListener(totalApparentPowerListener);
		eventListener.add(totalApparentPowerListener);
		return eventListener;
	}
}
