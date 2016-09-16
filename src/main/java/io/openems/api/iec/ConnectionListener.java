package io.openems.api.iec;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.App;
import io.openems.controller.ControllerWorker;
import io.openems.device.ess.Ess;
import io.openems.device.inverter.SolarLog;

public class ConnectionListener implements ConnectionEventListener {

	private final static Logger log = LoggerFactory.getLogger(ConnectionListener.class);
	private final static int MEASSUREMENTSSTARTADDRESS = 9001;
	private final static int MESSAGESSTARTADDRESS = 5001;
	private final static int ADDRESSOFFSET = 100;
	private final static int SETPOINTADDRESS = 21001;
	private final static int COMMANDADDRESS = 25001;

	private final Connection connection;
	private final int connectionId;
	private List<IecElementOnChangeListener> listeners;

	public ConnectionListener(Connection connection, int connectionId) {
		this.connection = connection;
		this.connectionId = connectionId;
		registerElementChangeListener();
	}

	private void registerElementChangeListener() {
		if (connection != null) {
			listeners = new ArrayList<>();
			int controllerCount = 0;
			for (ControllerWorker cw : App.getConfig().getControllerWorkers().values()) {
				IecControllable c = cw.getController();
				listeners.addAll(c.createChangeListeners(MEASSUREMENTSSTARTADDRESS + (controllerCount * 50),
						MESSAGESSTARTADDRESS + (controllerCount * 50), connection));
				controllerCount++;
			}
			int essCount = 0;
			int inverterCount = 0;
			for (IecControllable d : App.getConfig().getDevices().values()) {
				if (d instanceof Ess) {
					listeners.addAll(d.createChangeListeners(MEASSUREMENTSSTARTADDRESS + 100 + essCount * ADDRESSOFFSET,
							MESSAGESSTARTADDRESS + 100 + (essCount * ADDRESSOFFSET), connection));
					essCount++;
				} else if (d instanceof SolarLog) {
					listeners.addAll(
							d.createChangeListeners(MEASSUREMENTSSTARTADDRESS + 600 + inverterCount * ADDRESSOFFSET,
									MESSAGESSTARTADDRESS + 100 + (inverterCount * ADDRESSOFFSET), connection));
					inverterCount++;
				}
			}
		}
	}

	@Override
	public void newASdu(ASdu aSdu) {
		try {
			switch (aSdu.getTypeIdentification()) {
			// interrogation command
			// TODO add case for commands
			case C_IC_NA_1:
				connection.sendConfirmation(aSdu);
				System.out.println("Got interrogation command. Will send scaled measured values.\n");

				for (IecElementOnChangeListener listener : listeners) {
					if (listener.isMeassurement()) {
						connection.send(new ASdu(TypeId.M_ME_TF_1, false, CauseOfTransmission.REQUEST, false, false, 0,
								5101, new InformationObject[] { listener.getCurrentValue() }));
					} else {
						connection.send(new ASdu(TypeId.M_DP_TB_1, false, CauseOfTransmission.REQUEST, false, false, 0,
								5101, new InformationObject[] { listener.getCurrentValue() }));
					}
				}

				break;
			case C_SE_TC_1: {
				int address = aSdu.getInformationObjects()[0].getInformationObjectAddress();
				System.out.println(address);
				address -= SETPOINTADDRESS;
				int controllerId = address / ADDRESSOFFSET;
				int functionId = address % ADDRESSOFFSET;
				System.out.println(controllerId);
				System.out.println(functionId);
				List<ControllerWorker> cw = new ArrayList<>(App.getConfig().getControllerWorkers().values());
				IecControllable c = cw.get(controllerId).getController();
				IeShortFloat value = (IeShortFloat) aSdu.getInformationObjects()[0].getInformationElements()[0][0];
				c.handleSetPoint(functionId, value);
				connection.sendConfirmation(aSdu);
			}
				break;
			case C_DC_NA_1: {
				int address = aSdu.getInformationObjects()[0].getInformationObjectAddress();
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
		System.out.println("Connection (" + connectionId + ") was closed. " + e.getMessage());
		for (IecElementOnChangeListener listener : listeners) {
			listener.remove();
		}
	}
}
