/**
 * This FrenchUnicodeTokenizer component takes advantage of the
 * unicode classes of characters to split the text in words.
 * 
 * This file is licensed to you under the Apache License, 
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

// UIMA dependencies
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
// Transducer dependency
import tddts.preprocessing.tokenizer.FrenchTokenizerAutomaton.Signal;

/**
 * This class implements a tokenizer using a particular kind of transducer to
 * select the borders of words.
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 */
public class FrenchUnicodeTokenizer extends CasAnnotator_ImplBase {

	/** Name of the types to use for annotation */
	public static final String TOKEN_NAME    = "org.apache.uima.TokenAnnotation";

	/** Types of each kind of annotation */
	private Type tokenType;
	
	/** List of views to consider */
	private String[] sofaNames;

	/** The tokenizer automaton */
	private FrenchTokenizerAutomaton theTransducer;

	/**
	 * This method is called before any processing to prepare the types we need.
	 */
	@Override
	public void typeSystemInit(TypeSystem typeSystem)
	throws AnalysisEngineProcessException {
		super.typeSystemInit(typeSystem);
		// initialize CAS token type
		this.tokenType    = typeSystem.getType(TOKEN_NAME);
	}

	/**
	 * Initialize the component.
	 * It collects the names of the sofa which have to be tokenized, and prepare
	 * the automaton.
	 */
	@Override
	public void initialize(UimaContext context)
	throws ResourceInitializationException {
		super.initialize(context);
		// Configure the list of sofas 
		this.sofaNames = 
			(String[]) getContext().getConfigParameterValue("SofaNames");
		if (this.sofaNames == null || this.sofaNames.length <= 0)
			this.sofaNames = new String[]{ "_InitialView" };
		// Initialize the automaton
		theTransducer = new FrenchTokenizerAutomaton();
	}

	/**
	 * Process the content : split the text in words
	 * 
	 * @see org.apache.uima.analysis_component.CasAnnotator_ImplBase#process(org.apache.uima.cas.CAS)
	 */
	public void process(CAS aCas) throws AnalysisEngineProcessException {
		// Process each view specified
		for (int i = 0; i < this.sofaNames.length; i++) {
			CAS currView = aCas.getView( sofaNames[i] );
			doTokenization(currView);
		}
	}
	
	// PRIVATE METHODS ---------------------------------------------------------
	
	/**
	 * This method drives the automaton execution over the stream of chars.
	 */
	private void doTokenization(CAS view) {
		// Load the content of the SOFA
		char[] textContent = view.getDocumentText().toCharArray();
		// Initialize the execution
		int begin = -1;
		theTransducer.reset();
		// Run over the chars
		for(int i=0 ; i<textContent.length ; i++) {
			Signal s = theTransducer.feedChar( textContent[i] );
			switch(s) {
			case start_word:
				begin = i;
				break;
			case end_word:
				addWord(view, begin, i);
				begin = -1;
				break;
			case end_word_prev:
				addWord(view, begin, i-1);
				begin = -1;
				break;
			case switch_word:
				addWord(view, begin, i);
				begin = i;
				break;
			case switch_word_prev:
				addWord(view, begin, i-1);
				begin = i;
				break;
			case cancel_word:
				begin = -1;
				break;
			}
		}
		// Add the last one
		if (begin != -1) {
			addWord(view, begin, textContent.length-1);
		}
	}
	
	/**
	 * Create a token word annotation in the CAS using startPos and endPos.
	 * 
	 * @param view the CAS where the annotation is added
	 * @param startPos annotation start position
	 * @param endPos annotation end position
	 */
	private void addWord(CAS view, int startPos, int endPos) {
		AnnotationFS annot = view.createAnnotation(tokenType, startPos, endPos);
		view.addFsToIndexes(annot);
	}
}
