/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package havabol;

/**
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
