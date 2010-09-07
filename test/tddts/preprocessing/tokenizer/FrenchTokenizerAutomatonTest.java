/* This file is licensed to you under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except 
 * in compliance with the License.  You may obtain a copy of the 
 * License at
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
package tddts.preprocessing.tokenizer;

// Java dependencies
import java.util.ArrayList;
// JUnit dependencies
import static org.junit.Assert.*;
import org.junit.Test;
// Tested class dependencies
import tddts.preprocessing.tokenizer.FrenchTokenizerAutomaton.Signal;

/**
 * Test cases for the French tokenizer automaton.
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 */
public class FrenchTokenizerAutomatonTest {

	protected final String tstSimpleWords =
		"Le petit chat se gratte";
	protected final int[][] tstSimpleWordsOffsets = new int[][] {
			{0,1}, {3,7}, {9,12}, {14,15}, {17,22}
	};
	
	protected final String tstCompoundWords =
		"Le grille-pain des USA s'amuse, c-à-d qu’il est heureux ajourd'hui";
	protected final int[][] tstCompoundWordsOffsets = new int[][] {
			{0,1}, {3,13}, {15,17}, {19,21}, {23,24}, {25,29},
			{30,30}, {32,36}, {38,40}, {41,42}, {44,46}, {48,54}, {56,65}
	};
	
	protected final String tstSimpleNumbers	=
		"40 (quarante) et 179 (cent soixante dix-neuf) font 219 (deux cents dix-neuf)";
	protected final int[][] tstSimpleNumbersOffsets = new int[][] {
			{0,1}, {3,3}, {4,11}, {12,12}, {14,15}, {17,19}, {21,21},
			{22,25}, {27,34}, {36,43}, {44,44}, {46,49}, {51,53},
			{55,55}, {56,59}, {61,65}, {67,74}, {75,75}
	};
	
	protected final String tstComplexNumbers =
		"Le CAC-40 a perdu 2,17%, le NASDAQ vaut 100.786 $";
	protected final int[][] tstComplexNumbersOffsets = new int[][] {
		{0,1}, {3,8}, {10,10}, {12,16}, {18,22}, {23,23}, {25,26},
		{28,33}, {35,38}, {40,48}
	};
	
	protected final String tstPunctuations =
		"La punctuation, utilisée correctement, aère le texte. N'est-ce pas ?";
	protected final int[][] tstPunctuationsOffsets = new int[][] {
		{0,1}, {3,13}, {14,14}, {16,23}, {25,36}, {37,37}, {39,42},
		{44,45}, {47,51}, {52,52}, {54,55}, {56,61}, {63,65}, {67,67}
	};
	
	protected final String tstAllTogether =
		"Le grille-pain, d'une valeur de 56,78€ (avec une réduction de 5 %), " +
		"est heureux. Il grille des pains, des brioches, des tartines...";
	protected final int[][] tstAllTogetherOffsets = new int[][] {
		{0,1}, {3,13}, {14,14}, {16,17}, {18,20}, {22,27}, {29,30}, {32,37},
		{39,39}, {40,43}, {45,47}, {49,57}, {59,60}, {62,64}, {65,65}, {66,66},
		{68,70}, {72,78}, {79,79}, {81,82}, {84,89}, {91,93}, {95,99}, 
		{100,100}, {102,104}, {106,113}, {114,114}, {116,118}, {120,127},
		{128,130}
	};
	
	@Test
	public void testSimpleWords() {
		// Génération des résultats sur tstSimpleWords
		int[][] offsets = runAutomaton(tstSimpleWords);
		assertEquals(tstSimpleWordsOffsets.length, offsets.length);
		assertArrayEquals(tstSimpleWordsOffsets, offsets);
	}
	
	@Test
	public void tstCompoundWords() {
		// Génération des résultats sur tstSimpleWords
		int[][] offsets = runAutomaton(tstCompoundWords);
		assertEquals(tstCompoundWordsOffsets.length, offsets.length);
		assertArrayEquals(tstCompoundWordsOffsets, offsets);
	}

	@Test
	public void tstSimpleNumbers() {
		// Génération des résultats sur tstSimpleWords
		int[][] offsets = runAutomaton(tstSimpleNumbers);
		assertEquals(tstSimpleNumbersOffsets.length, offsets.length);
		assertArrayEquals(tstSimpleNumbersOffsets, offsets);
	}

	@Test
	public void tstComplexNumbers() {
		// Génération des résultats sur tstSimpleWords
		int[][] offsets = runAutomaton(tstComplexNumbers);
		assertEquals(tstComplexNumbersOffsets.length, offsets.length);
		assertArrayEquals(tstComplexNumbersOffsets, offsets);
	}

	@Test
	public void tstPunctuations() {
		// Génération des résultats sur tstSimpleWords
		int[][] offsets = runAutomaton(tstPunctuations);
		assertEquals(tstPunctuationsOffsets.length, offsets.length);
		assertArrayEquals(tstPunctuationsOffsets, offsets);
	}
	
	@Test
	public void tstAllTogether() {
		// Génération des résultats sur tstSimpleWords
		int[][] offsets = runAutomaton(tstAllTogether);
		
		assertArrayEquals(tstAllTogetherOffsets[0], offsets[0]);
		assertArrayEquals(tstAllTogetherOffsets[1], offsets[1]);
		assertArrayEquals(tstAllTogetherOffsets[2], offsets[2]);
		assertArrayEquals(tstAllTogetherOffsets[3], offsets[3]);
		assertArrayEquals(tstAllTogetherOffsets[4], offsets[4]);
		assertArrayEquals(tstAllTogetherOffsets[5], offsets[5]);
		assertArrayEquals(tstAllTogetherOffsets[6], offsets[6]);
		assertArrayEquals(tstAllTogetherOffsets[7], offsets[7]);
		assertArrayEquals(tstAllTogetherOffsets[8], offsets[8]);
		assertArrayEquals(tstAllTogetherOffsets[9], offsets[9]);
		assertArrayEquals(tstAllTogetherOffsets[10], offsets[10]);
		assertArrayEquals(tstAllTogetherOffsets[11], offsets[11]);
		assertArrayEquals(tstAllTogetherOffsets[12], offsets[12]);
		assertArrayEquals(tstAllTogetherOffsets[13], offsets[13]);
		assertArrayEquals(tstAllTogetherOffsets[14], offsets[14]);
		assertArrayEquals(tstAllTogetherOffsets[15], offsets[15]);
		
		assertEquals(tstAllTogetherOffsets.length, offsets.length);
		assertArrayEquals(tstAllTogetherOffsets, offsets);
	}
	
	/**
	 * Wrapper to run the automaton.
	 */
	private int[][] runAutomaton(String text) {
		// Prepare the variables
		ArrayList<int[]> offsets = new ArrayList<int[]>();
		int begin = -1;
		int end = -1;
		FrenchTokenizerAutomaton automaton = new FrenchTokenizerAutomaton();
		automaton.reset();
		// Run over the string
		for(int i=0 ; i<text.length() ; i++) {
			Signal s = automaton.feedChar( text.charAt(i) );
			switch(s) {
			case start_word:
				begin = i;
				break;
			case end_word:
				end = i-1;
				offsets.add( new int[]{begin, end} );
				begin = -1;
				end   = -1;
				break;
			case end_word_prev:
				end = i-2;
				offsets.add( new int[]{begin, end} );
				begin = -1;
				end   = -1;
				break;
			case switch_word:
				end = i-1;
				offsets.add( new int[]{begin, end} );
				begin = i;
				end   = -1;
				break;
			case switch_word_prev:
				end = i-2;
				offsets.add( new int[]{begin, end} );
				begin = i;
				end   = -1;
				break;
			case cancel_word:
				begin = -1;
				break;
			}
		}
		// Add the last one
		if (begin != -1) {
			end = text.length()-1;
			offsets.add( new int[]{begin, end} );
		}
		// Print the result -> debug
		for(int[] off: offsets) {
			System.out.println("{"+off[0]+","+off[1]+"} :- "+text.substring(off[0], off[1]+1));
		}
		// Return the result
		return offsets.toArray( new int[offsets.size()][] );
	}
	
}
