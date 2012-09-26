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

  StatRecord[] statRecords = new StatRecord[AndroBatConfiguration.MAX_BATERY_LEVEL - AndroBatConfiguration.MIN_BATTERY_LEVEL + 1];

  public StatisticsPercentageBasic() {
    // Just fill array with empty stat records
    for (int i = 0; i < statRecords.length; i++) {
      statRecords[i] = new StatRecord();
    }
  }

  @Override
  public void evaluate(long oldDate, int oldValue, long newDate, int newValue) {
    if (oldValue > newValue && (newDate - oldDate) < AndroBatConfiguration.MAX_MS_PER_PERCENT) {
      // Only these cases are interesting, there should be no duplicates ,and
      // values should not be too far apart
      int levelDifference = oldValue - newValue;
      long timeDifference = newDate - oldDate;

      for (int i = 0; i < levelDifference; i++) {
        updateStatRecord(statRecords[oldValue - i], timeDifference / levelDifference);
      }

    }
  }

  private void updateStatRecord(StatRecord statRecord, long value) {
    statRecord.average = (statRecord.average) * (statRecord.sampleCount / (statRecord.sampleCount + 1)) + value
        / (statRecord.sampleCount + 1);
    statRecord.sampleCount++;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.aivan.androitest1.StatisticsCalculator#dump()
   */
  @Override
  public String dump() {
    String result = "";
    int index = 0;
    for (StatRecord rec : statRecords) {
      result += "index=" + index + " samples=" + rec.sampleCount + " avgValue=" + rec.average;
      index++;
      if (index < statRecords.length) {
        result += "\n";
      }
    }
    return result;
  }

}
