package io.openems.api.rest;

import org.restlet.service.ConverterService;

public class RestConverterService extends ConverterService {
	//TODO: needs org.restlet: 2.3.7
/*
	@Override
	public Representation toRepresentation(Object source, Variant target, Resource resource) throws IOException {
		if (source instanceof ConfigException) {
			return new StringRepresentation(((ConfigException) source).getMessage());
		}
		return super.toRepresentation(source, target, resource);
	}
	*/
}
