package io.openems.api.iec;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;
import org.openmuc.j60870.IeTime56;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.App;
import io.openems.controller.ControllerWorker;

public class ConnectionListener extends Thread implements ConnectionEventListener {

	private final static Logger log = LoggerFactory.getLogger(ConnectionListener.class);
	// private final static int MEASSUREMENTSSTARTADDRESS = 9001;
	// private final static int MESSAGESSTARTADDRESS = 5001;
	private final static int ADDRESSOFFSET = 100;
	private final static int SETPOINTADDRESS = 21001;
	private final static int COMMANDADDRESS = 25001;

	private final Connection connection;
	private final int connectionId;
	private List<IecElementOnChangeListener> listeners;
	private List<ASdu> queue;
	private final JsonObject config;

	public ConnectionListener(Connection connection, int connectionId, JsonObject config) {
		this.connection = connection;
		this.connectionId = connectionId;
		this.config = config;
		queue = new LinkedList<>();
		registerElementChangeListener();
		this.setName("IECConnection" + connectionId);
		this.start();
	}

	private void registerElementChangeListener() {
		if (connection != null) {
			listeners = new ArrayList<>();
			List<IecControllable> things = new ArrayList<>(App.getConfig().getDevices().values());
			// int controllerCount = 0;
			for (ControllerWorker cw : App.getConfig().getControllerWorkers().values()) {
				things.add(cw.getController());
				// IecControllable c = cw.getController();
				// listeners.addAll(c.createChangeListeners(MEASSUREMENTSSTARTADDRESS
				// + (controllerCount * 50),
				// MESSAGESSTARTADDRESS + (controllerCount * 50), this));
				// controllerCount++;
			}
			// int essCount = 0;
			// int inverterCount = 0;
			for (IecControllable d : things) {
				JsonElement e = config.get(d.getName());
				if (e != null) {
					JsonObject jo = e.getAsJsonObject();
					int meassurementStartAddress = jo.get("meassurement").getAsInt();
					int messageStartAddress = jo.get("message").getAsInt();
					boolean negateValues = jo.get("negateValues").getAsBoolean();
					log.info("Create IEC Change Listener for " + d.getName());
					listeners.addAll(
							d.createChangeListeners(meassurementStartAddress, messageStartAddress, this, negateValues));
				}
				// if (d instanceof Ess) {
				// listeners.addAll(
				// d.createChangeListeners(MEASSUREMENTSSTARTADDRESS + 100 +
				// essCount * ADDRESSOFFSET,
				// MESSAGESSTARTADDRESS + 100 + (essCount * ADDRESSOFFSET),
				// this));
				// essCount++;
				// } else if (d instanceof SolarLog) {
				// listeners.addAll(
				// d.createChangeListeners(MEASSUREMENTSSTARTADDRESS + 600 +
				// inverterCount * ADDRESSOFFSET,
				// MESSAGESSTARTADDRESS + 100 + (inverterCount * ADDRESSOFFSET),
				// this));
				// inverterCount++;
				// }
			}
		}
	}

	public void addASduToQueue(ASdu aSdu) {
		synchronized (queue) {
			queue.add(aSdu);
		}
	}

	@Override
	public void newASdu(ASdu aSdu) {
		try {
			switch (aSdu.getTypeIdentification()) {
			// interrogation command
			case C_IC_NA_1:
				connection.sendConfirmation(aSdu);
				log.info("Got interrogation command. Will send scaled measured values.\n");

				for (IecElementOnChangeListener listener : listeners) {
					try {
						if (listener.getMessageType() == MessageType.MEASSUREMENT) {
							connection.send(new ASdu(TypeId.M_ME_TF_1, false, CauseOfTransmission.REQUEST, false, false,
									0, 5101, new InformationObject[] { listener.getCurrentValue() }));
						} else {
							connection.send(new ASdu(TypeId.M_SP_TB_1, false, CauseOfTransmission.REQUEST, false, false,
									0, 5101, new InformationObject[] { listener.getCurrentValue() }));
						}
					} catch (Exception e) {
						log.error("Failed to send IEC Interrogation value", e);
					}
				}

				break;
			case C_SE_NC_1: {
				int address = aSdu.getInformationObjects()[0].getInformationObjectAddress();
				System.out.println("SetPoint " + address);
				address -= SETPOINTADDRESS;
				int controllerId = address / ADDRESSOFFSET;
				int functionId = address % ADDRESSOFFSET;
				List<ControllerWorker> cw = new ArrayList<>(App.getConfig().getControllerWorkers().values());
				IecControllable c = cw.get(controllerId).getController();
				IeShortFloat value = (IeShortFloat) aSdu.getInformationObjects()[0].getInformationElements()[0][0];
				c.handleSetPoint(functionId, value);
				connection.sendConfirmation(aSdu);
			}
				break;
			case C_DC_NA_1: {
				int address = aSdu.getInformationObjects()[0].getInformationObjectAddress();
				System.out.println("Command " + address);
				address -= COMMANDADDRESS;
				int controllerId = address / ADDRESSOFFSET;
				int functionId = address % ADDRESSOFFSET;
				List<ControllerWorker> cw = new ArrayList<>(App.getConfig().getControllerWorkers().values());
				IecControllable c = cw.get(controllerId).getController();
				IeDoubleCommand value = (IeDoubleCommand) aSdu.getInformationObjects()[0]
						.getInformationElements()[0][0];
				c.handleCommand(functionId, value);
				connection.sendConfirmation(aSdu);
			}
				break;
			case C_CS_NA_1: {
				IeTime56 time = (IeTime56) (aSdu.getInformationObjects()[0].getInformationElements()[0][0]);
				if (System.currentTimeMillis() - time.getTimestamp() < 10000) {
					connection.sendConfirmation(aSdu);
				} else {
					log.error("IEC time is not synchronized!");
				}
			}
				break;
			default:
				System.out.println("Got unknown request: " + aSdu + ". Will not confirm it.\n");
			}

		} catch (EOFException e) {
			System.out.println(
					"Will quit listening for commands on connection (" + connectionId + ") because socket was closed.");
		} catch (IOException e) {
			System.out.println("Will quit listening for commands on connection (" + connectionId
					+ ") because of error: \"" + e.getMessage() + "\".");
		}
	}

	@Override
	public void connectionClosed(IOException e) {
		log.info("Connection (" + connectionId + ") was closed. " + e.getMessage());
		for (IecElementOnChangeListener listener : listeners) {
			listener.remove();
		}
		this.interrupt();
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				synchronized (queue) {
					for (int i = queue.size() - 1; i >= 0; i--) {
						ASdu aSdu = queue.get(i);
						queue.remove(aSdu);
						connection.send(aSdu);
					}
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				log.error("Failed to send IEC spontaneous values.", e);
			}
		}
	}
}
