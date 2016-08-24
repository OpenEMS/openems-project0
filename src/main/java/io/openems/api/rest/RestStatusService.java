package io.openems.api.rest;

import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.service.StatusService;;

public class RestStatusService extends StatusService {
	/*
	 * @Override public Status getStatus(Throwable throwable, Request request,
	 * Response response) { // TODO Auto-generated method stub
	 * System.out.println("getStatus"); System.out.println("THROWABLE: " +
	 * throwable); System.out.println("REQUEST: " + request);
	 * System.out.println("RESPONSE: " + response); // return new Status(404);
	 * ResourceException e = (ResourceException) throwable; System.out.println(
	 * "CAUSE: " + e.getCause()); /* if (throwable instanceof ConfigException) {
	 * return new Status(500, "test"); }
	 * 
	 * // return super.getStatus(e.getCause(), request, response); return new
	 * Status(404, "Hallo", "Hallo2"); }
	 */

	@Override
	public Representation toRepresentation(Status status, Request request, Response response) {
		System.out.println("getRepresentation");
		System.out.println("STATUS: " + status);
		System.out.println("REQUEST: " + request);
		System.out.println("RESPONSE: " + response);
		Representation r = super.toRepresentation(status, request, response);
		System.out.println("REPRE: " + r);
		return r;
	}

	// TODO implement custom StatusService to forward ConfigException to client
	/*
	 * @Override public Status toStatus(Throwable throwable, Request request,
	 * Response response) { if (throwable instanceof ConfigException) { return
	 * Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY; } return
	 * super.toStatus(throwable, request, response); }
	 * 
	 * @Override public Representation toRepresentation(Status status, Request
	 * request, Response response) { // According to the preferred media type if
	 * (isHtmlContentRequested(request)) { // return HTML representation
	 * System.out.println("isHtmlContentRequested"); if (status ==
	 * Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY) {
	 * System.out.println("CLIENT_ERROR_UNPROCESSABLE_ENTITY"); } status = new
	 * Status(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, "TEST TEST"); } else {
	 * // return structured representation of the error System.out.println(
	 * "isHtmlContentRequested ELSE"); } return super.toRepresentation(status,
	 * request, response); }
	 * 
	 * private boolean isHtmlContentRequested(Request request) { // Get accept
	 * media types for the client ClientInfo clientInfo =
	 * request.getClientInfo(); List<Preference<MediaType>> mediaTypes =
	 * clientInfo.getAcceptedMediaTypes(); for (Preference<MediaType> mediaType
	 * : mediaTypes) { // Check if the media type is HTML if
	 * (MediaType.TEXT_HTML.equals(mediaType.getMetadata())) { return true; } }
	 * return false; }
	 */

	private boolean isHtmlContentRequested(Request request) { // Get accept
		ClientInfo clientInfo = request.getClientInfo();
		List<Preference<MediaType>> mediaTypes = clientInfo.getAcceptedMediaTypes();
		for (Preference<MediaType> mediaType : mediaTypes) { // Check if the
																// media type is
																// HTML
			if (MediaType.TEXT_HTML.equals(mediaType.getMetadata())) {
				return true;
			}
		}
		return false;
	}
}
