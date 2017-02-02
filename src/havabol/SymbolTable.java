package havabol;

import java.util.HashMap;

/**
 * SymbolTable contains all meta meaning behind the language's definition.
 */
public class SymbolTable {
    
    
    private HashMap<String,STEntry> ht; 
    
	public SymbolTable()
	{
	
            this.ht = new HashMap<>();
            
	}
        
        public STEntry getSymbol(String symbol) {
            
            return this.ht.get(symbol);
            
        }
        
        public void putSymbol(String symbol, STEntry entry){
            
            this.ht.put(symbol, entry);
            
        }
        
        /**
         * 
         * initGlobal initializes the hash table ht to contain all possible
         * 
         *  TO DO: Add builtin functions, and if there are identifiers, 
         *      add those too.
         * 
         */
        private void initGlobal() {
            
            
            //STControls
            this.ht.put("if", new STControl("if", Token.CONTROL, Token.FLOW));
            this.ht.put("endif", new STControl("endif", Token.CONTROL, Token.END));
            this.ht.put("else", new STControl("else", Token.CONTROL, Token.END));
            this.ht.put("def", new STControl("def", Token.CONTROL, Token.FLOW));
            this.ht.put("enddef", new STControl("endder", Token.CONTROL, Token.END));
            this.ht.put("for", new STControl("for", Token.CONTROL, Token.FLOW));
            this.ht.put("endfor", new STControl("endfor", Token.CONTROL, Token.END));
            this.ht.put("while", new STControl("while", Token.CONTROL, Token.FLOW));
            this.ht.put("endwhile", new STControl("endwhile", Token.CONTROL, Token.END));
            this.ht.put("Int", new STControl("Int", Token.CONTROL, Token.DECLARE));
            this.ht.put("Float", new STControl("Float", Token.CONTROL, Token.DECLARE));
            this.ht.put("Date", new STControl("Date", Token.CONTROL, Token.DECLARE));
            this.ht.put("String", new STControl("String", Token.CONTROL, Token.DECLARE));
            this.ht.put("Bool", new STControl("Bool", Token.CONTROL, Token.DECLARE));
            
            
            //STEntries
            this.ht.put("and", new STEntry("and", Token.OPERATOR));
            this.ht.put("or", new STEntry("or", Token.OPERATOR));
            this.ht.put("not", new STEntry("not", Token.OPERATOR));
            this.ht.put("in", new STEntry("in", Token.OPERATOR));
            this.ht.put("notin", new STEntry("notin", Token.OPERATOR));
            
            
            //STFunctions ???? HOW DO WE DO THE ARGS
            //this.ht.put("print", new STFunction("print", Token.FUNCTION
            //        , Token.VOID, Token.BUILTIN, ""));
            
            //"LENGTH"
            //"MAXLENGTH"
            //"SPACES"        /*   TO DO    */
            //"ELEM"
            //"MAXELEM"
            
            //STIdentifiers
            
            
        }
        
}
