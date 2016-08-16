package io.openems.element.type;

public abstract class Type {
	public abstract boolean isEqual(Type otherType);

	public abstract String readable();
}
