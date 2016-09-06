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
package io.openems.device.ess.mini;

public enum MiniProtocol {
	SetParameterSetting, ParameterSetting, //
	SetOffGridInverterAllowLoad, OffGridInverterAllowLoad, //
	SetInverterAllowGridConnect, InverterAllowGridConnect, //
	SetChargeMode, ChargeMode, //
	SetDischargeMode, DischargeMode, //
	SetInverterAllowGridCharge, InverterAllowGridCharge, //
	SetInverterAllowGridDischarge, InverterAllowGridDischarge, //
	SetInverterChargeDischargeTimeMode, InverterChargeDischargeTimeMode, //
	SetBatteryVoltageLowLimit, BatteryVoltageLowLimit, //
	SetBatteryVoltageHighLimit, BatteryVoltageHighLimit, //
	SetGridVoltageLowLimit, GridVoltageLowLimit, //
	SetGridFrequencyLowLimit, GridFrequencyLowLimit, //
	SetGridVoltageHighLimit, GridVoltageHighLimit, //
	SetGridFrequencyHighLimit, GridFrequencyHighLimit, //
	SetInverterCertificationStandard, InverterCertificationStandard, //
	SetEnableSmoothPV, EnableSmoothPV, //
	SetSmoothSpeedPV, SmoothSpeedPV, //
	SetGridMaxOutputCurrent, GridMaxOutputCurrent, //
	SetInverterMaxChargeCurrent, InverterMaxChargeCurrent, //
	SetInverterMaxDischargeCurrent, InverterMaxDischargeCurrent, //
	SetGridMaxInputCurrent, GridMaxInputCurrent, //
	SetTimeModeStartChargeMinute, TimeModeStartChargeMinute, //
	SetTimeModeStartChargeHour, TimeModeStartChargeHour, //
	SetTimeModeStopChargeMinute, TimeModeStopChargeMinute, //
	SetTimeModeStopChargeHour, TimeModeStopChargeHour, //
	SetTimeModeStartDischargeMinute, TimeModeStartDischargeMinute, //
	SetTimeModeStartDischargeHour, TimeModeStartDischargeHour, //
	SetTimeModeStopDischargeMinute, TimeModeStopDischargeMinute, //
	SetTimeModeStopDischargeHour, TimeModeStopDischargeHour, //
	SetSystemMaxSOC, SystemMaxSOC, //
	SetSystemMinSOC, SystemMinSOC, //
	SetSystemChargeFromGridSOC, SystemChargeFromGridSOC, //
	SetGridSlowVoltageLowLimit, GridSlowVoltageLowLimit, //
	SetGridSlowVoltageHighLimit, GridSlowVoltageHighLimit, //
	SetGridSlowVoltageLowTimeLimit, GridSlowVoltageLowTimeLimit, //
	SetGridSlowVoltageHighTimeLimit, GridSlowVoltageHighTimeLimit, //
	SetGridFastVoltageLowTimeLimit, GridFastVoltageLowTimeLimit, //
	SetGridFastVoltageHighTimeLimit, GridFastVoltageHighTimeLimit, //
	SetGridFrequencyLowTimeLimit, GridFrequencyLowTimeLimit, //
	SetGridFrequencyHighTimeLimit, GridFrequencyHighTimeLimit, //
	SetGridFrequencyReconnectLowLimit, GridFrequencyReconnectLowLimit, //
	SetGridFrequencyReconnectHighLimit, GridFrequencyReconnectHighLimit, //
	SetGridFrequencyReconnectTimeLimit, GridFrequencyReconnectTimeLimit, //
	SetGridVoltageStartPowerFactorAdjustmentLimit, GridVoltageStartPowerFactorAdjustmentLimit, //
	SetGridVoltageStartPowerFactorAdjustmentPercentageLimit, GridVoltageStartPowerFactorAdjustmentPercentageLimit, //
	SetGridVoltageStopPowerFactorAdjustmentLimit, GridVoltageStopPowerFactorAdjustmentLimit, //
	SetGridVoltageReconnectLowLimit, GridVoltageReconnectLowLimit, //
	SetGridVoltageReconnectHighLimit, GridVoltageReconnectHighLimit, //
	SetGridReconnectPowerRisingSlope, GridReconnectPowerRisingSlope, //
	SetLocalRemoteMode, //
	SetPcsMode, PcsMode, //
	SetPowerMeterLimit, PowerMeterLimit, //
	SetInverterNetworking

	;
}
