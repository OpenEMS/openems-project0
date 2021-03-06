package io.openems.element.type;

import com.google.gson.JsonPrimitive;

public class DoubleType extends Type {
	private final double value;

	public DoubleType(double value) {
		this.value = value;
	}

	public Double toDouble() {
		return value;
	}

	@Override
	public boolean isEqual(Type otherType) {
		if (otherType == null || !(otherType instanceof DoubleType))
			return false;
		return ((DoubleType) otherType).toDouble() == value;
	}

	@Override
	public String readable() {
		return String.format("%.3f", value);
	}

	@Override
	public JsonPrimitive toJson() {
		return new JsonPrimitive(value);
	}
}
