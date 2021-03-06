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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonStreamParser;

import io.openems.element.ElementOnChangeListener;
import io.openems.element.type.Type;
import io.openems.monitoring.MonitoringWorker;

public class FeneconMonitoringWorker extends MonitoringWorker implements ElementOnChangeListener {
	private final static Logger log = LoggerFactory.getLogger(FeneconMonitoringWorker.class);
	private final static String URL = "https://fenecon.de/fems2";
	private final static int CYCLE = 60000;

	// public final static String CACHE_DB_PATH = "/opt/fems-cache.db";
	public final static int MAX_CACHE_ENTRIES_TO_TRANSFER = 10000;
	public final static String FEMS_SYSTEMMESSAGE = "FEMS Systemmessage";
	private static ConcurrentLinkedQueue<TimedElementValue> queue = new ConcurrentLinkedQueue<>();
	private final String devicekey;
	// private final FeneconMonitoringCache cache;

	public FeneconMonitoringWorker(String devicekey) {
		this.devicekey = devicekey;
		// cache = new FeneconMonitoringCache(this);
	}

	public String getDevicekey() {
		return devicekey;
	}

	@Override
	public void elementChanged(String fullName, Type newValue, Type oldValue) {
		TimedElementValue tev = new TimedElementValue(fullName, newValue);
		queue.offer(tev);
	}

	@Override
	public void run() {
		log.info("FeneconMonitoringWorker {} started", getName());
		// TODO initialize
		while (!isInterrupted()) {
			try {
				log.info("FeneconMonitoringWorker: " + queue.size() + " elements");
				if (queue.isEmpty()) {
					log.info("FENECON Online Monitoring: No new data to send");
				} else {
					ArrayList<TimedElementValue> currentQueue = new ArrayList<>(queue.size());
					for (int i = 0; i < MAX_CACHE_ENTRIES_TO_TRANSFER; i++) {
						TimedElementValue tev = queue.poll();
						if (tev == null)
							break;
						currentQueue.add(tev);
					}
					JsonObject resultObj = sendToOnlineMonitoring(currentQueue);
					if (resultObj != null) { // sending was successful
						handleJsonRpcResult(resultObj);
						currentQueue.clear(); // clear currentQueue
					}
				}
			} catch (Throwable t) {
				log.error("Error in FENECON Online-Monitoring: " + t.getMessage());
				t.printStackTrace();
			}

			try {
				Thread.sleep(CYCLE);
			} catch (InterruptedException e) {
				interrupt();
			}
		}
		log.info("FeneconMonitoringWorker {} stopped", getName());
	}

	private JsonObject sendToOnlineMonitoring(ArrayList<TimedElementValue> queue) {
		JsonObject resultObj = null;
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			String json = tevListToJson(queue);
			HttpPost post = new HttpPost(URL);
			post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
			HttpResponse response = client.execute(post);

			JsonStreamParser stream = new JsonStreamParser(new InputStreamReader(response.getEntity().getContent()));
			while (stream.hasNext()) {
				JsonElement mainElement = stream.next();
				if (mainElement.isJsonObject()) {
					JsonObject mainObj = mainElement.getAsJsonObject();
					// read result
					JsonElement resultElement = mainObj.get("result");
					if (resultElement != null) {
						if (resultElement.isJsonObject()) {
							resultObj = resultElement.getAsJsonObject();
						}
					}
					// read error
					JsonElement errorElement = mainObj.get("error");
					if (errorElement != null) {
						throw new IOException(errorElement.toString());
					}
				}
			}
			if (resultObj == null) {
				resultObj = new JsonObject();
			}
			log.info("Successfully sent data");
		} catch (IOException | JsonParseException e) {
			log.error("Send error: " + e.getMessage());
		}
		return resultObj;
	}

	private String tevListToJson(List<TimedElementValue> queue) {
		// create json rpc
		JsonObject json = new JsonObject();
		json.addProperty("jsonrpc", "2.0");
		json.addProperty("method", devicekey);
		json.addProperty("id", 1);
		JsonObject jsonValues = new JsonObject();
		for (TimedElementValue entry : queue) {
			String name = entry.getName();
			if (jsonValues.has(name)) {
				jsonValues.get(name).getAsJsonObject().add(Long.toString(entry.getTime()), entry.getValue().toJson());
			} else {
				JsonObject timedValues = new JsonObject();
				timedValues.add(Long.toString(entry.getTime()), entry.getValue().toJson());
				jsonValues.add(name, timedValues);
			}
		}
		json.add("params", jsonValues);
		return json.toString();
	}

	private void handleJsonRpcResult(JsonObject resultObj) {
		JsonElement yalerElement = resultObj.get("yaler");
		if (yalerElement != null) {
			String yalerRelayDomain = yalerElement.getAsString();
			try {
				if (yalerRelayDomain.equals("false")) {
					log.info("Yaler: deactivate Tunnel");
					// TODO FemsYaler.getFemsYaler().deactivateTunnel();
				} else {
					log.info("Yaler: activate Tunnel - " + yalerRelayDomain);
					// TODO
					// FemsYaler.getFemsYaler().activateTunnel(yalerRelayDomain);
				}
			} catch (Exception e) {
				log.error("Error while activating/deactivating yaler: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
