package org.aivan.androitest1;

public class AndroBatConfiguration {

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
	public static int MAX_MS_PER_PERCENT = MAX_MINUTES_PER_PERCENT * 60 * 1000;

	public static final int MIN_BATTERY_LEVEL = 0;

	public static final int MAX_BATERY_LEVEL = 100;
}
