/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package havabol;

/**
 *
 * Parser starts by another object calling parse() Objects: SymbolTable symbolTable:
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

    SymbolTable symbolTable;

    StorageManager storage;

    Scanner scanner;

    /**
     * Parser will go through user source code and execute statements
     *
     * @param symbolTable the shared symbol table from all objects
     * @param scanner shared scanner from all objects
     */
    public Parser(SymbolTable symbolTable, Scanner scanner) {

        this.symbolTable = symbolTable;

        this.scanner = scanner;

        this.storage = new StorageManager(symbolTable);

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
                case Token.SEPARATOR:
                    rt = null;
                    break;
                //if default happens, something is seriously wrong in our code
                default:
                    errorWithContext("Something went seriously wrong. Given: " + scanner.currentToken.tokenStr);
                    return null;
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
                errorWithContext("Something went seriously wrong in the control statement. Given: " + scanner.currentToken.tokenStr);
                return null;
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
                this.symbolTable.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.INTEGER, Token.INTEGER, 0));
                break;
            //if Float - put in SymbolTable as Float
            case "Float":
                this.symbolTable.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.FLOAT, Token.FLOAT, 0));
                break;
            //if String - put in SymbolTable as String
            case "String":
                this.symbolTable.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.STRING, Token.STRING, 0));
                break;
            //if nothing - throw exception (Assignment 3) - in future there will be more
            default:
                errorWithContext("Error?");
                return null; //Throw Exception

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

        switch (scanner.currentToken.tokenStr) {
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
        switch (scanner.currentToken.subClassif) {
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
        switch (scanner.currentToken.subClassif) {
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
            if (((STFunction) this.symbolTable.getSymbol(scanner.currentToken.tokenStr)) != null) {
                //if it does, get it
                STFunction stf = (STFunction) this.symbolTable.getSymbol(scanner.currentToken.tokenStr);
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
                STEntry ste = (STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr);
                // if does not exist, throw error
                if (ste == null) {

                    //needs to throw parser exception
                    /*System.err.println("Error!");*/
                    errorWithContext("Syntax Error");
                    return null;

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
     * @param execute Boolean to see whether or not we execute the statement
     * @return ResultValue of completed assignment NOTE: = 2 + 3 will return 5
     * @throws Exception //should be ParseException
     */
    private ResultValue assignments(boolean execute) throws Exception {

        ResultValue rt = null;
        //save off first token when method called
        Token firstToken = scanner.currentToken;


        //check to see if it is simple assignment
        if (";".equals(scanner.getNext())) {

            return null;

        }
        //move to assignment oparator
        //if (!"=".equals(scanner.currentToken.tokenStr)) {
        //    scanner.getNext();
        //}

        //this should be better than above because could be +=, -=, etc.
        if (!scanner.currentToken.tokenStr.contains("=")) {
            scanner.getNext();
        }

        // Creates and assigns a value into the first token
        if(scanner.currentToken.tokenStr.equals("=")) {
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
                    return new ResultValue(scanner.currentToken.tokenStr, Token.INTEGER);
                //save simple float (Assign 3)
                case Token.FLOAT:
                    Token currentFloatToken = scanner.currentToken;
                    rt = expressions(execute); //for now, throw error (Assign3)
                    this.storage.put(firstToken.tokenStr, Float.parseFloat(rt.szValue) + "");
                    return new ResultValue(scanner.currentToken.tokenStr, Token.FLOAT);
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
                    return new ResultValue(scanner.currentToken.tokenStr, 0); // TODO: find what data type this is
                //return new ResultValue(scanner.currentToken.tokenStr);

            }
        }
        else
        {
            // Assumes that the token has already been initialized and put into the symbol table
            rt = unaryOperation(firstToken, scanner.currentToken.tokenStr);
        }

        return rt;

    }

    /**
     * Simple unary assignment method. Assumes that simple assignment statements have
     * already occurred.
     * @param firstToken Left hand side of the assignment. This is what will be returned after
     *                   the operation has happened
     * @return Result value of the operation given. If x += 2 were given, will return x incremented
     * by 2.
     */
    private ResultValue unaryOperation(Token firstToken, String operator) throws Exception {
        scanner.getNext();
        Token secondToken = scanner.nextToken;

        ResultValue resOp1 = new ResultValue(firstToken.tokenStr, firstToken.subClassif);

        resOp1.szValue = this.storage.get(this, firstToken.tokenStr);
        if(resOp1.szValue == null)
            errorWithContext("Value must be initiated before use! Given: " + firstToken.tokenStr);


        // Grab first numeric, and grab the item from the storage manager
        Numeric nOp1 = new Numeric(this, resOp1, "1st operator", operator);


        // Grab second numeric, and grab the item from the storage manager
        ResultValue resOp2 = new ResultValue(secondToken.tokenStr, secondToken.subClassif);
        // Check if the value exists in storage. If it does, we'll set it to that value
        // If it doesn't, set it back to the second token's value. Numeric will take care
        // of it if it's not a proper value
        resOp2.szValue = this.storage.get(this, secondToken.tokenStr);
        if(resOp2.szValue == null)
            resOp2.szValue = secondToken.tokenStr;

        Numeric nOp2 = new Numeric(this, resOp2, "2nd operator", operator);

        // Create result values from numerics
        ResultValue returnValue = new ResultValue("", 0);
        switch (operator) {
            case "+=":
                returnValue = nOp2.add(nOp1, nOp2);
                break;
            case "-=":
                returnValue = nOp1.subtract(nOp1, nOp2);
                break;
            case "*=":
                returnValue = nOp2.multiply(nOp1, nOp2);
                break;
            case "/=":
                returnValue = nOp2.divide(nOp1, nOp2);
                break;
            case "^=":
                returnValue = nOp2.power(nOp1, nOp2);
                break;
            default:
                errorWithContext("Bad operator given: " + operator);
        }

        this.storage.put(firstToken.tokenStr, returnValue.szValue);
        return returnValue;
    }

    /**
     * expressions will handle RHS of assignment statements
     *
     *
     * NEED REFRACTORING BECAUSE MOST WAS MOVED TO ASSIGNMENTS
     *
     * @param execute Boolean to see whether or not we're executing
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
        while ("U-".equals(scanner.currentToken.tokenStr)) {

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
                    rt = new ResultValue(firstToken.tokenStr, Token.SEPARATOR);

                    return rt;

                } else if ("+".equals(scanner.currentToken.tokenStr)) {

                    System.out.println("Found the plus");
                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.INTEGER:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = x + Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(x + "", Token.INTEGER);
                            break;
                        case Token.FLOAT:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = (int) (x + Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(x + "", Token.FLOAT);
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
                            rt = new ResultValue(x + "", Token.INTEGER);
                            break;
                        case Token.FLOAT:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = (int) (x - Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(x + "", Token.FLOAT);
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
                            rt = new ResultValue(x + "", Token.INTEGER);
                            break;
                        case Token.FLOAT:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = (int) (x * Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(x + "", Token.FLOAT);
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
                            rt = new ResultValue(x + "", Token.INTEGER);
                            break;
                        case Token.FLOAT:
                            x = Integer.parseInt(firstToken.tokenStr);
                            x = (int) (x / Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(x + "", Token.FLOAT);
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
                    rt = new ResultValue(firstToken.tokenStr, Token.SEPARATOR);

                    return rt;

                } else if ("+".equals(scanner.currentToken.tokenStr)) {

                    System.out.println("Found the plus");
                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.INTEGER:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = y + Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(y + "", Token.INTEGER);
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = (y + Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "", Token.FLOAT);
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
                            rt = new ResultValue(y + "", Token.INTEGER);
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = (y - Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "", Token.FLOAT);
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
                            rt = new ResultValue(y + "", Token.INTEGER);
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = (y * Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "", Token.FLOAT);
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
                            rt = new ResultValue(y + "", Token.INTEGER);
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = (y / Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "", Token.FLOAT);
                            break;
                    }

                    return rt;

                }

        }

        errorWithContext("Bad joo-joo found: " + scanner.currentToken.tokenStr);

        return rt;

    }

}
