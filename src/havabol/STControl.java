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
public class STControl extends STEntry {
    
    //String szKey; //"if" "endif" etc.
    
    //int iPrimClassif; //def, enddef, if, endif, else, for, etc.
    
    int iType; //flow, end, declare, int, void, etc.
    
    public STControl(String symbol, int iPrimClassif, int iType) {
        
        super(symbol, iPrimClassif);
        
        this.iType = iType;
        
    }
    
    
}
