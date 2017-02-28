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
public class Parser {
    
    SymbolTable st;
    
    StorageManager storage;
    
    Scanner scanner;
    
    public Parser(SymbolTable st, Scanner scanner) {
        
        this.st = st;
        
        this.scanner = scanner;
        
        this.storage = new StorageManager(st);
        
    }
    
    
    public void parse() throws Exception {
        
        ResultValue rt = null;
        
        rt = statements(true);
                
    }
    
    
    private ResultValue statements(boolean execute) throws Exception {
        
        ResultValue rt = null;
        String szTokenStr;
        
        while(!scanner.getNext().isEmpty()) {
        System.out.println("In statemnts with " + scanner.currentToken.tokenStr);
            switch (scanner.currentToken.primClassif) {
                case Token.CONTROL:
                    rt = controlStatement(execute);
                    break;
                case Token.OPERAND:
                    rt = operand(execute);
                    break;
                default:
                    return rt;
            }
        
        }
        
        return rt;
        
    }
    
    
    private ResultValue controlStatement(boolean execute) throws Exception {
        
        ResultValue rt = null;
         
        switch (scanner.currentToken.subClassif) {
            case Token.DECLARE:
                rt = declareStatement(execute);
                break;
            case Token.FLOW:
                rt = flowStatement(execute);
                break;
            case Token.END:
                rt = endStatement(execute);
                break;
                
            default:
                return rt;
        }
        
        return rt;
        
    }
    
    
    
    private ResultValue declareStatement(boolean execute) throws Exception {
        
        ResultValue rt = null;
        
        System.out.println(scanner.currentToken.tokenStr);
        
        
        Token workingToken = scanner.currentToken;
        String sznewTokenStr = scanner.getNext();
        switch (workingToken.tokenStr) {
            
            case "Int":
                this.st.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.INTEGER, Token.INTEGER, 0));
                break;
            case "Float":
                this.st.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.FLOAT, Token.FLOAT, 0));
                break;
            case "String":
                this.st.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.STRING, Token.STRING, 0));
                break;
            default:
                return rt; //Throw Exception
            
            
        }
        //if separator, return
        if (";".equals(scanner.getNext())) {
            return rt;
        } else if ("".equals(scanner.currentToken.tokenStr)) {
            
           //call expressions(); 
        
        }  
        
        return rt;
        
    }
    
    
    
    private ResultValue flowStatement(boolean execute) {
        
        ResultValue rt = null;
        
        System.out.println(scanner.currentToken.tokenStr);
        
        return rt;
        
    }
    
    
    private ResultValue endStatement(boolean execute) {
        
        ResultValue rt = null;
        
        System.out.println(scanner.currentToken.tokenStr);
        
        return rt;
        
    }
    
    
    private ResultValue operand(boolean execute) throws Exception {
        ResultValue rt = null;
                
        System.out.println(scanner.currentToken.tokenStr);
        
        
        switch (scanner.currentToken.subClassif) {
            
            case Token.IDENTIFIER:
                //recall identifier
                STEntry ste = (STIdentifiers) st.getSymbol(scanner.currentToken.tokenStr);
                if (ste == null) {
                    
                    System.err.println("Error!");
                    return rt;
                   
                    
                } else {
                    
                    System.out.println("Success so far!");
                    rt = expressions(execute);
                    return rt;
                    
                }
                //if it exists, continue, otherwise break
            case Token.INTEGER:
            case Token.FLOAT:
            case Token.STRING:
            
        }
        
        return rt;
        
    }
    
    
    private ResultValue expressions(boolean execute) throws Exception {
        
        ResultValue rt = null;
        
        Token firstToken = scanner.currentToken;
        
        scanner.getNext();
        
        if ("=".equals(scanner.currentToken.tokenStr)) {
            
            System.out.println("Found '='");
            
            scanner.getNext();
            
            switch (scanner.currentToken.subClassif) {
                
                case Token.INTEGER:
                    Token currentToken = scanner.currentToken;
                    scanner.getNext();
                    if (!";".equals(scanner.currentToken.tokenStr))
                        return rt; //for now, throw error (Assign3)
                    this.storage.put(firstToken.tokenStr, scanner.currentToken.tokenStr);
                    System.out.println("Successfully put " + scanner.currentToken.tokenStr + " into " + firstToken.tokenStr);
                    return new ResultValue(scanner.currentToken.tokenStr);
                
            }
            
        } else {
            
            System.err.println("Found " + scanner.currentToken.tokenStr);
            
        }
                
        return rt;
        
    }
    
}
