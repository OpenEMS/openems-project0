package io.openems.application;

import java.io.FileNotFoundException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import io.openems.OpenemsGlobal;
import io.openems.api.iec.Iec60870Server;
import io.openems.api.rest.RestWorker;
import io.openems.config.Config;
import io.openems.config.exception.ConfigException;

@Component(name="io.openems")
public class OpenemsApplication {
	private final static Logger log = LoggerFactory.getLogger(OpenemsApplication.class);

	private static Config config = null;
	
	@Activate
	public void activate() throws JsonIOException, JsonSyntaxException, ConfigException, FileNotFoundException {
		log.info("Activate OpenemsApplication");
		config = new Config(Config.readJsonFile());
		OpenemsGlobal.updateConfig(config);
		// Run all api's
		try {
			RestWorker.startWorker();
		} catch (Exception e) {
			log.warn("Unable to start REST-Api");
			e.printStackTrace();
		}
		new Iec60870Server().start();
	}
}
