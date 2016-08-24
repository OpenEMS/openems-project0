package io.openems.api.rest;

import java.io.IOException;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;
import org.restlet.service.ConverterService;

import io.openems.config.exception.ConfigException;

public class ExceptionConverterService extends ConverterService {

	@Override
	public Representation toRepresentation(Object source, Variant target, Resource resource) throws IOException {
		if (source instanceof ConfigException) {
			return new StringRepresentation(((ConfigException) source).getMessage());
		}
		return super.toRepresentation(source, target, resource);
	}
}
