package io.openems.element.type;

import com.google.gson.JsonPrimitive;

public class StringType extends Type {
	private final String value;

	public StringType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public boolean isEqual(Type otherType) {
		if (otherType == null || !(otherType instanceof StringType))
			return false;
		return ((StringType) otherType).toString() == value;
	}

	@Override
	public String readable() {
		return value;
	}

	@Override
	public JsonPrimitive toJson() {
		return new JsonPrimitive(value);
	}
}
