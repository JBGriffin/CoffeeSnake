
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
    
    ArrayList<Object> argsM = new ArrayList<>();
    
    //need to probably changes string... to string[]
    public STFunction(String symbol, int iPrimClassif, int iType, int iStruct, Object[] argsM) {
        
        super(symbol, iPrimClassif);
        this.iType = iType; // add return type
        
        for (Object o : argsM) //add all args to arraylist
        {
            
            if (o instanceof Integer)
            {
                this.argsM.add((int) o);     
            } else if (o instanceof String)
            {
                this.argsM.add((String) o);
            } else if (o instanceof Float)
            {
                this.argsM.add((Float) o); 
            } else { 
                this.argsM.add( o);
            }
            
        }
    }
    
}
