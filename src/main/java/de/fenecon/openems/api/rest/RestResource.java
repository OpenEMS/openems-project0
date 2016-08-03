package de.fenecon.openems.api.rest;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class RestResource extends ServerResource {

	@Get
	public String represent() {
		return "hello, world";
	}

}