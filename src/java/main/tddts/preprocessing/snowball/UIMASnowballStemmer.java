package tddts.preprocessing.snowball;

// UIMA related dependencies
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
// Snowball related dependencies
import org.tartarus.snowball.SnowballStemmer;
// Type dependencies
import tddts.preprocessing.types.Stem;

/**
 * This class implements an Analysis Engine to wrap snowball for UIMA.
 * 
 * @author Fabien B. Poulard <fabien.poulard@univ-nantes.fr>
 */
public class UIMASnowballStemmer extends JCasAnnotator_ImplBase {

	/** Token annotation to consider for stemming */
	private final static String PARAM_TYPE = "WordAnnotationType";
	private String theWordTypeStr;
	
	/** Language to choose for the current text */
	private final static String PARAM_LANG = "Language";
	private String theLanguage;

	/** The stemmer instance for the specified language */
	private SnowballStemmer theStemmer;
	
	/**
	 *  In the initialize method we get the parameters values and we prepare
	 *  the right stemmer for the specified language. If such a language is not
	 *  supporter we send an exception concerning the component configuration.
	 */
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		// Obtain the name of the word annotation type
		theWordTypeStr = (String) context.getConfigParameterValue(PARAM_TYPE);
		// Obtain the language and prepare the stemmer
		theLanguage      = (String) context.getConfigParameterValue(PARAM_LANG);
		String className =  "org.tartarus.snowball.ext." + 
			theLanguage.toLowerCase() + "Stemmer";
		try {
			Class<?> stemClass = Class.forName(className);
			theStemmer = (SnowballStemmer) stemClass.newInstance();
		} catch (ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		} catch (InstantiationException e) {
			// Cannot instanciate the stemmer
			throw new ResourceInitializationException(e);
		} catch (IllegalAccessException e) {
			// Cannot access the stemmer class
			throw new ResourceInitializationException(e);
		}
		// now we are ready to stem...
	}

	/**
	 * This method is the main one, it does all the processing of the different
	 * CAS. It first gets the word annotation type from the CAS type system, 
	 * and then browse those words to stem them with the prepared stemmer. 
	 */
	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		// Get the annotation type from the CAS type system, and check it exists
		Type mWordType = cas.getTypeSystem().getType(theWordTypeStr);
		if (  (mWordType == null) ) {
			String errmsg = 
				"The word type " + theWordTypeStr + " does not exist in the CAS" +
						"type system !";
			throw new AnalysisEngineProcessException(errmsg, new Object[]{mWordType});
		}
		// Now we browse for all those word annotations...
		AnnotationIndex<Annotation> idxWord = cas.getAnnotationIndex(mWordType);
		FSIterator<Annotation> itWord       = idxWord.iterator();
		while (itWord.hasNext()) {
			// Stem the current word
			Annotation mWord = (Annotation) itWord.next();
			theStemmer.setCurrent( mWord.getCoveredText() );
			theStemmer.stem(); // FIXME: the stem operation can be applied several times
			// and annotate the stem into the CAS
			Stem mStemAnnot = new Stem(cas);
			mStemAnnot.setBegin( mWord.getBegin() );
			mStemAnnot.setEnd( mWord.getEnd() );
			mStemAnnot.setStem( theStemmer.getCurrent() );
			mStemAnnot.addToIndexes();
		}
	}

}
