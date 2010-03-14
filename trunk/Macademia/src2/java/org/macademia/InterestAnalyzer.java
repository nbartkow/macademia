package org.macademia;

import com.aliasi.spell.TfIdfDistance;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.net.MalformedURLException;

import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import org.json.JSONException;

/**
 * Created by IntelliJ IDEA.
 * User: shilad
 * Date: Nov 18, 2009
 * Time: 12:33:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class InterestAnalyzer {

    public void writeCorpus(File peoplePath, File corpusPath) throws Exception {
        Set<String> interests = getInterests(peoplePath);
        WikipediaTextDumper dumper = new WikipediaTextDumper();
        System.err.println("read : " + interests.size() + " interests");
        BufferedWriter writer = new BufferedWriter(new FileWriter(corpusPath));
        for (String interest : interests) {
            String text = dumper.getText(interest);
            if (text != null) {
                writer.write(interest + "\t" + text.replaceAll("\\s+", " ") + "\n");
                writer.flush();
            }
        }
        writer.close();
    }

    public Map<String, String> readCorpus(File corpusPath) throws Exception {
        Map<String, String> corpus = new HashMap<String, String>();
        BufferedReader reader = new BufferedReader(new FileReader(corpusPath));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            String [] tokens = line.split("\t");
            if (tokens.length == 2) {
                corpus.put(tokens[0], tokens[1]);
            } else {
                System.err.println("bad line: '" + line + "'");
            }
        }
        return corpus;
    }

    public Set<String> getInterests(File path) throws IOException {
        Set<String> interests = new HashSet<String>();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            String [] tokens = line.split("\t");
            if (tokens.length == 4) {
                for (String i : tokens[3].split(",")) {
                    interests.add(i.trim().toLowerCase());
                }
            } else {
                System.err.println("bad line: '" + line + "'");
            }
        }
        return interests;
    }

    public void outputSims(Map<String, String> corpus, File simPath) throws IOException {
        System.err.println("training tfidf..");
        TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.FACTORY;
        TfIdfDistance tfIdf = new TfIdfDistance(tokenizerFactory);
        for (String i : corpus.keySet()) {
            tfIdf.trainIdf(corpus.get(i));
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(simPath));
        for (String i1 : corpus.keySet()) {
            for (String i2 : corpus.keySet()) {
                double p = tfIdf.proximity(corpus.get(i1), corpus.get(i2));
                if (p > 0) {
                    writer.write(i1 + "\t" + i2 + "\t" + p + "\n");
                    writer.flush();
                }
            }
        }
        writer.close();
    }

    public static void main(String args[]) throws Exception {
        InterestAnalyzer analyzer = new InterestAnalyzer();
        
        // Read and write corpus
//        analyzer.writeCorpus(new File("db/prod/people.txt"), new File("dat/corpus.txt"));

        // Analyze similarities in corpus
        Map<String, String> corpus = analyzer.readCorpus(new File("dat/corpus.txt"));
        analyzer.outputSims(corpus, new File("dat/sims.txt"));
    }
}
