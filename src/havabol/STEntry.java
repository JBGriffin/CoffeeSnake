
package havabol;

/**
 *
 * The entry contains the symbol itself and the primary classification,
 * such as Operator, Operand, Control, etc.
 * 
 * NOTE: This is the super class to STControl, STFunction, and STIdentifier
 * 
 * @author Justin Hooge
 */
public class STEntry {
    
    String symbol;
    
    int iPrimClassif;
    
    public STEntry(String symbol, int iPrimClassif) {
        
        this.symbol = symbol;
        
        this.iPrimClassif = iPrimClassif;
        
        
    }
    
    
}
