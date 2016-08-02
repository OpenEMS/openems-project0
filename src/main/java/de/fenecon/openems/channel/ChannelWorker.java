package de.fenecon.openems.channel;

import java.util.ArrayList;
import java.util.List;

import de.fenecon.openems.device.Device;
import de.fenecon.openems.utils.Mutex;

public class ChannelWorker extends Thread {
	protected final List<Device> devices = new ArrayList<>();

	protected final Mutex initQueryFinished = new Mutex(false);
	protected final Mutex mainQueryFinished = new Mutex(false);

	/**
	 * Register a new device to this worker
	 * 
	 * @param device
	 */
	public synchronized void registerDevice(Device device) {
		synchronized (devices) {
			devices.add(device);
		}
	}

	public void waitForInit() throws InterruptedException {
		initQueryFinished.await();
	}

	public void waitForMain() throws InterruptedException {
		mainQueryFinished.await();
	}
}
