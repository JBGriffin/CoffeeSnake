
package havabol;

/**
 *
 * STIdentifiers extends STEntry and adds iDclType ('int' x),
 * iStruct (type of data structure like fixed array or unbound array),
 * iParmType - if it is a parameter, is it by reference of value
 * 
 * @author Justin Hooge
 */
public class STIdentifiers extends STEntry {
    
    int iDclType; //int, float, bool, date, string
    
    int iStruct; //primitive, fixed array, unbound array
    
    int iParmType; //not a parm, by refernece, or by value
    public STIdentifiers(String symbol, int iPrimClassif, int iDclType
            , int iStruct, int iParmType)
    {
        
        super(symbol, iPrimClassif);
        
        this.iDclType = iDclType;
        
        this.iStruct = iStruct;
        
        this.iParmType = iParmType;
        
    }
    
}
