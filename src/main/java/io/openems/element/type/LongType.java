package io.openems.element.type;

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
}
