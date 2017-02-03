
package havabol;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * STFunction extends STEntry and adds fields iType (return type)
 * , iStruct (BuiltIn or user function), and String array of arguments
 * Push test
 * @author Justin Hooge
 */
public class STFunction extends STEntry {
    
    //String szKey;
    
    //int iPrimClassif;
    
    int iType;
    
    ArrayList<String> ArgsM = new ArrayList<>();
    
    //need to probably changes string... to string[]
    public STFunction(String symbol, int iPrimClassif, int iType, int iStruct, String... szArgsM) {
        
        super(symbol, iPrimClassif);
        
        this.iType = iType; // add return type
        
        this.ArgsM.addAll(Arrays.asList(szArgsM)); //add all args to arraylist
        
    }
    
}
