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
    /*
    HashMap<String, String[]> htIntArray;

    HashMap<String, String[]> htFloatArray;

    HashMap<String, String[]> htBoolArray;
     */
    HashMap<String, String[]> htStringArray;

    public int DEFAULT_ARRAY_LENGTH = 20;

    public StorageManager(SymbolTable st) {

        this.st = st;

        ht = new HashMap<>();
        /*
        htIntArray = new HashMap<>();

        htFloatArray = new HashMap<>();

        htBoolArray = new HashMap<>();
         */
        htStringArray = new HashMap<>();

    }

    public void put(String key, String value) {

        //System.out.println(key + " --- " + value);
        ht.put(key, value);

    }

    public String get(Parser parser, String key) {

        return ht.get(key);

    }

    /**
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
                /*
                switch (iDeclareType) {

                    case Token.INTEGER:
                        String[] array = new String[iNumElem];
                        this.htIntArray.put(key, array);
                        break;

                    case Token.FLOAT:
                        String[] fArray = new String[iNumElem];
                        this.htFloatArray.put(key, fArray);
                        break;

                    case Token.BOOLEAN:
                        String[] bArray = new String[iNumElem];
                        this.htBoolArray.put(key, bArray);
                        break;

                    default:
                    //should return erro / thorw error here currently
                 */

            }
        } else {
            String[] array = new String[iNumElem];
            this.htStringArray.put(key, array);
        }
    }

    /*
        switch (iDeclareType) {

            case Token.INTEGER:
                String[] array = new String[iNumElem];
                this.htIntArray.put(key, array);
                break;

            case Token.FLOAT:
                String[] fArray = new String[iNumElem];
                this.htFloatArray.put(key, fArray);
                break;

            case Token.BOOLEAN:
                boolean[] bArray = new boolean[iNumElem];
                this.htBoolArray.put(key, bArray);
                break;

            default:
            //should return erro / thorw error here currently

        }*/
 /*
public void putArray(String key, int[] a) {
        
        htIntArray.put(key, a);

    }
    
    
    public void putArray(String key, float[] a) {

        htFloatArray.put(key, a);

    }
    
    
    public void putArray(String key, boolean[] a) {

        htBoolArray.put(key, a);

    }
     */
    public void putArray(String key, String[] a) {
        htStringArray.put(key, a);

        
        
    }
    
    public String getFromArray(String key, int index) {
        
        return ((String[]) this.htStringArray.get(key))[index];
        
    }
    
    public void putInArray(String key, int index, String value){
        
        String[] newArray = (String []) this.htStringArray.get(key);
        
        newArray[index] = value;
        
        this.htStringArray.put(key, newArray);
        
    }

}
