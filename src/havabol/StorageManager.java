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

    HashMap<String, String[]> htStringArray;

    public int DEFAULT_ARRAY_LENGTH = 20;

    public StorageManager(SymbolTable st) {

        this.st = st;

        ht = new HashMap<>();
        
        htStringArray = new HashMap<>();

    }

    /**
     * Put single element in storage
     * @param key
     * @param value 
     */
    public void put(String key, String value) {

        //System.out.println(key + " --- " + value);
        ht.put(key, value);

    }

    /**
     * Get single element from storage
     * @param parser
     * @param key
     * @return 
     */
    public String get(Parser parser, String key) {

        return ht.get(key);

    }

    /**
     *  Initialize array with given parameters
     * 
     * @param key
     * @param iNumElem
     * @param iDeclareType
     * @param iStructTYpe
     */
    public void initArray(String key, int iNumElem, int iDeclareType, int iStructType) {

        if (iNumElem < 1) {
            if (iStructType == Token.ARRAY_UNBOUND) {
                String[] array = new String[DEFAULT_ARRAY_LENGTH];
                this.htStringArray.put(key, array);
            }
        } else {
            String[] array = new String[iNumElem];
            for(int i = 0; i < iNumElem; i++) array[i] = null;
            this.htStringArray.put(key, array);
        }
    }

    public void putArray(String key, String[] a) {
        htStringArray.put(key, a);
    }
    /**
     * Get single element from array at index given
     * @param key
     * @param index
     * @return 
     */
    public String getFromArray(String key, int index) {
        
        return ((String[]) this.htStringArray.get(key))[index];
        
    }
    /**
     * Get the entire array as string[] for array name
     * @param key
     * @return 
     */
    public String[] getArray(String key) {
        return this.htStringArray.get(key);
    }
    
    /**
     * Put value into key array at the index given.
     * @param key
     * @param index
     * @param value 
     */
    public void putInArray(String key, int index, String value){
        
        String[] newArray = (String []) this.htStringArray.get(key);
        
        newArray[index] = value;
        
        this.htStringArray.put(key, newArray);
        
    }
    
    
    /**
     * Get a substring from a string, return as string
     * @param parser
     * @param key
     * @param startIndex
     * @param endIndex
     * @return 
     */
    public String getCharsFromString(Parser parser, String key, int startIndex, int endIndex) {
        
        String returnString = this.get(parser, key);
        
        String r = returnString.substring(startIndex, endIndex+1);
                
        return r;
        
    }
    
}
