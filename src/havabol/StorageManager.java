/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package havabol;

import java.util.HashMap;

/**
 *
 * @author Justin Hooge
 */
public class StorageManager {
    
    SymbolTable st;
    
    HashMap<String, String> ht;
    
    public StorageManager(SymbolTable st) {
        
        this.st = st;
        
        ht = new HashMap<>();
        
    }
    
    
    public void put(String key, String value) {
        
        ht.put(key, value);
        
    }
    
    public String get(String key) {
        
        return ht.get(key);
        
    }
    
}
