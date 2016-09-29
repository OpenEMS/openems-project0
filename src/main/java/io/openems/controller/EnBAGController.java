package io.openems.controller;

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

import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.api.iec.MessageType;
import io.openems.device.counter.Counter;
import io.openems.device.ess.Ess;
import io.openems.device.ess.EssCluster;
import io.openems.device.inverter.SolarLog;
import io.openems.device.io.IO;
import io.openems.element.Element;
import io.openems.element.InvalidValueExcecption;
import io.openems.element.type.BooleanType;
import io.openems.element.type.IntegerType;
import io.openems.element.type.LongType;

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
	private Element<LongType> inHousePowerConsumption;
	private boolean gridFeedLimitation = true;
	private Element<BooleanType> remoteStop;
	private boolean isRemoteControlled = false;
	private Element<IntegerType> remoteActivePower;
	private long time = 0;
	private long time2 = 0;
	private boolean isOffGrid = true;
	private int pvLimit = 100;
	private boolean pvOnGridSwitch = false;
	private boolean pvOffGridSwitch = false;
	private Map<String, Boolean> essOffGridSwitches;
	private EssCluster cluster;
	private int switchDelay = 10000;

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
		inHousePowerConsumption = new Element<LongType>("inHousePowerConsumption", "W");
		remoteStop = new Element<BooleanType>("remoteStop", "");
		remoteActivePower = new Element<IntegerType>("remoteActivePower", "W");
		remoteStop.setValue(new BooleanType(false));
		remoteStop.setValid(true);
		this.essOffGridSwitches = new HashMap<String, Boolean>();
		for (Entry<String, String> value : essOffGridSwitchMapping.entrySet()) {
			this.essOffGridSwitches.put(value.getValue(), false);
		}
		this.cluster = new EssCluster("Cluster", "", 0, 0, new ArrayList<Ess>(essDevices.values()));
	}

	public int getMaxGridFeedPower() throws InvalidValueExcecption {
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
		try {
			if (!cluster.isRunning()) {
				log.warn("ESS is not running. Start ESS");
				cluster.start();
			}
		} catch (InvalidValueExcecption e) {
			log.error("can't start some ess", e);
		}
		try {
			if (cluster.isOnGrid()) {
				isOffGrid = true;
			} else {
				isOffGrid = false;
			}
		} catch (InvalidValueExcecption e) {
			log.error("can't read grid state");
			isOffGrid = false;
		}
	}

	@Override
	public void run() {
		try {
			if (!remoteStop.getValue().toBoolean()) {
				ArrayList<Ess> allEss = new ArrayList<>(essDevices.values());
				boolean isOnGrid = true;
				try {
					if (!cluster.isOnGrid()) {
						isOnGrid = false;
					}
				} catch (InvalidValueExcecption e) {
					log.error("error on read grid state!");
				}
				if (isOnGrid) {
					// OnGrid
					// switch all ESS and PV to onGrid
					if (isOffGrid) {
						log.info("Switch to On-Grid");
						try {
							if (areAllEssDisconnected() && !io.readDigitalValue(pvOffGridSwitchName)
									&& !io.readDigitalValue(pvOnGridSwitchName)) {
								if (time + switchDelay <= System.currentTimeMillis()) {
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
						} catch (InvalidValueExcecption e) {
							log.error("failed to switch to OnGrid mode because there are invalid values!", e);
						}
					} else {
						try {
							this.totalActivePower.setValue(new IntegerType(cluster.getActivePower()));
							this.totalReactivePower.setValue(new IntegerType(cluster.getReactivePower()));
							this.totalApparentPower.setValue(new IntegerType(cluster.getApparentPower()));
							this.inHousePowerConsumption.setValue(new LongType(cluster.getActivePower()
									+ gridCounter.getActivePower() + solarLog.getActivePower()));
						} catch (InvalidValueExcecption e) {
							this.totalActivePower.setValid(false);
							this.totalReactivePower.setValid(false);
							this.totalApparentPower.setValid(false);
							this.inHousePowerConsumption.setValid(false);
						}
						try {
							int calculatedEssActivePower = gridCounter.getActivePower() + cluster.getActivePower();

							// overwrite ActivePower by Remote value
							if (isRemoteControlled) {
								calculatedEssActivePower = remoteActivePower.getValue().toInteger();
							}
							if (calculatedEssActivePower > 0) {
								// discharge
								if (cluster.getAllowedDischarge() < calculatedEssActivePower) {
									calculatedEssActivePower = cluster.getAllowedDischarge();
								}
							} else {
								// charge
								if (allowChargeFromAC) { // charging is allowed
									int reservedSoc = 20;
									// Reserve storage capacity for the Pv peak
									// at
									// midday
									if (new DateTime().getHourOfDay() <= 11 && cluster.getSOC() > 100 - reservedSoc
											&& gridCounter.getActivePower() < getMaxGridFeedPower()) {
										calculatedEssActivePower = calculatedEssActivePower / (reservedSoc * 2)
												* (reservedSoc - (cluster.getSOC() - (100 - reservedSoc)));
									} else {
										if (calculatedEssActivePower < cluster.getAllowedCharge()) {
											// not allowed to charge with such
											// high
											// power
											calculatedEssActivePower = cluster.getAllowedCharge();
										}
									}
								} else { // charging is not allowed
									calculatedEssActivePower = 0;
								}
							}

							// Reduce PV power
							int toGridPower = gridCounter.getActivePower() * -1;
							if (gridFeedLimitation && toGridPower >= getMaxGridFeedPower()
									|| solarLog.getPVLimit() < solarLog.getTotalPower()) {
								// set PV power
								pvLimit = toGridPower - getMaxGridFeedPower();
							}
							// Write new calculated ActivePower to Ess device
							cluster.setActivePower(calculatedEssActivePower);
							log.info(cluster.getCurrentDataAsString() + gridCounter.getCurrentDataAsString() + " SET: ["
									+ calculatedEssActivePower + "]");
							lastActivePower = calculatedEssActivePower;
						} catch (InvalidValueExcecption e) {
							log.error("An error occured on controll the storages!", e);
							pvLimit = 0;
							try {
								cluster.setActivePower(0);
							} catch (InvalidValueExcecption e1) {
								log.error("Failed to stop ess!");
							}
						}
						solarLog.setPVLimit(pvLimit);
					}
				} else {
					// OffGrid
					if (isOffGrid) {
						// Check soc of activeEss
						try {
							if (activeEss.getSOC() <= 76) {
								if (areAllEssDisconnected()) {
									if (time2 + switchDelay <= System.currentTimeMillis()) {
										// switch to next Ess
										try {
											activeEss = availableEss.iterator().next();
											// Start if not running
											if (!activeEss.isRunning()) {
												activeEss.start();
											}
											availableEss.remove(activeEss);
											essOffGridSwitches.put(essOffGridSwitchMapping.get(activeEss.getName()),
													true);
										} catch (NoSuchElementException ex) {
											log.info("Off-Grid: All Storages are empty!");
										}
									}
								} else {
									// switch primary Ess off (is seperately
									// needed
									// because
									// primary Ess output is inverted)
									for (Ess ess : allEss) {
										if (ess.getName() != primaryOffGridEss) {
											essOffGridSwitches.put(essOffGridSwitchMapping.get(ess.getName()), false);
										}
									}
									essOffGridSwitches.put(essOffGridSwitchMapping.get(primaryOffGridEss), true);
									// switch active Ess off
									time2 = System.currentTimeMillis();
								}
							} else if (activeEss.getSOC() >= 95) {
								pvOffGridSwitch = false;
							}
						} catch (InvalidValueExcecption e) {
							log.error("can't switch to the next storage, because ther are invalid values", e);
						}
					} else {
						log.info("Switch to Off-Grid");
						try {
							if (areAllEssDisconnected() && !io.readDigitalValue(pvOffGridSwitchName)
									&& !io.readDigitalValue(pvOnGridSwitchName)) {
								if (time2 + switchDelay <= System.currentTimeMillis()) {
									// switch primary Ess On
									essOffGridSwitches.put(essOffGridSwitchMapping.get(primaryOffGridEss), false);
									activeEss = essDevices.get(primaryOffGridEss);
									System.out.println("ActiveEss :" + activeEss);
									availableEss = new LinkedList<>(essDevices.values());
									availableEss.remove(activeEss);
									// Switch Solar to OffGrid
									// if (activeEss.getSOC() < 95) {
									// pvOffGridSwitch = true;
									// }
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
								// pvLimit = 35000;
								time2 = System.currentTimeMillis();
							}
						} catch (InvalidValueExcecption e) {
							log.error("can't switch to OffGrid because there are invalid values!");
						}
					}
				}
				io.writeDigitalValue(pvOnGridSwitchName, pvOnGridSwitch);
				io.writeDigitalValue(pvOffGridSwitchName, pvOffGridSwitch);
				for (Entry<String, Boolean> value : essOffGridSwitches.entrySet()) {
					io.writeDigitalValue(value.getKey(), value.getValue());
				}
			}
		} catch (InvalidValueExcecption e) {
			log.error("Error on reading remote Stop Element", e);
		}
	}

	private boolean areAllEssDisconnected() throws InvalidValueExcecption {
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

	@Override
	public void handleSetPoint(int function, IeShortFloat informationElement) {
		switch (function) {
		case 0:
			remoteActivePower.setValue(new IntegerType((int) (informationElement.getValue() * 100)));
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
				cluster.start();
				remoteStop.setValue(new BooleanType(false));
				break;
			case OFF:
				cluster.stop();
				remoteStop.setValue(new BooleanType(true));
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
				connection, startAddressMeassurements + 0, 0.001f, MessageType.MEASSUREMENT);
		totalActivePower.addOnChangeListener(totalActivePowerListener);
		eventListener.add(totalActivePowerListener);
		IecElementOnChangeListener totalReactivePowerListener = new IecElementOnChangeListener(totalReactivePower,
				connection, startAddressMeassurements + 1, 0.001f, MessageType.MEASSUREMENT);
		totalReactivePower.addOnChangeListener(totalReactivePowerListener);
		eventListener.add(totalReactivePowerListener);
		IecElementOnChangeListener totalApparentPowerListener = new IecElementOnChangeListener(totalApparentPower,
				connection, startAddressMeassurements + 2, 0.001f, MessageType.MEASSUREMENT);
		totalApparentPower.addOnChangeListener(totalApparentPowerListener);
		eventListener.add(totalApparentPowerListener);
		IecElementOnChangeListener inHousePowerConsumptionListener = new IecElementOnChangeListener(
				inHousePowerConsumption, connection, startAddressMeassurements + 3, 0.001f, MessageType.MEASSUREMENT);
		inHousePowerConsumption.addOnChangeListener(inHousePowerConsumptionListener);
		eventListener.add(inHousePowerConsumptionListener);
		IecElementOnChangeListener maxGridFeedPowerListener = new IecElementOnChangeListener(maxGridFeedPower,
				connection, startAddressMeassurements + 4, 0.001f, MessageType.MEASSUREMENT);
		maxGridFeedPower.addOnChangeListener(maxGridFeedPowerListener);
		eventListener.add(maxGridFeedPowerListener);
		IecElementOnChangeListener remoteActivePowerListener = new IecElementOnChangeListener(remoteActivePower,
				connection, startAddressMeassurements + 5, 0.001f, MessageType.MEASSUREMENT);
		remoteActivePower.addOnChangeListener(remoteActivePowerListener);
		eventListener.add(remoteActivePowerListener);
		IecElementOnChangeListener remoteStopListener = new IecElementOnChangeListener(remoteStop, connection,
				startAddressMessages + 0, 0, MessageType.MESSAGE);
		remoteStop.addOnChangeListener(remoteStopListener);
		eventListener.add(remoteStopListener);
		return eventListener;
	}
}
