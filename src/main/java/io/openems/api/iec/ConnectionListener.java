package io.openems.api.iec;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.App;
import io.openems.device.Device;
import io.openems.device.counter.Counter;
import io.openems.device.ess.Ess;

public class ConnectionListener implements ConnectionEventListener {

	private final static Logger log = LoggerFactory.getLogger(ConnectionListener.class);
	private final static int STARTADDRESS = 8193;
	private final static int ADDRESSOFFSET = 200;

	private final Connection connection;
	private final int connectionId;
	private final String[] essValueElements = { "", "" };
	private final String[] essPointElements = { "", "" };

	public ConnectionListener(Connection connection, int connectionId) {
		this.connection = connection;
		this.connectionId = connectionId;
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

				List<Device> devices = new ArrayList<>(App.getConfig().getDevices().values());

				int index = 0;
				int essCount = 0;
				int counterCount = 0;
				InformationObject[] values = new InformationObject[devices.size()];
				for (Device d : devices) {
					if (d instanceof Ess) {
						values[index] = new InformationObject(STARTADDRESS + essCount * ADDRESSOFFSET,
								d.getIecValues());
						essCount++;
					} else if (d instanceof Counter) {
						values[index] = new InformationObject(STARTADDRESS + 1000 + counterCount * ADDRESSOFFSET,
								d.getIecValues());
						counterCount++;
					}
					index++;
				}
				// TODO send all values
				// Meassured values
				connection.send(new ASdu(TypeId.M_IT_TB_1, true, CauseOfTransmission.SPONTANEOUS, false, false, 0,
						aSdu.getCommonAddress(), values));

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
	}
}
