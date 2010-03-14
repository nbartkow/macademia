package org.macademia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.macademia.jad.DISCO;


public class InterestComparer {
	private static final double FRACTION_GOOD_INTERESTS = 0.25;
	private static final int MAX_SIMILARITIES_PER_INTEREST = 5;

    PhraseComparer phraseComparer;
	private Map<String, Double> thresholds = new HashMap<String, Double>();
    private Map<String, Map<String, Double>> interestSims =
		 		new HashMap<String, Map<String, Double>>();
	
	public InterestComparer(PhraseComparer phraseComparer) {
		this.phraseComparer = phraseComparer;
	}
	
	public void buildInterestSims(Collection<Professor> profs) throws IOException {
		final Set<String> interests = new TreeSet<String>();
		
		for (Professor p : profs) {
            for (String i : p.getInterests()) {
			    interests.add(cleanInterest(i));
            }
		}

		ExecutorService pool = Executors.newFixedThreadPool(7);
		for (final String i1 : interests) {
			pool.execute(new Runnable() {
				public void run() {
					analyzeSims(i1, interests);
				}				
			});
		}
		pool.shutdown();
		try {
			pool.awaitTermination(60*60*24*30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			pool.shutdownNow();
		}
	}
	
	public void outputSims() {

		// Pass 1: Calculate the overall threshold
		List<Double> values = new ArrayList<Double>();
		for (Map<String, Double> m : interestSims.values()) {
			values.addAll(m.values());
		}
		Collections.sort(values);
		Collections.reverse(values);
		int n = interestSims.size();
		double overallThreshold = values.get((int)(.25 * n ));
	
		// Pass 1: Print top n similarity scores for each interest
		for (String i1 : interestSims.keySet()) {
			Map<String, Double> m = interestSims.get(i1);
			List<Double> v = new ArrayList<Double>(m.values());
			v.remove(m.get(i1));	// remove score for the identity match
			Collections.sort(v);
			Collections.reverse(v);
            double threshold = Math.min(overallThreshold, v.get((int)(interestSims.size()*0.25)));
            for (String i2 : m.keySet()) {
                double s = m.get(i2);
                if (s >= threshold) {
                    System.out.println(i1 + "\t" + i2 + "\t" + s);
                }
            }
		}
    }

	public void calculateGoodnessThreshold() {
		
		// Pass 1: build up a list of top n similarity scores for each interest
		Map<String, List<Double>> interestValues = new HashMap<String, List<Double>>();		
		for (String i1 : interestSims.keySet()) {
			Map<String, Double> m = interestSims.get(i1);
			List<Double> v = new ArrayList<Double>(m.values());
			v.remove(m.get(i1));	// remove score for the identity match
			Collections.sort(v);
			Collections.reverse(v);
			v = v.subList(0, MAX_SIMILARITIES_PER_INTEREST);
			interestValues.put(i1, v);
		}
		
		// Pass 2: build up a list of all values
		List<Double> values = new ArrayList<Double>();
		for (List<Double> v : interestValues.values()) {
			values.addAll(v);
		}
		Collections.sort(values);
		Collections.reverse(values);
		int n = interestSims.size();
		double overallThreshold = values.get((int)(FRACTION_GOOD_INTERESTS * n ));

		for (String i1 : interestValues.keySet()) {
			List<Double> v = interestValues.get(i1);
			double t = Math.max(overallThreshold, v.get(v.size()-1));
			thresholds.put(i1, t);
			System.err.println("setting threshold for " + i1 + " to " + t);
		}
		
//		for (Map.Entry<String, Map<String, Double>> entry : interestSims.entrySet()) {
//			String i1 = entry.getKey();
//			for (String i2 : entry.getValue().keySet()) {
//				double s = entry.getValue().get(i2);
//				if (s >= threshold && !i1.equals(i2)) {
//					System.err.println("similarity of " + i1 + " and " + i2 + " is " + s);
//				}
//			}
//		}
	}
	
	public Map<Professor, Set<String>> findNeighbors(Professor prof, Collection<Professor> faculty) {
		Map<Professor, Set<String>> sim = new HashMap<Professor, Set<String>>();
		
		for (Professor peer : faculty) {
			if (peer == prof) {
				continue;
			}
			Set<String> sharedInterests = new HashSet<String>();
			for (String i1 : prof.getInterests()) {
				for (String i2 : peer.getInterests()) {
					if (isSimilarInterest(i1, i2)) {
						sharedInterests.add(i1 + " <=> " + i2);
					}
				}
			}
			if (sharedInterests.size() > 0) {
				sim.put(peer, sharedInterests);
			}
		}
		return sim;
	}
	
	public void outputNeighbors(Collection<Professor> faculty) {
		for (Professor prof : faculty) {
			System.out.println(prof);
			Map<Professor, Set<String>> neighbors = findNeighbors(prof, faculty);
			
			for (Professor peer : neighbors.keySet()) {				
				System.out.println("\t" + peer + ": ");
				for (String sharedInterest : neighbors.get(peer)) {
					System.out.println("\t\t" + sharedInterest);
				}
			}
		}
	}
	
	public boolean isSimilarInterest(String i1, String i2) {
        i1 = cleanInterest(i1);
        i2 = cleanInterest(i2);
		double s = getSimilarity(i1, i2);
		return (s >= thresholds.get(i1) && s >= thresholds.get(i2));
	}
	
	public double getSimilarity(String i1, String i2) {
		if (!interestSims.containsKey(i1)) {
			return -1;
		} else if (!interestSims.get(i1).containsKey(i2)) {
			return -1;
		} else {
			return interestSims.get(i1).get(i2);
		}
	}
	
    private String cleanInterest(String i) {
        i = i.replaceAll("'", "");
        return i.replaceAll("[^a-zA-Z0-9 ]", " ");
    }

	protected void analyzeSims(String i1, Set<String> interests) {
		System.err.println("calculating interests for " + i1);
		for (String i2 : interests) {
            double s;
            try {
                s = phraseComparer.phraseSimilarity(i1, i2);
//					s = disco.firstOrderSimilarity(i1, i2);
//					s = disco.secondOrderSimilarity(i1, i2);
            } catch (IOException e) {
                System.err.println("comparison of interests " + i1 + " and " + i2 + " failed:");
                e.printStackTrace();
                continue;
            }
            synchronized (interestSims) {
                if (!interestSims.containsKey(i1)) {
                    interestSims.put(i1, new HashMap<String, Double>());
                }
                if (!interestSims.containsKey(i2)) {
                    interestSims.put(i2, new HashMap<String, Double>());
                }
                interestSims.get(i1).put(i2, s);
                interestSims.get(i2).put(i1, s);
            }
		}
	}

	public static void main(String args[]) throws IOException {
		InterestsReader reader = new InterestsReader();
		Collection<Professor> profs = reader.read(new File(args[0]));
        Set<String> allInterests = new HashSet<String>();
        for  (Professor p : profs) {
            for (String i : p.getInterests()) {
			    allInterests.add(i);
            }
		}
		System.err.println("read " + profs.size() + " professors from " + args[0]);

        DISCO disco = new DISCO(Utils.WIKIPEDIA);
        PhraseComparer phraseCmp = new PhraseComparer(disco, allInterests);
		InterestComparer interestCmp = new InterestComparer(phraseCmp);
		interestCmp.buildInterestSims(profs);
		interestCmp.calculateGoodnessThreshold();
		interestCmp.outputSims();
		//cmp.outputNeighbors(profs);
	}
}
