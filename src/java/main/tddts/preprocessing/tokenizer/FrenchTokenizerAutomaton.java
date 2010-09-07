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



/**
 * This class implements a transducer with somehow a stack system in order
 * to deal with previously processed elements.
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 */
public class FrenchTokenizerAutomaton {
	
	/** The various states of the automaton */
	public enum States {
		O0, U0, P0,
		L0, L1, L2, L3, L4, L5, L6,
		N0, N1, N2, N3, N4
	};
	
	/** The various signals sent by the automaton */
	public enum Signal {
		start_word,       // a word starts at the current offset
		end_word,         // the current word ended at the offset-1, the 
		                  // current char is out of any word
		switch_now,       // the current word ended at the offset-1, the current
		                  // char starts a new one
		switch_word,      // the current word ended at the current offset, the 
		                  // next char will start a new word
		end_word_prev,    // the current word ended at the offset-2, the last
		                  // two chars are out of any word
		switch_word_prev, // the current word ended at the offset-2, the 
		                  // offset-1 char is out of any word and the current
		                  // char starts a new word
		cancel_word,      // cancel the starting of the current word and get to
		                  // an out of word state
		nop               // no operation
	};
	
	/** State the automaton is in right now */
	public States theCurrentState = States.O0;
	
	// INTERFACE ---------------------------------------------------------------
	
	/**
	 * This method resets the status of the automaton so that it can
	 * be fed with brand new character stream.
	 */
	public void reset() {
		theCurrentState = States.O0;
	}
	
	/**
	 * This method is the entry point for each new character to be 
	 * considered by the automaton. It routes the character depending
	 * on the current status.
	 * 
	 * @param c the character
	 * 
	 * @return 
	 */
	public Signal feedChar(char c) {
		switch ( theCurrentState ) {
		// state : out of any word
		case O0:
			return feedCharO0(c);
		// state : in an unknown kind of word
		case U0:
			return feedCharU0(c);
		// state : dealing with punctuation
		case P0:
			return feedCharP0(c);
		// state : constructing a word made of alphabetic chars
		case L0:
			return feedCharL0(c);
		case L1:
			return feedCharL1(c);
		case L2:
			return feedCharL2(c);
		case L3:
			return feedCharL3(c);
		case L4:
			return feedCharL4(c);
		case L5:
			return feedCharL5(c);
		case L6:
			return feedCharL6(c);
		// state : constructing a word made of numerical chars
		case N0:
			return feedCharN0(c);
		case N1:
			return feedCharN1(c);
		case N2:
			return feedCharN2(c);
		case N3:
			return feedCharN3(c);
		case N4:
			return feedCharN4(c);
		}
		// Should not be reached
		return null;
	}
	
	// STATES HANDLING : SPECIAL ONES ------------------------------------------

	/**
	 * The automaton is in its initial state, out of any word.
	 * null -> O0
	 * O0 -> O0 [ label = "Zl, Zp, Zs" ];
	 * O0 -> U0 [ label = "other" ];
	 * O0 -> N0 [ label = "Nd, Nl, No" ];
	 * O0 -> L0 [ label = "Ll, Lu, Lm, Lo, Lt" ];
	 */
	 private Signal feedCharO0(char c) {
		switch ( Character.getType(c) ) {
		// Stay in this state as long as we get separator chars
		// Zl = LINE_SEPARATOR
		// Zp = PARAGRAPH_SEPARATOR
		// Zs = SPACE_SEPARATOR
		case Character.LINE_SEPARATOR:
		case Character.PARAGRAPH_SEPARATOR:
		case Character.SPACE_SEPARATOR:
			return Signal.nop;
		// Switch to state N0 if we get a number char
		// Nd = DECIMAL_DIGIT_NUMBER
		// Nl = LETTER_NUMBER
		// No = OTHER_NUMBER
		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.LETTER_NUMBER:
		case Character.OTHER_NUMBER:
			theCurrentState = States.N0;
			return Signal.start_word;
		// Switch to state L0 if we get a letter char
		// Ll = LOWERCASE_LETTER
		// Lu = UPPERCASE_LETTER
		// Lm = MODIFIER_LETTER
		// Lo = OTHER_LETTER
		// Lt = TITLECASE_LETTER
		case Character.LOWERCASE_LETTER:
		case Character.UPPERCASE_LETTER:
		case Character.MODIFIER_LETTER:
		case Character.OTHER_LETTER:
		case Character.TITLECASE_LETTER:
			theCurrentState = States.L0;
			return Signal.start_word;
		// Punctuation keep coming
		// Pc = CONNECTOR_PUNCTUATION
		// Pd = DASH_PUNCTUATION
		// Pe = END_PUNCTUATION
		// Pi = INITIAL_QUOTE_PUNCTUATION
		// Pf = FINAL_QUOTE_PUNCTUATION
		// Po = OTHER_PUNCTUATION
		// Ps = START_PUNCTUATION
		case Character.CONNECTOR_PUNCTUATION:
		case Character.DASH_PUNCTUATION:
		case Character.END_PUNCTUATION:
		case Character.INITIAL_QUOTE_PUNCTUATION:
		case Character.FINAL_QUOTE_PUNCTUATION:
		case Character.OTHER_PUNCTUATION:
		case Character.START_PUNCTUATION:
			theCurrentState = States.P0;
			return Signal.start_word;
		// In all other case, we do not know what we are dealing with... so 
		// just switch to the particular state U0 (UNKNOWN)
		default:
			theCurrentState = States.U0;
			return Signal.start_word;
		}
	}

	 /**
	  * The automaton is in the state where we deal with unrecognized type
	  * of word. As long as we get special chars we stay in this state,
	  * otherwise we jump to something more appropriate.
	  * U0 -> U0 [ label = "other" ];
	  * U0 -> L0 [ label = "Ll, Lu, Lm, Lo, Lt/switch_word" ];
	  * U0 -> N0 [ label = "Nd, Nl, No/switch_word" ];
	  * U0 -> O0 [ label = "Zl, Zp, Zs" ];
	  */
	private Signal feedCharU0(char c) {
		switch ( Character.getType(c) ) {
		// Jump to the out of word state if we encounter some space
		// Zl = LINE_SEPARATOR
		// Zp = PARAGRAPH_SEPARATOR
		// Zs = SPACE_SEPARATOR
		case Character.LINE_SEPARATOR:
		case Character.PARAGRAPH_SEPARATOR:
		case Character.SPACE_SEPARATOR:
			theCurrentState = States.O0;
			return Signal.end_word;
		// Jump to a word of numbers if we encounter a numerical char
		// Nd = DECIMAL_DIGIT_NUMBER
		// Nl = LETTER_NUMBER
		// No = OTHER_NUMBER
		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.LETTER_NUMBER:
		case Character.OTHER_NUMBER:
			theCurrentState = States.N0;
			return Signal.switch_word;
		// Switch to state L0 if we get a letter char
		// Ll = LOWERCASE_LETTER
		// Lu = UPPERCASE_LETTER
		// Lm = MODIFIER_LETTER
		// Lo = OTHER_LETTER
		// Lt = TITLECASE_LETTER
		case Character.LOWERCASE_LETTER:
		case Character.UPPERCASE_LETTER:
		case Character.MODIFIER_LETTER:
		case Character.OTHER_LETTER:
		case Character.TITLECASE_LETTER:
			theCurrentState = States.L0;
			return Signal.switch_word;
		// Anything else, we keep going in the same state
		default:
			return Signal.nop;
		}
	}
	
	 /**
	  * The automaton is in the state where we deal with punctuation.
	  * P0 -> P0 [ label = "Pc, Pd, Pe, Pi, Pf, Po, Ps" ];
	  * P0 -> L0 [ label = "Ll, Lu, Lm, Lo, Lt/switch_word" ];
	  * P0 -> N0 [ label = "Nd, Nl, No/switch_word" ];
	  * P0 -> O0 [ label = "Zl, Zp, Zs" ];
	  */
	private Signal feedCharP0(char c) {
		switch ( Character.getType(c) ) {
		// Punctuation keep coming
		// Pc = CONNECTOR_PUNCTUATION
		// Pd = DASH_PUNCTUATION
		// Pe = END_PUNCTUATION
		// Pi = INITIAL_QUOTE_PUNCTUATION
		// Pf = FINAL_QUOTE_PUNCTUATION
		// Po = OTHER_PUNCTUATION
		// Ps = START_PUNCTUATION
		case Character.CONNECTOR_PUNCTUATION:
		case Character.DASH_PUNCTUATION:
		case Character.END_PUNCTUATION:
		case Character.INITIAL_QUOTE_PUNCTUATION:
		case Character.FINAL_QUOTE_PUNCTUATION:
		case Character.OTHER_PUNCTUATION:
		case Character.START_PUNCTUATION:
			theCurrentState = States.P0;
			return Signal.nop;
		// Jump to the out of word state if we encounter some space
		// Zl = LINE_SEPARATOR
		// Zp = PARAGRAPH_SEPARATOR
		// Zs = SPACE_SEPARATOR
		case Character.LINE_SEPARATOR:
		case Character.PARAGRAPH_SEPARATOR:
		case Character.SPACE_SEPARATOR:
			theCurrentState = States.O0;
			return Signal.end_word;
		// Jump to a word of numbers if we encounter a numerical char
		// Nd = DECIMAL_DIGIT_NUMBER
		// Nl = LETTER_NUMBER
		// No = OTHER_NUMBER
		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.LETTER_NUMBER:
		case Character.OTHER_NUMBER:
			theCurrentState = States.N0;
			return Signal.switch_word;
		// Switch to state L0 if we get a letter char
		// Ll = LOWERCASE_LETTER
		// Lu = UPPERCASE_LETTER
		// Lm = MODIFIER_LETTER
		// Lo = OTHER_LETTER
		// Lt = TITLECASE_LETTER
		case Character.LOWERCASE_LETTER:
		case Character.UPPERCASE_LETTER:
		case Character.MODIFIER_LETTER:
		case Character.OTHER_LETTER:
		case Character.TITLECASE_LETTER:
			theCurrentState = States.L0;
			return Signal.switch_word;
		// Anything else, jump to U0
		default:
			theCurrentState = States.U0;
			return Signal.switch_word;
		}
	}
	
	// STATES HANDLING : LETTERS -----------------------------------------------
	
	/**
	 * The automaton is in the state of a one letter word. If any more letter
	 * arise, we get to start a multiple chars word, otherwise if we encounter
	 * an apostrophe, we most likely are in presence of a contracted article.
	 * Any other case is problematic -> handle softly
	 * L0 -> L1 [ label = "Ll, Lu, Lm, Lo, Lt" ];
	 * L0 -> L2 [ label = "U+0027, U+02BC/switch_word" ];
	 * L0 -> L4 [ label = "Pd, U+002E, U+002F" ];
	 * L0 -> O0 [ label = "Zl, Zp, Zs/end_word" color="#A0ffA0" ];
	 * L0 -> U0 [ label = "other/end_word" color="#ffA0A0" ];
	 */
	private Signal feedCharL0(char c) {
		// First check the particular case of the apostrophes and
		// the hyphens
		Character cApostrophe1 = new Character( (char) 0x0027);
		Character cApostrophe2 = new Character( (char) 0x02BC);
		Character cHyphen1     = new Character( (char) 0x002E);
		Character cHyphen2     = new Character( (char) 0x002F);
		Character cU1          = new Character( (char) 0x0075); // u
		Character cU2          = new Character( (char) 0x0055); // U
		if ( cApostrophe1.equals(c) || cApostrophe2.equals(c) ) {
			theCurrentState = States.L2;
			return Signal.nop;
		} else if ( cHyphen1.equals(c) || cHyphen2.equals(c) ) {
			theCurrentState = States.L4;
			return Signal.nop;
		} else if ( cU1.equals(c) || cU2.equals(c) ) {
			theCurrentState = States.L5;
			return Signal.nop;
		} else {
			// Back to unicode classes
			switch ( Character.getType(c) ) {
			// Handle dashes that are not hyphens
			// Pd = DASH_PUNCTUATION
			case Character.DASH_PUNCTUATION:
				theCurrentState = States.L4;
				return Signal.nop;
			// Switch to multiple char if we encounter letters
			// Ll = LOWERCASE_LETTER
			// Lu = UPPERCASE_LETTER
			// Lm = MODIFIER_LETTER
			// Lo = OTHER_LETTER
			// Lt = TITLECASE_LETTER
			case Character.LOWERCASE_LETTER:
			case Character.UPPERCASE_LETTER:
			case Character.MODIFIER_LETTER:
			case Character.OTHER_LETTER:
			case Character.TITLECASE_LETTER:
				theCurrentState = States.L1;
				return Signal.nop;
			// Jump to the out of word state if we encounter some space
			// Zl = LINE_SEPARATOR
			// Zp = PARAGRAPH_SEPARATOR
			// Zs = SPACE_SEPARATOR
			case Character.LINE_SEPARATOR:
			case Character.PARAGRAPH_SEPARATOR:
			case Character.SPACE_SEPARATOR:
				theCurrentState = States.O0;
				return Signal.end_word;
			// Punctuation keep coming
			// Pc = CONNECTOR_PUNCTUATION
			// Pd = DASH_PUNCTUATION
			// Pe = END_PUNCTUATION
			// Pi = INITIAL_QUOTE_PUNCTUATION
			// Pf = FINAL_QUOTE_PUNCTUATION
			// Po = OTHER_PUNCTUATION
			// Ps = START_PUNCTUATION
			case Character.CONNECTOR_PUNCTUATION:
			case Character.END_PUNCTUATION:
			case Character.INITIAL_QUOTE_PUNCTUATION:
			case Character.FINAL_QUOTE_PUNCTUATION:
			case Character.OTHER_PUNCTUATION:
			case Character.START_PUNCTUATION:
				theCurrentState = States.P0;
				return Signal.switch_now;
			// Otherwise, jump to the unknown state
			default:
				theCurrentState = States.U0;
				return Signal.switch_word;
			}
		}
	}
	
	/**
	 * The automaton is in the state of a multiple letters word.
	 * We can have dash connected words or even apostrophe connected ones. 
	 * L1 -> L1 [ label = "Ll, Lu, Lm, Lo, Lt" ];
	 * L1 -> L3 [ label = "U+0027, U+02BC" ];
	 * L1 -> O0 [ label = "Zl, Zp, Zs/end_word" color="#A0ffA0" ];
	 * L1 -> U0 [ label = "other/end_word" color="#ffA0A0" ]; 
	 */
	private Signal feedCharL1(char c) {
		// First check the particular case of the apostrophes and
		// the hyphens
		Character cApostrophe1 = new Character( (char) 0x0027);
		Character cApostrophe2 = new Character( (char) 0x02BC);
		//Character cHyphen1     = new Character( (char) 0x002E);
		Character cHyphen2     = new Character( (char) 0x002F);
		if ( cApostrophe1.equals(c) || cApostrophe2.equals(c) ) {
			theCurrentState = States.L3;
			return Signal.nop;
		//} else if ( cHyphen1.equals(c) || cHyphen2.equals(c) ) {
		} else if ( cHyphen2.equals(c) ) {
			theCurrentState = States.L3;
			return Signal.nop;
		} else {
			// Back to unicode classes
			switch ( Character.getType(c) ) {
			// Handle dashes that are not hyphens
			// Pd = DASH_PUNCTUATION
			case Character.DASH_PUNCTUATION:
				theCurrentState = States.L3;
				return Signal.nop;
			// Keep going if we encounter letters
			// Ll = LOWERCASE_LETTER
			// Lu = UPPERCASE_LETTER
			// Lm = MODIFIER_LETTER
			// Lo = OTHER_LETTER
			// Lt = TITLECASE_LETTER
			case Character.LOWERCASE_LETTER:
			case Character.UPPERCASE_LETTER:
			case Character.MODIFIER_LETTER:
			case Character.OTHER_LETTER:
			case Character.TITLECASE_LETTER:
				return Signal.nop;
			// Jump to the out of word state if we encounter some space
			// Zl = LINE_SEPARATOR
			// Zp = PARAGRAPH_SEPARATOR
			// Zs = SPACE_SEPARATOR
			case Character.LINE_SEPARATOR:
			case Character.PARAGRAPH_SEPARATOR:
			case Character.SPACE_SEPARATOR:
				theCurrentState = States.O0;
				return Signal.end_word;
			// Punctuation keep coming
			// Pc = CONNECTOR_PUNCTUATION
			// Pd = DASH_PUNCTUATION
			// Pe = END_PUNCTUATION
			// Pi = INITIAL_QUOTE_PUNCTUATION
			// Pf = FINAL_QUOTE_PUNCTUATION
			// Po = OTHER_PUNCTUATION
			// Ps = START_PUNCTUATION
			case Character.CONNECTOR_PUNCTUATION:
			case Character.END_PUNCTUATION:
			case Character.INITIAL_QUOTE_PUNCTUATION:
			case Character.FINAL_QUOTE_PUNCTUATION:
			case Character.OTHER_PUNCTUATION:
			case Character.START_PUNCTUATION:
				theCurrentState = States.P0;
				return Signal.switch_now;
			// Otherwise, jump to the unknown state
			default:
				theCurrentState = States.U0;
				return Signal.switch_word;
			}
		}
	}
	
	/**
	 * The automaton is in the state where it has encountered an apostrophe
	 * and the word only have one letter.
	 * We will start a new word or get out.
	 * L2 -> L0 [ label = "Ll, Lu, Lm, Lo, Lt" ];
	 * L2 -> O0 [ label = "Zl, Zp, Zs" ];
	 * L2 -> U0 [ label = "other" ];
	 */
	private Signal feedCharL2(char c) {
		switch ( Character.getType(c) ) {
		// Jump to state L1 we encounter a letter
		// Ll = LOWERCASE_LETTER
		// Lu = UPPERCASE_LETTER
		// Lm = MODIFIER_LETTER
		// Lo = OTHER_LETTER
		// Lt = TITLECASE_LETTER
		case Character.LOWERCASE_LETTER:
		case Character.UPPERCASE_LETTER:
		case Character.MODIFIER_LETTER:
		case Character.OTHER_LETTER:
		case Character.TITLECASE_LETTER:
			theCurrentState = States.L1;
			return Signal.switch_word;
		// Jump to the out of word state if we encounter some space
		// Zl = LINE_SEPARATOR
		// Zp = PARAGRAPH_SEPARATOR
		// Zs = SPACE_SEPARATOR
		case Character.LINE_SEPARATOR:
		case Character.PARAGRAPH_SEPARATOR:
		case Character.SPACE_SEPARATOR:
			theCurrentState = States.O0;
			return Signal.cancel_word;
		// Punctuation keep coming
		// Pc = CONNECTOR_PUNCTUATION
		// Pd = DASH_PUNCTUATION
		// Pe = END_PUNCTUATION
		// Pi = INITIAL_QUOTE_PUNCTUATION
		// Pf = FINAL_QUOTE_PUNCTUATION
		// Po = OTHER_PUNCTUATION
		// Ps = START_PUNCTUATION
		case Character.CONNECTOR_PUNCTUATION:
		case Character.DASH_PUNCTUATION:
		case Character.END_PUNCTUATION:
		case Character.INITIAL_QUOTE_PUNCTUATION:
		case Character.FINAL_QUOTE_PUNCTUATION:
		case Character.OTHER_PUNCTUATION:
		case Character.START_PUNCTUATION:
			theCurrentState = States.P0;
			return Signal.switch_now;
		// Otherwise, jump to the unknown state
		default:
			theCurrentState = States.U0;
			return Signal.nop;
		}
	}
	
	/**
	 * The automaton is in the state where it has encountered an apostrophe
	 * or a dash and is composed of several letters.
	 * If we get a letter, we continue the word, otherwise we softly fail.
	 * L3 -> L1 [ label = "Ll, Lu, Lm, Lo, Lt" ];
	 * L3 -> U0 [ label = "other/end_word" color="#ffA0A0" ];
	 * L3 -> O0 [ label = "Zl, Zp, Zs/end_word" color="#A0ffA0" ];
	 */
	private Signal feedCharL3(char c) {
		switch ( Character.getType(c) ) {
		// Jump to state L1 we encounter a letter
		// Ll = LOWERCASE_LETTER
		// Lu = UPPERCASE_LETTER
		// Lm = MODIFIER_LETTER
		// Lo = OTHER_LETTER
		// Lt = TITLECASE_LETTER
		case Character.LOWERCASE_LETTER:
		case Character.UPPERCASE_LETTER:
		case Character.MODIFIER_LETTER:
		case Character.OTHER_LETTER:
		case Character.TITLECASE_LETTER:
			theCurrentState = States.L1;
			return Signal.nop;
		// Jump to the out of word state if we encounter some space, but 
		// exclude the last char from the word.
		// Zl = LINE_SEPARATOR
		// Zp = PARAGRAPH_SEPARATOR
		// Zs = SPACE_SEPARATOR
		case Character.LINE_SEPARATOR:
		case Character.PARAGRAPH_SEPARATOR:
		case Character.SPACE_SEPARATOR:
			theCurrentState = States.O0;
			return Signal.end_word_prev;
		// Special case if we get a number char
		// Nd = DECIMAL_DIGIT_NUMBER
		// Nl = LETTER_NUMBER
		// No = OTHER_NUMBER
		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.LETTER_NUMBER:
		case Character.OTHER_NUMBER:
			theCurrentState = States.L6;
			return Signal.nop;
		// Punctuation keep coming
		// Pc = CONNECTOR_PUNCTUATION
		// Pd = DASH_PUNCTUATION
		// Pe = END_PUNCTUATION
		// Pi = INITIAL_QUOTE_PUNCTUATION
		// Pf = FINAL_QUOTE_PUNCTUATION
		// Po = OTHER_PUNCTUATION
		// Ps = START_PUNCTUATION
		case Character.CONNECTOR_PUNCTUATION:
		case Character.DASH_PUNCTUATION:
		case Character.END_PUNCTUATION:
		case Character.INITIAL_QUOTE_PUNCTUATION:
		case Character.FINAL_QUOTE_PUNCTUATION:
		case Character.OTHER_PUNCTUATION:
		case Character.START_PUNCTUATION:
			theCurrentState = States.P0;
			return Signal.switch_now;
		// Otherwise, jump to the unknown state and move the last char
		// in the newly constructing word
		default:
			theCurrentState = States.U0;
			return Signal.switch_word_prev;
		}
	}
		
	/**
	 * The automaton is in a transitional state from one char to two or more
	 * ones. Keep going.
	 * L4 -> L1 [ label = "Ll, Lu, Lm, Lo, Lt" ];
	 * L4 -> O0 [ label = "Zl, Zp, Zs/split_words_-1" ];
	 * L4 -> U0 [ label = "other/split_words_-1" ];
	 */
	private Signal feedCharL4(char c) {
		switch ( Character.getType(c) ) {
		// Jump to state L1 we encounter a letter
		// Ll = LOWERCASE_LETTER
		// Lu = UPPERCASE_LETTER
		// Lm = MODIFIER_LETTER
		// Lo = OTHER_LETTER
		// Lt = TITLECASE_LETTER
		case Character.LOWERCASE_LETTER:
		case Character.UPPERCASE_LETTER:
		case Character.MODIFIER_LETTER:
		case Character.OTHER_LETTER:
		case Character.TITLECASE_LETTER:
			theCurrentState = States.L1;
			return Signal.nop;
		// Jump to the out of word state if we encounter some space.
		// Zl = LINE_SEPARATOR
		// Zp = PARAGRAPH_SEPARATOR
		// Zs = SPACE_SEPARATOR
		case Character.LINE_SEPARATOR:
		case Character.PARAGRAPH_SEPARATOR:
		case Character.SPACE_SEPARATOR:
			theCurrentState = States.O0;
			return Signal.end_word;
		// Special case if we get a number char
		// Nd = DECIMAL_DIGIT_NUMBER
		// Nl = LETTER_NUMBER
		// No = OTHER_NUMBER
		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.LETTER_NUMBER:
		case Character.OTHER_NUMBER:
			theCurrentState = States.L6;
			return Signal.nop;
		// Punctuation keep coming
		// Pc = CONNECTOR_PUNCTUATION
		// Pd = DASH_PUNCTUATION
		// Pe = END_PUNCTUATION
		// Pi = INITIAL_QUOTE_PUNCTUATION
		// Pf = FINAL_QUOTE_PUNCTUATION
		// Po = OTHER_PUNCTUATION
		// Ps = START_PUNCTUATION
		case Character.CONNECTOR_PUNCTUATION:
		case Character.DASH_PUNCTUATION:
		case Character.END_PUNCTUATION:
		case Character.INITIAL_QUOTE_PUNCTUATION:
		case Character.FINAL_QUOTE_PUNCTUATION:
		case Character.OTHER_PUNCTUATION:
		case Character.START_PUNCTUATION:
			theCurrentState = States.P0;
			return Signal.switch_now;			
		// Otherwise, jump to the unknown state
		default:
			theCurrentState = States.U0;
			return Signal.switch_word;
		}
	}
	
	/**
	 * Found something like "qu" (most likely ?u), and checking for 
	 * contractions like qu'
	 * L5 -> L2 [ label = "U+0027, U+02BC" ];
	 * L5 -> L1 [ label = "Ll, Lu, Lm, Lo, Lt" ];
	 * L5 -> O0 [ label = "Zl, Zp, Zs/end_word" ];
	 * L5 -> U0 [ label = "other/switch_word" ];
	 */
	private Signal feedCharL5(char c) {
		// First check the particular case of the apostrophes
		Character cApostrophe1 = new Character( (char) 0x0027);
		Character cApostrophe2 = new Character( (char) 0x02BC);
		if ( cApostrophe1.equals(c) || cApostrophe2.equals(c) ) {
			theCurrentState = States.L2;
			return Signal.nop;
		} else {
			// Back to unicode classes
			switch ( Character.getType(c) ) {
			// Jump to state L1 we encounter a letter
			// Ll = LOWERCASE_LETTER
			// Lu = UPPERCASE_LETTER
			// Lm = MODIFIER_LETTER
			// Lo = OTHER_LETTER
			// Lt = TITLECASE_LETTER
			case Character.LOWERCASE_LETTER:
			case Character.UPPERCASE_LETTER:
			case Character.MODIFIER_LETTER:
			case Character.OTHER_LETTER:
			case Character.TITLECASE_LETTER:
				theCurrentState = States.L1;
				return Signal.nop;
			// Jump to the out of word state if we encounter some space.
			// Zl = LINE_SEPARATOR
			// Zp = PARAGRAPH_SEPARATOR
			// Zs = SPACE_SEPARATOR
			case Character.LINE_SEPARATOR:
			case Character.PARAGRAPH_SEPARATOR:
			case Character.SPACE_SEPARATOR:
				theCurrentState = States.O0;
				return Signal.end_word;
			// Punctuation keep coming
			// Pc = CONNECTOR_PUNCTUATION
			// Pd = DASH_PUNCTUATION
			// Pe = END_PUNCTUATION
			// Pi = INITIAL_QUOTE_PUNCTUATION
			// Pf = FINAL_QUOTE_PUNCTUATION
			// Po = OTHER_PUNCTUATION
			// Ps = START_PUNCTUATION
			case Character.CONNECTOR_PUNCTUATION:
			case Character.DASH_PUNCTUATION:
			case Character.END_PUNCTUATION:
			case Character.INITIAL_QUOTE_PUNCTUATION:
			case Character.FINAL_QUOTE_PUNCTUATION:
			case Character.OTHER_PUNCTUATION:
			case Character.START_PUNCTUATION:
				theCurrentState = States.P0;
				return Signal.switch_now;
			// Otherwise, jump to the unknown state
			default:
				theCurrentState = States.U0;
				return Signal.switch_word;
			}
		}
	}
	
	/**
	 * We are in presence of a number in a word separated by a dash.
	 * L6 -> L6 [ label = "Nd, Nl, No" ];
	 * L6 -> L1 [ label = "Ll, Lu, Lm, Lo, Lt" ];
	 * L6 -> O0 [ label = "Zl, Zp, Zs/end_word" ];
	 * L6 -> U0 [ label = "other/switch_word" ];
	 */
	private Signal feedCharL6(char c) {
		switch ( Character.getType(c) ) {
		// Keep going if we get a number char
		// Nd = DECIMAL_DIGIT_NUMBER
		// Nl = LETTER_NUMBER
		// No = OTHER_NUMBER
		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.LETTER_NUMBER:
		case Character.OTHER_NUMBER:
			theCurrentState = States.L6;
			return Signal.nop;
		// Jump to state L1 we encounter a letter
		// Ll = LOWERCASE_LETTER
		// Lu = UPPERCASE_LETTER
		// Lm = MODIFIER_LETTER
		// Lo = OTHER_LETTER
		// Lt = TITLECASE_LETTER
		case Character.LOWERCASE_LETTER:
		case Character.UPPERCASE_LETTER:
		case Character.MODIFIER_LETTER:
		case Character.OTHER_LETTER:
		case Character.TITLECASE_LETTER:
			theCurrentState = States.L1;
			return Signal.nop;
		// If we encounter a space O0
		// Zl = LINE_SEPARATOR
		// Zp = PARAGRAPH_SEPARATOR
		// Zs = SPACE_SEPARATOR
		case Character.LINE_SEPARATOR:
		case Character.PARAGRAPH_SEPARATOR:
		case Character.SPACE_SEPARATOR:
			theCurrentState = States.O0;
			return Signal.end_word;
		// Punctuation keep coming
		// Pc = CONNECTOR_PUNCTUATION
		// Pd = DASH_PUNCTUATION
		// Pe = END_PUNCTUATION
		// Pi = INITIAL_QUOTE_PUNCTUATION
		// Pf = FINAL_QUOTE_PUNCTUATION
		// Po = OTHER_PUNCTUATION
		// Ps = START_PUNCTUATION
		case Character.CONNECTOR_PUNCTUATION:
		case Character.DASH_PUNCTUATION:
		case Character.END_PUNCTUATION:
		case Character.INITIAL_QUOTE_PUNCTUATION:
		case Character.FINAL_QUOTE_PUNCTUATION:
		case Character.OTHER_PUNCTUATION:
		case Character.START_PUNCTUATION:
			theCurrentState = States.P0;
			return Signal.switch_now;
		// Otherwise, jump to the unknown state
		default:
			theCurrentState = States.U0;
			return Signal.switch_word;
		}
	}
	
	// STATES HANDLING : NUMBERS -----------------------------------------------
	
	/**
	 * In this state we start a brand new word composed of numbers
	 * N0 -> N0 [ label = "Nd, Nl, No" ];
	 * N0 -> N1 [ label = "U+002E, U+002C" ];
	 * N0 -> N3 [ label = "Sc, U+0025" ];
	 * N0 -> O0 [ label = "Zl, Zp, Zs/end_word" ];
	 * N0 -> U0 [ label = "other/switch_word" ];
	 */
	private Signal feedCharN0(char c) {
		// First check the particular case of the decimal separators
		Character cSep1    = new Character( (char) 0x002E);
		Character cSep2    = new Character( (char) 0x002C);
		Character cPercent = new Character( (char) 0x0025);
		if ( cSep1.equals(c) || cSep2.equals(c) ) {
			theCurrentState = States.N1;
			return Signal.nop;
		// As well as the percentage sign
		} else if ( cPercent.equals(c) ) {
			theCurrentState = States.N3;
			return Signal.nop;
		} else {
			// Back to unicode classes
			switch ( Character.getType(c) ) {
			// Keep going if we get a number char
			// Nd = DECIMAL_DIGIT_NUMBER
			// Nl = LETTER_NUMBER
			// No = OTHER_NUMBER
			case Character.DECIMAL_DIGIT_NUMBER:
			case Character.LETTER_NUMBER:
			case Character.OTHER_NUMBER:
				theCurrentState = States.N0;
				return Signal.nop;
			// If we encounter a currency value or a %, jump to N3
			// Sc = CURRENCY_SYMBOL
			case Character.CURRENCY_SYMBOL:
				theCurrentState = States.N3;
				return Signal.nop;
			// If we encounter a space, that's the end of the word
			// Zl = LINE_SEPARATOR
			// Zp = PARAGRAPH_SEPARATOR
			// Zs = SPACE_SEPARATOR
			case Character.LINE_SEPARATOR:
			case Character.PARAGRAPH_SEPARATOR:
			case Character.SPACE_SEPARATOR:
				theCurrentState = States.O0;
				return Signal.end_word;
			// Punctuation keep coming
			// Pc = CONNECTOR_PUNCTUATION
			// Pd = DASH_PUNCTUATION
			// Pe = END_PUNCTUATION
			// Pi = INITIAL_QUOTE_PUNCTUATION
			// Pf = FINAL_QUOTE_PUNCTUATION
			// Po = OTHER_PUNCTUATION
			// Ps = START_PUNCTUATION
			case Character.CONNECTOR_PUNCTUATION:
			case Character.DASH_PUNCTUATION:
			case Character.END_PUNCTUATION:
			case Character.INITIAL_QUOTE_PUNCTUATION:
			case Character.FINAL_QUOTE_PUNCTUATION:
			case Character.OTHER_PUNCTUATION:
			case Character.START_PUNCTUATION:
				theCurrentState = States.P0;
				return Signal.switch_now;
			// Otherwise, jump to the unknown state
			default:
				theCurrentState = States.U0;
				return Signal.switch_word;
			}
		}
	}
	
	/**
	 * We started a decimal number.
	 * N1 -> N2 [ label = "Nd, Nl, No" ];
	 * N1 -> O0 [ label = "Zl, Zp, Zs/split_words_-1" ];
	 * N1 -> U0 [ label = "other/split_words_-1" ];
	 */
	private Signal feedCharN1(char c) {
		switch ( Character.getType(c) ) {
		// Keep going if we get a number char
		// Nd = DECIMAL_DIGIT_NUMBER
		// Nl = LETTER_NUMBER
		// No = OTHER_NUMBER
		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.LETTER_NUMBER:
		case Character.OTHER_NUMBER:
			theCurrentState = States.N2;
			return Signal.nop;
		// If we encounter a space, that's the end of the word, so the 
		// separator was a punctuation, not a decimal separator
		// Zl = LINE_SEPARATOR
		// Zp = PARAGRAPH_SEPARATOR
		// Zs = SPACE_SEPARATOR
		case Character.LINE_SEPARATOR:
		case Character.PARAGRAPH_SEPARATOR:
		case Character.SPACE_SEPARATOR:
			theCurrentState = States.O0;
			return Signal.switch_word_prev;
		// Punctuation keep coming
		// Pc = CONNECTOR_PUNCTUATION
		// Pd = DASH_PUNCTUATION
		// Pe = END_PUNCTUATION
		// Pi = INITIAL_QUOTE_PUNCTUATION
		// Pf = FINAL_QUOTE_PUNCTUATION
		// Po = OTHER_PUNCTUATION
		// Ps = START_PUNCTUATION
		case Character.CONNECTOR_PUNCTUATION:
		case Character.DASH_PUNCTUATION:
		case Character.END_PUNCTUATION:
		case Character.INITIAL_QUOTE_PUNCTUATION:
		case Character.FINAL_QUOTE_PUNCTUATION:
		case Character.OTHER_PUNCTUATION:
		case Character.START_PUNCTUATION:
			theCurrentState = States.P0;
			return Signal.switch_now;
		// Otherwise, jump to the unknown state
		default:
			theCurrentState = States.U0;
			return Signal.switch_word;
		}
	}
	
	/**
	 * We are in presence of a decimal number.
	 * N2 -> N2 [ label = "Nd, Nl, No" ];
	 * N2 -> N3 [ label = "Sc, U+0025" ];
	 * N2 -> N4 [ label = "Zl, Zp, Zs" ];
	 * N2 -> U0 [ label = "other/end_word" ];
	 */
	private Signal feedCharN2(char c) {
		// First check the particular case of the percentage sign
		Character cPercent = new Character( (char) 0x0025);
		 if ( cPercent.equals(c) ) {
			theCurrentState = States.N3;
			return Signal.nop;
		} else {
			// Back to unicode classes
			switch ( Character.getType(c) ) {
			// If we encounter a currency value or a %, jump to N3
			// Sc = CURRENCY_SYMBOL
			case Character.CURRENCY_SYMBOL:
				theCurrentState = States.N3;
				return Signal.nop;
			// Keep going if we get a number char
			// Nd = DECIMAL_DIGIT_NUMBER
			// Nl = LETTER_NUMBER
			// No = OTHER_NUMBER
			case Character.DECIMAL_DIGIT_NUMBER:
			case Character.LETTER_NUMBER:
			case Character.OTHER_NUMBER:
				theCurrentState = States.N2;
				return Signal.nop;
			// If we encounter a space, maybe we can have a currency then so
			// jump to N4
			// Zl = LINE_SEPARATOR
			// Zp = PARAGRAPH_SEPARATOR
			// Zs = SPACE_SEPARATOR
			case Character.LINE_SEPARATOR:
			case Character.PARAGRAPH_SEPARATOR:
			case Character.SPACE_SEPARATOR:
				theCurrentState = States.N4;
				return Signal.nop;
			// Punctuation keep coming
			// Pc = CONNECTOR_PUNCTUATION
			// Pd = DASH_PUNCTUATION
			// Pe = END_PUNCTUATION
			// Pi = INITIAL_QUOTE_PUNCTUATION
			// Pf = FINAL_QUOTE_PUNCTUATION
			// Po = OTHER_PUNCTUATION
			// Ps = START_PUNCTUATION
			case Character.CONNECTOR_PUNCTUATION:
			case Character.DASH_PUNCTUATION:
			case Character.END_PUNCTUATION:
			case Character.INITIAL_QUOTE_PUNCTUATION:
			case Character.FINAL_QUOTE_PUNCTUATION:
			case Character.OTHER_PUNCTUATION:
			case Character.START_PUNCTUATION:
				theCurrentState = States.P0;
				return Signal.switch_now;
			// Otherwise, jump to the unknown state
			default:
				theCurrentState = States.U0;
				return Signal.switch_word;
			}
		}
	}
	
	/**
	 * We have a complete number with currency. Whatever we get, we end this 
	 * word.
	 * N3 -> N0 [ label = "Nd, Nl, No" ];
	 * N3 -> O0 [ label = "Zl, Zp, Zs" ];
	 * N3 -> U0 [ label = "other" ];
	 */
	private Signal feedCharN3(char c) {
		switch ( Character.getType(c) ) {
		// Space : ends the word
		// Zl = LINE_SEPARATOR
		// Zp = PARAGRAPH_SEPARATOR
		// Zs = SPACE_SEPARATOR
		case Character.LINE_SEPARATOR:
		case Character.PARAGRAPH_SEPARATOR:
		case Character.SPACE_SEPARATOR:
			theCurrentState = States.O0;
			return Signal.end_word;
		// If we get a number, start a new number
		// Nd = DECIMAL_DIGIT_NUMBER
		// Nl = LETTER_NUMBER
		// No = OTHER_NUMBER
		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.LETTER_NUMBER:
		case Character.OTHER_NUMBER:
			theCurrentState = States.N0;
			return Signal.switch_word;
		// Jump to state L0 we encounter a letter
		// Ll = LOWERCASE_LETTER
		// Lu = UPPERCASE_LETTER
		// Lm = MODIFIER_LETTER
		// Lo = OTHER_LETTER
		// Lt = TITLECASE_LETTER
		case Character.LOWERCASE_LETTER:
		case Character.UPPERCASE_LETTER:
		case Character.MODIFIER_LETTER:
		case Character.OTHER_LETTER:
		case Character.TITLECASE_LETTER:
			theCurrentState = States.L0;
			return Signal.switch_word;
		// Punctuation keep coming
		// Pc = CONNECTOR_PUNCTUATION
		// Pd = DASH_PUNCTUATION
		// Pe = END_PUNCTUATION
		// Pi = INITIAL_QUOTE_PUNCTUATION
		// Pf = FINAL_QUOTE_PUNCTUATION
		// Po = OTHER_PUNCTUATION
		// Ps = START_PUNCTUATION
		case Character.CONNECTOR_PUNCTUATION:
		case Character.DASH_PUNCTUATION:
		case Character.END_PUNCTUATION:
		case Character.INITIAL_QUOTE_PUNCTUATION:
		case Character.FINAL_QUOTE_PUNCTUATION:
		case Character.OTHER_PUNCTUATION:
		case Character.START_PUNCTUATION:
			theCurrentState = States.P0;
			return Signal.switch_now;
		// Otherwise, jump to the unknown state
		default:
			theCurrentState = States.U0;
			return Signal.switch_word;
		}
	}
	
	/**
	 * We have encountered a space and were waiting for a currency symbol. We
	 * may obtain it and finalize or number, or get something else and then
	 * end the word before the space.
	 * N4 -> N3 [ label = "Sc, U+0025" ];
	 * N4 -> O0 [ label = "Zl, Zp, Zs/end_words_-1" ];
	 * N4 -> U0 [ label = "other/end_words_-1" ];
	 */
	private Signal feedCharN4(char c) {
		// First check the particular case of the percentage sign
		Character cPercent = new Character( (char) 0x0025);
		 if ( cPercent.equals(c) ) {
			theCurrentState = States.N3;
			return Signal.nop;
		} else {
			// Back to unicode classes
			switch ( Character.getType(c) ) {
			// If we encounter a currency value or a %, jump to N3
			// Sc = CURRENCY_SYMBOL
			case Character.CURRENCY_SYMBOL:
				theCurrentState = States.N3;
				return Signal.nop;
			// Space encountered... too bad, we must consider that we failed
			// the word must be ended before
			// Zl = LINE_SEPARATOR
			// Zp = PARAGRAPH_SEPARATOR
			// Zs = SPACE_SEPARATOR
			case Character.LINE_SEPARATOR:
			case Character.PARAGRAPH_SEPARATOR:
			case Character.SPACE_SEPARATOR:
				theCurrentState = States.O0;
				return Signal.end_word_prev;
			// If we get a number, start a new number
			// Nd = DECIMAL_DIGIT_NUMBER
			// Nl = LETTER_NUMBER
			// No = OTHER_NUMBER
			case Character.DECIMAL_DIGIT_NUMBER:
			case Character.LETTER_NUMBER:
			case Character.OTHER_NUMBER:
				theCurrentState = States.N0;
				return Signal.switch_word_prev;
			// Jump to state L0 we encounter a letter
			// Ll = LOWERCASE_LETTER
			// Lu = UPPERCASE_LETTER
			// Lm = MODIFIER_LETTER
			// Lo = OTHER_LETTER
			// Lt = TITLECASE_LETTER
			case Character.LOWERCASE_LETTER:
			case Character.UPPERCASE_LETTER:
			case Character.MODIFIER_LETTER:
			case Character.OTHER_LETTER:
			case Character.TITLECASE_LETTER:
				theCurrentState = States.L0;
				return Signal.switch_word_prev;
			// Punctuation keep coming
			// Pc = CONNECTOR_PUNCTUATION
			// Pd = DASH_PUNCTUATION
			// Pe = END_PUNCTUATION
			// Pi = INITIAL_QUOTE_PUNCTUATION
			// Pf = FINAL_QUOTE_PUNCTUATION
			// Po = OTHER_PUNCTUATION
			// Ps = START_PUNCTUATION
			case Character.CONNECTOR_PUNCTUATION:
			case Character.DASH_PUNCTUATION:
			case Character.END_PUNCTUATION:
			case Character.INITIAL_QUOTE_PUNCTUATION:
			case Character.FINAL_QUOTE_PUNCTUATION:
			case Character.OTHER_PUNCTUATION:
			case Character.START_PUNCTUATION:
				theCurrentState = States.P0;
				return Signal.switch_now;
			// Otherwise, jump to the unknown state
			default:
				theCurrentState = States.U0;
				return Signal.switch_word_prev;
			}
		}
	}
}
