package io.openems.config.exception;

import com.google.gson.JsonObject;

public class ConfigParseException extends ConfigException {
	private static final long serialVersionUID = 1L;

	private final static String MESSAGE = "Invalid internet address '%s' from configuration: %s";

	public ConfigParseException(String memberName, JsonObject jsonObject) {
		super(String.format(MESSAGE, memberName, jsonObject.toString()));
	}

}
