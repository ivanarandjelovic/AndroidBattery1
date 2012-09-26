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

  StatRecord[] statRecords = new StatRecord[AndroBatConfiguration.MAX_BATERY_LEVEL - AndroBatConfiguration.MIN_BATTERY_LEVEL];

  public StatisticsPercentageBasic() {
    // Just fill array with empty stat records
    for (int i = 0; i < statRecords.length; i++) {
      statRecords[i] = new StatRecord();
    }
  }

  @Override
  public void evaluate(long oldDate, int oldValue, long newDate, int newValue) {

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
