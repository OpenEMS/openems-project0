package io.openems.element.type;

public class IntegerType extends Type {
	private final Integer value;

	public IntegerType(int value) {
		this.value = value;
	}

	public Integer toInteger() {
		return value;
	}

	@Override
	public boolean isEqual(Type otherType) {
		if (otherType == null || !(otherType instanceof IntegerType))
			return false;
		return ((IntegerType) otherType).toInteger() == value;
	}
}
