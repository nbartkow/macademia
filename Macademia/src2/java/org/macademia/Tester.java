package org.macademia;


import java.io.IOException;

import de.linguatools.disco.DISCO;

public class Tester {
	public static void main(String args[]) throws IOException {
		DISCO d = new DISCO();
		for (String word : d.similarWords(Utils.PUBMED, args[0]).words) {
			System.out.println("word is " + word);
		}
	}
}
