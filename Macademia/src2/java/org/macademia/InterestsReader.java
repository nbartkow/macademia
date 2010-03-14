package org.macademia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InterestsReader {

	public Collection<Professor> read(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		Map<String, Professor> profs = new HashMap<String, Professor>();
		
		while (line != null) {
			String tokens[] = line.trim().split("\t");
			if (tokens.length != 4) {
				System.err.println("invalid line: " + line.trim());
			    line = reader.readLine();
				continue;
			}
			String name = tokens[0];
			String dept = tokens[1];
			String email = tokens[2];
			String interest = tokens[3];
			
			if (!profs.containsKey(name)) {
				profs.put(name, new Professor(name, dept, email));
			}
			profs.get(name).addInterest(interest);
			line = reader.readLine();
		}
		
		return profs.values();
	}
}
