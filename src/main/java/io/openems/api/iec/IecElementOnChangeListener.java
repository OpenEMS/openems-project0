package io.openems.api.iec;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.IeQuality;
import org.openmuc.j60870.IeShortFloat;
import org.openmuc.j60870.IeSinglePointWithQuality;
import org.openmuc.j60870.IeTime56;
import org.openmuc.j60870.InformationElement;
import org.openmuc.j60870.InformationObject;
import org.openmuc.j60870.TypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.device.protocol.BitElement;
import io.openems.element.Element;
import io.openems.element.ElementOnChangeListener;
import io.openems.element.InvalidValueExcecption;
import io.openems.element.type.BooleanType;
import io.openems.element.type.DoubleType;
import io.openems.element.type.IntegerType;
import io.openems.element.type.LongType;
import io.openems.element.type.Type;

public class IecElementOnChangeListener implements ElementOnChangeListener {

	private final static Logger log = LoggerFactory.getLogger(IecElementOnChangeListener.class);

	private Element<?> element;
	private ConnectionListener iecConnection;
	private int iOA;
	private float multiplier;
	private final MessageType messageType;
	private final int wait = 3000;
	private long lastSend = 0;

	public IecElementOnChangeListener(Element<?> element, ConnectionListener iecConnection, int iOA, float multiplier,
			MessageType messageType) {
		super();
		this.element = element;
		this.iecConnection = iecConnection;
		this.iOA = iOA;
		this.multiplier = multiplier;
		this.messageType = messageType;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	@Override
	public void elementChanged(String name, Type newValue, Type oldValue) {
		if (!newValue.isEqual(oldValue)) {
			if (lastSend + wait <= System.currentTimeMillis()) {
				if (iecConnection != null && name.equals(element.getFullName())) {
					switch (messageType) {
					case COMMAND:
						break;
					case MEASSUREMENT:
						try {
							iecConnection
									.addASduToQueue(new ASdu(TypeId.M_ME_TF_1, false, CauseOfTransmission.SPONTANEOUS,
											false, false, 0, 5101, new InformationObject[] { getCurrentValue() }));
						} catch (Exception e) {
							log.error("Failed to send Iec Spontaneous Values");
							remove();
						}
						break;
					case MESSAGE:
						try {
							iecConnection
									.addASduToQueue(new ASdu(TypeId.M_SP_TB_1, false, CauseOfTransmission.SPONTANEOUS,
											false, false, 0, 5101, new InformationObject[] { getCurrentValue() }));
						} catch (Exception e) {
							log.error("Failed to send Iec Spontaneous Values");
							remove();
						}
						break;
					case SCALEDVALUES:
						break;
					}
				}
				lastSend = System.currentTimeMillis();
			}
		}
	}

	public InformationObject getCurrentValue() throws InvalidValueExcecption {
		InformationObject io = null;
		switch (messageType) {
		case COMMAND:
			break;
		case MEASSUREMENT:
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
				io = new InformationObject(iOA,
						new InformationElement[][] { { new IeShortFloat((value) * multiplier),
								new IeQuality(false, false, false, false, false),
								new IeTime56(element.getLastUpdate().getMillis()) } });
			} else {
				io = new InformationObject(iOA, new InformationElement[][] {
						{ new IeShortFloat(0), new IeQuality(false, false, false, false, false), new IeTime56(0) } });
			}
			break;
		case MESSAGE:
			if (element != null && element.getLastUpdate() != null) {
				boolean value = false;
				boolean invalid = true;
				if (element instanceof BitElement) {
					BitElement el = (BitElement) element;
					value = el.getValue().toBoolean();
					invalid = false;
				} else if (element.getValue() instanceof BooleanType) {
					value = ((BooleanType) element.getValue()).toBoolean();
					invalid = false;
				}
				io = new InformationObject(iOA,
						new InformationElement[][] { { new IeSinglePointWithQuality(value, false, false, false, false),
								new IeTime56(element.getLastUpdate().getMillis()) } });
			} else {
				io = new InformationObject(iOA, new InformationElement[][] {
						{ new IeSinglePointWithQuality(false, false, false, false, false), new IeTime56(0) } });
			}
			break;
		}
		return io;
	}

	public void remove() {
		element.removeOnChangeListener(this);
	}
}
