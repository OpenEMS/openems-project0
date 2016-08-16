package io.openems.element.type;

public class DoubleType extends Type {
	private final Double value;

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
}
