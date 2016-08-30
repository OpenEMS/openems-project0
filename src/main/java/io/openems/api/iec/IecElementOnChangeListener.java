package io.openems.api.iec;

import io.openems.device.protocol.BitElement;
import io.openems.device.protocol.NumberElement;
import io.openems.element.Element;
import io.openems.element.ElementOnChangeListener;
import io.openems.element.type.DoubleType;
import io.openems.element.type.IntegerType;
import io.openems.element.type.LongType;
import io.openems.element.type.Type;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.IeDoublePointWithQuality;
import org.openmuc.j60870.IeDoublePointWithQuality.DoublePointInformation;
import org.openmuc.j60870.IeQuality;
import org.openmuc.j60870.IeShortFloat;
import org.openmuc.j60870.IeTime56;
import org.openmuc.j60870.InformationElement;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IecElementOnChangeListener implements ElementOnChangeListener {

	private final static Logger log = LoggerFactory.getLogger(IecElementOnChangeListener.class);

	private Element<?> element;
	private Connection iecConnection;
	private int iOA;
	private float multiplier;
	private boolean isMeassurement = false;

	public IecElementOnChangeListener(Element<?> element, Connection iecConnection, int iOA, float multiplier) {
		super();
		this.element = element;
		this.iecConnection = iecConnection;
		this.iOA = iOA;
		this.multiplier = multiplier;
		if (element instanceof NumberElement<?>) {
			isMeassurement = true;
		}
	}

	public IecElementOnChangeListener(Element<?> element, Connection iecConnection, int iOA) {
		this(element, iecConnection, iOA, 0);
	}

	public boolean isMeassurement() {
		return isMeassurement;
	}

	@Override
	public void elementChanged(String name, Type newValue, Type oldValue) {
		if (iecConnection != null && name.equals(element.getFullName())) {
			if (isMeassurement) {
				try {
					iecConnection.send(new ASdu(TypeId.M_ME_TF_1, false, CauseOfTransmission.SPONTANEOUS, false, false,
							0, 5101, new InformationObject[] { getCurrentValue() }));
				} catch (Exception e) {
					log.error("Failed to send Iec Spontaneous Values");
					remove();
				}
			}
		}
	}

	public InformationObject getCurrentValue() {
		InformationObject io = null;
		if (isMeassurement) {
			if (element != null && element.getLastUpdate() != null) {
				float value = 0;
				Object newValue = element.getValue();
				if (newValue instanceof LongType) {
					value = ((LongType) newValue).toLong();
				} else if (newValue instanceof IntegerType) {
					value = ((IntegerType) newValue).toInteger();
				} else if (newValue instanceof DoubleType) {
					value = ((DoubleType) newValue).toDouble().floatValue();
				}
				io = new InformationObject(iOA, new InformationElement[][] { { new IeShortFloat((value) * multiplier),
						new IeQuality(false, false, false, false, false),
						new IeTime56(element.getLastUpdate().getMillis()) } });
			} else {
				io = new InformationObject(iOA, new InformationElement[][] { { new IeShortFloat(0),
						new IeQuality(false, false, false, false, false), new IeTime56(0) } });
			}
		} else {
			if (element != null && element.getLastUpdate() != null) {
				BitElement el = (BitElement) element;
				DoublePointInformation dpi = DoublePointInformation.INDETERMINATE;
				if (el.getValue().toBoolean()) {
					dpi = DoublePointInformation.ON;
				} else {
					dpi = DoublePointInformation.OFF;
				}
				io = new InformationObject(iOA, new InformationElement[][] { {
						new IeDoublePointWithQuality(dpi, false, false, false, false), new IeTime56(0) } });
			} else {
				io = new InformationObject(iOA, new InformationElement[][] { {
						new IeDoublePointWithQuality(DoublePointInformation.INDETERMINATE, false, false, false, false),
						new IeTime56(0) } });
			}
		}
		return io;
	}

	public void remove() {
		element.removeOnChangeListener(this);
	}
}
