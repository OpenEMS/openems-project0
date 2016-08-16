package io.openems.element.type;

public class BooleanType extends Type {
	private final Boolean value;

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
}
