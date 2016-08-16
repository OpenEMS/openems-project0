package io.openems.element.type;

import com.google.gson.JsonPrimitive;

public class BooleanType extends Type {
	private final boolean value;

	public BooleanType(boolean value) {
		this.value = value;
	}

	public Boolean toBoolean() {
		return value;
	}

	@Override
	public boolean isEqual(Type otherType) {
		if (otherType == null || !(otherType instanceof BooleanType))
			return false;
		return ((BooleanType) otherType).toBoolean() == value;
	}

	@Override
	public String readable() {
		if (value) {
			return "true";
		} else {
			return "false";
		}
	}

	@Override
	public JsonPrimitive toJson() {
		return new JsonPrimitive(value);
	}
}
