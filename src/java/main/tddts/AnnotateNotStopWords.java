/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package lina.uima.bagofwords.utils;

// UIMA dependencies
import java.util.ArrayList;
import java.util.HashMap;

import lina.uima.bagofwords.types.NotStopWord;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.ConstraintFactory;
import org.apache.uima.cas.FSIntConstraint;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FSMatchConstraint;
import org.apache.uima.cas.FSTypeConstraint;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeaturePath;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

/**
 * This class is part of a UIMA component that annotate the tokens that are
 * not annotated as StopWord as NotStopWord. In other words it annotated not
 * stop words tokens.
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 */
public class AnnotateNotStopWords extends JCasAnnotator_ImplBase {

	/** Constants : name of the parameters in the descriptor */
	private static String PARAM_TOKEN_TYPE     = "TokenType";
	private static String PARAM_STOPWORD_TYPE  = "StopWordType";
	/** Configuration variables */
	private String theTokenTypeStr;
	private String theStopWordTypeStr;
	/** Various class variables */
	private Logger theLogger;
	
	// BELOW THE API IMPLEMENTATION --------------------------------------------
	
	/**
	 * This method configures the component with the values passed in parameters
	 */
	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		// Parent initialization
 		super.initialize(aContext);
 		// Logging system
 		theLogger = aContext.getLogger();
 		// The types
 		theTokenTypeStr =  
 			(String) aContext.getConfigParameterValue(PARAM_TOKEN_TYPE);
 		theStopWordTypeStr =  
 			(String) aContext.getConfigParameterValue(PARAM_STOPWORD_TYPE);
 		// Everything is ready
 		theLogger.log(Level.INFO, "BagOfWords component initialized");
	}
	
	/**
	 * Main method: process the documents looking for tokens not covered
	 * by a stop word annotation.
	 */
	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		// Communicate on the processing starting
		theLogger.log(Level.INFO, "AnnotateNotStopWords starts processing");
		// Retrieve types from the TypeSystem
		Type theTokenType =
				cas.getTypeSystem().getType( theTokenTypeStr );
		Type theStopWordType =
			cas.getTypeSystem().getType( theStopWordTypeStr );
		if  ( (theTokenType == null) || (theStopWordType == null))
			throw new AnalysisEngineProcessException(theTokenTypeStr + " or " +
					theStopWordTypeStr + " no such type in the Type System", null);
		// Browse the tokens
		AnnotationIndex idxToken = cas.getAnnotationIndex(theTokenType);
		FSIterator itToken       = idxToken.iterator();
		while (itToken.hasNext()) {
			Annotation token = (Annotation) itToken.next();
			if ( ! isStopWord(cas, token, theStopWordType) ) {
				// Add NotStopWord annotation
				NotStopWord a = new NotStopWord(cas);
				a.setBegin( token.getBegin() );
				a.setEnd( token.getEnd() );
				a.addToIndexes();
			}
		}
	}
	
	// Private Methods ---------------------------------------------------------
	
	/**
	 * This method checks if there is a StopWord annotation covered or covering
	 * the token annotation passed in parameter.
	 */
	private Boolean isStopWord(JCas jcas, Annotation token, Type StopWordType) {
	       // Use constraint factory
	       ConstraintFactory cf = jcas.getConstraintFactory();
	       // Constraint on annotation start
	       FSIntConstraint beginCons = cf.createIntConstraint();
	       beginCons.geq(token.getBegin()); // begin >=
	       Feature beginFeature = token.getType().getFeatureByBaseName("begin");
	       FeaturePath beginPath = jcas.createFeaturePath();
	       beginPath.addFeature(beginFeature);
	       FSMatchConstraint begin = cf.embedConstraint(beginPath, beginCons);
	       // Constraint on annotation ends
	       FSIntConstraint endCons = cf.createIntConstraint();
	       endCons.leq(token.getEnd()); // end <=
	       Feature endFeature = token.getType().getFeatureByBaseName("end");
	       FeaturePath endPath = jcas.createFeaturePath();
	       endPath.addFeature(endFeature);
	       FSMatchConstraint end = cf.embedConstraint(endPath, endCons);
	       // AND
	       FSMatchConstraint beginAndEnd = cf.and(begin, end);
	       // Iterator
	       FSIterator filteredIterator =
	              jcas.createFilteredIterator(
	            		  jcas.getAnnotationIndex(StopWordType).iterator(), 
	            		  beginAndEnd);
	       // Indicate if the annotation is covering a stop word
	       return filteredIterator.hasNext();
	}


}
