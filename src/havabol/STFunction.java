/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package havabol;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author wimbotexan23
 */
public class STFunction extends STEntry {
    
    //String szKey;
    
    //int iPrimClassif;
    
    int iType;
    
    ArrayList<String> ArgsM = new ArrayList<>();
    
    //need to probably changes string... to string[]
    public STFunction(String symbol, int iPrimClassif, int iType, int iStruct, String... szArgsM) {
        
        super(symbol, iPrimClassif);
        
        this.ArgsM.addAll(Arrays.asList(szArgsM)); //add all args to arraylist
        
    }
    
}
