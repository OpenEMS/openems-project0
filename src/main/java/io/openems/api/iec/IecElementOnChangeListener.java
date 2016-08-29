package io.openems.api.iec;

import io.openems.element.ElementOnChangeListener;
import io.openems.element.type.DoubleType;
import io.openems.element.type.IntegerType;
import io.openems.element.type.LongType;
import io.openems.element.type.Type;

import java.io.IOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
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

	private String elementName;
	private Connection iecConnection;
	private int iOA;
	private double multiplier;

	public IecElementOnChangeListener(String elementName, Connection iecConnection, int iOA, double multiplier) {
		super();
		this.elementName = elementName;
		this.iecConnection = iecConnection;
		this.iOA = iOA;
		this.multiplier = multiplier;
	}

	@Override
	public void elementChanged(String name, Type newValue, Type oldValue) {
		if (iecConnection != null && name.equals(elementName)) {
			float value = 0;
			if (newValue instanceof LongType) {
				value = ((LongType) newValue).toLong();
			} else if (newValue instanceof IntegerType) {
				value = ((IntegerType) newValue).toInteger();
			} else if (newValue instanceof DoubleType) {
				value = ((DoubleType) newValue).toDouble().floatValue();
			}
			try {
				iecConnection.send(new ASdu(TypeId.M_ME_TF_1, false, CauseOfTransmission.SPONTANEOUS, false, false, 0,
						5101, new InformationObject[] { new InformationObject(iOA, new InformationElement[][] { {
								new IeShortFloat((float) (value * multiplier)),
								new IeQuality(false, false, false, false, false),
								new IeTime56(System.currentTimeMillis()) } }) }));
			} catch (IOException e) {
				log.error("Failed to send Iec Spontaneous Values");
			}
		}
	}

}
