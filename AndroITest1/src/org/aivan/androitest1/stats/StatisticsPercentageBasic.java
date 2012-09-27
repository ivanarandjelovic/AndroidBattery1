package org.aivan.androitest1.stats;

import org.aivan.androitest1.AndroBatConfiguration;

/**
 * @author aivan
 * 
 */
public class StatisticsPercentageBasic implements StatisticsCalculator {

	/**
	 * This class keeps track of stats for one percent point
	 * 
	 * @author aivan
	 * 
	 */
	class StatRecord {
		int sampleCount = 0;
		long average = 0;
	}

	StatRecord[] statRecords = new StatRecord[AndroBatConfiguration.MAX_BATERY_LEVEL
			- AndroBatConfiguration.MIN_BATTERY_LEVEL + 1];

	public StatisticsPercentageBasic() {
		// Just fill array with empty stat records
		for (int i = 0; i < statRecords.length; i++) {
			statRecords[i] = new StatRecord();
		}
	}

	public void evaluate(long oldDate, int oldValue, long newDate, int newValue) {
		if (oldValue > newValue
				&& (newDate - oldDate) < AndroBatConfiguration.MAX_MS_PER_PERCENT) {
			// Only these cases are interesting, there should be no duplicates
			// ,and
			// values should not be too far apart
			int levelDifference = oldValue - newValue;
			long timeDifference = newDate - oldDate;

			for (int i = 0; i < levelDifference; i++) {
				updateStatRecord(statRecords[oldValue - i], timeDifference
						/ levelDifference);
			}

		}
	}

	private void updateStatRecord(StatRecord statRecord, long value) {
		statRecord.average = (statRecord.average)
				* (statRecord.sampleCount / (statRecord.sampleCount + 1))
				+ value / (statRecord.sampleCount + 1);
		statRecord.sampleCount++;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aivan.androitest1.stats.StatisticsCalculator#fillTheblanks()
	 */
	public void fillTheblanks() {
		// Calculate overall average:
		long totalSum = 0;
		long count = 0;
		for (StatRecord rec : statRecords) {
			if (rec.average > 0) {
				totalSum += rec.average;
				count++;
			}
		}
		long totalAverage = 0;
		if (count > 0) {
			totalAverage = totalSum / count;
		}
		for (StatRecord rec : statRecords) {
			if (rec.sampleCount == 0) {
				rec.average = totalAverage;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aivan.androitest1.StatisticsCalculator#dump()
	 */
	public String dump() {
		String result = "";
		int index = 0;
		for (StatRecord rec : statRecords) {
			result += "index=" + index + " samples=" + rec.sampleCount
					+ " avgValue=" + rec.average;
			index++;
			if (index < statRecords.length) {
				result += "\n";
			}
		}
		return result;
	}

}
