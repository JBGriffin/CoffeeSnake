/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package havabol;

/**
 *
 * Parser starts by another object calling parse() Objects: SymbolTable st:
 * SymbolTable to reference all global symbols and put new user defined symbols
 * in.
 *
 * StorageManager storage: Storage reference for the user specified data.
 *
 * Scanner scanner: Scanner shared by all objects in Havabol. It is needed here
 * to continually go through all lines given by a user, mainly using .getNext();
 *
 *
 * @author Justin Hooge
 */
public class Parser {

    SymbolTable st;

    StorageManager storage;

    Scanner scanner;

    /**
     * Parser will go through user source code and execute statements
     *
     * @param st the shared symbol table from all objects
     * @param scanner shared scanner from all objects
     */
    public Parser(SymbolTable st, Scanner scanner) {

        this.st = st;

        this.scanner = scanner;

        this.storage = new StorageManager(st);

    }

    /**
     * Begin parsing from HavaBol
     *
     * @throws Exception should be parser exception
     */
    public void parse() throws Exception {
        //init result value as null - don't think it is necessary here though
        ResultValue rt = null;

        //begin grabbing items from scanner
        rt = statements(true);

    }

    /**
     * Calls exception class for user error.
     *
     * @param msg Message on where the error ocurred.
     * @throws Exception Prints out the line number, token, and a message on
     * where the error occured.
     */
    public void errorWithContext(String msg) throws Exception {
        throw new ParserException(scanner.currentToken.iSourceLineNr, msg);
    }

    /**
     * Begins grabbing tokens from scanner class statements calls appropriate
     * submethod to handle primClassif
     *
     *
     * @param execute boolean - execute if true
     * @return ResultValue - at this point not necessary here
     * @throws Exception should be ScannerException
     */
    private ResultValue statements(boolean execute) throws Exception {

        //init result value so methods can return into rt
        ResultValue rt = null;

        //go until all source code is empty
        while (!scanner.getNext().isEmpty()) {
            //System.out.println("In statemnts with " + scanner.currentToken.tokenStr);
            //checking for all possible primary classifications
            switch (scanner.currentToken.primClassif) {
                //handle control
                case Token.CONTROL:
                    rt = controlStatement(execute);
                    break;
                //handle operands
                case Token.OPERAND:
                    rt = operand(execute);
                    break;
                //handle functions - currently only built in (Assign 3)
                case Token.FUNCTION:
                    rt = function(execute);
                    break;
                //handle operators - this probably should not be legal
                //and will need to throw proper exception
                case Token.OPERATOR:
                    /*System.err.println("Unexpected operator found");*/
                    errorWithContext("Unexpected operator found. Usage: " + scanner.currentToken.tokenStr);
                    break;
                //if default happens, something is seriously wrong in our code
                default:
                    return rt;
            }

        }

        return rt; //returns only to parse()

    }

    /**
     * controlStatement handles control primary classes it will call on the
     * proper submethod based off the sub class
     *
     * OPTIONS: Declare, Flow, End
     *
     * @param execute if execute
     * @return ResultValue
     * @throws Exception //should be parser exception
     */
    private ResultValue controlStatement(boolean execute) throws Exception {

        ResultValue rt = null; //init for assignments

        //grab the current token sub class
        switch (scanner.currentToken.subClassif) {
            //if declare - call declareStatement
            case Token.DECLARE:
                rt = declareStatement(execute);
                break;
            //if flow (if, while, etc.) call flow
            case Token.FLOW:
                rt = flowStatement(execute);
                break;
            //if end (endif, endwhile, etc.) call end
            case Token.END:
                rt = endStatement(execute);
                break;
            //should not be possible (Throw Parser Exception)
            default:
                return rt;
        }

        //return rt to controlStatement method
        return rt;

    }

    /**
     * declareStatement like "Int i;" is handled here. This means parser found a
     * control - declare token and the next should be an identifier - if not
     * throw exception
     *
     * @param execute
     * @return ResultValue to controlStatement
     * @throws Exception should be ParseException
     */
    private ResultValue declareStatement(boolean execute) throws Exception {

        ResultValue rt = null; //init for return

        //System.out.println(scanner.currentToken.tokenStr);
        Token workingToken = scanner.currentToken;
        String sznewTokenStr = scanner.getNext();

        //if subClass is not an Identifer - illegal execution
        if (scanner.currentToken.subClassif != Token.IDENTIFIER) /*throw new Exception();//THROW EXCEPTION HERE*/ {
            errorWithContext("Subclass is not an identifier. Usage: " + scanner.currentToken.tokenStr);
        }

        switch (workingToken.tokenStr) {
            //if Int - put in SymbolTable as Int
            case "Int":
                this.st.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.INTEGER, Token.INTEGER, 0));
                break;
            //if Float - put in SymbolTable as Float
            case "Float":
                this.st.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.FLOAT, Token.FLOAT, 0));
                break;
            //if String - put in SymbolTable as String
            case "String":
                this.st.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.STRING, Token.STRING, 0));
                break;
            //if nothing - throw exception (Assignment 3) - in future there will be more
            default:
                errorWithContext("Error?");
                return rt; //Throw Exception

        }

        //after identifier handle RHS
        rt = assignments(execute);

        return rt;

    }

    /**
     *
     * NEEDS CODE - would be called for if,while, etc.
     *
     * @param execute
     * @return ResultValue
     */
    private ResultValue flowStatement(boolean execute) throws Exception {

        ResultValue rt = null;

        switch(scanner.currentToken.tokenStr) {
            case "if":
                ifStatement(execute);
            case "while":
                whileStatement(execute);
                
            
            
        }
        
        return rt;

    }
    
    
    private ResultValue ifStatement(boolean execute) throws Exception {
        
        ResultValue rt = null;
        
        scanner.getNext();
        switch(scanner.currentToken.subClassif) {
            case Token.IDENTIFIER:
                break;
            case Token.INTEGER:
                break;
            case Token.FLOAT:
                break;
            case Token.STRING:
                break;
                    
                    
        }
        
        return rt;
        
    }
    
    
    private ResultValue whileStatement(boolean execute) throws Exception {
        
        ResultValue rt = null;
        
        scanner.getNext();
        switch(scanner.currentToken.subClassif) {
            case Token.IDENTIFIER:
                break;
            case Token.INTEGER:
                break;
            case Token.FLOAT:
                break;
            case Token.STRING:
                break;
                    
                    
        }
        
        return rt;
        
    }

    /**
     *
     * NEEDS CODE
     *
     * @param execute
     * @return ResultValue
     */
    private ResultValue endStatement(boolean execute) {

        ResultValue rt = null;

        //System.out.println(scanner.currentToken.tokenStr);
        return rt;

    }

    /**
     * function currently only handles built in function "print"
     *
     * @param execute
     * @return ResultValue
     * @throws Exception
     */
    private ResultValue function(boolean execute) throws Exception {

        ResultValue rt = null; //init for assignment

        //System.out.println(scanner.currentToken.tokenStr);
        //for now only builtins are possible (Assign 3)
        if (scanner.currentToken.subClassif == Token.BUILTIN) {
            //make sure symbol exists in global table
            if (((STFunction) this.st.getSymbol(scanner.currentToken.tokenStr)) != null) {
                //if it does, get it
                STFunction stf = (STFunction) this.st.getSymbol(scanner.currentToken.tokenStr);
                //find out which function is being called
                switch (stf.symbol) {
                    //for now only handles print
                    //if print statement
                    case "print":
                        //handle print (TEMPORARY Assign 3) I think this 
                        //will need its own submethod
                        StringBuilder sb = new StringBuilder(); //build string to print

                        //go until a necessary ; is found
                        while (!";".equals(scanner.getNext())) {
                            //check using sub class
                            switch (scanner.currentToken.subClassif) {
                                //if idenifier - get from storage
                                //currently, if identifier does not exist it 
                                //will just be null and continue
                                case Token.IDENTIFIER:
                                    sb.append(this.storage.get(this, scanner.currentToken.tokenStr));
                                    break;
                                //if it is a string, append string to statement
                                case Token.STRING:
                                    sb.append(scanner.currentToken.tokenStr);
                                    break;
                                //if separator, continue
                                case Token.SEPARATOR:
                                    break;
                                //soon we can add '+' to also append strings
                                default:
                                    break;

                            }

                        }
                        // ; was found, print and continue parsing
                        System.out.println(sb.toString());

                }

            }

        }

        return rt;

    }

    /**
     * handles operands such as String, Identifier, Int, Float, etc if found and
     * identifier exist - go to assignments, else - exit So far, only called
     * from statements method.
     *
     * @param execute
     * @return
     * @throws Exception
     */
    private ResultValue operand(boolean execute) throws Exception {
        ResultValue rt = null;

        //System.out.println(scanner.currentToken.tokenStr);
        switch (scanner.currentToken.subClassif) {

            case Token.IDENTIFIER:
                //recall identifier
                STEntry ste = (STIdentifiers) st.getSymbol(scanner.currentToken.tokenStr);
                // if does not exist, throw error
                if (ste == null) {

                    //needs to throw parser exception
                    /*System.err.println("Error!");*/
                    errorWithContext("Syntax Error");
                    return rt;

                    //it does exist so call assignments to handle the rest
                } else {

                    //System.out.println("Success so far!");
                    rt = assignments(execute);
                    return rt;

                }
            //if it exists, continue, otherwise break

            //if operand found as int, float or string, maybe return?? idk
            //so far, does not get to this point
            case Token.INTEGER:
            case Token.FLOAT:
            case Token.STRING:

        }

        return rt;

    }

    /**
     *
     * assinments will handle anything starting with =, +=, -=, etc.
     *
     * and will call expressions after determining which assignment type it is
     *
     *
     *
     * @param execute
     * @return ResultValue of completed assignment NOTE: = 2 + 3 will return 5
     * @throws Exception //should be ParseException
     */
    private ResultValue assignments(boolean execute) throws Exception {

        ResultValue rt = null;
        //save off first token when method called
        Token firstToken = scanner.currentToken;

        //check to see if it is simple assignment
        if (";".equals(scanner.getNext())) {

            return rt;

        }
        //move to assignment oparator
        //if (!"=".equals(scanner.currentToken.tokenStr)) {
        //    scanner.getNext();
        //}

        //this should be better than above because could be +=, -=, etc.
        if (!scanner.currentToken.tokenStr.contains("=")) {
            scanner.getNext();
        }

        //determind what type of assignment it is
        switch (scanner.currentToken.tokenStr) {
            //if simple assignment -> send RHS to expressions()
            case "=":
                scanner.getNext();
                //
                switch (scanner.currentToken.subClassif) {
                    //save simple integer (Assign 3)
                    case Token.INTEGER:
                        Token currentToken = scanner.currentToken;
                        //scanner.getNext();
                        //if (!";".equals(scanner.currentToken.tokenStr))
                        rt = expressions(execute);
                        this.storage.put(firstToken.tokenStr, rt.szValue);
                        return new ResultValue(scanner.currentToken.tokenStr);
                    //save simple float (Assign 3)
                    case Token.FLOAT:
                        Token currentFloatToken = scanner.currentToken;
                        rt = expressions(execute); //for now, throw error (Assign3)
                        this.storage.put(firstToken.tokenStr, Float.parseFloat(rt.szValue) + "");
                        return new ResultValue(scanner.currentToken.tokenStr);
                    //save simple string (Assign 3)
                    case Token.STRING:
                        Token currentStringToken = scanner.currentToken;
                        scanner.getNext(); //for assign3 should be ; only
                        if (!";".equals(scanner.currentToken.tokenStr)) {
                            return rt; //for now, throw error (Assign3)
                        }
                        this.storage.put(firstToken.tokenStr, currentStringToken.tokenStr);
                        break;
                    //System.out.println("Successfully put " + scanner.currentToken.tokenStr + " into " + firstToken.tokenStr);
                    default:
                        Token newToken = scanner.currentToken;
                        rt = expressions(execute);
                        System.out.println(rt.szValue);
                        this.storage.put(firstToken.tokenStr, rt.szValue);
                        return new ResultValue(scanner.currentToken.tokenStr);
                    //return new ResultValue(scanner.currentToken.tokenStr);

                }
            //break;
            //to come soon
            case "+=":
                //will need to call Utility.add
                break;
            case "-=":
                break;
            case "*=":
                break;
            case "/=":
                break;
            case "^=":
                break;
            default:

        }

        return rt;

    }

    /**
     * expressions will handle RHS of assignment statements
     *
     *
     * NEED REFRACTORING BECAUSE MOST WAS MOVED TO ASSIGNMENTS
     *
     * @param execute
     * @return ResultValue NOTE: = 3 + 5; should return 8
     * @throws Exception should be Parser Exception
     */
    private ResultValue expressions(boolean execute) throws Exception {

        ResultValue rt = null;
        //save off current token
        Token firstToken = scanner.currentToken;

        boolean firstIsNegative = false;
        int x = 0;
        float y = (float) 0.0;

        //get all negative signs (Unary -)
        while ("-".equals(scanner.currentToken.tokenStr)) {

            firstIsNegative = !firstIsNegative;
            scanner.getNext();
            firstToken = scanner.currentToken;

        }
        switch (firstToken.subClassif) {
            case Token.INTEGER:
                //if negative, make negative
                if (firstIsNegative) {
                    firstToken.tokenStr = (Integer.parseInt(firstToken.tokenStr) * -1) + "";
                }

                //means it was a simple assignment
                if (";".equals(scanner.getNext())) {
                    rt = new ResultValue(firstToken.tokenStr);

                    return rt;

                } else if ("+".equals(scanner.currentToken.tokenStr)) {

                    System.out.println("Found the plus");
                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.INTEGER:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = x + Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(x + "");
                            break;
                        case Token.FLOAT:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = (int) (x + Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(x + "");
                            break;
                    }

                    return rt;

                } else if ("-".equals(scanner.currentToken.tokenStr)) {

                    System.out.println("Found the plus");
                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.INTEGER:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = x - Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(x + "");
                            break;
                        case Token.FLOAT:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = (int) (x - Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(x + "");
                            break;
                    }

                    return rt;

                } else if ("*".equals(scanner.currentToken.tokenStr)) {

                    System.out.println("Found the plus");
                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.INTEGER:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = x * Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(x + "");
                            break;
                        case Token.FLOAT:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = (int) (x * Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(x + "");
                            break;
                    }

                    return rt;

                } else if ("/".equals(scanner.currentToken.tokenStr)) {
                    
                    System.out.println("Found the plus");
                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        
                        case Token.INTEGER:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = x / Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(x + "");
                            break;
                        case Token.FLOAT:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = (int) (x / Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(x + "");
                            break;
                    }

                    return rt;

                }
            case Token.FLOAT:
                //if negative, make negative
                if (firstIsNegative) {
                    firstToken.tokenStr = (Integer.parseInt(firstToken.tokenStr) * -1) + "";
                }

                //means it was a simple assignment
                if (";".equals(scanner.getNext())) {
                    rt = new ResultValue(firstToken.tokenStr);

                    return rt;

                } else if ("+".equals(scanner.currentToken.tokenStr)) {

                    System.out.println("Found the plus");
                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.INTEGER:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = y + Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(y + "");
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = (y + Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "");
                            break;
                    }

                    return rt;

                } else if ("-".equals(scanner.currentToken.tokenStr)) {

                    System.out.println("Found the plus");
                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.INTEGER:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = y - Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(y + "");
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = (y - Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "");
                            break;
                    }

                    return rt;

                } else if ("*".equals(scanner.currentToken.tokenStr)) {

                    System.out.println("Found the plus");
                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.INTEGER:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = y * Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(y + "");
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = (y * Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "");
                            break;
                    }

                    return rt;

                } else if ("/".equals(scanner.currentToken.tokenStr)) {

                    System.out.println("Found the plus");
                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.INTEGER:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = y / Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(y + "");
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = (y / Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "");
                            break;
                    }

                    return rt;

                }

        }

        System.err.println(scanner.currentToken.tokenStr);

        return rt;

    }

}
