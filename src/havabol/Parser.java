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
        
        scanner.getNext();
        
        Token workingToken = scanner.currentToken;
        
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
    
    
    private ResultValue operand(boolean execute) {
        ResultValue rt = null;
                
        System.out.println(scanner.currentToken.tokenStr);
        
        return rt;
        
    }
    
}
