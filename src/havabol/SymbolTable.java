package havabol;

import java.util.HashMap;

/**
 * SymbolTable contains all meta meaning behind the language's definition.
 */
public class SymbolTable {
    
    
    private HashMap<String,STEntry> ht; //Symbol Table [String:Entry]
    
	public SymbolTable()
	{
	
            this.ht = new HashMap<>();
            
            this.initGlobal();
                        
	}
        
        /**
         * 
         * getSymbol will check the hash table for symbol, if it exists
         * method will return Symbol.
         * 
         * @param symbol
         * @return Associated STEntry for symbol
         */
        public STEntry getSymbol(String symbol) {
            
            return this.ht.get(symbol);
            
        }
        
        /**
         * 
         * putSymbol will place the STEntry into the table under the symbol name
         * 
         * @param symbol to search for
         * @param entry to put in table
         */
        public void putSymbol(String symbol, STEntry entry){
            //System.out.println(symbol + " being saved");
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
            
            
            this.ht.put("print", new STFunction("print", Token.FUNCTION
                    , Token.VOID, Token.BUILTIN));
            this.ht.put("LENGTH", new STFunction("LENGTH", Token.FUNCTION
                    , Token.INTEGER, Token.BUILTIN));
            this.ht.put("MAXLENGTH", new STFunction("MAXLENGTH", Token.FUNCTION
                    , Token.INTEGER, Token.BUILTIN));
            this.ht.put("SPACES", new STFunction("SPACES", Token.FUNCTION
                    , Token.INTEGER, Token.BUILTIN));
            this.ht.put("ELEM", new STFunction("ELEM", Token.FUNCTION
                    , Token.INTEGER, Token.BUILTIN));
            this.ht.put("MAXELEM", new STFunction("MAXELEM", Token.FUNCTION
                    , Token.INTEGER, Token.BUILTIN));

            
            //STIdentifiers
            
            
        }
        
}
