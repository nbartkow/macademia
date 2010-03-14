package org.macademia;

import org.macademia.jad.DISCO;

import java.util.*;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Measures the similarity between two phrases
 */
public class PhraseComparer {
	private Map<String, Map<String, Double>> wordSims =
		 		Collections.synchronizedMap(
                         new HashMap<String, Map<String, Double>>()
                     );
	private Map<String, Map<String, Double>> phraseSims =
		 		Collections.synchronizedMap(
                         new HashMap<String, Map<String, Double>>()
                     );
    private Map<String, Integer> counts = new HashMap<String, Integer>();
    private static String[] STOP_WORDS = new String[] {
                                        "a",
                                        "an",
                                        "the",
                                        "of",
                                        "",
                                        "in"
                                };
    private static final double MAX_SIM = 0.05;
    private DISCO disco;

    public PhraseComparer(DISCO disco, Collection<String> interests) {
        this.disco = disco;
        for (String i : interests) {
            for (String word : split(clean(i))) {
                if (!counts.containsKey(word)) {
                    counts.put(word, 0);
                }
                counts.put(word, counts.get(word) + 1);
            }
        }
    }

    public double phraseSimilarity(String phrase1, String phrase2) throws IOException {
        phrase1 = clean(phrase1);
        phrase2 = clean(phrase2);
        
        // Swap so we don't have to check order both ways
        if (phrase1.compareTo(phrase2) < 0) {
            String t = phrase1;
            phrase1 = phrase2;
            phrase2 = t;
        }
        
        Map<String, Double> onePhraseSims = phraseSims.get(phrase1);
        if (onePhraseSims == null) {
            onePhraseSims = Collections.synchronizedMap(new HashMap<String, Double>());
            phraseSims.put(phrase1,onePhraseSims);
        }
        if (onePhraseSims.containsKey(phrase2)) {
            return onePhraseSims.get(phrase2);
        }

        String [] words1 = split(phrase1);
        String [] words2 = split(phrase2);

        int count1 = 0;
        int count2 = 0;
        for (String w : words1) {
            if (!isStopWord(w)) {
                count1++;
            }
        }
        for (String w : words2) {
            if (!isStopWord(w)) {
                count2++;
            }
        }

        double sumSims = 0.0;
        for (String w1 : words1) {
            if (isStopWord(w1)) {
                continue;
            }
            double maxSim = 0.0;
            for (String w2 : words2) {
                if (isStopWord(w2)) {
                    continue;
                }
                double s = wordSims(w1, w2);
                maxSim = Math.max(s, maxSim);
            }
            sumSims += maxSim;
        }

        double result = 0.0;
        if (count1 + count2 > 0) {
            result = sumSims / Math.max(count1, count2);
        }
        onePhraseSims.put(phrase2, result);
        //System.out.println("" + format(result) + "\tphrase\t" + phrase1 + "\t" + phrase2);
        
        return result;
    }

    private double wordSims(String word1, String word2) throws IOException {
        // Swap so we don't have to check order both ways
        if (word1.compareTo(word2) < 0) {
            String t = word1;
            word1 = word2;
            word2 = t;
        }

        Map<String, Double> oneWordSims = wordSims.get(word1);
        if (oneWordSims == null) {
            oneWordSims = Collections.synchronizedMap(new HashMap<String, Double>());
            wordSims.put(word1,oneWordSims);
        }
        if (oneWordSims.containsKey(word2)) {
            return oneWordSims.get(word2);
        }
        double s = disco.firstOrderSimilarity(word1, word2);
        s = Math.min(s, MAX_SIM) / (4 + popularityPenalty(word1) + popularityPenalty(word2));
        oneWordSims.put(word2, s);
        //System.out.println("" + format(s) + "\tword\t" + word1 + "\t" + word2);
        return s;
    }

    public double popularityPenalty(String word) {
        return Math.log(counts.get(word) + 1);
    }

    public static String[] split(String phrase) {
        return phrase.split("\\W+");
    }

    public static String clean(String phrase) {
        String cleaned = phrase.toLowerCase().replace("'", "");
        return cleaned.replaceAll("\\W+", " ");
    }

    public static boolean isStopWord(String word) {
        word = word.toLowerCase();
        for (int i = 0; i < STOP_WORDS.length; i++) {
            if (word.equals(STOP_WORDS[i])) {
                return true;
            }
        }
        return false;
    }

    private static DecimalFormat formatter = new DecimalFormat("#.###################");  
    public static String format(double d) {
        return formatter.format(d);
    }
}
