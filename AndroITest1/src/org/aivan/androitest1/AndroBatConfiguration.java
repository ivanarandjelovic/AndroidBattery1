package org.aivan.androitest1;

public class AndroBatConfiguration {

	// Time constants:
	static final long MS_PER_SECOND = 1000;
	static final long MS_PER_MINUTE = MS_PER_SECOND * 60;
	static final long MS_PER_HOUR = MS_PER_MINUTE * 60;
	static final long MS_PER_DAY = MS_PER_HOUR * 24;

	
	public static long RECALC_STATS_PERIOD = 6 * MS_PER_HOUR; // 6 hours at least
	/**
	 * How many days we keep in our history
	 */
  public static int MAX_DAYS_IN_HISTORY = 60;
	
	/**
	 * How long we assume 1% of battery can last. If it's any longer
	 * then this then we assume this is completely different reading
	 */
	public static int MAX_MINUTES_PER_PERCENT = 12 * 60;

	/**
	 * MAX_MINUTES_PER_PERCENT calculated to ms
	 */
	public static long MAX_MS_PER_PERCENT = MAX_MINUTES_PER_PERCENT * MS_PER_MINUTE;

	public static final int MIN_BATTERY_LEVEL = 0;

	public static final int MAX_BATERY_LEVEL = 100;

	static final String PREFERENCES_LEVEL = "level";

	static final String PREFERENCES_DATE = "date";

	static final String PREFERENCES_IS_CHARING = "isCharging";
	
	public static final String PREFERENCES_STATS_TIMESTAMP = "statsTimestamp";
	
  static final int ESTIMATE_UPDATE_INTERVAL_IN_MINUTES = 5;
	

}
