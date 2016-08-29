package io.openems.element.type;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonPrimitive;

import io.openems.device.protocol.BitElement;

public class BooleanMapType extends Type {
	private final Map<String, BitElement> map = new HashMap<>();

	@Override
	public boolean isEqual(Type otherType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String readable() {
		// TODO implement proper readable method
		return "[BooleanMapType]";
	}

	@Override
	public JsonPrimitive toJson() {
		// TODO proper implementation
		return new JsonPrimitive("");
	}
}
