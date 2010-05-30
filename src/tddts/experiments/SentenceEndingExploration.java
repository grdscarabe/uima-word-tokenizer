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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;

/**
 * This class is just an experimentation regarding the model we proposed for
 * detecting sentences ending.
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 */
public class SentenceEndingExploration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Retrieve the file from which we load the various instances and 
			// prepare the reading buffer
			FileInputStream fis = new FileInputStream(args[0]);
			BufferedReader dis  = new BufferedReader(
					new InputStreamReader(fis, Charset.forName("UTF-8")));
			// Now browse line per line, one line corresponds to an instance
	        String line = null;
	        while ( (line = dis.readLine()) != null) {
	        	String[] cl = new String[line.length()];
	        	// remove line jump to get the instance and browse each char
	        	for(int i=0 ; i<line.length() ; i++) {
	        		switch ( Character.getType(line.charAt(i)) ) {
	        		case Character.COMBINING_SPACING_MARK:
	        			cl[i] = "COMBINING_SPACING_MARK";
	        			break;
	        		case Character.CONNECTOR_PUNCTUATION:
	        			cl[i] = "CONNECTOR_PUNCTUATION";
	        			break;
	        		case Character.CONTROL:
	        			cl[i] = "CONTROL";
	        			break;
	        		case Character.CURRENCY_SYMBOL:
	        			cl[i] = "CURRENCY_SYMBOL";
	        			break;
	        		case Character.DASH_PUNCTUATION:
	        			cl[i] = "DASH_PUNCTUATION";
	        			break;
	        		case Character.DECIMAL_DIGIT_NUMBER:
	        			cl[i] = "DECIMAL_DIGIT_NUMBER";
	        			break;
	        		case Character.ENCLOSING_MARK:
	        			cl[i] = "ENCLOSING_MARK";
	        			break;
	        		case Character.END_PUNCTUATION:
	        			cl[i] = "END_PUNCTUATION";
	        			break;
	        		case Character.FINAL_QUOTE_PUNCTUATION:
	        			cl[i] = "FINAL_QUOTE_PUNCTUATION";
	        			break;
	        		case Character.FORMAT:
	        			cl[i] = "FORMAT";
	        			break;
	        		case Character.INITIAL_QUOTE_PUNCTUATION:
	        			cl[i] = "INITIAL_QUOTE_PUNCTUATION";
	        			break;
	        		case Character.LETTER_NUMBER:
	        			cl[i] = "LETTER_NUMBER";
	        			break;
	        		case Character.LINE_SEPARATOR:
	        			cl[i] = "LINE_SEPARATOR";
	        			break;
	        		case Character.LOWERCASE_LETTER:
	        			cl[i] = "LOWERCASE_LETTER";
	        			break;
	        		case Character.MATH_SYMBOL:
	        			cl[i] = "MATH_SYMBOL";
	        			break;
	        		case Character.MODIFIER_LETTER:
	        			cl[i] = "MODIFIER_LETTER";
	        			break;
	        		case Character.MODIFIER_SYMBOL:
	        			cl[i] = "MODIFIER_SYMBOL";
	        			break;
	        		case Character.NON_SPACING_MARK:
	        			cl[i] = "NON_SPACING_MARK";
	        			break;
	        		case Character.OTHER_LETTER:
	        			cl[i] = "OTHER_LETTER";
	        			break;
	        		case Character.OTHER_NUMBER:
	        			cl[i] = "OTHER_NUMBER";
	        			break;
	        		case Character.OTHER_PUNCTUATION:
	        			cl[i] = "OTHER_PUNCTUATION";
	        			break;
	        		case Character.OTHER_SYMBOL:
	        			cl[i] = "OTHER_SYMBOL";
	        			break;
	        		case Character.PARAGRAPH_SEPARATOR:
	        			cl[i] = "PARAGRAPH_SEPARATOR";
	        			break;
	        		case Character.PRIVATE_USE:
	        			cl[i] = "PRIVATE_USE";
	        			break;
	        		case Character.SPACE_SEPARATOR:
	        			cl[i] = "SPACE_SEPARATOR";
	        			break;
	        		case Character.START_PUNCTUATION:
	        			cl[i] = "START_PUNCTUATION";
	        			break;
	        		case Character.SURROGATE:
	        			cl[i] = "SURROGATE";
	        			break;
	        		case Character.TITLECASE_LETTER:
	        			cl[i] = "TITLECASE_LETTER";
	        			break;
	        		case Character.UNASSIGNED:
	        			cl[i] = "UNASSIGNED";
	        			break;
	        		case Character.UPPERCASE_LETTER:
	        			cl[i] = "UPPERCASE_LETTER";
	        			break;
	        		}
	        	}
	        	// Output
	        	System.out.println(line + "->" + StringUtils.join(cl, ";"));
	        }
	        dis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
