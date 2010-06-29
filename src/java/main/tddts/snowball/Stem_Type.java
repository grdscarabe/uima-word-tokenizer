
/* First created by JCasGen Tue Sep 08 17:14:39 CEST 2009 */
package lina.uima.snowball;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** The stem of a token word.
 * Updated by JCasGen Tue Sep 08 17:16:14 CEST 2009
 * @generated */
public class Stem_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Stem_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Stem_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Stem(addr, Stem_Type.this);
  			   Stem_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Stem(addr, Stem_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Stem.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("net.atlanstic.lina.snowball.Stem");
 
  /** @generated */
  final Feature casFeat_stem;
  /** @generated */
  final int     casFeatCode_stem;
  /** @generated */ 
  public String getStem(int addr) {
        if (featOkTst && casFeat_stem == null)
      jcas.throwFeatMissing("stem", "net.atlanstic.lina.snowball.Stem");
    return ll_cas.ll_getStringValue(addr, casFeatCode_stem);
  }
  /** @generated */    
  public void setStem(int addr, String v) {
        if (featOkTst && casFeat_stem == null)
      jcas.throwFeatMissing("stem", "net.atlanstic.lina.snowball.Stem");
    ll_cas.ll_setStringValue(addr, casFeatCode_stem, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Stem_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_stem = jcas.getRequiredFeatureDE(casType, "stem", "uima.cas.String", featOkTst);
    casFeatCode_stem  = (null == casFeat_stem) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_stem).getCode();

  }
}



    