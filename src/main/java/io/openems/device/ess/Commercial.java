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
package io.openems.device.ess;

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
import io.openems.element.type.IntegerType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class Commercial extends Ess {
	@SuppressWarnings("unused")
	private final static Logger log = LoggerFactory.getLogger(Commercial.class);

	public Commercial(String name, String channel, int unitid, int minSoc) throws IOException,
			ParserConfigurationException, SAXException {
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
				EssProtocol.BatteryStringSoc.name()));
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
				new ElementBuilder(0x0101).device(name).name(EssProtocol.SystemState) //
						.bit(new BitElement(1, EssProtocol.SystemStates.Stop.name())) //
						.bit(new BitElement(2, EssProtocol.SystemStates.PvCharging.name())) //
						.bit(new BitElement(3, EssProtocol.SystemStates.Standby.name())) //
						.bit(new BitElement(4, EssProtocol.SystemStates.Running.name())) //
						.bit(new BitElement(5, EssProtocol.SystemStates.Fault.name())) //
						.bit(new BitElement(6, EssProtocol.SystemStates.Debug.name())).build(), //
				new ElementBuilder(0x0102).device(name).name(EssProtocol.ControlMode)//
						.bit(new BitElement(0, EssProtocol.ControlModes.Remote.name()))//
						.bit(new BitElement(1, EssProtocol.ControlModes.LocalManual.name())).build(),
				new ElementBuilder(0x0103).device(name).name(EssProtocol.WorkMode)//
						.bit(new BitElement(9, EssProtocol.RemoteDispatch.name())).build(), //
				new ElementBuilder(0x0104).device(name).name(EssProtocol.BatteryMaintananceState).build(), //
				new ElementBuilder(0x0105).device(name).name(EssProtocol.InverterState)//
						.bit(new BitElement(0, EssProtocol.InverterStates.Initial.name()))//
						.bit(new BitElement(1, EssProtocol.InverterStates.Fault.name()))//
						.bit(new BitElement(2, EssProtocol.InverterStates.Stop.name()))//
						.bit(new BitElement(3, EssProtocol.InverterStates.Standby.name()))//
						.bit(new BitElement(4, EssProtocol.InverterStates.GridMonitoring.name()))//
						.bit(new BitElement(5, EssProtocol.InverterStates.Ready.name()))//
						.bit(new BitElement(6, EssProtocol.InverterStates.Running.name()))//
						.bit(new BitElement(7, EssProtocol.InverterStates.Debug.name())).build(), //
				new ElementBuilder(0x0106).device(name).name(EssProtocol.GridMode).build(), //
				new ElementBuilder(0x0107).device(name).type(ElementType.PLACEHOLDER).intLength(0x0108 - 0x0107)
						.build(), //
				new ElementBuilder(0x0108).device(name).name(EssProtocol.ProtocolVersion).build(), //
				new ElementBuilder(0x0109).device(name).name(EssProtocol.SystemManufacturer).build(), //
				new ElementBuilder(0x010A).device(name).name(EssProtocol.SystemType).build()));
		protocol.addElementRange(new ElementRange(0x0110,//
				new ElementBuilder(0x0110).device(name)//
						.bit(new BitElement(2, EssProtocol.Information.EmergencyStop.name()))//
						.bit(new BitElement(6, EssProtocol.Information.ManualStop.name())).build(),//
				new ElementBuilder(0x0111).device(name)//
						.bit(new BitElement(3, EssProtocol.Information.TransformertPH1TempSensInvalidation.name()))//
						.bit(new BitElement(12, EssProtocol.Information.SDCardInvalidation.name())).build()));
		protocol.addElementRange(new ElementRange(
				0x0125,//
				new ElementBuilder(0x0125).device(name)//
						.bit(new BitElement(0, EssProtocol.Information.InverterCommunicationAbnormity.name()))//
						.bit(new BitElement(1, EssProtocol.Information.BatteryCommunicationAbnormity.name()))//
						.bit(new BitElement(2, EssProtocol.Information.AmmeterCommunicationAbnormity.name()))//
						.bit(new BitElement(4, EssProtocol.Information.RemoteCommunicationAbnormity.name())).build(),
				new ElementBuilder(0x0126).device(name)
						//
						.bit(new BitElement(3, EssProtocol.Information.TransformerSevereOvertemperature.name()))
						.build()));
		protocol.addElementRange(new ElementRange(0x0150,//
				new ElementBuilder(0x0150).device(name).name(EssProtocol.SwitchStates) //
						.bit(new BitElement(1, EssProtocol.Switches.DCMain.name())) //
						.bit(new BitElement(2, EssProtocol.Switches.DCPrecharge.name())) //
						.bit(new BitElement(3, EssProtocol.Switches.ACBreaker.name())) //
						.bit(new BitElement(4, EssProtocol.Switches.ACMain.name())) //
						.bit(new BitElement(5, EssProtocol.Switches.ACPrecharge.name())).build()));
		protocol.addElementRange(new ElementRange(0x0180,//
				new ElementBuilder(0x0180).device(name)
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
		protocol.addElementRange(new ElementRange(0x0182,//
				new ElementBuilder(0x0182)
						.device(name)
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
						.build(),//
				new ElementBuilder(0x0183)
						.device(name)
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
						.bit(new BitElement(15, EssProtocol.Abnormity.DSPProtection.name())).build(),
				new ElementBuilder(0x0184).device(name)
						//
						.bit(new BitElement(0, EssProtocol.Abnormity.Phase1GridVoltageSevereOvervoltageProtection
								.name()))//
						.bit(new BitElement(1, EssProtocol.Abnormity.Phase1GridVoltageGeneralOvervoltageProtection
								.name()))//
						.bit(new BitElement(2, EssProtocol.Abnormity.Phase2GridVoltageSevereOvervoltageProtection
								.name()))//
						.bit(new BitElement(3, EssProtocol.Abnormity.Phase2GridVoltageGeneralOvervoltageProtection
								.name()))//
						.bit(new BitElement(4, EssProtocol.Abnormity.Phase3GridVoltageSevereOvervoltageProtection
								.name()))//
						.bit(new BitElement(5, EssProtocol.Abnormity.Phase3GridVoltageGeneralOvervoltageProtection
								.name()))//
						.bit(new BitElement(6, EssProtocol.Abnormity.Phase1GridVoltageSevereUndervoltageProtection
								.name()))//
						.bit(new BitElement(7, EssProtocol.Abnormity.Phase1GridVoltageGeneralUndervoltageProtection
								.name()))//
						.bit(new BitElement(8, EssProtocol.Abnormity.Phase2GridVoltageSevereUndervoltageProtection
								.name()))//
						.bit(new BitElement(9, EssProtocol.Abnormity.Phase2GridVoltageGeneralUndervoltageProtection
								.name()))//
						.bit(new BitElement(10, EssProtocol.Abnormity.Phase3GridVoltageSevereUndervoltageProtection
								.name()))//
						.bit(new BitElement(11, EssProtocol.Abnormity.Phase3GridVoltageGeneralUndervoltageProtection
								.name()))//
						.bit(new BitElement(12, EssProtocol.Abnormity.SevereOverfequencyProtection.name()))//
						.bit(new BitElement(13, EssProtocol.Abnormity.GeneralOverfrequencyProtection.name()))//
						.bit(new BitElement(14, EssProtocol.Abnormity.SevereUnderfrequencyProtection.name()))//
						.bit(new BitElement(15, EssProtocol.Abnormity.GeneralUnderPrequencyProtection.name()))//
						.build(),//
				new ElementBuilder(0x0185)
						.device(name)
						//
						.bit(new BitElement(0, EssProtocol.Abnormity.Phase1GridLoss.name()))
						.bit(new BitElement(1, EssProtocol.Abnormity.Phase2GridLoss.name()))
						.bit(new BitElement(2, EssProtocol.Abnormity.Phase3GridLoss.name()))
						.bit(new BitElement(3, EssProtocol.Abnormity.IslandingProtection.name()))
						.bit(new BitElement(4, EssProtocol.Abnormity.Phase1UnderVoltageRideThrough.name()))
						.bit(new BitElement(5, EssProtocol.Abnormity.Phase2UnderVoltageRideThrough.name()))
						.bit(new BitElement(6, EssProtocol.Abnormity.Phase3UnderVoltageRideThrough.name()))
						.bit(new BitElement(7, EssProtocol.Abnormity.Phase1InverterVoltageSevereOvervoltageProtection
								.name()))
						.bit(new BitElement(8, EssProtocol.Abnormity.Phase1InverterVoltageGeneralOvervoltageProtection
								.name()))
						.bit(new BitElement(9, EssProtocol.Abnormity.Phase2InverterVoltageSevereOvervoltageProtection
								.name()))
						.bit(new BitElement(10, EssProtocol.Abnormity.Phase2InverterVoltageGeneralOvervoltageProtection
								.name()))
						.bit(new BitElement(11, EssProtocol.Abnormity.Phase3InverterVoltageSevereOvervoltageProtection
								.name()))
						.bit(new BitElement(12, EssProtocol.Abnormity.Phase3InverterVoltageGeneralOvervoltageProtection
								.name()))
						.bit(new BitElement(13,
								EssProtocol.Abnormity.InverterPeakVoltageHighProtectionCauseByACDisconnect.name()))
						.build(),//
				new ElementBuilder(0x0186)
						.device(name)
						//
						.bit(new BitElement(0, EssProtocol.Information.DCPrechargeContactorInspectionAbnormity.name()))
						.bit(new BitElement(1, EssProtocol.Information.DCBreaker1InspectionAbnormity.name()))
						.bit(new BitElement(2, EssProtocol.Information.DCBreaker2InspectionAbnormity.name()))
						.bit(new BitElement(3, EssProtocol.Information.ACPrechargeContactorInspectionAbnormity.name()))
						.bit(new BitElement(4, EssProtocol.Information.ACMainContactorInspectionAbnormity.name()))
						.bit(new BitElement(5, EssProtocol.Information.ACBreakerInspectionAbnormity.name()))
						.bit(new BitElement(6, EssProtocol.Information.DCBreaker1CloseUnsuccessfully.name()))
						.bit(new BitElement(7, EssProtocol.Information.DCBreaker2CloseUnsuccessfully.name()))
						.bit(new BitElement(8, EssProtocol.Information.ControlSignalCloseAbnormallyInspectedBySystem
								.name()))
						.bit(new BitElement(9, EssProtocol.Information.ControlSignalOpenAbnormallyInspectedBySystem
								.name()))
						.bit(new BitElement(10, EssProtocol.Information.NeutralWireContactorCloseUnsuccessfully.name()))
						.bit(new BitElement(11, EssProtocol.Information.NeutralWireContactorOpenUnsuccessfully.name()))
						.bit(new BitElement(12, EssProtocol.Information.WorkDoorOpen.name()))
						.bit(new BitElement(13, EssProtocol.Information.EmergencyStop2.name()))
						.bit(new BitElement(14, EssProtocol.Information.ACBreakerCloseUnsuccessfully.name()))
						.bit(new BitElement(15, EssProtocol.Information.ControlSwitchStop.name())).build(),//
				new ElementBuilder(0x0187)
						.device(name)
						//
						.bit(new BitElement(0, EssProtocol.Information.GeneralOverload.name()))
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
						.bit(new BitElement(11, EssProtocol.Information.LargeTemperatureDifferenceAmongIGBTThreePhases
								.name()))
						.bit(new BitElement(12, EssProtocol.Information.EEPROMParametersOverRange.name()))
						.bit(new BitElement(13, EssProtocol.Information.EEPROMParametersBackupFailed.name()))
						.bit(new BitElement(14, EssProtocol.Information.DCBreakerCloseUnsuccessfully.name())).build(),//
				new ElementBuilder(0x0188)
						.device(name)
						//
						.bit(new BitElement(0, EssProtocol.Information.CommunicationBetweenInverterAndBSMUDisconnected
								.name()))
						.bit(new BitElement(1,
								EssProtocol.Information.CommunicationBetweenInverterAndMasterDisconnected.name()))
						.bit(new BitElement(2, EssProtocol.Information.CommunicationBetweenInverterAndUCDisconnected
								.name()))
						.bit(new BitElement(3, EssProtocol.Information.BMSStartOvertimeControlledByPCS.name()))
						.bit(new BitElement(4, EssProtocol.Information.BMSStopOvertimeControlledByPCS.name()))
						.bit(new BitElement(5, EssProtocol.Information.SyncSignalInvalidation.name()))
						.bit(new BitElement(6, EssProtocol.Information.SyncSignalContinuousCaptureFault.name()))
						.bit(new BitElement(7, EssProtocol.Information.SyncSignalSeveralTimesCaptureFault.name()))
						.build()//
		));
		protocol.addElementRange(new ElementRange(0x0200, new ElementBuilder(0x0200).device(name)
				.name(EssProtocol.DcVoltage).unit("mV").multiplier(100).build(),//
				new ElementBuilder(0x0201).device(name).name(EssProtocol.DcCurrent).unit("mA").multiplier(100).build(),//
				new ElementBuilder(0x0202).device(name).name(EssProtocol.DcPower).unit("W").multiplier(100).build(),//
				new ElementBuilder(0x0203).device(name).type(ElementType.PLACEHOLDER).intLength(0x0208 - 0x0203)
						.build(), //
				new ElementBuilder(0x0208).device(name).name(EssProtocol.ChargeEnergy).unit("Wh")
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).multiplier(100).build(), //
				new ElementBuilder(0x020A).device(name).name(EssProtocol.DischargeEnergy).unit("Wh").multiplier(100)
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build()));
		protocol.addElementRange(new ElementRange(0x0210, new ElementBuilder(0x0210).name(EssProtocol.ActivePower)
				.multiplier(100).signed(true).unit("W").build(), new ElementBuilder(0x0211)
				.name(EssProtocol.ReactivePower).multiplier(100).signed(true).unit("Var").build(), //
				new ElementBuilder(0x0212).name(EssProtocol.ApparentPower).multiplier(100).unit("VA").build(), //
				new ElementBuilder(0x0213).name(EssProtocol.CurrentPhase1).signed(true).multiplier(100).unit("mA")
						.build(), //
				new ElementBuilder(0x0214).name(EssProtocol.CurrentPhase2).signed(true).multiplier(100).unit("mA")
						.build(), //
				new ElementBuilder(0x0215).name(EssProtocol.CurrentPhase3).signed(true).multiplier(100).unit("mA")
						.build(), //
				new ElementBuilder(0x0216).device(name).type(ElementType.PLACEHOLDER).intLength(0x219 - 0x216).build(), //
				new ElementBuilder(0x0219).name(EssProtocol.VoltagePhase1).multiplier(100).unit("mV").build(), //
				new ElementBuilder(0x021A).name(EssProtocol.VoltagePhase2).multiplier(100).unit("mV").build(), //
				new ElementBuilder(0x021B).name(EssProtocol.VoltagePhase3).multiplier(100).unit("mV").build(), //
				new ElementBuilder(0x021C).name(EssProtocol.Frequency).multiplier(100).unit("mHZ").build(), //
				new ElementBuilder(0x021D).device(name).type(ElementType.PLACEHOLDER).intLength(0x230 - 0x21D).build(), //
				new ElementBuilder(0x0230).device(name).name(EssProtocol.AllowedCharge).multiplier(100).signed(true)
						.unit("W").build(), //
				new ElementBuilder(0x0231).device(name).name(EssProtocol.AllowedDischarge).multiplier(100).unit("W")
						.build(), //
				new ElementBuilder(0x0232).device(name).name(EssProtocol.AllowedApparent).multiplier(100).unit("Var")
						.build()));
		protocol.addElementRange(new ElementRange(0x0240,//
				new ElementBuilder(0x0240).device(name).name(EssProtocol.IPMPhase1Temperature).unit("°C").signed(true)
						.build(),//
				new ElementBuilder(0x0241).device(name).name(EssProtocol.IPMPhase2Temperature).unit("°C").signed(true)
						.build(),//
				new ElementBuilder(0x0242).device(name).name(EssProtocol.IPMPhase3Temperature).unit("°C").signed(true)
						.build(),//
				new ElementBuilder(0x0243).device(name).type(ElementType.PLACEHOLDER).intLength(0x0249 - 0x0243)
						.build(),//
				new ElementBuilder(0x0249).device(name).name(EssProtocol.TransformerPhase1Temperature).unit("°C")
						.signed(true).build()));
		protocol.addElementRange(new ElementRange(0x0300,//
				new ElementBuilder(0x0300).device(name).name(EssProtocol.TotalEnergy).unit("kWh")
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build(),//
				new ElementBuilder(0x0302).device(name).name(EssProtocol.TotalYearEnergy).unit("kWh")
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build(),//
				new ElementBuilder(0x0304).device(name).name(EssProtocol.TotalMonthEnergy).unit("kWh")
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build(),//
				new ElementBuilder(0x0306).device(name).name(EssProtocol.TotalDateEnergy).unit("kWh").build()));
		protocol.addElementRange(new ElementRange(0x0500, new ElementBuilder(0x0500).device(name)
				.name(EssProtocol.SetWorkState) //
				.signed(true)//
				.build()));
		protocol.addElementRange(new ElementRange(0x0501, //
				new ElementBuilder(0x0501).device(name).name(EssProtocol.SetActivePower).multiplier(100).signed(true)
						.unit("W").build()));
		protocol.addElementRange(new ElementRange(0x0502, new ElementBuilder(0x0502).device(name)
				.name(EssProtocol.SetReactivePower).multiplier(100).signed(true).unit("var").build()));
		protocol.addElementRange(new ElementRange(
				0x1100,//
				new ElementBuilder(0x1100).device(name).name(EssProtocol.BatteryState)//
						.bit(new BitElement(0, EssProtocol.BatteryStates.Initial.name()))//
						.bit(new BitElement(1, EssProtocol.BatteryStates.Stop.name()))//
						.bit(new BitElement(2, EssProtocol.BatteryStates.StartingUp.name()))//
						.bit(new BitElement(3, EssProtocol.BatteryStates.Running.name()))//
						.bit(new BitElement(4, EssProtocol.BatteryStates.Fault.name())).build(),//
				new ElementBuilder(0x1101).device(name).name(EssProtocol.BatterySwitchState)
						.bit(new BitElement(0, EssProtocol.BatterySwitches.MainContactor.name()))
						.bit(new BitElement(1, EssProtocol.BatterySwitches.PrechargeContactor.name()))
						.bit(new BitElement(2, EssProtocol.BatterySwitches.FANContactor.name()))
						.bit(new BitElement(3, EssProtocol.BatterySwitches.BMUPowerSupplyRelay.name()))
						.bit(new BitElement(4, EssProtocol.BatterySwitches.MiddleRelay.name())).build(),//
				new ElementBuilder(0x1102).device(name).name(EssProtocol.BatteryPeripheralIOState)
						.bit(new BitElement(0, EssProtocol.PheripheralIOs.Fuse.name()))
						.bit(new BitElement(4, EssProtocol.PheripheralIOs.IsolatedSwitch.name())).build(),//
				new ElementBuilder(0x1103).device(name)
						.bit(new BitElement(0, EssProtocol.BatteryInformation.ChargeGeneralOvercurrent.name()))
						.bit(new BitElement(1, EssProtocol.BatteryInformation.DischargeGeneralOvercurrent.name()))
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
						.bit(new BitElement(14, EssProtocol.BatteryInformation.DischargeSevereOvercurrent.name()))
						.bit(new BitElement(15, EssProtocol.BatteryInformation.CapacityAbnormity.name())).build(),//
				new ElementBuilder(0x1104).device(name).type(ElementType.PLACEHOLDER).intLength(0x1105 - 0x1104)
						.build(),
				new ElementBuilder(0x1105)
						.device(name)
						.bit(new BitElement(2, EssProtocol.BatteryAbnormity.VoltageSamplingRouteInvalidation.name()))
						.bit(new BitElement(4, EssProtocol.BatteryAbnormity.VoltageSamplingRouteDisconnected.name()))
						.bit(new BitElement(5, EssProtocol.BatteryAbnormity.TemperatureSamplingRouteDisconnected.name()))
						.bit(new BitElement(6, EssProtocol.BatteryAbnormity.InsideCANDisconnected.name()))
						.bit(new BitElement(9, EssProtocol.BatteryAbnormity.CurrentSamplingCircuitAbnormity.name()))
						.bit(new BitElement(10, EssProtocol.BatteryAbnormity.BatteryCellInvalidation.name()))
						.bit(new BitElement(11, EssProtocol.BatteryAbnormity.MainContactorInspectionAbnormity.name()))
						.bit(new BitElement(12, EssProtocol.BatteryAbnormity.PrechargeContactorInspectionAbnormity
								.name()))
						.bit(new BitElement(13, EssProtocol.BatteryAbnormity.NegativeContactorInspectionAbnormity
								.name()))
						.bit(new BitElement(14, EssProtocol.BatteryAbnormity.PowerSupplyRelayContactorDisconnected
								.name()))
						.bit(new BitElement(15, EssProtocol.BatteryAbnormity.MiddleRelayAbnormity.name())).build(),//
				new ElementBuilder(0x1106)
						.device(name)
						.bit(new BitElement(2, EssProtocol.BatteryAbnormity.SevereOvertemperature.name()))
						.bit(new BitElement(7, EssProtocol.BatteryAbnormity.SmogFault.name()))
						.bit(new BitElement(8, EssProtocol.BatteryAbnormity.BlownFuseIndicatorFault.name()))
						.bit(new BitElement(10, EssProtocol.BatteryAbnormity.GeneralLeakage.name()))
						.bit(new BitElement(11, EssProtocol.BatteryAbnormity.SevereLeakage.name()))
						.bit(new BitElement(12, EssProtocol.BatteryAbnormity.BecuToPeripheryCanDisconnected.name()))
						.bit(new BitElement(14, EssProtocol.BatteryAbnormity.PowerSupplyRelayContactorDisconnected
								.name())).build()));
		protocol.addElementRange(new ElementRange(0x1400,//
				new ElementBuilder(0x1400).device(name).name(EssProtocol.BatteryVoltage).unit("mV").multiplier(100)
						.build(),//
				new ElementBuilder(0x1401).device(name).name(EssProtocol.BatteryAmperage).unit("mA").signed(true)
						.multiplier(100).build(), //
				new ElementBuilder(0x1402).device(name).name(EssProtocol.BatteryStringSoc).unit("%").build(), //
				new ElementBuilder(0x1403).device(name).name(EssProtocol.BatteryStringSOH).unit("%").build(), //
				new ElementBuilder(0x1404).device(name).name(EssProtocol.BatteryCellAverageTemperature).signed(true)
						.unit("°C").build(),//
				new ElementBuilder(0x1405).device(name).type(ElementType.PLACEHOLDER).intLength(0x1406 - 0x1405)
						.build(),//
				new ElementBuilder(0x1406).device(name).name(EssProtocol.BatteryChargeCurrentLimit).unit("mA")
						.multiplier(100).build(),//
				new ElementBuilder(0x1407).device(name).name(EssProtocol.BatteryDischargeCurrentLimit).unit("mA")
						.multiplier(100).build(),//
				new ElementBuilder(0x1408).device(name).type(ElementType.PLACEHOLDER).intLength(0x140A - 0x1408)
						.build(),//
				new ElementBuilder(0x140A).device(name).name(EssProtocol.ChargeDischargeTimes)
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build(),//
				new ElementBuilder(0x140C).device(name).type(ElementType.PLACEHOLDER).intLength(0x1418 - 0x140C)
						.build(), //
				new ElementBuilder(0x1418).device(name).name(EssProtocol.BatteryChargeEnergy).unit("Wh")
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build(), //
				new ElementBuilder(0x141A).device(name).name(EssProtocol.BatteryDischargeEnergy).unit("Wh")
						.length(ElementLength.DOUBLEWORD).wordOrder(WordOrder.LSWMSW).build(), //
				new ElementBuilder(0x141C).device(name).type(ElementType.PLACEHOLDER).intLength(0x1420 - 0x141C)
						.build(), //
				new ElementBuilder(0x1420).device(name).name(EssProtocol.BatteryPower).unit("W").signed(true)
						.multiplier(100).build()));
		protocol.addElementRange(new ElementRange(0xA600, new ElementBuilder(0xA600).device(name)
				.name(EssProtocol.Pv1State) //
				.bit(new BitElement(1, EssProtocol.DcStates.Initial.name())) //
				.bit(new BitElement(2, EssProtocol.DcStates.Stop.name())) //
				.bit(new BitElement(3, EssProtocol.DcStates.Ready.name())) //
				.bit(new BitElement(4, EssProtocol.DcStates.Running.name())) //
				.bit(new BitElement(5, EssProtocol.DcStates.Fault.name())) //
				.bit(new BitElement(6, EssProtocol.DcStates.Debug.name())) //
				.bit(new BitElement(7, EssProtocol.DcStates.Locked.name())).build()));
		protocol.addElementRange(new ElementRange(0xA730, new ElementBuilder(0xA730).device(name)
				.name(EssProtocol.Pv1OutputVoltage).multiplier(10).signed(true).unit("V").build(), //
				new ElementBuilder(0xA731).device(name).name(EssProtocol.Pv1OutputCurrent).multiplier(10).signed(true)
						.unit("A").build(), //
				new ElementBuilder(0xA732).device(name).name(EssProtocol.Pv1OutputPower).multiplier(100).signed(true)
						.unit("W").build(), //
				new ElementBuilder(0xA733).device(name).name(EssProtocol.Pv1InputVoltage).multiplier(10).signed(true)
						.unit("V").build(), //
				new ElementBuilder(0xA734).device(name).name(EssProtocol.Pv1InputCurrent).multiplier(10).signed(true)
						.unit("A").build(), //
				new ElementBuilder(0xA735).device(name).name(EssProtocol.Pv1InputPower).multiplier(100).signed(true)
						.unit("W").build(), //
				new ElementBuilder(0xA736).device(name).name(EssProtocol.Pv1InputEnergy).multiplier(100).signed(true)
						.unit("Wh").build(), //
				new ElementBuilder(0xA737).device(name).name(EssProtocol.Pv1OutputEnergy).multiplier(100).signed(true)
						.unit("Wh").build()));
		protocol.addElementRange(new ElementRange(0xA900, new ElementBuilder(0xA900).device(name)
				.name(EssProtocol.Pv2State) //
				.bit(new BitElement(1, EssProtocol.DcStates.Initial.name())) //
				.bit(new BitElement(2, EssProtocol.DcStates.Stop.name())) //
				.bit(new BitElement(3, EssProtocol.DcStates.Ready.name())) //
				.bit(new BitElement(4, EssProtocol.DcStates.Running.name())) //
				.bit(new BitElement(5, EssProtocol.DcStates.Fault.name())) //
				.bit(new BitElement(6, EssProtocol.DcStates.Debug.name())) //
				.bit(new BitElement(7, EssProtocol.DcStates.Locked.name())).build()));
		protocol.addElementRange(new ElementRange(0xAA30, new ElementBuilder(0xAA30).device(name)
				.name(EssProtocol.Pv2OutputVoltage).multiplier(10).signed(true).unit("V").build(), //
				new ElementBuilder(0xAA31).device(name).name(EssProtocol.Pv2OutputCurrent).multiplier(10).signed(true)
						.unit("A").build(), //
				new ElementBuilder(0xAA32).device(name).name(EssProtocol.Pv2OutputPower).multiplier(100).signed(true)
						.unit("W").build(), //
				new ElementBuilder(0xAA33).device(name).name(EssProtocol.Pv2InputVoltage).multiplier(10).signed(true)
						.unit("V").build(), //
				new ElementBuilder(0xAA34).device(name).name(EssProtocol.Pv2InputCurrent).multiplier(10).signed(true)
						.unit("A").build(), //
				new ElementBuilder(0xAA35).device(name).name(EssProtocol.Pv2InputPower).multiplier(100).signed(true)
						.unit("W").build(), //
				new ElementBuilder(0xAA36).device(name).name(EssProtocol.Pv2InputEnergy).multiplier(100).signed(true)
						.unit("Wh").build(), //
				new ElementBuilder(0xAA37).device(name).name(EssProtocol.Pv2OutputEnergy).multiplier(100).signed(true)
						.unit("Wh").build()));
		int index = 0;
		List<ModbusElement<?>> voltageElements = new ArrayList<>();
		List<ModbusElement<?>> temperatureElements = new ArrayList<>();
		while (index < 224) {
			voltageElements.add(new ElementBuilder(0x1500 + index).unit("mV").device(name)
					.name("Cell" + (index + 1) + "Voltage").build());
			temperatureElements.add(new ElementBuilder(0x1700 + index).unit("°C").device(name)
					.name("Cell" + (index + 1) + "Temperature").build());
			if (voltageElements.size() == 85) {
				protocol.addElementRange(new ElementRange(voltageElements.get(0).getAddress(), voltageElements
						.toArray(new ModbusElement<?>[voltageElements.size()])));
				voltageElements.clear();
				protocol.addElementRange(new ElementRange(temperatureElements.get(0).getAddress(), temperatureElements
						.toArray(new ModbusElement<?>[temperatureElements.size()])));
				temperatureElements.clear();
			}
			index++;
		}
		return protocol;
	}

	@Override
	public int getActivePower() {
		return ((SignedIntegerWordElement) getElement(EssProtocol.ActivePower.name())).getValue().toInteger();
	}

	public SignedIntegerWordElement getReactivePower() {
		return (SignedIntegerWordElement) getElement(EssProtocol.ReactivePower.name());
	}

	public UnsignedShortWordElement getApparentPower() {
		return (UnsignedShortWordElement) getElement(EssProtocol.ApparentPower.name());
	}

	@Override
	public int getAllowedCharge() {
		return ((SignedIntegerWordElement) getElement(EssProtocol.AllowedCharge.name())).getValue().toInteger();
	}

	@Override
	public int getAllowedDischarge() {
		return ((UnsignedShortWordElement) getElement(EssProtocol.AllowedDischarge.name())).getValue().toInteger();
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

	public Boolean getPv1StateInitial() {
		return ((BitsElement) getElement(EssProtocol.Pv1State.name())).getBit(EssProtocol.DcStates.Initial.name())
				.getValue().toBoolean();
	}

	public String getDcState(BitsElement s) {
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

	public String getPv1State() {
		return getDcState((BitsElement) getElement(EssProtocol.Pv1State.name()));
	}

	public String getPv2State() {
		return getDcState((BitsElement) getElement(EssProtocol.Pv2State.name()));
	}

	@Override
	public EssProtocol.GridStates getGridState() {
		if (((UnsignedShortWordElement) getElement(EssProtocol.GridMode.name())).getValue().toInteger() == 2) {
			return EssProtocol.GridStates.OnGrid;
		}
		return EssProtocol.GridStates.OffGrid;
	}

	private SignedIntegerWordElement getSetWorkState() {
		return ((SignedIntegerWordElement) getElement(EssProtocol.SetWorkState.name()));
	}

	@Override
	public void setActivePower(int power) {
		addToWriteQueue(getSetActivePower(), getSetActivePower().toRegister(new IntegerType(power)));
	}

	@Override
	public int getSOC() {
		return ((UnsignedShortWordElement) getElement(EssProtocol.BatteryStringSoc.name())).getValue().toInteger();
	}

	@Override
	public void start() {
		addToWriteQueue(getSetWorkState(), getSetWorkState().toRegister(new IntegerType(64)));
	}

	@Override
	public void stop() {
		addToWriteQueue(getSetWorkState(), getSetWorkState().toRegister(new IntegerType(4)));
	}

	@Override
	public String getCurrentDataAsString() {
		return "[" + getElement(EssProtocol.BatteryStringSoc.name()).readable() + "] PWR: ["
				+ getElement(EssProtocol.ActivePower.name()).readable() + " "
				+ getElement(EssProtocol.ReactivePower.name()).readable() + " "
				+ getElement(EssProtocol.ApparentPower.name()).readable() + "] DCPV: ["
				+ getElement(EssProtocol.Pv1OutputPower.name()).readable()
				+ getElement(EssProtocol.Pv2OutputPower.name()).readable() + "] ";
	}
}
