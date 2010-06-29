

/* First created by JCasGen Tue Sep 08 17:14:39 CEST 2009 */
package tddts.preprocessing.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** The stem of a token word.
 * Updated by JCasGen Tue Sep 08 17:16:14 CEST 2009
 * XML source: /home/grdscarabe/TRAVAIL_FAC/WORKSPACE-BZR/piithie/Piithie/desc/AE/aeUIMASnowballStemmer.xml
 * @generated */
public class Stem extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Stem.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Stem() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Stem(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Stem(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Stem(JCas jcas, int begin, int end) {
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
     
 
    
  //*--------------*
  //* Feature: stem

  /** getter for stem - gets The value of the stem.
   * @generated */
  public String getStem() {
    if (Stem_Type.featOkTst && ((Stem_Type)jcasType).casFeat_stem == null)
      jcasType.jcas.throwFeatMissing("stem", "net.atlanstic.lina.snowball.Stem");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Stem_Type)jcasType).casFeatCode_stem);}
    
  /** setter for stem - sets The value of the stem. 
   * @generated */
  public void setStem(String v) {
    if (Stem_Type.featOkTst && ((Stem_Type)jcasType).casFeat_stem == null)
      jcasType.jcas.throwFeatMissing("stem", "net.atlanstic.lina.snowball.Stem");
    jcasType.ll_cas.ll_setStringValue(addr, ((Stem_Type)jcasType).casFeatCode_stem, v);}    
  }

    