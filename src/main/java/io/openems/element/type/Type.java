package io.openems.element.type;

import com.google.gson.JsonPrimitive;

public abstract class Type {
	public abstract boolean isEqual(Type otherType);

	public abstract String readable();

	public abstract JsonPrimitive toJson();

}
