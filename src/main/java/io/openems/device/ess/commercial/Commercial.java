/*
 * OpenEMS - Open Source Energy Management System
 * Copyright (c) 2016 FENECON GmbH & Co. KG
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.openems.device.ess.commercial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmuc.j60870.IeDoubleCommand;
import org.openmuc.j60870.IeShortFloat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.api.iec.ConnectionListener;
import io.openems.api.iec.IecElementOnChangeListener;
import io.openems.api.iec.MessageType;
import io.openems.channel.modbus.write.ModbusSingleRegisterWriteRequest;
import io.openems.device.ess.Ess;
import io.openems.device.ess.EssProtocol;
import io.openems.device.protocol.BitElement;
import io.openems.device.protocol.BitsElement;
import io.openems.device.protocol.ElementBuilder;
import io.openems.device.protocol.ElementLength;
import io.openems.device.protocol.ElementRange;
import io.openems.device.protocol.ElementType;
import io.openems.device.protocol.ModbusElement;
import io.openems.device.protocol.ModbusProtocol;
import io.openems.device.protocol.SignedIntegerWordElement;
import io.openems.device.protocol.UnsignedShortWordElement;
import io.openems.device.protocol.WordOrder;
import io.openems.element.Element;
import io.openems.element.InvalidValueExcecption;

public class Commercial extends Ess {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(Commercial.class);

	public Commercial(String name, String channel, int unitid, int minSoc) {
		super(name, channel, unitid, minSoc);
	}

	@Override
	public String toString() {
		return "Commercial [name=" + name + ", unitid=" + unitid + "]";
	}

	@Override
	public Set<String> getInitElements() {
		return new HashSet<String>(Arrays.asList( //
				EssProtocol.SystemState.name()));
	}

	@Override
	public Set<String> getMainElements() {
		return new HashSet<String>(Arrays.asList( //
				EssProtocol.ActivePower.name(), //
				EssProtocol.ReactivePower.name(), //
				EssProtocol.ApparentPower.name(), //
				EssProtocol.AllowedCharge.name(), //
				EssProtocol.AllowedDischarge.name(), //
				EssProtocol.AllowedApparent.name(), //
				EssProtocol.GridMode.name(), //
				EssProtocol.BatteryStringSoc.name(), //
				EssProtocol.InverterActivePower.name()));
	}

	@Override
	public Set<String> getWriteElements() {
		return new HashSet<String>(Arrays.asList( //
				EssProtocol.SetActivePower.name(), //
				EssProtocol.SetWorkState.name()));
	}

	@Override
	protected ModbusProtocol getProtocol() {
		ModbusProtocol protocol = new ModbusProtocol(name);
		protocol.addElementRange(new ElementRange(0x0101,
				new ElementBuilder(0x0101, name).name(EssProtocol.SystemState) //
						.bit(new BitElement(1, EssProtocol.SystemStates.Stop.name())) //
						.bit(new BitElement(2, EssProtocol.SystemStates.PvCharging.name())) //
						.bit(new BitElement(3, EssProtocol.SystemStates.Standby.name())) //
						.bit(new BitElement(4, EssProtocol.SystemStates.Running.name())) //
						.bit(new BitElement(5, EssProtocol.SystemStates.Fault.name())) //
						.bit(new BitElement(6, EssProtocol.SystemStates.Debug.name())).build(), //
				new ElementBuilder(0x0102, name).name(EssProtocol.ControlMode)//
						.bit(new BitElement(0, EssProtocol.ControlModes.Remote.name()))//
						.bit(new BitElement(1, EssProtocol.ControlModes.LocalManual.name())).build(),
				new ElementBuilder(0x0103, name).name(EssProtocol.WorkMode)//
						.bit(new BitElement(9, EssProtocol.RemoteDispatch.name())).build(), //
				new ElementBuilder(0x0104, name).name(EssProtocol.BatteryMaintananceState).build(), //
				new ElementBuilder(0x0105, name).name(EssProtocol.InverterState)//
						.bit(new BitElement(0, EssProtocol.InverterStates.Initial.name()))//
						.bit(new BitElement(1, EssProtocol.InverterStates.Fault.name()))//
						.bit(new BitElement(2, EssProtocol.InverterStates.Stop.name()))//
						.bit(new BitElement(3, EssProtocol.InverterStates.Standby.name()))//
						.bit(new BitElement(4, EssProtocol.InverterStates.GridMonitoring.name()))//
						.bit(new BitElement(5, EssProtocol.InverterStates.Ready.name()))//
						.bit(new BitElement(6, EssProtocol.InverterStates.Running.name()))//
						.bit(new BitElement(7, EssProtocol.InverterStates.Debug.name())).build(), //
				new ElementBuilder(0x0106, name).name(EssProtocol.GridMode).build(), //
				new ElementBuilder(0x0107, name).type(ElementType.PLACEHOLDER).intLength(0x0108 - 0x0107).build(), //
				new ElementBuilder(0x0108, name).name(EssProtocol.ProtocolVersion).build(), //
				new ElementBuilder(0x0109, name).name(EssProtocol.SystemManufacturer).build(), //
				new ElementBuilder(0x010A, name).name(EssProtocol.SystemType).build()));
		protocol.addElementRange(
				new ElementRange(0x0110, //
						new ElementBuilder(0x0110, name)//
								.bit(new BitElement(2, EssProtocol.Information.EmergencyStop.name()))//
								.bit(new BitElement(6, EssProtocol.Information.ManualStop.name())).build(), //
						new ElementBuilder(0x0111, name)//
								.bit(new BitElement(3,
										EssProtocol.Information.TransformertPH1TempSensInvalidation.name()))//
								.bit(new BitElement(12, EssProtocol.Information.SDCardInvalidation.name())).build()));
		protocol.addElementRange(new ElementRange(0x0125, //
				new ElementBuilder(0x0125, name)//
						.bit(new BitElement(0, EssProtocol.Information.InverterCommunicationAbnormity.name()))//
						.bit(new BitElement(1, EssProtocol.Information.BatteryCommunicationAbnormity.name()))//
						.bit(new BitElement(2, EssProtocol.Information.AmmeterCommunicationAbnormity.name()))//
						.bit(new BitElement(4, EssProtocol.Information.RemoteCommunicationAbnormity.name())).build(),
				new ElementBuilder(0x0126, name)
						//
						.bit(new BitElement(3, EssProtocol.Information.TransformerSevereOvertemperature.name()))
						.build()));
		protocol.addElementRange(new ElementRange(0x0150, //
				new ElementBuilder(0x0150, name).name(EssProtocol.SwitchStates) //
						.bit(new BitElement(1, EssProtocol.Switches.DCMain.name())) //
						.bit(new BitElement(2, EssProtocol.Switches.DCPrecharge.name())) //
						.bit(new BitElement(3, EssProtocol.Switches.ACBreaker.name())) //
						.bit(new BitElement(4, EssProtocol.Switches.ACMain.name())) //
						.bit(new BitElement(5, EssProtocol.Switches.ACPrecharge.name())).build()));
		protocol.addElementRange(new ElementRange(0x0180, //
				new ElementBuilder(0x0180, name)
						.bit(new BitElement(0, EssProtocol.Abnormity.DCPrechargeContactorCloseUnsuccessfully.name()))
						.bit(new BitElement(1, EssProtocol.Abnormity.ACPrechargeContactorCloseUnsuccessfully.name()))
						.bit(new BitElement(2, EssProtocol.Abnormity.ACMainContactorCloseUnsuccessfully.name()))
						.bit(new BitElement(3, EssProtocol.Abnormity.DCElectricalBreaker1CloseUnsuccessfully.name()))
						.bit(new BitElement(4, EssProtocol.Abnormity.DCMainContactorCloseUnsuccessfully.name()))
						.bit(new BitElement(5, EssProtocol.Abnormity.ACBreakerTrip.name()))
						.bit(new BitElement(6, EssProtocol.Abnormity.ACMainContactorOpenWhenRunning.name()))
						.bit(new BitElement(7, EssProtocol.Abnormity.DCMainContactorOpenWhenRunning.name()))
						.bit(new BitElement(8, EssProtocol.Abnormity.ACMainContactorOpenUnsuccessfully.name()))
						.bit(new BitElement(9, EssProtocol.Abnormity.DCElectricalBreaker1OpenUnsuccessfully.name()))
						.bit(new BitElement(10, EssProtocol.Abnormity.DCMainContactorOpenUnsuccessFully.name()))
						.bit(new BitElement(11, EssProtocol.Abnormity.HardwarePDPFault.name()))
						.bit(new BitElement(12, EssProtocol.Abnormity.MasterStopSuddenly.name())).build()));
		protocol.addElementRange(new ElementRange(0x0182, //
				new ElementBuilder(0x0182, name)

						//
						.bit(new BitElement(0, EssProtocol.Abnormity.DCShortCircuitProtection.name()))
						.bit(new BitElement(1, EssProtocol.Abnormity.DCOvervoltageProtection.name()))
						.bit(new BitElement(2, EssProtocol.Abnormity.DCUndervoltageProtection.name()))
						.bit(new BitElement(3, EssProtocol.Abnormity.DCInverseConnectionProtection.name()))
						.bit(new BitElement(4, EssProtocol.Abnormity.DCDisconnectionProtection.name()))
						.bit(new BitElement(5, EssProtocol.Abnormity.CommutingColtageAbnormityProtection.name()))
						.bit(new BitElement(6, EssProtocol.Abnormity.DCOvercurrentProtection.name()))
						.bit(new BitElement(7, EssProtocol.Abnormity.Phase1PeakCurrentOverLimitProtection.name()))
						.bit(new BitElement(8, EssProtocol.Abnormity.Phase2PeakCurrentOverLimitProtection.name()))
						.bit(new BitElement(9, EssProtocol.Abnormity.Phase3PeakCurrentOverLimitProtection.name()))
						.bit(new BitElement(10, EssProtocol.Abnormity.Phase1VirtualCurrentOverLimitProtection.name()))
						.bit(new BitElement(11, EssProtocol.Abnormity.Phase2VirtualCurrentOverLimitProtection.name()))
						.bit(new BitElement(12, EssProtocol.Abnormity.Phase3VirtualCurrentOverLimitProtection.name()))
						.bit(new BitElement(13, EssProtocol.Abnormity.Phase1GridVoltageSamplingInvalidation.name()))
						.bit(new BitElement(14, EssProtocol.Abnormity.Phase2GridVoltageSamplingInvalidation.name()))
						.bit(new BitElement(15, EssProtocol.Abnormity.Phase3GridVoltageSamplingInvalidation.name()))
						.build(), //
				new ElementBuilder(0x0183, name)

						//
						.bit(new BitElement(0, EssProtocol.Abnormity.Phase1InverterVoltageSamplingInvalidation.name()))
						.bit(new BitElement(1, EssProtocol.Abnormity.Phase2InverterVoltageSamplingInvalidation.name()))
						.bit(new BitElement(2, EssProtocol.Abnormity.Phase3InverterVoltageSamplingInvalidation.name()))
						.bit(new BitElement(3, EssProtocol.Abnormity.ACCurrentSamplingInvalidation.name()))
						.bit(new BitElement(4, EssProtocol.Abnormity.DCCurrentSamplingInvalidation.name()))
						.bit(new BitElement(5, EssProtocol.Abnormity.Phase1OvertemperatureProtection.name()))
						.bit(new BitElement(6, EssProtocol.Abnormity.Phase2OvertemperatureProtection.name()))
						.bit(new BitElement(7, EssProtocol.Abnormity.Phase3OvertemperatureProtection.name()))
						.bit(new BitElement(8, EssProtocol.Abnormity.Phase1TemperatureSamplingInvalidation.name()))
						.bit(new BitElement(9, EssProtocol.Abnormity.Phase2TemperatureSamplingInvalidation.name()))
						.bit(new BitElement(10, EssProtocol.Abnormity.Phase3TemperatureSamplingInvalidation.name()))
						.bit(new BitElement(11, EssProtocol.Abnormity.Phase1PrechargeUnmetProtection.name()))
						.bit(new BitElement(12, EssProtocol.Abnormity.Phase2PrechargeUnmetProtection.name()))
						.bit(new BitElement(13, EssProtocol.Abnormity.Phase3PrechargeUnmetProtection.name()))
						.bit(new BitElement(14, EssProtocol.Abnormity.UnadaptablePhaseSequenceErrorProtection.name()))
						.bit(new BitElement(15, EssProtocol.Abnormity.DSPProtection.name()))
						.build(),
				new ElementBuilder(0x0184, name)
						//
						.bit(new BitElement(0,
								EssProtocol.Abnormity.Phase1GridVoltageSevereOvervoltageProtection.name()))//
						.bit(new BitElement(1,
								EssProtocol.Abnormity.Phase1GridVoltageGeneralOvervoltageProtection.name()))//
						.bit(new BitElement(2,
								EssProtocol.Abnormity.Phase2GridVoltageSevereOvervoltageProtection.name()))//
						.bit(new BitElement(3,
								EssProtocol.Abnormity.Phase2GridVoltageGeneralOvervoltageProtection.name()))//
						.bit(new BitElement(4,
								EssProtocol.Abnormity.Phase3GridVoltageSevereOvervoltageProtection.name()))//
						.bit(new BitElement(5,
								EssProtocol.Abnormity.Phase3GridVoltageGeneralOvervoltageProtection.name()))//
						.bit(new BitElement(6,
								EssProtocol.Abnormity.Phase1GridVoltageSevereUndervoltageProtection.name()))//
						.bit(new BitElement(7,
								EssProtocol.Abnormity.Phase1GridVoltageGeneralUndervoltageProtection.name()))//
						.bit(new BitElement(8,
								EssProtocol.Abnormity.Phase2GridVoltageSevereUndervoltageProtection.name()))//
						.bit(new BitElement(9,
								EssProtocol.Abnormity.Phase2GridVoltageGeneralUndervoltageProtection.name()))//
						.bit(new BitElement(10,
								EssProtocol.Abnormity.Phase3GridVoltageSevereUndervoltageProtection.name()))//
						.bit(new BitElement(11,
								EssProtocol.Abnormity.Phase3GridVoltageGeneralUndervoltageProtection.name()))//
						.bit(new BitElement(12, EssProtocol.Abnormity.SevereOverfequencyProtection.name()))//
						.bit(new BitElement(13, EssProtocol.Abnormity.GeneralOverfrequencyProtection.name()))//
						.bit(new BitElement(14, EssProtocol.Abnormity.SevereUnderfrequencyProtection.name()))//
						.bit(new BitElement(15, EssProtocol.Abnormity.GeneralUnderPrequencyProtection.name()))//
						.build(), //
				new ElementBuilder(0x0185, name)

						//
						.bit(new BitElement(0, EssProtocol.Abnormity.Phase1GridLoss.name()))
						.bit(new BitElement(1, EssProtocol.Abnormity.Phase2GridLoss.name()))
						.bit(new BitElement(2, EssProtocol.Abnormity.Phase3GridLoss.name()))
						.bit(new BitElement(3, EssProtocol.Abnormity.IslandingProtection.name()))
						.bit(new BitElement(4, EssProtocol.Abnormity.Phase1UnderVoltageRideThrough.name()))
						.bit(new BitElement(5, EssProtocol.Abnormity.Phase2UnderVoltageRideThrough.name()))
						.bit(new BitElement(6, EssProtocol.Abnormity.Phase3UnderVoltageRideThrough.name()))
						.bit(new BitElement(7,
								EssProtocol.Abnormity.Phase1InverterVoltageSevereOvervoltageProtection.name()))
						.bit(new BitElement(8,
								EssProtocol.Abnormity.Phase1InverterVoltageGeneralOvervoltageProtection.name()))
						.bit(new BitElement(9,
								EssProtocol.Abnormity.Phase2InverterVoltageSevereOvervoltageProtection.name()))
						.bit(new BitElement(10,
								EssProtocol.Abnormity.Phase2InverterVoltageGeneralOvervoltageProtection.name()))
						.bit(new BitElement(11,
								EssProtocol.Abnormity.Phase3InverterVoltageSevereOvervoltageProtection.name()))
						.bit(new BitElement(12,
								EssProtocol.Abnormity.Phase3InverterVoltageGeneralOvervoltageProtection.name()))
						.bit(new BitElement(13,
								EssProtocol.Abnormity.InverterPeakVoltageHighProtectionCauseByACDisconnect.name()))
						.build(), //
				new ElementBuilder(0x0186, name)

						//
						.bit(new BitElement(0, EssProtocol.Information.DCPrechargeContactorInspectionAbnormity.name()))
						.bit(new BitElement(1, EssProtocol.Information.DCBreaker1InspectionAbnormity.name()))
						.bit(new BitElement(2, EssProtocol.Information.DCBreaker2InspectionAbnormity.name()))
						.bit(new BitElement(3, EssProtocol.Information.ACPrechargeContactorInspectionAbnormity.name()))
						.bit(new BitElement(4, EssProtocol.Information.ACMainContactorInspectionAbnormity.name()))
						.bit(new BitElement(5, EssProtocol.Information.ACBreakerInspectionAbnormity.name()))
						.bit(new BitElement(6, EssProtocol.Information.DCBreaker1CloseUnsuccessfully.name()))
						.bit(new BitElement(7, EssProtocol.Information.DCBreaker2CloseUnsuccessfully.name()))
						.bit(new BitElement(8,
								EssProtocol.Information.ControlSignalCloseAbnormallyInspectedBySystem.name()))
						.bit(new BitElement(9,
								EssProtocol.Information.ControlSignalOpenAbnormallyInspectedBySystem.name()))
						.bit(new BitElement(10, EssProtocol.Information.NeutralWireContactorCloseUnsuccessfully.name()))
						.bit(new BitElement(11, EssProtocol.Information.NeutralWireContactorOpenUnsuccessfully.name()))
						.bit(new BitElement(12, EssProtocol.Information.WorkDoorOpen.name()))
						.bit(new BitElement(13, EssProtocol.Information.EmergencyStop2.name()))
						.bit(new BitElement(14, EssProtocol.Information.ACBreakerCloseUnsuccessfully.name()))
						.bit(new BitElement(15, EssProtocol.Information.ControlSwitchStop.name())).build(), //
				new ElementBuilder(0x0187, name).bit(new BitElement(0, EssProtocol.Information.GeneralOverload.name()))
						.bit(new BitElement(1, EssProtocol.Information.SevereOverload.name()))
						.bit(new BitElement(2, EssProtocol.Information.BatteryCurrentOverLimit.name()))
						.bit(new BitElement(3, EssProtocol.Information.PowerDecreaseCausedByOvertemperature.name()))
						.bit(new BitElement(4, EssProtocol.Information.InverterGeneralOvertemperature.name()))
						.bit(new BitElement(5, EssProtocol.Information.ACThreePhaseCurrentUnbalance.name()))
						.bit(new BitElement(6, EssProtocol.Information.RestoreFactorySettingUnsuccessfully.name()))
						.bit(new BitElement(7, EssProtocol.Information.PoleBoardInvalidatioin.name()))
						.bit(new BitElement(8, EssProtocol.Information.SelfInspectionFailed.name()))
						.bit(new BitElement(9, EssProtocol.Information.ReceiveBMSFaultAndStop.name()))
						.bit(new BitElement(10, EssProtocol.Information.RefrigerationEquipmentInvalidation.name()))
						.bit(new BitElement(11,
								EssProtocol.Information.LargeTemperatureDifferenceAmongIGBTThreePhases.name()))
						.bit(new BitElement(12, EssProtocol.Information.EEPROMParametersOverRange.name()))
						.bit(new BitElement(13, EssProtocol.Information.EEPROMParametersBackupFailed.name()))
						.bit(new BitElement(14, EssProtocol.Information.DCBreakerCloseUnsuccessfully.name())).build(), //
				new ElementBuilder(0x0188, name)

						//
						.bit(new BitElement(0,
								EssProtocol.Information.CommunicationBetweenInverterAndBSMUDisconnected.name()))
						.bit(new BitElement(1,
								EssProtocol.Information.CommunicationBetweenInverterAndMasterDisconnected.name()))
						.bit(new BitElement(2,
								EssProtocol.Information.CommunicationBetweenInverterAndUCDisconnected.name()))
						.bit(new BitElement(3, EssProtocol.Information.BMSStartOvertimeControlledByPCS.name()))
						.bit(new BitElement(4, EssProtocol.Information.BMSStopOvertimeControlledByPCS.name()))
						.bit(new BitElement(5, EssProtocol.Information.SyncSignalInvalidation.name()))
						.bit(new BitElement(6, EssProtocol.Information.SyncSignalContinuousCaptureFault.name()))
						.bit(new BitElement(7, EssProtocol.Information.SyncSignalSeveralTimesCaptureFault.name()))
						.build()//
		));
		protocol.addElementRange(new ElementRange(0x0200,
				new ElementBuilder(0x0200, name).name(EssProtocol.DcVoltage).unit("mV").multiplier(100).build(), //
				new ElementBuilder(0x0201, name).name(EssProtocol.DcCurrent).unit("mA").multiplier(100).build(), //
				new ElementBuilder(0x0202, name).name(EssProtocol.DcPower).unit("W").multiplier(100).build(), //
				new ElementBuilder(0x0203, name).type(ElementType.PLACEHOLDER).intLength(0x0208 - 0x0203).build(), //
				new ElementBuilder(0x0208, name).name(EssProtocol.ChargeEnergy).unit("Wh")
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).multiplier(100).build(), //
				new ElementBuilder(0x020A, name).name(EssProtocol.DischargeEnergy).unit("Wh").multiplier(100)
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build()));
		protocol.addElementRange(new ElementRange(0x0210,
				new ElementBuilder(0x0210, name).name(EssProtocol.ActivePower).multiplier(100).signed(true).unit("W")
						.build(),
				new ElementBuilder(0x0211, name).name(EssProtocol.ReactivePower).multiplier(100).signed(true)
						.unit("Var").build(), //
				new ElementBuilder(0x0212, name).name(EssProtocol.ApparentPower).multiplier(100).unit("VA").build(), //
				new ElementBuilder(0x0213, name).name(EssProtocol.CurrentPhase1).signed(true).multiplier(100).unit("mA")
						.build(), //
				new ElementBuilder(0x0214, name).name(EssProtocol.CurrentPhase2).signed(true).multiplier(100).unit("mA")
						.build(), //
				new ElementBuilder(0x0215, name).name(EssProtocol.CurrentPhase3).signed(true).multiplier(100).unit("mA")
						.build(), //
				new ElementBuilder(0x0216, name).type(ElementType.PLACEHOLDER).intLength(0x219 - 0x216).build(), //
				new ElementBuilder(0x0219, name).name(EssProtocol.VoltagePhase1).multiplier(100).unit("mV").build(), //
				new ElementBuilder(0x021A, name).name(EssProtocol.VoltagePhase2).multiplier(100).unit("mV").build(), //
				new ElementBuilder(0x021B, name).name(EssProtocol.VoltagePhase3).multiplier(100).unit("mV").build(), //
				new ElementBuilder(0x021C, name).name(EssProtocol.Frequency).multiplier(10).unit("mHZ").build(), //
				new ElementBuilder(0x021D, name).type(ElementType.PLACEHOLDER).intLength(0x222 - 0x21D).build(), //
				new ElementBuilder(0x0222, name).name(EssProtocol.InverterVoltagePhase1).signed(true).multiplier(100)
						.unit("mV").build(), //
				new ElementBuilder(0x0223, name).name(EssProtocol.InverterVoltagePhase2).signed(true).multiplier(100)
						.unit("mV").build(), //
				new ElementBuilder(0x0224, name).name(EssProtocol.InverterVoltagePhase3).signed(true).multiplier(100)
						.unit("mV").build(), //
				new ElementBuilder(0x0225, name).name(EssProtocol.InverterCurrentPhase1).signed(true).multiplier(100)
						.unit("mA").build(), //
				new ElementBuilder(0x0226, name).name(EssProtocol.InverterCurrentPhase2).signed(true).multiplier(100)
						.unit("mA").build(), //
				new ElementBuilder(0x0227, name).name(EssProtocol.InverterCurrentPhase3).signed(true).multiplier(100)
						.unit("mA").build(), //
				new ElementBuilder(0x0228, name).name(EssProtocol.InverterActivePower).signed(true).multiplier(100)
						.unit("W").build(), //
				new ElementBuilder(0x0229, name).type(ElementType.PLACEHOLDER).intLength(0x230 - 0x229).build(), //
				new ElementBuilder(0x0230, name).name(EssProtocol.AllowedCharge).multiplier(100).signed(true).unit("W")
						.build(), //
				new ElementBuilder(0x0231, name).name(EssProtocol.AllowedDischarge).multiplier(100).unit("W").build(), //
				new ElementBuilder(0x0232, name).name(EssProtocol.AllowedApparent).multiplier(100).unit("Var")
						.build()));
		protocol.addElementRange(new ElementRange(0x0240, //
				new ElementBuilder(0x0240, name).name(EssProtocol.IPMPhase1Temperature).unit("°C").signed(true).build(), //
				new ElementBuilder(0x0241, name).name(EssProtocol.IPMPhase2Temperature).unit("°C").signed(true).build(), //
				new ElementBuilder(0x0242, name).name(EssProtocol.IPMPhase3Temperature).unit("°C").signed(true).build(), //
				new ElementBuilder(0x0243, name).type(ElementType.PLACEHOLDER).intLength(0x0249 - 0x0243).build(), //
				new ElementBuilder(0x0249, name).name(EssProtocol.TransformerPhase1Temperature).unit("°C").signed(true)
						.build()));
		protocol.addElementRange(new ElementRange(0x0300, //
				new ElementBuilder(0x0300, name).name(EssProtocol.TotalEnergy).unit("kWh")
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build(), //
				new ElementBuilder(0x0302, name).name(EssProtocol.TotalYearEnergy).unit("kWh")
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build(), //
				new ElementBuilder(0x0304, name).name(EssProtocol.TotalMonthEnergy).unit("kWh")
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build(), //
				new ElementBuilder(0x0306, name).name(EssProtocol.TotalDateEnergy).unit("kWh").build()));
		protocol.addElementRange(new ElementRange(0x0500,
				new ElementBuilder(0x0500, name).name(EssProtocol.SetWorkState) //
						.signed(true)//
						.build()));
		protocol.addElementRange(new ElementRange(0x0501, //
				new ElementBuilder(0x0501, name).name(EssProtocol.SetActivePower).multiplier(100).signed(true).unit("W")
						.build()));
		protocol.addElementRange(new ElementRange(0x0502, new ElementBuilder(0x0502, name)
				.name(EssProtocol.SetReactivePower).multiplier(100).signed(true).unit("var").build()));
		protocol.addElementRange(
				new ElementRange(0x1100, //
						new ElementBuilder(0x1100, name).name(EssProtocol.BatteryState)//
								.bit(new BitElement(0, EssProtocol.BatteryStates.Initial.name()))//
								.bit(new BitElement(1, EssProtocol.BatteryStates.Stop.name()))//
								.bit(new BitElement(2, EssProtocol.BatteryStates.StartingUp.name()))//
								.bit(new BitElement(3, EssProtocol.BatteryStates.Running.name()))//
								.bit(new BitElement(4, EssProtocol.BatteryStates.Fault.name())).build(), //
						new ElementBuilder(0x1101, name).name(EssProtocol.BatterySwitchState).bit(new BitElement(0,
								EssProtocol.BatterySwitches.MainContactor.name()))
								.bit(new BitElement(1,
										EssProtocol.BatterySwitches.PrechargeContactor.name()))
								.bit(new BitElement(2, EssProtocol.BatterySwitches.FANContactor.name()))
								.bit(new BitElement(3, EssProtocol.BatterySwitches.BMUPowerSupplyRelay.name()))
								.bit(new BitElement(4, EssProtocol.BatterySwitches.MiddleRelay.name())).build(), //
						new ElementBuilder(0x1102, name).name(EssProtocol.BatteryPeripheralIOState)
								.bit(new BitElement(0, EssProtocol.PheripheralIOs.Fuse.name()))
								.bit(new BitElement(4, EssProtocol.PheripheralIOs.IsolatedSwitch.name())).build(), //
						new ElementBuilder(0x1103, name)
								.bit(new BitElement(0, EssProtocol.BatteryInformation.ChargeGeneralOvercurrent.name()))
								.bit(new BitElement(1,
										EssProtocol.BatteryInformation.DischargeGeneralOvercurrent.name()))
								.bit(new BitElement(2, EssProtocol.BatteryInformation.ChargeCurrentOverLimit.name()))
								.bit(new BitElement(3, EssProtocol.BatteryInformation.DischargeCurrentOverLimit.name()))
								.bit(new BitElement(4, EssProtocol.BatteryInformation.GeneralOvervoltage.name()))
								.bit(new BitElement(5, EssProtocol.BatteryInformation.GeneralUndervoltage.name()))
								.bit(new BitElement(7, EssProtocol.BatteryInformation.GeneralOverTemperature.name()))
								.bit(new BitElement(8, EssProtocol.BatteryInformation.GeneralUnderTemperature.name()))
								.bit(new BitElement(10, EssProtocol.BatteryInformation.SevereOvervoltage.name()))
								.bit(new BitElement(11, EssProtocol.BatteryInformation.SevereUnderVoltage.name()))
								.bit(new BitElement(12, EssProtocol.BatteryInformation.SevereUnderTemperature.name()))
								.bit(new BitElement(13, EssProtocol.BatteryInformation.ChargeSevereOvercurrent.name()))
								.bit(new BitElement(14,
										EssProtocol.BatteryInformation.DischargeSevereOvercurrent.name()))
								.bit(new BitElement(15, EssProtocol.BatteryInformation.CapacityAbnormity.name()))
								.build(), //
						new ElementBuilder(0x1104, name).type(ElementType.PLACEHOLDER).intLength(0x1105 - 0x1104)
								.build(),
						new ElementBuilder(0x1105, name)

								.bit(new BitElement(2,
										EssProtocol.BatteryAbnormity.VoltageSamplingRouteInvalidation.name()))
								.bit(new BitElement(4,
										EssProtocol.BatteryAbnormity.VoltageSamplingRouteDisconnected.name()))
								.bit(new BitElement(5,
										EssProtocol.BatteryAbnormity.TemperatureSamplingRouteDisconnected.name()))
								.bit(new BitElement(6, EssProtocol.BatteryAbnormity.InsideCANDisconnected.name()))
								.bit(new BitElement(9,
										EssProtocol.BatteryAbnormity.CurrentSamplingCircuitAbnormity.name()))
								.bit(new BitElement(10, EssProtocol.BatteryAbnormity.BatteryCellInvalidation.name()))
								.bit(new BitElement(11,
										EssProtocol.BatteryAbnormity.MainContactorInspectionAbnormity.name()))
								.bit(new BitElement(12,
										EssProtocol.BatteryAbnormity.PrechargeContactorInspectionAbnormity.name()))
								.bit(new BitElement(13,
										EssProtocol.BatteryAbnormity.NegativeContactorInspectionAbnormity.name()))
								.bit(new BitElement(14,
										EssProtocol.BatteryAbnormity.PowerSupplyRelayContactorDisconnected.name()))
								.bit(new BitElement(15, EssProtocol.BatteryAbnormity.MiddleRelayAbnormity.name()))
								.build(), //
						new ElementBuilder(0x1106, name)

								.bit(new BitElement(2, EssProtocol.BatteryAbnormity.SevereOvertemperature.name()))
								.bit(new BitElement(7, EssProtocol.BatteryAbnormity.SmogFault.name()))
								.bit(new BitElement(8, EssProtocol.BatteryAbnormity.BlownFuseIndicatorFault.name()))
								.bit(new BitElement(10, EssProtocol.BatteryAbnormity.GeneralLeakage.name()))
								.bit(new BitElement(11, EssProtocol.BatteryAbnormity.SevereLeakage.name()))
								.bit(new BitElement(12,
										EssProtocol.BatteryAbnormity.BecuToPeripheryCanDisconnected.name()))
								.bit(new BitElement(14,
										EssProtocol.BatteryAbnormity.PowerSupplyRelayContactorDisconnected.name()))
								.build()));
		protocol.addElementRange(new ElementRange(0x1400, //
				new ElementBuilder(0x1400, name).name(EssProtocol.BatteryVoltage).unit("mV").multiplier(100).build(), //
				new ElementBuilder(0x1401, name).name(EssProtocol.BatteryCurrent).unit("mA").signed(true)
						.multiplier(100).build(), //
				new ElementBuilder(0x1402, name).name(EssProtocol.BatteryStringSoc).unit("%").build(), //
				new ElementBuilder(0x1403, name).name(EssProtocol.BatteryStringSOH).unit("%").build(), //
				new ElementBuilder(0x1404, name).name(EssProtocol.BatteryCellAverageTemperature).signed(true).unit("°C")
						.build(), //
				new ElementBuilder(0x1405, name).type(ElementType.PLACEHOLDER).intLength(0x1406 - 0x1405).build(), //
				new ElementBuilder(0x1406, name).name(EssProtocol.BatteryChargeCurrentLimit).unit("mA").multiplier(100)
						.build(), //
				new ElementBuilder(0x1407, name).name(EssProtocol.BatteryDischargeCurrentLimit).unit("mA")
						.multiplier(100).build(), //
				new ElementBuilder(0x1408, name).type(ElementType.PLACEHOLDER).intLength(0x140A - 0x1408).build(), //
				new ElementBuilder(0x140A, name).name(EssProtocol.ChargeDischargeTimes).length(ElementLength.DOUBLEWORD)
						.wordOrder(WordOrder.LSWMSW).build(), //
				new ElementBuilder(0x140C, name).type(ElementType.PLACEHOLDER).intLength(0x1418 - 0x140C).build(), //
				new ElementBuilder(0x1418, name).name(EssProtocol.BatteryChargeEnergy).unit("Wh")
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build(), //
				new ElementBuilder(0x141A, name).name(EssProtocol.BatteryDischargeEnergy).unit("Wh")
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build(), //
				new ElementBuilder(0x141C, name).type(ElementType.PLACEHOLDER).intLength(0x1420 - 0x141C).build(), //
				new ElementBuilder(0x1420, name).name(EssProtocol.BatteryPower).unit("W").signed(true).multiplier(100)
						.build()));
		protocol.addElementRange(new ElementRange(0xA600,
				new ElementBuilder(0xA600, name).name(EssProtocol.Pv1State) //
						.bit(new BitElement(1, EssProtocol.DcStates.Initial.name())) //
						.bit(new BitElement(2, EssProtocol.DcStates.Stop.name())) //
						.bit(new BitElement(3, EssProtocol.DcStates.Ready.name())) //
						.bit(new BitElement(4, EssProtocol.DcStates.Running.name())) //
						.bit(new BitElement(5, EssProtocol.DcStates.Fault.name())) //
						.bit(new BitElement(6, EssProtocol.DcStates.Debug.name())) //
						.bit(new BitElement(7, EssProtocol.DcStates.Locked.name())).build()));
		protocol.addElementRange(new ElementRange(0xA730,
				new ElementBuilder(0xA730, name).name(EssProtocol.Pv1OutputVoltage).multiplier(10).signed(true)
						.unit("V").build(), //
				new ElementBuilder(0xA731, name).name(EssProtocol.Pv1OutputCurrent).multiplier(10).signed(true)
						.unit("A").build(), //
				new ElementBuilder(0xA732, name).name(EssProtocol.Pv1OutputPower).multiplier(100).signed(true).unit("W")
						.build(), //
				new ElementBuilder(0xA733, name).name(EssProtocol.Pv1InputVoltage).multiplier(10).signed(true).unit("V")
						.build(), //
				new ElementBuilder(0xA734, name).name(EssProtocol.Pv1InputCurrent).multiplier(10).signed(true).unit("A")
						.build(), //
				new ElementBuilder(0xA735, name).name(EssProtocol.Pv1InputPower).multiplier(100).signed(true).unit("W")
						.build(), //
				new ElementBuilder(0xA736, name).name(EssProtocol.Pv1InputEnergy).multiplier(100).signed(true)
						.unit("Wh").build(), //
				new ElementBuilder(0xA737, name).name(EssProtocol.Pv1OutputEnergy).multiplier(100).signed(true)
						.unit("Wh").build()));
		protocol.addElementRange(new ElementRange(0xA900,
				new ElementBuilder(0xA900, name).name(EssProtocol.Pv2State) //
						.bit(new BitElement(1, EssProtocol.DcStates.Initial.name())) //
						.bit(new BitElement(2, EssProtocol.DcStates.Stop.name())) //
						.bit(new BitElement(3, EssProtocol.DcStates.Ready.name())) //
						.bit(new BitElement(4, EssProtocol.DcStates.Running.name())) //
						.bit(new BitElement(5, EssProtocol.DcStates.Fault.name())) //
						.bit(new BitElement(6, EssProtocol.DcStates.Debug.name())) //
						.bit(new BitElement(7, EssProtocol.DcStates.Locked.name())).build()));
		protocol.addElementRange(new ElementRange(0xAA30,
				new ElementBuilder(0xAA30, name).name(EssProtocol.Pv2OutputVoltage).multiplier(10).signed(true)
						.unit("V").build(), //
				new ElementBuilder(0xAA31, name).name(EssProtocol.Pv2OutputCurrent).multiplier(10).signed(true)
						.unit("A").build(), //
				new ElementBuilder(0xAA32, name).name(EssProtocol.Pv2OutputPower).multiplier(100).signed(true).unit("W")
						.build(), //
				new ElementBuilder(0xAA33, name).name(EssProtocol.Pv2InputVoltage).multiplier(10).signed(true).unit("V")
						.build(), //
				new ElementBuilder(0xAA34, name).name(EssProtocol.Pv2InputCurrent).multiplier(10).signed(true).unit("A")
						.build(), //
				new ElementBuilder(0xAA35, name).name(EssProtocol.Pv2InputPower).multiplier(100).signed(true).unit("W")
						.build(), //
				new ElementBuilder(0xAA36, name).name(EssProtocol.Pv2InputEnergy).multiplier(100).signed(true)
						.unit("Wh").build(), //
				new ElementBuilder(0xAA37, name).name(EssProtocol.Pv2OutputEnergy).multiplier(100).signed(true)
						.unit("Wh").build()));
		int index = 0;
		List<ModbusElement<?>> voltageElements = new ArrayList<>();
		List<ModbusElement<?>> temperatureElements = new ArrayList<>();
		while (index < 224) {
			voltageElements.add(
					new ElementBuilder(0x1500 + index, name).unit("mV").name("Cell" + (index + 1) + "Voltage").build());
			temperatureElements.add(new ElementBuilder(0x1700 + index, name).unit("°C")
					.name("Cell" + (index + 1) + "Temperature").build());
			if (voltageElements.size() == 85) {
				protocol.addElementRange(new ElementRange(voltageElements.get(0).getAddress(),
						voltageElements.toArray(new ModbusElement<?>[voltageElements.size()])));
				voltageElements.clear();
				protocol.addElementRange(new ElementRange(temperatureElements.get(0).getAddress(),
						temperatureElements.toArray(new ModbusElement<?>[temperatureElements.size()])));
				temperatureElements.clear();
			}
			index++;
		}
		return protocol;
	}

	@Override
	public int getActivePower() throws InvalidValueExcecption {
		return ((SignedIntegerWordElement) getElement(EssProtocol.InverterActivePower.name())).getValue().toInteger();
	}

	@Override
	public int getReactivePower() throws InvalidValueExcecption {
		return ((SignedIntegerWordElement) getElement(EssProtocol.ReactivePower.name())).getValue().toInteger();
	}

	@Override
	public int getApparentPower() throws InvalidValueExcecption {
		return ((UnsignedShortWordElement) getElement(EssProtocol.ApparentPower.name())).getValue().toInteger();
	}

	@Override
	public int getAllowedCharge() throws InvalidValueExcecption {
		return ((SignedIntegerWordElement) getElement(EssProtocol.AllowedCharge.name())).getValue().toInteger();
	}

	@Override
	public int getAllowedDischarge() throws InvalidValueExcecption {
		if (getSOC() > minSoc.getValue().toInteger()) {
			return ((UnsignedShortWordElement) getElement(EssProtocol.AllowedDischarge.name())).getValue().toInteger();
		}
		return 0;
	}

	public UnsignedShortWordElement getAllowedApparent() {
		return (UnsignedShortWordElement) getElement(EssProtocol.AllowedApparent.name());
	}

	public SignedIntegerWordElement getSetActivePower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.SetActivePower.name());
	}

	public SignedIntegerWordElement getPv1OutputPower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.Pv1OutputPower.name());
	}

	public SignedIntegerWordElement getPv1InputPower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.Pv1InputPower.name());
	}

	public SignedIntegerWordElement getPv2OutputPower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.Pv2OutputPower.name());
	}

	public SignedIntegerWordElement getPv2InputPower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.Pv2InputPower.name());
	}

	public Boolean getPv1StateInitial() throws InvalidValueExcecption {
		return ((BitsElement) getElement(EssProtocol.Pv1State.name())).getBit(EssProtocol.DcStates.Initial.name())
				.getValue().toBoolean();
	}

	public String getDcState(BitsElement s) throws InvalidValueExcecption {
		if (s.getBit(EssProtocol.DcStates.Initial.name()).getValue().toBoolean()) {
			return EssProtocol.DcStates.Initial.name();
		} else if (s.getBit(EssProtocol.DcStates.Stop.name()).getValue().toBoolean()) {
			return EssProtocol.DcStates.Stop.name();
		} else if (s.getBit(EssProtocol.DcStates.Ready.name()).getValue().toBoolean()) {
			return EssProtocol.DcStates.Ready.name();
		} else if (s.getBit(EssProtocol.DcStates.Running.name()).getValue().toBoolean()) {
			return EssProtocol.DcStates.Running.name();
		} else if (s.getBit(EssProtocol.DcStates.Fault.name()).getValue().toBoolean()) {
			return EssProtocol.DcStates.Fault.name();
		} else if (s.getBit(EssProtocol.DcStates.Debug.name()).getValue().toBoolean()) {
			return EssProtocol.DcStates.Debug.name();
		} else if (s.getBit(EssProtocol.DcStates.Locked.name()).getValue().toBoolean()) {
			return EssProtocol.DcStates.Locked.name();
		}
		return "NO STATUS";
	}

	public String getPv1State() throws InvalidValueExcecption {
		return getDcState((BitsElement) getElement(EssProtocol.Pv1State.name()));
	}

	public String getPv2State() throws InvalidValueExcecption {
		return getDcState((BitsElement) getElement(EssProtocol.Pv2State.name()));
	}

	@Override
	public boolean isOnGrid() throws InvalidValueExcecption {
		if (((UnsignedShortWordElement) getElement(EssProtocol.GridMode.name())).getValue().toInteger() == 2) {
			return true;
		}
		return false;
	}

	private SignedIntegerWordElement getSetWorkState() {
		return ((SignedIntegerWordElement) getElement(EssProtocol.SetWorkState.name()));
	}

	@Override
	public void setActivePower(int power) throws InvalidValueExcecption {
		if (power > 0 && getSOC() <= minSoc.getValue().toInteger()) {
			power = 0;
		}
		addToWriteRequestQueue(new ModbusSingleRegisterWriteRequest(getSetActivePower(), power));
	}

	@Override
	public int getSOC() throws InvalidValueExcecption {
		return ((UnsignedShortWordElement) getElement(EssProtocol.BatteryStringSoc.name())).getValue().toInteger();
	}

	@Override
	public void start() {
		int START = 64;
		addToWriteRequestQueue(new ModbusSingleRegisterWriteRequest(getSetWorkState(), START));
	}

	@Override
	public void stop() {
		int STOP = 4;
		addToWriteRequestQueue(new ModbusSingleRegisterWriteRequest(getSetWorkState(), STOP));
	}

	@Override
	public String getCurrentDataAsString() {
		try {
			return this.getName() + ": [" + getElement(EssProtocol.BatteryStringSoc.name()).readable()
					+ "] PWR: [ GridFeedPower: " + getElement(EssProtocol.ActivePower.name()).readable()
					+ " InverterOutputPower: " + getElement(EssProtocol.InverterActivePower.name()).readable() + " "
					+ getElement(EssProtocol.ReactivePower.name()).readable() + " "
					+ getElement(EssProtocol.ApparentPower.name()).readable() + "] DCPV: ["
					+ getElement(EssProtocol.Pv1OutputPower.name()).readable()
					+ getElement(EssProtocol.Pv2OutputPower.name()).readable() + "] ";
		} catch (InvalidValueExcecption e) {
			log.error("invalid device data", e);
			return this.getName() + " is invalid";
		}
	}

	// @Override
	// public List<InformationObject> getMeassurements(int startAddress) {
	// ArrayList<InformationObject> informationObjects = new ArrayList<>();
	// for (IecValueParameter entry : IECMEASSUREMENTELEMENTS) {
	// float value = 0;
	// Long time = System.currentTimeMillis();
	// ModbusElement<?> element = getElement(entry.getElementName());
	// if (element != null && element.getLastUpdate() != null) {
	// time = element.getLastUpdate().getMillis();
	// if (element instanceof UnsignedIntegerDoublewordElement) {
	// value = ((UnsignedIntegerDoublewordElement) element).getValue().toLong();
	// } else if (element instanceof UnsignedShortWordElement) {
	// value = ((UnsignedShortWordElement) element).getValue().toInteger();
	// } else if (element instanceof SignedIntegerWordElement) {
	// value = ((SignedIntegerWordElement) element).getValue().toInteger();
	// } else if (element instanceof SignedIntegerDoublewordElement) {
	// value = ((SignedIntegerDoublewordElement)
	// element).getValue().toInteger();
	// }
	// }
	// informationObjects.add(new InformationObject(startAddress +
	// entry.getAddressOffset(),
	// new InformationElement[][] { { new IeShortFloat((float) (value *
	// entry.getMultiplier())),
	// new IeQuality(false, false, false, false, false), new IeTime56(time) }
	// }));
	// }
	// return informationObjects;
	// }
	//
	// @Override
	// public List<InformationObject> getMessages(int startAddress) {
	// ArrayList<InformationObject> informationObjects = new ArrayList<>();
	// for (IecValueParameter entry : IECMESSAGEELEMENTS) {
	// DoublePointInformation dpi = DoublePointInformation.INDETERMINATE;
	// Long time = System.currentTimeMillis();
	// ModbusElement<?> element = getElement(entry.getElementName());
	// if (element != null && element.getLastUpdate() != null) {
	// time = element.getLastUpdate().getMillis();
	// BitElement e = (BitElement) element;
	// if (e.getValue().toBoolean()) {
	// dpi = DoublePointInformation.ON;
	// } else {
	// dpi = DoublePointInformation.OFF;
	// }
	// }
	// informationObjects.add(new InformationObject(startAddress +
	// entry.getAddressOffset(),
	// new InformationElement[][] { { new IeDoublePointWithQuality(dpi, false,
	// false, false, false),
	// new IeTime56(time) } }));
	// }
	// return informationObjects;
	// }

	@Override
	public void handleSetPoint(int function, IeShortFloat informationElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCommand(int function, IeDoubleCommand informationElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IecElementOnChangeListener> createChangeListeners(int startAddressMeassurements,
			int startAddressMessages, ConnectionListener connection) {
		ArrayList<IecElementOnChangeListener> eventListener = new ArrayList<>();
		/* Meassurements */
		eventListener.add(createMeassurementListener(EssProtocol.ChargeEnergy.name(), startAddressMeassurements + 0,
				0.01f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.DischargeEnergy.name(), startAddressMeassurements + 1,
				0.01f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.BatteryChargeEnergy.name(),
				startAddressMeassurements + 2, 0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.BatteryDischargeEnergy.name(),
				startAddressMeassurements + 3, 0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.InverterActivePower.name(),
				startAddressMeassurements + 10, 0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.ReactivePower.name(), startAddressMeassurements + 11,
				0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.ApparentPower.name(), startAddressMeassurements + 12,
				0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.InverterCurrentPhase1.name(),
				startAddressMeassurements + 13, 0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.InverterCurrentPhase2.name(),
				startAddressMeassurements + 14, 0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.InverterCurrentPhase3.name(),
				startAddressMeassurements + 15, 0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.InverterVoltagePhase1.name(),
				startAddressMeassurements + 16, 0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.InverterVoltagePhase2.name(),
				startAddressMeassurements + 17, 0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.InverterVoltagePhase3.name(),
				startAddressMeassurements + 18, 0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.Frequency.name(), startAddressMeassurements + 19,
				0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.AllowedCharge.name(), startAddressMeassurements + 20,
				0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.AllowedDischarge.name(),
				startAddressMeassurements + 21, 0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.AllowedApparent.name(), startAddressMeassurements + 22,
				0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.BatteryVoltage.name(), startAddressMeassurements + 30,
				0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.BatteryCurrent.name(), startAddressMeassurements + 31,
				0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.BatteryPower.name(), startAddressMeassurements + 32,
				0.001f, connection));
		eventListener.add(createMeassurementListener(EssProtocol.BatteryStringSoc.name(),
				startAddressMeassurements + 33, 1, connection));
		eventListener.add(createMeassurementListener(EssProtocol.BatteryStringSOH.name(),
				startAddressMeassurements + 34, 1, connection));
		IecElementOnChangeListener minSocListener = new IecElementOnChangeListener(minSoc, connection,
				startAddressMeassurements + 35, 1, MessageType.MEASSUREMENT);
		minSoc.addOnChangeListener(minSocListener);
		eventListener.add(minSocListener);
		/* Messages */
		eventListener.add(createMessageListener(EssProtocol.SystemState.name(), EssProtocol.SystemStates.Stop.name(),
				startAddressMessages + 0, connection));
		eventListener.add(createMessageListener(EssProtocol.SystemState.name(),
				EssProtocol.SystemStates.PvCharging.name(), startAddressMessages + 1, connection));
		eventListener.add(createMessageListener(EssProtocol.SystemState.name(), EssProtocol.SystemStates.Standby.name(),
				startAddressMessages + 2, connection));
		eventListener.add(createMessageListener(EssProtocol.SystemState.name(), EssProtocol.SystemStates.Running.name(),
				startAddressMessages + 3, connection));
		eventListener.add(createMessageListener(EssProtocol.SystemState.name(), EssProtocol.SystemStates.Fault.name(),
				startAddressMessages + 4, connection));
		eventListener.add(createMessageListener(EssProtocol.SystemState.name(), EssProtocol.SystemStates.Debug.name(),
				startAddressMessages + 5, connection));
		eventListener.add(createMessageListener(EssProtocol.BatteryState.name(),
				EssProtocol.BatteryStates.Initial.name(), startAddressMessages + 6, connection));
		eventListener.add(createMessageListener(EssProtocol.BatteryState.name(), EssProtocol.BatteryStates.Stop.name(),
				startAddressMessages + 7, connection));
		eventListener.add(createMessageListener(EssProtocol.BatteryState.name(),
				EssProtocol.BatteryStates.StartingUp.name(), startAddressMessages + 8, connection));
		eventListener.add(createMessageListener(EssProtocol.BatteryState.name(),
				EssProtocol.BatteryStates.Running.name(), startAddressMessages + 9, connection));
		eventListener.add(createMessageListener(EssProtocol.BatteryState.name(), EssProtocol.BatteryStates.Fault.name(),
				startAddressMessages + 10, connection));
		eventListener.add(createMessageListener(EssProtocol.InverterState.name(),
				EssProtocol.InverterStates.Initial.name(), startAddressMessages + 11, connection));
		eventListener.add(createMessageListener(EssProtocol.InverterState.name(),
				EssProtocol.InverterStates.Fault.name(), startAddressMessages + 12, connection));
		eventListener.add(createMessageListener(EssProtocol.InverterState.name(),
				EssProtocol.InverterStates.Stop.name(), startAddressMessages + 13, connection));
		eventListener.add(createMessageListener(EssProtocol.InverterState.name(),
				EssProtocol.InverterStates.Standby.name(), startAddressMessages + 14, connection));
		eventListener.add(createMessageListener(EssProtocol.InverterState.name(),
				EssProtocol.InverterStates.GridMonitoring.name(), startAddressMessages + 15, connection));
		eventListener.add(createMessageListener(EssProtocol.InverterState.name(),
				EssProtocol.InverterStates.Ready.name(), startAddressMessages + 16, connection));
		eventListener.add(createMessageListener(EssProtocol.InverterState.name(),
				EssProtocol.InverterStates.Running.name(), startAddressMessages + 17, connection));
		eventListener.add(createMessageListener(EssProtocol.InverterState.name(),
				EssProtocol.InverterStates.Debug.name(), startAddressMessages + 18, connection));

		return eventListener;
	}

	private IecElementOnChangeListener createMeassurementListener(String elementName, int address, float multiplier,
			ConnectionListener connection) {
		Element<?> element = getElement(elementName);
		IecElementOnChangeListener ieocl = new IecElementOnChangeListener(element, connection, address, multiplier,
				MessageType.MEASSUREMENT);
		element.addOnChangeListener(ieocl);
		return ieocl;
	}

	private IecElementOnChangeListener createMessageListener(String elementName, String bitName, int address,
			ConnectionListener connection) {
		BitsElement element = (BitsElement) getElement(elementName);
		BitElement bit = element.getBit(bitName);
		IecElementOnChangeListener ieocl = new IecElementOnChangeListener(bit, connection, address, 0,
				MessageType.MESSAGE);
		bit.addOnChangeListener(ieocl);
		return ieocl;
	}

	@Override
	public int getMaxCapacity() {
		return 40000;
	}

	@Override
	public void setReactivePower(int power) {
		addToWriteRequestQueue(new ModbusSingleRegisterWriteRequest(
				(SignedIntegerWordElement) getElement(EssProtocol.SetReactivePower.name()), power));
	}

	@Override
	public boolean isRunning() throws InvalidValueExcecption {
		BitsElement bitsElement = (BitsElement) getElement(EssProtocol.SystemState.name());
		BitElement essRunning = bitsElement.getBit(EssProtocol.SystemStates.Running.name());
		return essRunning.getValue().toBoolean();
	}
}
