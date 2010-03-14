// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DISCO.java

package org.macademia.jad;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

// Referenced classes of package de.linguatools.disco:
//            ReturnDataBN, ReturnDataCol, ValueComparator

public class DISCO
{
	private IndexSearcher is;

    public DISCO(String indexName) throws IOException
    {
    	org.apache.lucene.store.Directory fsDir = FSDirectory.getDirectory(indexName);
    	is = new IndexSearcher(fsDir);
    }

    public Document searchIndex(String word)
        throws IOException
    {
        Hits hits;
        WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
        QueryParser parser = new QueryParser("word", analyzer);
        org.apache.lucene.search.Query query;
		try {
			query = parser.parse(word);
		} catch (ParseException e) {
			throw new IOException(e.getMessage());
		}
        hits = is.search(query);
        if(hits.length() == 0)
            return null;
        Document doc = hits.doc(0);
		return doc;
    }

    public int numberOfWords(String indexName)
        throws IOException
    {
        org.apache.lucene.store.Directory fsDir = FSDirectory.getDirectory(indexName);
        IndexReader ir = IndexReader.open(fsDir);
        return ir.numDocs();
    }

    public int frequency(String word)
        throws IOException
    {
        Document doc = searchIndex(word);
        if(doc == null)
            return 0;
        else
            return Integer.parseInt(doc.get("freq"));
    }

    public ReturnDataBN similarWords(String word)
        throws IOException
    {
        Document doc = searchIndex(word);
        if(doc == null)
        {
            return null;
        } else
        {
            ReturnDataBN res = new ReturnDataBN();
            res.words = doc.get("dsb").split(" ");
            res.values = doc.get("dsbSim").split(" ");
            return res;
        }
    }

    public ReturnDataCol[] collocations(String word)
        throws IOException
    {
        Document doc = searchIndex(word);
        if(doc == null)
            return null;
        Hashtable colloHash = new Hashtable();
        int i;
        for(int rel = 1; rel <= 6; rel++)
        {
            String wordsBuffer[] = doc.get((new StringBuilder()).append("kol").append(Integer.toString(rel)).toString()).split(" ");
            String valuesBuffer[] = doc.get((new StringBuilder()).append("kol").append(Integer.toString(rel)).append("Sig").toString()).split(" ");
            for(i = 1; i < wordsBuffer.length; i++)
                if(colloHash.get(wordsBuffer[i]) == null)
                {
                    colloHash.put(wordsBuffer[i], Float.valueOf(Float.parseFloat(valuesBuffer[i])));
                } else
                {
                    float sig = Float.parseFloat(valuesBuffer[i]) + ((Float)colloHash.get(wordsBuffer[i])).floatValue();
                    colloHash.put(wordsBuffer[i], Float.valueOf(sig));
                }

        }

        ReturnDataCol res[] = new ReturnDataCol[colloHash.size()];
        Enumeration e = colloHash.keys();
        i = 0;
        while(e.hasMoreElements()) 
        {
            String w = (String)e.nextElement();
            res[i++] = new ReturnDataCol(w, ((Float)colloHash.get(w)).floatValue());
        }
        Arrays.sort(res, new ValueComparator());
        return res;
    }

    public float firstOrderSimilarity(String w1, String w2)
        throws IOException
    {
        Document doc1 = searchIndex(w1);
        Document doc2 = searchIndex(w2);
        if(doc1 == null || doc2 == null)
            return -1F;
        Hashtable colloHash = new Hashtable();
        float nenner = 0.0F;
        for(int rel = 1; rel <= 6; rel++)
        {
            String wordsBuffer[] = doc1.get((new StringBuilder()).append("kol").append(Integer.toString(rel)).toString()).split(" ");
            String valuesBuffer[] = doc1.get((new StringBuilder()).append("kol").append(Integer.toString(rel)).append("Sig").toString()).split(" ");
            for(int i = 1; i < wordsBuffer.length; i++)
            {
                float v = Float.parseFloat(valuesBuffer[i]);
                colloHash.put((new StringBuilder()).append(wordsBuffer[i]).append("_").append(Integer.toString(rel)).toString(), Float.valueOf(v));
                nenner += v;
            }

        }

        float zaehler = 0.0F;
        for(int rel = 1; rel <= 6; rel++)
        {
            String wordsBuffer[] = doc2.get((new StringBuilder()).append("kol").append(Integer.toString(rel)).toString()).split(" ");
            String valuesBuffer[] = doc2.get((new StringBuilder()).append("kol").append(Integer.toString(rel)).append("Sig").toString()).split(" ");
            for(int i = 1; i < wordsBuffer.length; i++)
            {
                float v = Float.parseFloat(valuesBuffer[i]);
                if(colloHash.containsKey((new StringBuilder()).append(wordsBuffer[i]).append("_").append(Integer.toString(rel)).toString()))
                    zaehler += v + ((Float)colloHash.get((new StringBuilder()).append(wordsBuffer[i]).append("_").append(Integer.toString(rel)).toString())).floatValue();
                nenner += v;
            }

        }

        return zaehler / nenner;
    }

    public float secondOrderSimilarity(String w1, String w2)
        throws IOException
    {
        Document doc1 = searchIndex(w1);
        Document doc2 = searchIndex(w2);
        if(doc1 == null || doc2 == null)
            return -1F;
        Hashtable simHash = new Hashtable();
        float nenner = 0.0F;
        String wordsBuffer[] = doc1.get("dsb").split(" ");
        String valuesBuffer[] = doc1.get("dsbSim").split(" ");
        for(int i = 1; i < wordsBuffer.length; i++)
        {
            float v = Float.parseFloat((new StringBuilder()).append("0.").append(valuesBuffer[i]).toString());
            simHash.put(wordsBuffer[i], Float.valueOf(v));
            nenner += v;
        }

        float zaehler = 0.0F;
        wordsBuffer = doc2.get("dsb").split(" ");
        valuesBuffer = doc2.get("dsbSim").split(" ");
        for(int i = 1; i < wordsBuffer.length; i++)
        {
            float v = Float.parseFloat((new StringBuilder()).append("0.").append(valuesBuffer[i]).toString());
            if(simHash.containsKey(wordsBuffer[i]))
                zaehler += v + ((Float)simHash.get(wordsBuffer[i])).floatValue();
            nenner += v;
        }

        return zaehler / nenner;
    }
}
