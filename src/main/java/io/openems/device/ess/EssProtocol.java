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

public enum EssProtocol {
	SystemState, SystemMode,

	ActivePower, ReactivePower, ApparentPower,

	AllowedCharge, AllowedDischarge, AllowedApparent,

	BatteryVoltage, BatteryCurrent, BatteryPower, BatteryStringSoc, BatteryStringSOH,

	SetActivePower, SetReactivePower, SetWorkState,

	GridMode,

	SwitchStates,

	Pv1State, Pv1OutputVoltage, Pv1OutputCurrent, Pv1OutputPower, Pv1InputVoltage, Pv1InputCurrent, Pv1InputPower, Pv1InputEnergy, Pv1OutputEnergy,

	Pv2State, Pv2OutputVoltage, Pv2OutputCurrent, Pv2OutputPower, Pv2InputVoltage, Pv2InputCurrent, Pv2InputPower, Pv2InputEnergy, Pv2OutputEnergy,

	BatteryState, InverterState,

	ChargeEnergy, DischargeEnergy, BatteryChargeEnergy, BatteryDischargeEnergy,

	CurrentPhase1, CurrentPhase2, CurrentPhase3,

	VoltagePhase1, VoltagePhase2, VoltagePhase3,

	Frequency,

	ControlMode, WorkMode, RemoteDispatch, BatteryMaintananceState, ProtocolVersion, SystemType, SystemManufacturer, DcVoltage, DcCurrent, DcPower, IPMPhase1Temperature, IPMPhase2Temperature, IPMPhase3Temperature, TransformerPhase1Temperature, TotalEnergy, TotalYearEnergy, TotalMonthEnergy, TotalDateEnergy, BatterySwitchState, BatteryPeripheralIOState, BatteryChargeCurrentLimit, BatteryCellAverageTemperature, BatteryDischargeCurrentLimit, ChargeDischargeTimes;

	public enum SystemStates {
		Stop, PvCharging, Standby, Running, Fault, Debug
	}

	public enum DcStates {
		Initial, Stop, Ready, Running, Fault, Debug, Locked
	}

	public enum GridStates {
		OnGrid, OffGrid
	}

	public enum Switches {
		DCMain, DCPrecharge, ACBreaker, ACMain, ACPrecharge
	}

	public enum WorkStates {
		Stop, Run
	}

	public enum BatteryStates {
		Initial, Stop, StartingUp, Running, Fault
	}

	public enum InverterStates {
		Initial, Fault, Stop, Standby, GridMonitoring, Ready, Running, Debug
	}

	public enum ControlModes {
		Remote, LocalManual
	}

	public enum Information {
		EmergencyStop, ManualStop, TransformertPH1TempSensInvalidation, SDCardInvalidation, InverterCommunicationAbnormity, BatteryCommunicationAbnormity, AmmeterCommunicationAbnormity, RemoteCommunicationAbnormity, TransformerSevereOvertemperature, //

		DCPrechargeContactorInspectionAbnormity, DCBreaker1InspectionAbnormity, DCBreaker2InspectionAbnormity, ACPrechargeContactorInspectionAbnormity, ACMainContactorInspectionAbnormity, ACBreakerInspectionAbnormity, DCBreaker1CloseUnsuccessfully, DCBreaker2CloseUnsuccessfully, ControlSignalCloseAbnormallyInspectedBySystem, ControlSignalOpenAbnormallyInspectedBySystem, NeutralWireContactorCloseUnsuccessfully, NeutralWireContactorOpenUnsuccessfully, WorkDoorOpen, EmergencyStop2, ACBreakerCloseUnsuccessfully, ControlSwitchStop, //

		GeneralOverload, SevereOverload, BatteryCurrentOverLimit, PowerDecreaseCausedByOvertemperature, InverterGeneralOvertemperature, ACThreePhaseCurrentUnbalance, RestoreFactorySettingUnsuccessfully, PoleBoardInvalidatioin, SelfInspectionFailed, ReceiveBMSFaultAndStop, RefrigerationEquipmentInvalidation, LargeTemperatureDifferenceAmongIGBTThreePhases, EEPROMParametersOverRange, EEPROMParametersBackupFailed, DCBreakerCloseUnsuccessfully, //

		CommunicationBetweenInverterAndBSMUDisconnected, CommunicationBetweenInverterAndMasterDisconnected, CommunicationBetweenInverterAndUCDisconnected, BMSStartOvertimeControlledByPCS, BMSStopOvertimeControlledByPCS, SyncSignalInvalidation, SyncSignalContinuousCaptureFault, SyncSignalSeveralTimesCaptureFault
	}

	public enum Abnormity {
		DCPrechargeContactorCloseUnsuccessfully, ACPrechargeContactorCloseUnsuccessfully, ACMainContactorCloseUnsuccessfully, DCElectricalBreaker1CloseUnsuccessfully, DCMainContactorCloseUnsuccessfully, ACBreakerTrip, ACMainContactorOpenWhenRunning, DCMainContactorOpenWhenRunning, ACMainContactorOpenUnsuccessfully, DCElectricalBreaker1OpenUnsuccessfully, DCMainContactorOpenUnsuccessFully, HardwarePDPFault, MasterStopSuddenly, //

		DCShortCircuitProtection, DCOvervoltageProtection, DCUndervoltageProtection, DCInverseConnectionProtection, DCDisconnectionProtection, CommutingColtageAbnormityProtection, DCOvercurrentProtection, Phase1PeakCurrentOverLimitProtection, Phase2PeakCurrentOverLimitProtection, Phase3PeakCurrentOverLimitProtection, Phase1VirtualCurrentOverLimitProtection, Phase2VirtualCurrentOverLimitProtection, Phase3VirtualCurrentOverLimitProtection, Phase1GridVoltageSamplingInvalidation, Phase2GridVoltageSamplingInvalidation, Phase3GridVoltageSamplingInvalidation, //

		Phase1InverterVoltageSamplingInvalidation, Phase2InverterVoltageSamplingInvalidation, Phase3InverterVoltageSamplingInvalidation, ACCurrentSamplingInvalidation, DCCurrentSamplingInvalidation, Phase1OvertemperatureProtection, Phase2OvertemperatureProtection, Phase3OvertemperatureProtection, Phase1TemperatureSamplingInvalidation, Phase2TemperatureSamplingInvalidation, Phase3TemperatureSamplingInvalidation, Phase1PrechargeUnmetProtection, Phase2PrechargeUnmetProtection, Phase3PrechargeUnmetProtection, UnadaptablePhaseSequenceErrorProtection, DSPProtection, //

		Phase1GridVoltageSevereOvervoltageProtection, Phase2GridVoltageSevereOvervoltageProtection, Phase3GridVoltageSevereOvervoltageProtection, Phase1GridVoltageGeneralOvervoltageProtection, Phase2GridVoltageGeneralOvervoltageProtection, Phase3GridVoltageGeneralOvervoltageProtection, Phase1GridVoltageSevereUndervoltageProtection, Phase2GridVoltageSevereUndervoltageProtection, Phase3GridVoltageSevereUndervoltageProtection, Phase1GridVoltageGeneralUndervoltageProtection, Phase2GridVoltageGeneralUndervoltageProtection, Phase3GridVoltageGeneralUndervoltageProtection, SevereOverfequencyProtection, GeneralOverfrequencyProtection, SevereUnderfrequencyProtection, GeneralUnderPrequencyProtection, //

		Phase1GridLoss, Phase2GridLoss, Phase3GridLoss, IslandingProtection, Phase1UnderVoltageRideThrough, Phase2UnderVoltageRideThrough, Phase3UnderVoltageRideThrough, Phase1InverterVoltageSevereOvervoltageProtection, Phase2InverterVoltageSevereOvervoltageProtection, Phase3InverterVoltageSevereOvervoltageProtection, Phase1InverterVoltageGeneralOvervoltageProtection, Phase2InverterVoltageGeneralOvervoltageProtection, Phase3InverterVoltageGeneralOvervoltageProtection, InverterPeakVoltageHighProtectionCauseByACDisconnect, //
	}

	public enum BatterySwitches {
		MainContactor, PrechargeContactor, FANContactor, BMUPowerSupplyRelay, MiddleRelay
	}

	public enum PheripheralIOs {
		Fuse, IsolatedSwitch
	}

	public enum BatteryInformation {
		ChargeGeneralOvercurrent, DischargeGeneralOvercurrent, ChargeCurrentOverLimit, DischargeCurrentOverLimit, GeneralOvervoltage, GeneralUndervoltage, GeneralOverTemperature, GeneralUnderTemperature, SevereOvervoltage, SevereUnderVoltage, SevereUnderTemperature, ChargeSevereOvercurrent, DischargeSevereOvercurrent, CapacityAbnormity
	}

	public enum BatteryAbnormity {
		VoltageSamplingRouteInvalidation, VoltageSamplingRouteDisconnected, TemperatureSamplingRouteDisconnected, InsideCANDisconnected, CurrentSamplingCircuitAbnormity, BatteryCellInvalidation, MainContactorInspectionAbnormity, PrechargeContactorInspectionAbnormity, NegativeContactorInspectionAbnormity, MiddleRelayAbnormity, SevereOvertemperature, SmogFault, BlownFuseIndicatorFault, GeneralLeakage, SevereLeakage, BecuToPeripheryCanDisconnected, PowerSupplyRelayContactorDisconnected
	}
}
