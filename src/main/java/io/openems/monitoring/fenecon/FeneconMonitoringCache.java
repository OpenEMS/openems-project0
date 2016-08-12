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

public class FeneconMonitoringCache {
	/*
	 * public static final String CACHE_DB_PATH = "/opt/fems/cache.db";
	 * 
	 * private Logger logger =
	 * LoggerFactory.getLogger(FeneconMonitoringCache.class); private DB db =
	 * null; // only use after calling getMapDB()
	 * BlockingQueue<TimedElementValue> cache = null; // only use after calling
	 * // getMapDB() private final FeneconMonitoringWorker worker;
	 * 
	 * public FeneconMonitoringCache(FeneconMonitoringWorker worker) {
	 * this.worker = worker; }
	 * 
	 * @SuppressWarnings("deprecation") private void getMapDB() throws Exception
	 * { if (cache == null) { File cacheDbFile = new File(CACHE_DB_PATH); try {
	 * logger.info("Opening cache database"); if (db == null) { db =
	 * DBMaker.fileDB(cacheDbFile).fileLockDisable().serializerRegisterClass(
	 * TimedElementValue.class) .closeOnJvmShutdown().make(); } cache =
	 * db.getQueue("fems"); logger.info("Opening cache database: finished");
	 * 
	 * } catch (Exception e) { logger.error(
	 * "Error opening cache database; delete and try again"); worker.offer(new
	 * TimedElementValue(FeneconMonitoringWorker.FEMS_SYSTEMMESSAGE,
	 * "ERROR opening cache database: " + e.getMessage())); e.printStackTrace();
	 * Files.delete(cacheDbFile.toPath()); try { if (db == null) { db =
	 * DBMaker.fileDB(cacheDbFile).fileLockDisable()
	 * .serializerRegisterClass(TimedElementValue.class).closeOnJvmShutdown().
	 * make(); } cache = db.getQueue("fems"); } catch (Exception e1) {
	 * worker.offer(new
	 * TimedElementValue(FeneconMonitoringWorker.FEMS_SYSTEMMESSAGE,
	 * "REPEATED ERROR opening cache database: " + e.getMessage()));
	 * e.printStackTrace(); db = null; throw e; } } } }
	 * 
	 * public void dispose() { logger.info("Closing cache database"); try {
	 * getMapDB(); db.commit(); db.close(); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 * 
	 * public ArrayList<TimedElementValue> pollMany(int count) throws Exception
	 * { ArrayList<TimedElementValue> returnList = new ArrayList<>(count);
	 * getMapDB(); for (int i = 0; i < count && !cache.isEmpty(); i++) {
	 * TimedElementValue tev = cache.poll(); if (tev == null) break;
	 * returnList.add(tev); } db.commit(); return returnList; }
	 * 
	 * public void addAll(Collection<TimedElementValue> c) throws Exception {
	 * getMapDB(); for (TimedElementValue tev : c) { cache.add(tev); }
	 * db.commit(); }
	 * 
	 * public String isEmpty() { try { getMapDB(); if (cache.isEmpty()) { return
	 * "empty"; } else { return "filled"; } } catch (Exception e) { return
	 * "not available"; } }
	 */
}
