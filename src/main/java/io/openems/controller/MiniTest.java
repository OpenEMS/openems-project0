package io.openems.controller;

import java.util.Map;

import io.openems.device.ess.Ess;

public class MiniTest extends Controller {

	private final Map<String, Ess> essDevices;

	public MiniTest(String name, Map<String, Ess> essDevices) {
		super(name);
		this.essDevices = essDevices;
	}

	@Override
	public void run() {
		// Mini mini = (Mini) essDevices.values().iterator().next();

		Ess ess = essDevices.values().iterator().next();
		System.out.println(ess.getSOC());
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
