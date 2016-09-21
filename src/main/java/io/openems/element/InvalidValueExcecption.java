package io.openems.element;

/*
 * This exception occures if there is no possibility to update the value of an device 
 */
public class InvalidValueExcecption extends Exception {
	public InvalidValueExcecption(String message) {
		super(message);
	}
}
