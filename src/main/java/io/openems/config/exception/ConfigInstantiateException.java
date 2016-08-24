package io.openems.config.exception;

import com.google.gson.JsonElement;

public class ConfigInstantiateException extends ConfigException {
	private static final long serialVersionUID = 1L;

	private final static String MESSAGE = "Unable to instantiate '%s' from configuration: %s";

	public ConfigInstantiateException(String factoryName, JsonElement jsonElement) {
		super(String.format(MESSAGE, factoryName, jsonElement.toString()));
	}

}
