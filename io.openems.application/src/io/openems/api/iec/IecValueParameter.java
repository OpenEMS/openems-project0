package io.openems.api.iec;

public class IecValueParameter {
	private int addressOffset;
	private String ElementName;
	private double multiplier;

	public int getAddressOffset() {
		return addressOffset;
	}

	public void setAddressOffset(int offset) {
		this.addressOffset = offset;
	}

	public String getElementName() {
		return ElementName;
	}

	public void setElementName(String elementName) {
		ElementName = elementName;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

	public IecValueParameter(int addressOffset, String elementName, double multiplier) {
		super();
		this.addressOffset = addressOffset;
		ElementName = elementName;
		this.multiplier = multiplier;
	}
}
