package io.openems.device.inverter;

public enum InverterProtocol {
	PAC, PDC, UAC, UDC, //
	DailyYield, YesterdayYield, MonthlyYield, YearlyYield, TotalYield, //
	SetLimitType, SetLimit, WatchDog, Placeholder, Status, GetLimit
}
