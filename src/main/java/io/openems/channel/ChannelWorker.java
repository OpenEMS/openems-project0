package io.openems.channel;

import io.openems.device.Device;
import io.openems.utils.Mutex;

import java.util.ArrayList;
import java.util.List;

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
