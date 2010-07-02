package org.macademia;

import java.util.HashMap;

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
public class TimingAnalysis {
    HashMap<String, Long> calls = new HashMap<String, Long>();
    HashMap<String, Long> totalTime = new HashMap<String, Long>();
    Long lastTime;

    public TimingAnalysis() {
        startTime();
    }

    public void startTime() {
        lastTime = System.currentTimeMillis();
    }

    public String recordTime(String label) {
        Long currentTime = System.currentTimeMillis();
        Long callTime = currentTime - lastTime;
        if (calls.containsKey(label)) {
            calls.put(label, calls.get(label) + 1);
            totalTime.put(label, totalTime.get(label) + callTime);
        } else {
            calls.put(label, (long)1);
            totalTime.put(label,callTime);
        }
        lastTime = currentTime;
        return label + " took " + callTime + " milliseconds.";

    }

    public void analyze() {
        for (String label : calls.keySet()) {
            System.out.println(label + " took an average of " + (totalTime.get(label)/calls.get(label)) +
                    " milliseconds over " + calls.get(label) + " calls and " + totalTime.get(label) +
                    " total milliseconds.");
        }
    }
}
