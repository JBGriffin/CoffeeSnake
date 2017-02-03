
package havabol;

/**
 *
 * STControl extends STEntry and adds a type
 * 
 * @author Justin Hooge
 */
public class STControl extends STEntry {
    
    //String szKey; //"if" "endif" etc.
    
    //int iPrimClassif; //def, enddef, if, endif, else, for, etc.
    
    int iType; //flow, end, declare, int, void, etc.
    
    public STControl(String symbol, int iPrimClassif, int iType) {
        
        super(symbol, iPrimClassif);
        
        this.iType = iType;
        
    }
    
    
}
