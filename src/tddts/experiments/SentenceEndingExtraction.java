/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package tddts.experiments;

// Java dependencies
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
// CSV dependencies
import com.csvreader.CsvWriter;

/**
 * This class browse all the text files passed in parameter (must be utf-8) and
 * extract all the ending punctuations as well as its context and export 
 * everything in a CSV file analysis.
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 */
public class SentenceEndingExtraction {

	/**
	 * Main method
	 */
	public static void main(String[] args) {
		// Prepare the CSV data
		CsvWriter writer = 
			new CsvWriter("/tmp/output.csv", ';', Charset.forName("UTF-8"));
		try {
			writer.writeRecord(new String[]{
				"char-4","class-4",
				"char-3","class-3",
				"char-2","class-2",
				"char-1","class-1",
				"punc","class",
				"char+1","class+1",
				"char+2","class+2"});
		} catch (IOException e) {
			System.out.println("Cannot write in csv file.");
			System.exit(-1);
		}
		// Load the content of each file
		for(String file: args) {
			try {
				// Extract the contexts
				PunctuationContext[] entries = extract(file);
				// Save in the CSV
				for(PunctuationContext entry: entries) {
					writer.writeRecord(entry.toCSVRecord());
				}
			} catch(IOException e) {
				System.out.println("Cannot deal with file "+file+". Passing...");
			}
		}
		// The CSV has been built, close it
		writer.close();
	}

	
	/**
	 * Extract from a file the ending punctuations with their context.
	 * 
	 * @param filename the name of the file to be loaded
	 * @return the contexts
	 */
	public static PunctuationContext[] extract(String filename) {
		// Prepare the list of contexts
		ArrayList<PunctuationContext> contexts = 
			new ArrayList<PunctuationContext>(); 
		try {
			// Open the file for reading
			FileInputStream fis = new FileInputStream(filename);
			BufferedReader dis  = new BufferedReader(
					new InputStreamReader(fis, Charset.forName("UTF-8")));
			StringBuffer buff = new StringBuffer();
			String line;
			while( (line = dis.readLine())  != null ) {
				Boolean eraseBuffer = true;
				buff.append(line);
				for(int i=0 ; i<buff.length() ; i++) {
					if ( isEndingPunc(buff.charAt(i)) ) {
						// Check there is enough data around
						if ( (i+2) < buff.length() ) {
							// Extract the context from the buffer
							PunctuationContext contxt = new PunctuationContext(4, 2);
							contxt.setContext(i, buff.toString());
							contexts.add(contxt);
						} else {
							// Otherwise wait for the data to come
							eraseBuffer = false;
						}
					}
				}
				if (eraseBuffer)
					buff = new StringBuffer();
			}
			// Return the contexts
			return contexts.toArray(new PunctuationContext[contexts.size()]);
		} catch (IOException e) {
			System.out.println("Cannot find file "+filename);
			return contexts.toArray(new PunctuationContext[contexts.size()]);
		}
	}


	/**
	 * Check that the character in parameter is an ending punctuation to 
	 * consider
	 */
	public static boolean isEndingPunc(Character c) {
		return (Character.getType(c) == Character.OTHER_PUNCTUATION);
	}
}

