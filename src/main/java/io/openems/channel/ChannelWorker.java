package io.openems.channel;

import java.util.ArrayList;
import java.util.List;

import io.openems.device.Device;
import io.openems.utils.Mutex;

public abstract class ChannelWorker extends Thread {
	protected final List<Device> devices = new ArrayList<>();

	protected final Mutex initQueryFinished = new Mutex(false);

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
}
