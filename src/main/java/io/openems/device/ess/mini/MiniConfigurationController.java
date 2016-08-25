package io.openems.device.ess.mini;

import io.openems.controller.Controller;;

public class MiniConfigurationController extends Controller {

	private Mini mini;

	public MiniConfigurationController(String name, Mini mini) {
		super(name);
		this.mini = mini;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {

	}

}
