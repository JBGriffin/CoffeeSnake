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
    
    public Parser(SymbolTable st) {
        
        this.st = st;
        
        this.storage = new StorageManager(st);
        
    }
    
    
    private void parse() {
        
        ResultValue rt = null;
        
        rt = statements(true);
        
    }
    
    
    private ResultValue statements(boolean execute) {
        
        ResultValue rt = null;
        
        
        
        return rt;
        
    }
    
    
    
    private ResultValue declareStatement(boolean execute) {
        
        ResultValue rt = null;
        
        
        
        return rt;
        
    }
    
}
