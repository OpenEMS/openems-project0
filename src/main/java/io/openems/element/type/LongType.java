package io.openems.element.type;

import com.google.gson.JsonPrimitive;

public class LongType extends Type {
	private final Long value;

	public LongType(long value) {
		this.value = value;
	}

	public Long toLong() {
		return value;
	}

	@Override
	public boolean isEqual(Type otherType) {
		if (otherType == null || !(otherType instanceof LongType))
			return false;
		return ((LongType) otherType).toLong() == value;
	}

	@Override
	public String readable() {
		return String.format("%d", value);
	}

	@Override
	public JsonPrimitive toJson() {
		return new JsonPrimitive(value);
	}
}
