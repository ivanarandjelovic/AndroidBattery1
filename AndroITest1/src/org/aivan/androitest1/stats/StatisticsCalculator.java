package org.aivan.androitest1.stats;

import android.content.Context;

public interface StatisticsCalculator {
  /**
   * Process one pair of readings, two successive records
   * 
   * @param oldDate
   * @param oldValue
   * @param newDate
   * @param newValue
   */
  void evaluate(long oldDate, int oldValue, long newDate, int newValue);
  
  /**
   * Some values might be missing, fill them with average values, or .. whatever we find suitable
   */
  void fillTheblanks();
  
  /**
   * Helper method to dump all statis into a string, mostly for debugging purposes
   * @return
   */
  String dump(Context context);
}
