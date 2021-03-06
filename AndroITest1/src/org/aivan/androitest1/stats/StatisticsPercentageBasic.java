package org.aivan.androitest1.stats;

import java.text.SimpleDateFormat;

import org.aivan.androitest1.AndroBatConfiguration;
import org.aivan.androitest1.db.HistoryDAO;

import android.content.Context;
import android.content.SharedPreferences;

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
	public class StatRecord {
		public int sampleCount = 0;
		public long average = 0;
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
	public String dump(Context context) {
		String result = "";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(getStatusTimeFromPreferences(context)>0) {
		  result += "Stats calculated at: "+sdf.format(getStatusTimeFromPreferences(context))+"\n";
		} else {
		  result += "Stats not calculated yet.\n";
		}
		for (int i=statRecords.length-1;i>=0;i--) {
		  StatRecord rec = statRecords[i];
			result += "index=" + i + " samples=" + rec.sampleCount
					+ " avgValue=" + rec.average;
		
			if (i > 0) {
				result += "\n";
			}
		}
		return result;
	}

  static protected long getStatusTimeFromPreferences(Context context) {
    SharedPreferences prefs = context.getSharedPreferences("IvanService", Context.MODE_PRIVATE);
    return prefs.getLong(AndroBatConfiguration.PREFERENCES_STATS_TIMESTAMP, 0);
  } 
  
	public void store(HistoryDAO historyDao) {
		historyDao.storeStats(statRecords);
	}

	public void load(HistoryDAO historyDao) {
		statRecords = historyDao.loadStats(this);
	}

	public long estimateForLevel(int lastLevel) {
		long result = 0;

		boolean found = false;
		for (int i = 0; i <= lastLevel && i < statRecords.length; i++) {
			if (statRecords != null) {
				result += statRecords[i].average;
				found = true;
			}
		}

		if (found) {
			return result;
		} else {
			return Long.MIN_VALUE;
		}
	}
}
