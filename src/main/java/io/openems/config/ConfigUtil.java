package io.openems.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.config.exception.ConfigException;
import io.openems.config.exception.ConfigInetAddressException;
import io.openems.config.exception.ConfigNotFoundException;
import io.openems.config.exception.ConfigParseException;

public class ConfigUtil {
	public static JsonElement getAsJsonElement(JsonElement jsonElement, String memberName) {
		try {
			return jsonElement.getAsJsonObject().get(memberName);
		} catch (ClassCastException | IllegalStateException e) {
			throw new ConfigParseException(memberName, jsonElement);
		}
	}

	public static JsonElement getAsJsonElement(JsonObject jsonObject, String memberName) throws ConfigException {
		if (jsonObject.has(memberName)) {
			return jsonObject.get(memberName);
		}
		throw new ConfigNotFoundException(memberName, jsonObject);
	}

	public static String getAsString(JsonObject jsonObject, String memberName) throws ConfigException {
		try {
			return getAsJsonElement(jsonObject, memberName).getAsString();
		} catch (ClassCastException | IllegalStateException e) {
			throw new ConfigParseException(memberName, jsonObject);
		}
	}

	public static int getAsInt(JsonObject jsonObject, String memberName) throws ConfigException {
		try {
			return getAsJsonElement(jsonObject, memberName).getAsInt();
		} catch (ClassCastException | IllegalStateException e) {
			throw new ConfigParseException(memberName, jsonObject);
		}
	}

	public static boolean getAsBoolean(JsonElement jsonElement, String memberName) throws ConfigException {
		try {
			return jsonElement.getAsBoolean();
		} catch (ClassCastException | IllegalStateException e) {
			throw new ConfigParseException(memberName, jsonElement);
		}
	}

	public static boolean getAsBoolean(JsonObject jsonObject, String memberName) throws ConfigException {
		try {
			return getAsBoolean(getAsJsonElement(jsonObject, memberName), memberName);
		} catch (ClassCastException | IllegalStateException e) {
			throw new ConfigParseException(memberName, jsonObject);
		}
	}

	public static JsonArray getAsJsonArray(JsonObject jsonObject, String memberName) throws ConfigException {
		try {
			return getAsJsonElement(jsonObject, memberName).getAsJsonArray();
		} catch (ClassCastException | IllegalStateException e) {
			throw new ConfigParseException(memberName, jsonObject);
		}
	}

	public static InetAddress getAsInetAddress(JsonObject jsonObject, String memberName) throws ConfigException {
		try {
			return InetAddress.getByName(getAsString(jsonObject, memberName));
		} catch (ClassCastException | IllegalStateException e) {
			throw new ConfigParseException(memberName, jsonObject);
		} catch (UnknownHostException e) {
			throw new ConfigInetAddressException(memberName, jsonObject);
		}
	}
}
