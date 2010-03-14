package org.macademia;


import java.io.IOException;
import java.io.FileInputStream;
import java.util.Map;
import java.util.HashMap;

import de.linguatools.disco.DISCO;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.Dictionary;
import shef.nlp.wordnet.similarity.SimilarityMeasure;

public class Tester {
	public static void main(String args[]) throws Exception {
//		DISCO d = new DISCO();
//		for (String word : d.similarWords(Utils.PUBMED, args[0]).words) {
//			System.out.println("word is " + word);
//		}
        		//and create a similarity measure otherwise nasty things
		//might happen!
		JWNL.initialize(new FileInputStream("dat/wordnet.xml"));

		//Create a map to hold the similarity config params
		Map<String,String> params = new HashMap<String,String>();

		//the simType parameter is the class name of the measure to use
		params.put("simType","shef.nlp.wordnet.similarity.JCn");

		//this param should be the URL to an infocontent file (if required
		//by the similarity measure being loaded)
		params.put("infocontent","file:dat/ic-bnc-resnik-add1.dat");

		//this param should be the URL to a mapping file if the
		//user needs to make synset mappings
//		params.put("mapping","file:dat/domain_independent.txt");

		//create the similarity measure
		SimilarityMeasure sim = SimilarityMeasure.newInstance(params);

		//Get two words from WordNet
		Dictionary dict = Dictionary.getInstance();
		IndexWord word1 = dict.getIndexWord(POS.NOUN, "ethnicity");
		IndexWord word2 = dict.getIndexWord(POS.NOUN,"globalization");
        assert(word1 != null);
        assert(word2 != null);
        System.out.println("words: " + word1 + ", " + word2);

		//and get the similarity between the first senses of each word
		System.out.println(word1.getLemma()+"#"+word1.getPOS().getKey()+"#1  " + word2.getLemma()+"#"+word2.getPOS().getKey()+"#1  " + sim.getSimilarity(word1.getSense(1), word2.getSense(1)));

		//get similarity using the string methods (note this also makes use
		//of the fake root node)
		System.out.println(sim.getSimilarity("time#n","cat#n"));
	}
}
