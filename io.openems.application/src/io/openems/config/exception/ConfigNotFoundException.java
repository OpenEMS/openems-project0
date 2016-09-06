package io.openems.config.exception;

import com.google.gson.JsonObject;

public class ConfigNotFoundException extends ConfigException {
	private static final long serialVersionUID = 1L;

	private final static String MESSAGE = "Missing '%s' in configuration: %s";

	public ConfigNotFoundException(String memberName, JsonObject jsonObject) {
		super(String.format(MESSAGE, memberName, jsonObject.toString()));
	}

}
