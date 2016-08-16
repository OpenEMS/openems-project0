package io.openems.element.type;

public class StringType extends Type {
	private final String value;

	public StringType(String value) {
		this.value = value;
	}

	public String toString() {
		return value;
	}

	@Override
	public boolean isEqual(Type otherType) {
		if (otherType == null || !(otherType instanceof StringType))
			return false;
		return ((StringType) otherType).toString() == value;
	}
}
