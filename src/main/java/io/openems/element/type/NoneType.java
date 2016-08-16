package io.openems.element.type;

public class NoneType extends Type {
	public NoneType() {
	}

	@Override
	public boolean isEqual(Type otherType) {
		if (otherType == null || !(otherType instanceof NoneType))
			return false;
		return equals(otherType);
	}
}
