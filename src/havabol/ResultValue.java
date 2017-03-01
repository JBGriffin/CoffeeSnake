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
public class ResultValue {
    
    public int type;             // Data type of result
    public String szValue;          // Data in string representation 
    public String structure;        // Primitive, fixed array, unbounded array
    public String terminatingStr;   // Used for end of lists of things (e.g.,
                                    // list of starter terminated by "endwhile"
    
    public ResultValue(String value) {
        
        this.szValue = value;

        switch(szValue)
        {
            case "INTEGER":
                this.type = Token.INTEGER;
                break;
            case "FLOAT":
                this.type = Token.FLOAT;
                break;
        }
        
    }
    
    
}
