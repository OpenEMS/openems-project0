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
package io.openems.monitoring.fenecon;

import java.util.Calendar;

import io.openems.element.type.Type;

public class TimedElementValue {
	private final Long time;
	private final String name;
	private final Type value;

	/**
	 * T ideally is a java.util class, which is directly supported by MapDB
	 * serializer
	 *
	 * @param name
	 * @param value
	 */
	public TimedElementValue(String name, Type value) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		this.time = calendar.getTimeInMillis() / 1000;
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Type getValue() {
		return value;
	}

	public Long getTime() {
		return time;
	}
}
