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
package de.fenecon.openems.device.ess;

public enum EssProtocol {
	SystemState, SystemMode,

	ActivePower, ReactivePower, ApparentPower,

	AllowedCharge, AllowedDischarge, AllowedApparent,

	BatteryStringSoc,

	SetActivePower, SetReactivePower, SetWorkState,

	GridMode,

	SwitchStates,

	Pv1State, Pv1OutputVoltage, Pv1OutputCurrent, Pv1OutputPower, Pv1InputVoltage, Pv1InputCurrent, Pv1InputPower, Pv1InputEnergy, Pv1OutputEnergy,

	Pv2State, Pv2OutputVoltage, Pv2OutputCurrent, Pv2OutputPower, Pv2InputVoltage, Pv2InputCurrent, Pv2InputPower, Pv2InputEnergy, Pv2OutputEnergy;

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
}
