

/* First created by JCasGen Tue Jun 29 16:52:23 CEST 2010 */
package tddts.preprocessing.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Jun 29 16:52:23 CEST 2010
 * XML source: /home/grdscarabe/WORKSPACES_ECLIPSE/these/TDDTS-Preprocessing/bin/ts-stopwords.xml
 * @generated */
public class NotStopWord extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(NotStopWord.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected NotStopWord() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public NotStopWord(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public NotStopWord(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public NotStopWord(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
}

    