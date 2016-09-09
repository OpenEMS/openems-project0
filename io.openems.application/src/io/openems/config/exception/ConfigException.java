package io.openems.config.exception;

//TODO OSGi import org.restlet.resource.Status;

//TODO OSGi @Status(value = 422, serialize = true)
public class ConfigException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(Exception exception) {
		super(exception.getMessage());
	}
}
