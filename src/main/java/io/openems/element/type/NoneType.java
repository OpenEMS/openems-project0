package io.openems.element.type;

import com.google.gson.JsonPrimitive;

public class NoneType extends Type {
	public NoneType() {
	}

	@Override
	public boolean isEqual(Type otherType) {
		if (otherType == null || !(otherType instanceof NoneType))
			return false;
		return equals(otherType);
	}

	@Override
	public String readable() {
		return "[None]";
	}

	@Override
	public JsonPrimitive toJson() {
		return new JsonPrimitive("");
	}
}
