package tddts.experiments;

/**
 * This class is used to store the context of a particular punctuation
 */
public class PunctuationContext {

	private char[] theLeftContext;
	private Integer theLeftLength;
	private char[] theRightContext;
	private Integer theRightLength;
	private char thePunctuation;
	
	/**
	 * Constructor, defines the size of the context
	 * 
	 *  @param left the number of elements in the left context
	 *  @param right the number of elements in the right context
	 */
	public PunctuationContext(Integer left, Integer right) {
		// Save the size and initialize the context
		theLeftLength   = left;
		theLeftContext  = new char[left];
		theRightLength  = right;
		theRightContext = new char[right];
	}
	
	/**
	 * Extract the context of the punctuation in position i in the String passed
	 * in parameter.
	 * 
	 * @param i the index where the punctuation is in the string
	 * @param str the string
	 */
	public void setContext(Integer i, String str) {
		// Save the punctuation
		char[] tmp = new char[1];
		str.getChars(i, i+1, tmp, 0);
		thePunctuation = tmp[0];
		// Save the left context
		Integer begin = i-theLeftLength;
		Integer offset = 0;
		if ( begin < 0 ) {
			offset = Math.abs(begin);
			begin = 0;
		}
		str.getChars(begin, i, theLeftContext, offset);
		for(int n=0 ; n<offset ; n++)
			theLeftContext[n] = 0x0;
		// Save the right context
		Integer end = i+1+theRightLength;
		if ( end >= str.length() ) {
			end = str.length();
		}
		str.getChars(i+1, end, theRightContext, 0);
		for(int n=end ; n<theRightLength ; n++)
			theRightContext[n] = 0x0;
	}
	
	/**
	 * This method creates a line record to be saved in a CSV file and which
	 * consists of the following :
	 * ... ; char-n ; class-n ; ... ; char-1 ; class-1 ; PUNC ; ...
	 * 
	 * @return
	 */
	public String[] toCSVRecord() {
		String[] cells = new String[2*(theLeftLength+1+theRightLength)];
		Integer c = 0;
		// Export the left context
		for(int i=0 ; i<theLeftContext.length ; i++) {
			if ( theLeftContext[i] == 0x0 ) {
				cells[c] = "null";
				cells[c+1] = "null";
			} else {
				cells[c] = theLeftContext[i]+"";
				cells[c+1] = toCharType(theLeftContext[i]);
			}
			c += 2;
		}
		// Export the punctuation
		cells[c] = thePunctuation+"";
		cells[c+1] = toCharType(thePunctuation);
		c += 2;
		// Export the right context
		for(int i=0 ; i<theRightContext.length ; i++) {
			if ( theRightContext[i] == 0x0 ) {
				cells[c] = "null";
				cells[c+1] = "null";
			} else {
				cells[c] = theRightContext[i]+"";
				cells[c+1] = toCharType(theRightContext[i]);
			}
			c += 2;
		}
		// return the result
		return cells;
	}
	
	/**
	 * This method returns, as a string, the unicode class of the character.
	 * 
	 * @param c the character we are interested in the type
	 * @return
	 */
	public static String toCharType(char c) {
		switch ( Character.getType(c) ) {
		case Character.COMBINING_SPACING_MARK:
			return "COMBINING_SPACING_MARK";
		case Character.CONNECTOR_PUNCTUATION:
			return "CONNECTOR_PUNCTUATION";
		case Character.CONTROL:
			return "CONTROL";
		case Character.CURRENCY_SYMBOL:
			return "CURRENCY_SYMBOL";
		case Character.DASH_PUNCTUATION:
			return "DASH_PUNCTUATION";
		case Character.DECIMAL_DIGIT_NUMBER:
			return "DECIMAL_DIGIT_NUMBER";
		case Character.ENCLOSING_MARK:
			return "ENCLOSING_MARK";
		case Character.END_PUNCTUATION:
			return "END_PUNCTUATION";
		case Character.FINAL_QUOTE_PUNCTUATION:
			return "FINAL_QUOTE_PUNCTUATION";
		case Character.FORMAT:
			return "FORMAT";
		case Character.INITIAL_QUOTE_PUNCTUATION:
			return "INITIAL_QUOTE_PUNCTUATION";
		case Character.LETTER_NUMBER:
			return "LETTER_NUMBER";
		case Character.LINE_SEPARATOR:
			return "LINE_SEPARATOR";
		case Character.LOWERCASE_LETTER:
			return "LOWERCASE_LETTER";
		case Character.MATH_SYMBOL:
			return "MATH_SYMBOL";
		case Character.MODIFIER_LETTER:
			return "MODIFIER_LETTER";
		case Character.MODIFIER_SYMBOL:
			return "MODIFIER_SYMBOL";
		case Character.NON_SPACING_MARK:
			return "NON_SPACING_MARK";
		case Character.OTHER_LETTER:
			return "OTHER_LETTER";
		case Character.OTHER_NUMBER:
			return "OTHER_NUMBER";
		case Character.OTHER_PUNCTUATION:
			return "OTHER_PUNCTUATION";
		case Character.OTHER_SYMBOL:
			return "OTHER_SYMBOL";
		case Character.PARAGRAPH_SEPARATOR:
			return "PARAGRAPH_SEPARATOR";
		case Character.PRIVATE_USE:
			return "PRIVATE_USE";
		case Character.SPACE_SEPARATOR:
			return "SPACE_SEPARATOR";
		case Character.START_PUNCTUATION:
			return "START_PUNCTUATION";
		case Character.SURROGATE:
			return "SURROGATE";
		case Character.TITLECASE_LETTER:
			return "TITLECASE_LETTER";
		case Character.UNASSIGNED:
			return "UNASSIGNED";
		case Character.UPPERCASE_LETTER:
			return "UPPERCASE_LETTER";
		}
		return null;
	}
}
