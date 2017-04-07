package havabol;

import java.util.ArrayList;

/**
 *
 * Parser starts by another object calling parse() Objects: SymbolTable
 * symbolTable: SymbolTable to reference all global symbols and put new user
 * defined symbols in.
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

    Token lastOpenStatement;

    Numeric numeric;
    
    Expressions localExpression;

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

        this.numeric = new Numeric(this);
        
        this.localExpression = new Expressions(this);
    }

    /**
     * Logical main to the interpreter. Begins executing HavaBol code. Will
     * throw an error and kill the program if it runs into code that is unable
     * to execute
     *
     * @throws Exception should be parser exception
     */
    public void parse() throws Exception {
        //init result value as null - don't think it is necessary here though
        ResultValue rt = new ResultValue("", 0);

        //begin grabbing items from scanner
        scanner.getNext();
        rt = statements(true);

    }

    /**
     * Calls exception class for user error.
     *
     * @param msg Message on where the error occurred.
     * @throws Exception Prints out the line number, token, and a message on
     * where the error occurred.
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
        while (!scanner.currentToken.tokenStr.isEmpty()) {
            //System.out.println("In statemnts with " + scanner.currentToken.tokenStr 
            //        + " at line number: " + scanner.currentToken.iSourceLineNr);
            //checking for all possible primary classifications
            switch (scanner.currentToken.primClassif) {
                //handle control
                case Token.CONTROL:
                    rt = controlStatement(execute);
                    //return if it is "else" or "endif"

                    if (rt != null && (rt.szValue.equals("else") || rt.szValue.equals("endif") || (rt.szValue.equals("endwhile")))) {
                        return rt;
                    }
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
                    //errorWithContext("Unexpected operator found. Usage: " + scanner.currentToken.tokenStr);
                    scanner.getNext();
                    continue;
                case Token.SEPARATOR:
                    rt = null;
                    break;
                //if default happens, something is seriously wrong in our code
                default:
                    errorWithContext("Something went seriously wrong. Given: " + scanner.currentToken.tokenStr);
                    return null;
            }
            //iterate to next token
            scanner.getNext();
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
        //p("control stmt: " + scanner.currentToken.tokenStr);
        ResultValue returnValue = null; //init for assignments
        //grab the current token sub class
        switch (scanner.currentToken.subClassif) {
            //if declare - call declareStatement
            case Token.DECLARE:
                returnValue = declareStatement(execute);
                break;
            //if flow (if, while, etc.) call flow
            case Token.FLOW:
                flowStatement(execute);
                break;
            //if end (endif, endwhile, etc.) call end
            case Token.END:
                returnValue = endStatement(execute);
                return returnValue;
            //should not be possible (Throw Parser Exception)
            default:
                errorWithContext("Something went seriously wrong in the control statement. Given: " + scanner.currentToken.tokenStr);
                return null;
        }
        //return rt to controlStatement method
        return returnValue;

    }

    /**
     * declareStatement like "Int i;" is handled here. This means parser found a
     * control - declare token and the next should be an identifier - if not
     * throw exception
     *
     * @param execute Boolean to determine whether or not the statement needs to
     * be executed
     * @return ResultValue to controlStatement
     * @throws Exception should be ParseException
     */
    private ResultValue declareStatement(boolean execute) throws Exception {
        ResultValue returnValue = null; //init for return
        //p("declare");
        //System.out.println(scanner.currentToken.tokenStr);
        Token workingToken = scanner.currentToken;
        String sznewTokenStr = scanner.getNext();

        //if subClass is not an Identifer - illegal execution
        if (scanner.currentToken.subClassif != Token.IDENTIFIER) {
            errorWithContext("Subclass is not an identifier. Usage: " + scanner.currentToken.tokenStr);
        }

        //if execute is true, put item into symbol table
        if (execute) {
            switch (workingToken.tokenStr) {
                //if Int - put in SymbolTable as Int
                case "Int":
                    this.symbolTable.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.INTEGER, Token.INTEGER, Token.INTEGER));
                    break;
                //if Float - put in SymbolTable as Float
                case "Float":
                    this.symbolTable.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.FLOAT, Token.FLOAT, Token.FLOAT));
                    break;
                //if String - put in SymbolTable as String
                case "String":
                    this.symbolTable.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.STRING, Token.STRING, Token.STRING));
                    break;
                //if nothing - throw exception (Assignment 3) - in future there will be more
                case "Bool":
                    this.symbolTable.putSymbol(sznewTokenStr, new STIdentifiers(sznewTokenStr, Token.CONTROL, Token.BOOLEAN, Token.BOOLEAN, Token.BOOLEAN));
                    break;
                default:
                    errorWithContext("Declaration not recognized. Given: " + workingToken.tokenStr);
                    return null; //Throw Exception

            }
        }

        //after identifier handle RHS
        returnValue = assignments(execute);

        return returnValue;

    }

    /**
     *
     * Called to determine what flow statement to call, e.g. if, while, for
     * loops
     *
     * @param execute Whether or not to execute the statements
     */
    private void flowStatement(boolean execute) throws Exception {

        //p("flow statement");
        ResultValue returnValue = null;

        //currently the only possibilities here are if and while. 
        switch (scanner.currentToken.tokenStr) {
            case "if":
                ifStatement(execute);
                break;
            case "while":
                whileStatement(execute);
                break;
            case "for":
                forStatement(execute);
                break;
            default:
                errorWithContext("Unexpected flow statement found");

        }
    }

    /**
     * Method to iterate through expressions from a starting statement to a
     * separator. Called if a previous expression has already been evaluated to
     * false. So, if given `if i < 0:` it will skip from "if" to the ":". @param
     * startPosition Starting st
     *
     * atement in the thread. Provided as error checker. @param endPosition
     * Separator to return out of. @throws Exception If a separator is never
     * encountered, i.e. end of file is
     *
     * r
     * eached, error will be thrown.
     */
    private void skipTo(String startPosition, String endPosition) throws Exception {
        int startColPos = scanner.currentToken.iColPos;
        int startLnPos = scanner.currentToken.iSourceLineNr;
        //p("skipto");
        while (true) {
            scanner.getNext();

            //go until end position is found, advance past and return to caller
            if (scanner.currentToken.tokenStr.equals(endPosition)) {
                scanner.getNext();
                return;
            }

            //missing closing - tell user
            if (scanner.currentToken.primClassif == Token.EOF) {
                errorWithContext("Separator never encountered for " + startPosition
                        + " statement found at line number " + startLnPos + ", position " + startColPos);
            }
        }
    }

    /**
     * Evaluates if/else statements. Will decide whether or not to execute the
     * next statements. Is able to execute multi-lined if/else statements, but
     * can not execute nested if statements.
     *
     * @param execute Boolean to decide whether or not to execute statements.
     * @throws Exception Kills the program if something goes wrong
     */
    private void ifStatement(boolean execute) throws Exception {
        //p("ifStatement");

        //if we want to execute, go through statement
        //else, skip to next branch
        if (execute)
        {
            //move past "if"
            int iIfLine = scanner.currentToken.iSourceLineNr;
            scanner.getNext();
            //returns = "T" or "F"
            ResultValue resultCond = evaluateEquality(execute, scanner.currentToken, scanner.getNext());
            ResultValue toExecute;
            //move past ":"
            scanner.getNext();

            //if true, we want to execute statements until endif
            if (resultCond.szValue.equals("T"))
            {
                toExecute = statements(true);
                //do not execute this else
                if (toExecute == null)
                {
                    errorWithContext("Expected \"endif\" or \"else\" near line " + iIfLine + " not found");
                }
                assert toExecute != null;
                if (toExecute.szValue.equals("else"))
                {
                    //skip past else
                    scanner.getNext();
                    //skip past ":"
                    scanner.getNext();
                    //start executing with False until endif found
                    statements(false);
                    if (! "endif".equals(scanner.currentToken.tokenStr))
                    {
                        errorWithContext("Expected endif near " + iIfLine + " not found");
                    }
                    //leave ifStatements once endif found
                }
                else if (toExecute.szValue.equals("endif"))
                {
                    if (! scanner.getNext().equals(";"))
                    {
                        errorWithContext("Expected ';' at the end of statement. Usage: " + scanner.currentToken.tokenStr);
                    }
                }
            } //if was false
            else
            {
                //skip first branch by sending false to statements
                toExecute = statements(false);
                //execute the else branch
                assert toExecute != null; // Making IntelliJ happy
                if (toExecute.szValue.equals("else"))
                {
                    //skip past else
                    scanner.getNext();
                    //skip past :
                    scanner.getNext();
                    statements(true);
                    if (!"endif".equals(scanner.currentToken.tokenStr))
                    {
                        errorWithContext("Expected endif near " + iIfLine + " not found");
                    }
                    //stop at endif
                }
                else if (toExecute.szValue.equals("endif"))
                {
                    //skip over endif and ;
                    if (! scanner.getNext().equals(";"))
                    {
                        errorWithContext("Expected ';' at the end of endif. Usage: " + scanner.currentToken.tokenStr);
                    }
                }
            }
        }
        else
        {
            skipTo("if", ":");
            statements(false);
            if(! scanner.currentToken.tokenStr.equals("endif") ||
                    ! scanner.nextToken.tokenStr.equals(";"))
                errorWithContext("Incorrect ending statement. Given: " + scanner.currentToken.tokenStr);
        }
    }

    /**
     * Loops through code until user defined exit statement
     *
     * @param execute Boolean to determine whether or not loop needs to be
     * execute
     * @throws Exception Kills the program if something goes wrong
     */
    private void whileStatement(boolean execute) throws Exception {
        //p("whileStatement");
        if (execute) {
            int iWhileStart = scanner.currentToken.iSourceLineNr - 1;
            int iEndWhile;
            int iColEnd;
            scanner.getNext();
            ResultValue resultCond = evaluateEquality(execute, scanner.currentToken, scanner.getNext());
            ResultValue toExecute = null;
            scanner.getNext();

            //p(iWhileStart + " " + iColPos);
            //p(scanner.sourceFileM.get(iWhileStart));
            // Will loop until condition is false.
            while (resultCond.szValue.equals("T")) {
                toExecute = statements(true);

                //once found, re-eval expression
                if (toExecute.szValue.equals("endwhile")) {
                    //p("TO EXECUTE FOUND ENDWHILE");
                    iColEnd = scanner.iColPos;
                    iEndWhile = scanner.iSourceLineNr;

                    //rewind loop
                    scanner.loopReset(iWhileStart);
                    scanner.getNext();  // Skip while
                    //re-eval
                    resultCond = evaluateEquality(execute, scanner.currentToken, scanner.getNext());

                    scanner.getNext(); //move pass :
                    //if not true, advance to end of loop
                    if (! resultCond.szValue.equals("T")) {
                        scanner.iSourceLineNr = iEndWhile;
                        scanner.iColPos = iColEnd;
                        scanner.advanceLine();
                        return;
                    }
                } else {
                    //found endif probably.
                    //need to figure out what exactly this is about
                    scanner.getNext();
                }
            }
            //while evaluation was false
            toExecute = statements(false);
            //advance past endwhile and :
            if (toExecute.szValue.equals("endwhile")) {
                if (scanner.getNext().equals(":")) {

                    scanner.getNext();
                }
            }

            //skip to end if needed
        } else {
            skipTo("while", ":");
            statements(false);
        }
    }

    /**
     * For loop which takes an index and a starting variable, and runs until the
     * ending variable. Currently only increments by 1. Will be able to
     * increment different positive numbers in the future.
     * <p>
     * <blockquote><pre>
     * // Example for loop execution:
     * for controlVar = startVar to endVar
     *      body
     * endfor;
     * </pre></blockquote><p>
     * Note: The only part of the for loop expression that is able to be
     * re-evaluated is the incrementer.
     * <p>
     * @param execute Boolean to determine whether or not to execute the loop.
     */
    private void forStatement(boolean execute) throws Exception {
        //p("for loop");
        if (execute) {
            int iForStart = scanner.currentToken.iSourceLineNr - 1;
            int iEndFor;
            int iColEnd;
            scanner.getNext();
            ResultValue resultCond = expressions(execute);
            p("" + resultCond.szValue);
            ResultValue toExecute = null;
            scanner.getNext();
            /*
            //p(iWhileStart + " " + iColPos);
            //p(scanner.sourceFileM.get(iWhileStart));
            // Will loop until condition is false.
            while (resultCond.szValue.equals("T")) {
                toExecute = statements(true);

                //once found, re-eval expression
                if (toExecute.szValue.equals("endwhile")) {
                    //p("TO EXECUTE FOUND ENDWHILE");
                    iColEnd = scanner.iColPos;
                    iEndFor = scanner.iSourceLineNr;

                    //rewind loop
                    scanner.loopReset(iForStart);

                    //re-eval
                    resultCond = evaluateEquality(execute, scanner.currentToken, scanner.getNext());
                    scanner.getNext(); //move pass :
                    //if not true, advance to end of loop
                    if (!resultCond.szValue.equals("T")) {
                        scanner.iSourceLineNr = iEndFor;
                        scanner.iColPos = iColEnd;
                        scanner.advanceLine();
                        return;
                    }
                } else {
                    //found endif probably.

                    //need to figure out what exactly this is about
                    scanner.getNext();
                }
            }
            //for evaluation was false
            toExecute = statements(false);
            //advance past endfor and :
            if (toExecute.szValue.equals("endfor")) {
                if (scanner.getNext().equals(":")) {
                    scanner.getNext();
                }
            }

            //skip to end if needed
        } else {
            skipTo("for", ":");
            statements(false);
             */
        }

    }

    /**
     *
     * Ending statement has been encountered. Will return the proper ending
     * statement
     *
     * @param execute Whether or not to execute the statement
     * @return ResultValue Value of the item given. Will return endif, endwhile,
     * etc.
     */
    private ResultValue endStatement(boolean execute) throws Exception {

        //p("endStatement");
        if (scanner.currentToken.primClassif == Token.EOF) {
            errorWithContext("End of file reached with no closing 'endif' statement. Last used if statement used at line "
                    + lastOpenStatement.iSourceLineNr + " , position " + lastOpenStatement.iColPos);
        }
        ResultValue rt;

        //determine which end statement it is
        //needed for nested loops
        switch (scanner.currentToken.tokenStr) {
            case "endif":
                rt = new ResultValue("endif", Token.END);
                return rt;
            case "else":
                rt = new ResultValue("else", Token.END);
                return rt;
            case "endwhile":
                rt = new ResultValue("endwhile", Token.END);
                return rt;
            case "endfor":
                rt = new ResultValue("endfor", Token.END);
                return rt;
            default:
                errorWithContext("Expected ending statement. Given: " + scanner.currentToken.tokenStr);
        }
        // Will never reach this
        return null;
    }

    /**
     * Function currently only handles built in function "print"
     *
     * @param execute Whether or not to execute the function
     * @return ResultValue Returns value for the function. Will only return
     * something from user defined functions
     * @throws Exception If something goes wrong, kills the program
     */
    private ResultValue function(boolean execute) throws Exception {
        ResultValue rt = null; //init for assignment
        int parens = 0;
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

                        scanner.getNext();
                        //go until a necessary ; is found
                        while (!";".equals(scanner.currentToken.tokenStr)) {
                            //check using sub class
                            if (!execute) {
                                scanner.getNext();
                                continue;
                            }
                            //if ("(".equals(scanner.currentToken.tokenStr)) parens++;
                            //else if (")".equals(scanner.currentToken.tokenStr)) parens--;
                            if (",".equals(scanner.currentToken.tokenStr)) {
                                sb.append(" ");
                                scanner.getNext();
                                continue;
                            }
                            switch (scanner.currentToken.subClassif) {
                                //if idenifier - get from storage
                                //if it is a string, append string to statement
                                case Token.STRING:
                                    sb.append(scanner.currentToken.tokenStr);
                                    break;
                                //currently, if identifier does not exist it
                                //will just be null and continue
                                case Token.IDENTIFIER:
                                    if (((STIdentifiers) this.symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.STRING) {
                                        sb.append(this.storage.get(this, scanner.currentToken.tokenStr));
                                    } else {
                                        sb.append(expressions(execute).szValue).append(" ");
                                    }
                                    break;
                                case Token.INTEGER:
                                case Token.FLOAT:
                                    sb.append(expressions(execute).szValue).append(" ");
                                    break;
                                //if separator, continue
                                //case Token.SEPARATOR:
                                //break;
                                //soon we can add '+' to also append strings
                                default:
                                    break;

                            }
                            scanner.getNext();
                        }
                        // ; was found, print and continue parsing
                        if (execute) {
                            System.out.println(sb.toString());
                        }

                        break;
                        //LENGTH(string)
                    case "LENGTH":
                        scanner.getNext(); //move past length
                        scanner.getNext(); //move past '('
                        if (scanner.currentToken.subClassif == Token.IDENTIFIER) {
                            String workingString = "";                            
                            workingString = this.storage.get(this, scanner.currentToken.tokenStr);
                            //if (!scanner.getNext().equals(")")) 
                            //    errorWithContext("Expected \")\" in function call to LENGTH not found");
                            return new ResultValue(workingString.length() + "", Token.INTEGER);
                        }
                        break;
                        //spaces(string)
                    case "SPACES":
                        break;
                        //elem(array)
                    case "ELEM":
                        break;
                        //maxelem(array)
                    case "MAXELEM":
                        break;
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
     * @param execute Whether or not to execute the statement
     * @return The value of the assignment
     * @throws Exception Thrown if assignment set up incorrectly
     */
    private ResultValue operand(boolean execute) throws Exception {

        ResultValue rt = null;
        if (execute) {

            //System.out.println(scanner.currentToken.tokenStr);
            switch (scanner.currentToken.subClassif) {

                case Token.IDENTIFIER:
                    //recall identifier
                    STEntry ste = (STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr);
                    // if does not exist, throw error
                    if (ste == null) {

                        //needs to throw parser exception
                        errorWithContext("Incorrect token given. Usage: " + scanner.currentToken.tokenStr);
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
        }
        return rt;

    }

    /**
     *
     * Assignments will handle anything starting with =, +=, -=, etc. and will
     * call expressions after determining which assignment type it is
     * <p>
     * @param execute Boolean to see whether or not we execute the statement
     * @return ResultValue of completed assignment NOTE: = 2 + 3 will return 5
     * @throws Exception //should be ParseException
     */
    private ResultValue assignments(boolean execute) throws Exception {

        //p("in assignments");
        ResultValue rt = null;
        //save off first token when method called
        Token firstToken = scanner.currentToken;

        //check to see if it is simple assignment
        if (";".equals(scanner.getNext())) {

            return null;

        }

        //this should be better than above because could be +=, -=, etc.
        if (!scanner.currentToken.tokenStr.contains("=") && !scanner.currentToken.tokenStr.equals("[")) {
            scanner.getNext();
        }
        //p(scanner.currentToken.tokenStr + " HERE");
        if (scanner.currentToken.tokenStr.equals("[")) {
            //p(750);
            return arrayAssignments(execute, firstToken);
        }
        
        //if (scanner.currentToken.primClassif == Token.FUNCTION)
        //    return function(execute);

        String assignToken = scanner.currentToken.tokenStr;
        scanner.getNext();
        // Creates and assigns a value into the first token
//        switch (assignToken) {
//            case "=":
        if(assignToken.equals("=")) {
            //scanner.getNext();
            if (scanner.currentToken.primClassif == Token.FUNCTION) {
                int value = 0;
                rt = function(execute);
                value = Integer.parseInt(rt.szValue);
                p(scanner.currentToken.tokenStr);
                this.storage.put(firstToken.tokenStr, rt.szValue + "");
                return rt;
            }
            switch (scanner.currentToken.subClassif) {
                //save simple integer (Assign 3)
                case Token.INTEGER:
                    Token currentToken = scanner.currentToken;
                    //scanner.getNext();
                    //if (!";".equals(scanner.currentToken.tokenStr))
                    rt = expressions(execute);

                    if (execute) {
                        if (rt.szValue != null) {
                            if (scanner.bShowExpr) {
                                System.out.println("\t\t... Expression = " + rt.szValue);
                            }
                            if (scanner.bShowAssign) {
                                System.out.println("\t\t... Assignment: " + firstToken.tokenStr + " = " + rt.szValue);
                            }
                        }
                        switch (firstToken.subClassif) {
                            case Token.INTEGER:
                                rt.szValue = ((int) Float.parseFloat(rt.szValue)) + "";
                                break;
                            case Token.FLOAT:
                                rt.szValue = ((float) Float.parseFloat(rt.szValue)) + "";
                                break;
                        }
                        this.storage.put(firstToken.tokenStr, rt.szValue + "");
                    }
                    return new ResultValue(scanner.currentToken.tokenStr, firstToken.subClassif);
                //save simple float (Assign 3)
                case Token.FLOAT:
                    Token currentFloatToken = scanner.currentToken;
                    rt = expressions(execute); //for now, throw error (Assign3)

                    if (execute) {
                        if (rt.szValue != null) {
                            if (scanner.bShowExpr) {
                                System.out.println("\t\t... Expression = " + rt.szValue);
                            }
                            if (scanner.bShowAssign) {
                                System.out.println("\t\t" + firstToken.tokenStr + " = " + rt.szValue);
                            }
                        }
                        switch (firstToken.subClassif) {
                            case Token.INTEGER:
                                rt.szValue = ((int) Float.parseFloat(rt.szValue)) + "";
                                break;
                            case Token.FLOAT:
                                rt.szValue = ((float) Float.parseFloat(rt.szValue)) + "";
                                break;
                        }
                        this.storage.put(firstToken.tokenStr, Float.parseFloat(rt.szValue) + "");
                    }
                    return new ResultValue(scanner.currentToken.tokenStr, firstToken.subClassif);
                //save simple string (Assign 3)
                case Token.STRING:
                    Token currentStringToken = scanner.currentToken;
                    scanner.getNext(); //for assign3 should be ; only
                    if (!";".equals(scanner.currentToken.tokenStr)) {
                        return rt; //for now, throw error (Assign3)
                    }
                    if (execute) {
                        if (rt != null) {
                            if (scanner.bShowAssign) {
                                System.out.println("\t\t" + firstToken.tokenStr + " = " + rt.szValue);
                            }
                        }
                        this.storage.put(firstToken.tokenStr, currentStringToken.tokenStr);
                    }
                    return new ResultValue(scanner.currentToken.tokenStr, Token.STRING);
                //System.out.println("Successfully put " + scanner.currentToken.tokenStr + " into " + firstToken.tokenStr);
                case Token.BOOLEAN:
                    //System.out.println("I'M HERE!!!");
                    //if (scanner.bShowExpr)
                    //  System.out.println(rt.szValue);
                default:
                    Token newToken = scanner.currentToken;
                    rt = expressions(execute);
                    //System.out.println(rt.szValue);

                    if (execute) {
                        if (rt != null) {
                            if (scanner.bShowExpr) {
                                System.out.println("\t\t... Expression = " + rt.szValue);
                            }
                            if (scanner.bShowAssign) {
                                System.out.println("\t\t" + firstToken.tokenStr + " = " + rt.szValue);
                            }
                        }
                        this.storage.put(firstToken.tokenStr, rt.szValue);
                    }
                    return new ResultValue(scanner.currentToken.tokenStr, 0); // TODO: find what data type this is
                //return new ResultValue(scanner.currentToken.tokenStr);

            }
        } else {
            // Create left and right hand tokens, and create a new value using the assignment token.
            // Put the new value into the storage table after calculation
            ResultValue leftHS = new ResultValue(firstToken.tokenStr, firstToken.subClassif);
            leftHS.szValue = this.storage.get(this, firstToken.tokenStr);
            leftHS.type = ((STIdentifiers) this.symbolTable.getSymbol(firstToken.tokenStr)).iDclType;
            if (leftHS.szValue == null)
                errorWithContext("Item must be assigned before " + assignToken + " may be used. Given: " + firstToken.tokenStr);
            ResultValue rightHS = new ResultValue(scanner.currentToken.tokenStr, scanner.currentToken.subClassif);
            if ((rightHS.szValue = this.storage.get(this, scanner.currentToken.tokenStr)) == null)
                rightHS.szValue = scanner.currentToken.tokenStr;
            else
                rightHS.type = ((STIdentifiers) this.symbolTable.getSymbol(scanner.currentToken.tokenStr)).iDclType;

            switch (assignToken) {
                // Assumes that the token has already been initialized and put into the symbol table
                case "+=":
                    rt = numeric.add(leftHS, rightHS);
                    break;
                case "-=":
                    rt = numeric.subtract(leftHS, rightHS);
                    break;
                case "*=":
                    rt = numeric.multiply(leftHS, rightHS);
                    break;
                case "/=":
                    rt = numeric.divide(leftHS, rightHS);
                    break;
                case "^=":
                    rt = numeric.power(leftHS, rightHS);
                    break;
                default:
                    errorWithContext("Expected assignment or \";.\" Found: " + scanner.currentToken.tokenStr);
            }
            if(execute)
                this.storage.put(firstToken.tokenStr, rt.szValue);
            return rt;
        }
    }

    /**
     * handles array assignments
     * passed to by the assignments function when seeing subscripting
     * 
     * @param execute
     * @param identifier
     * @return
     * @throws Exception 
     */
    private ResultValue arrayAssignments(boolean execute, Token identifier) throws Exception {
        ResultValue rt = null;

        //p("in array assignments with identifier = " + identifier.tokenStr);
        //p("array is type " + ((STIdentifiers) this.symbolTable.getSymbol(identifier.tokenStr)).iParmType);
        if (scanner.currentToken.tokenStr.equals("[")) {
            scanner.getNext();
        }
        
        //if true, this has already been allocated
        if (((STIdentifiers) this.symbolTable.getSymbol(identifier.tokenStr)).iStruct == Token.ARRAY_FIXED
                || ((STIdentifiers) this.symbolTable.getSymbol(identifier.tokenStr)).iStruct == Token.ARRAY_UNBOUND) {
            
            int indexForArray;
            //p(scanner.currentToken.tokenStr);
            rt = expressions(execute);
            indexForArray = Integer.parseInt(rt.szValue);
            while(scanner.getNext().equals("=") || scanner.currentToken.tokenStr.equals("]"));
            rt = expressions(execute);
            //p(this.storage.getFromArray(identifier.tokenStr, Integer.parseInt(rt.szValue)));
            this.storage.putInArray(identifier.tokenStr, indexForArray, rt.szValue);
            //////need to get that value, then evaluate passed the = sign;
            
            return rt;
            
        }

        //check for what is in brackets
        //empty, this will be followed by an assignment. 
        //Max elements is number of items in following list
        if ("]".equals(scanner.currentToken.tokenStr)) {
            STIdentifiers sti = (STIdentifiers) this.symbolTable.getSymbol(identifier.tokenStr);
            sti.iStruct = Token.ARRAY_FIXED;
            this.symbolTable.updateSymbol(sti.symbol, sti);
            this.storage.initArray(identifier.tokenStr, storage.DEFAULT_ARRAY_LENGTH, sti.iDclType, sti.iStruct);

            scanner.getNext();

            if (!"=".equals(scanner.currentToken.tokenStr)) {
                errorWithContext("Expected \"=\" for array assignment");
            }
            scanner.getNext();
            ArrayList<String> resOpsM = new ArrayList<>();
            while (!scanner.currentToken.tokenStr.equals(";")) {
                ResultValue resOp1 = expressions(execute);
                resOpsM.add(resOp1.szValue);
                //p(resOp1.szValue);
                if (scanner.currentToken.tokenStr.equals(","))
                    scanner.getNext();
            }
            
            if (resOpsM.isEmpty()) errorWithContext("Values must be given for unspecifed array length");
            String[] tempM = new String[resOpsM.size()];
            for (int i = 0; i < resOpsM.size(); i++)
                tempM[i] = resOpsM.get(i);
            
            this.storage.putArray(identifier.tokenStr, tempM);

            return rt;
        }

        //unbound
        if ("unbound".equals(scanner.currentToken.tokenStr)) {
            STIdentifiers sti = (STIdentifiers) this.symbolTable.getSymbol(identifier.tokenStr);

            //if entry not in table, something went wrong
            if (sti == null) {
                errorWithContext("Cannot use special word \"unbound\" as identifier");
            }

            //if array is unbound, next token must be ]
            if (!scanner.getNext().equals("]")) {
                errorWithContext("Expected \"]\" not found");
            }
            if (";".equals(scanner.currentToken.tokenStr)) return rt;
            //p(924);
            sti.iStruct = Token.ARRAY_UNBOUND;
            this.symbolTable.updateSymbol(sti.symbol, sti);
            this.storage.initArray(identifier.tokenStr, storage.DEFAULT_ARRAY_LENGTH, sti.iDclType, sti.iStruct);
            
            
            scanner.getNext();

            if (!"=".equals(scanner.currentToken.tokenStr) && !";".equals(scanner.currentToken.tokenStr)) {
                errorWithContext("Expected \"=\" or \";\" for array assignment");
            }
            scanner.getNext();
            ArrayList<String> resOpsM = new ArrayList<>();
            while (!scanner.currentToken.tokenStr.equals(";")) {
                ResultValue resOp1 = expressions(execute);
                resOpsM.add(resOp1.szValue);
                //p(resOp1.szValue);
                if (scanner.currentToken.tokenStr.equals(","))
                    scanner.getNext();
            }
            
            if (resOpsM.isEmpty()) errorWithContext("Values must be given for unspecifed array length");
            String[] tempM = new String[storage.DEFAULT_ARRAY_LENGTH];
            for (int i = 0; i < resOpsM.size(); i++)
                tempM[i] = resOpsM.get(i);
            
            this.storage.putArray(identifier.tokenStr, tempM);
            return rt;
        }

        //p(1003);
        //integer: max elements of this integer
        if (scanner.currentToken.subClassif == Token.INTEGER) {
            STIdentifiers sti = (STIdentifiers) this.symbolTable.getSymbol(identifier.tokenStr);
            sti.iStruct = Token.ARRAY_FIXED;
            this.symbolTable.updateSymbol(sti.symbol, sti);

            //p(scanner.currentToken.tokenStr);
            rt = expressions(execute);
            //p(rt.szValue);
            this.storage.initArray(identifier.tokenStr,
                     Integer.parseInt(rt.szValue), sti.iDclType, sti.iStruct);
            int sizeForArray = Integer.parseInt(rt.szValue);
            scanner.getNext();

            //p(scanner.currentToken.tokenStr);
            if (!"=".equals(scanner.currentToken.tokenStr) && !";".equals(scanner.currentToken.tokenStr)) {
                errorWithContext("Expected \"=\" or \";\" for array assignment");
            }
            if (";".equals(scanner.currentToken.tokenStr)) return rt;
            scanner.getNext();
            //p("SCANNER AT:" + scanner.currentToken.tokenStr);
            ArrayList<String> resOpsM = new ArrayList<>();
            while (!scanner.currentToken.tokenStr.equals("]") || !scanner.currentToken.tokenStr.equals(";")) {
                //p(1025);
                ResultValue resOp1 = expressions(execute);
                //p(1027);
                resOpsM.add(resOp1.szValue);
                //p(resOp1.szValue);
                if (scanner.currentToken.tokenStr.equals(","))
                    scanner.getNext();
            }
            
            if (resOpsM.isEmpty()) errorWithContext("Values must be given for unspecifed array length");
            if (resOpsM.size() > sizeForArray) errorWithContext("Array not large enough to store list");
            String[] tempM = new String[sizeForArray];
            for (int i = 0; i < resOpsM.size(); i++)
                tempM[i] = resOpsM.get(i);
            
            this.storage.putArray(identifier.tokenStr, tempM);
            p(1040);
            return rt;
        }
        
        
        //identifier: max elements of this identifier
        if (scanner.currentToken.subClassif == Token.IDENTIFIER) {
            STIdentifiers sti = (STIdentifiers) this.symbolTable.getSymbol(identifier.tokenStr);
            sti.iStruct = Token.ARRAY_FIXED;
            this.symbolTable.updateSymbol(sti.symbol, sti);

            //p(scanner.currentToken.tokenStr);
            rt = expressions(execute);
            //p(rt.szValue);
            this.storage.initArray(identifier.tokenStr,
                     Integer.parseInt(rt.szValue), sti.iDclType, sti.iStruct);
            int sizeForArray = Integer.parseInt(rt.szValue);
            scanner.getNext();

            //p(scanner.currentToken.tokenStr);
            if (!"=".equals(scanner.currentToken.tokenStr) && !";".equals(scanner.currentToken.tokenStr)) {
                errorWithContext("Expected \"=\" or \";\" for array assignment");
            }
            if (";".equals(scanner.currentToken.tokenStr)) return rt;
            scanner.getNext();
            //p("SCANNER AT:" + scanner.currentToken.tokenStr);
            ArrayList<String> resOpsM = new ArrayList<>();
            while (!scanner.currentToken.tokenStr.equals("]") || !scanner.currentToken.tokenStr.equals(";")) {
                p(1025);
                ResultValue resOp1 = expressions(execute);
                p(1027);
                resOpsM.add(resOp1.szValue);
                //p(resOp1.szValue);
                if (scanner.currentToken.tokenStr.equals(","))
                    scanner.getNext();
            }
            
            if (resOpsM.isEmpty()) errorWithContext("Values must be given for unspecifed array length");
            if (resOpsM.size() > sizeForArray) errorWithContext("Array not large enough to store list");
            String[] tempM = new String[sizeForArray];
            for (int i = 0; i < resOpsM.size(); i++)
                tempM[i] = resOpsM.get(i);
            
            this.storage.putArray(identifier.tokenStr, tempM);
            return rt;
        }
        
        //integer: max elements of this integer
        if (scanner.currentToken.subClassif == Token.FUNCTION) {
            STIdentifiers sti = (STIdentifiers) this.symbolTable.getSymbol(identifier.tokenStr);
            sti.iStruct = Token.ARRAY_FIXED;
            this.symbolTable.updateSymbol(sti.symbol, sti);

            //p(scanner.currentToken.tokenStr);
            rt = function(execute);
            p(rt.szValue);
            //p(rt.szValue);
            this.storage.initArray(identifier.tokenStr,
                     Integer.parseInt(rt.szValue), sti.iDclType, sti.iStruct);
            int sizeForArray = Integer.parseInt(rt.szValue);
            scanner.getNext();

            //p(scanner.currentToken.tokenStr);
            if (!"=".equals(scanner.currentToken.tokenStr) && !";".equals(scanner.currentToken.tokenStr)) {
                errorWithContext("Expected \"=\" or \";\" for array assignment");
            }
            scanner.getNext();
            ArrayList<String> resOpsM = new ArrayList<>();
            while (!scanner.currentToken.tokenStr.equals(";")) {
                ResultValue resOp1 = expressions(execute);
                resOpsM.add(resOp1.szValue);
                //p(resOp1.szValue);
                if (scanner.currentToken.tokenStr.equals(","))
                    scanner.getNext();
            }
            
            if (resOpsM.isEmpty()) errorWithContext("Values must be given for unspecifed array length");
            if (resOpsM.size() > sizeForArray) errorWithContext("Array not large enough to store list");
            String[] tempM = new String[sizeForArray];
            for (int i = 0; i < resOpsM.size(); i++)
                tempM[i] = resOpsM.get(i);
            
            this.storage.putArray(identifier.tokenStr, tempM);
            return rt;
            
        }

        p("REACHING END");
        //
        return rt;
    }

    /**
     * Method to check and return an equality statement. Currently made a
     * function because I'm not quite sure if this will only be called in one
     * place. Note: LHS and RHS may or may not be symbols, so it needs to check
     * for both. Will not throw an error if it doesn't exist in the storage.
     *
     * @param leftToken Left hand side of the equality statement
     * @param comparison Operator to compare against
     * @return True or False based on whether or not the item is equal
     * @throws Exception Exception thrown if something went seriously wrong
     */
    private ResultValue evaluateEquality(boolean execute, Token leftToken, String comparison) throws Exception {
        ResultValue retVal = new ResultValue(null, 0);
        boolean notSet = false;
        if (leftToken.tokenStr.equals("not")) {
            scanner.getNext();
            leftToken = scanner.currentToken;
            comparison = scanner.nextToken.tokenStr;
            notSet = true;
        }


        //meaning there was an ending to the comparison of an if or while
        if (comparison.equals(":")) {
            if (leftToken.subClassif == Token.BOOLEAN) {
                return new ResultValue(leftToken.tokenStr, Token.BOOLEAN);
            } else if (leftToken.subClassif == Token.IDENTIFIER) {
                if (execute) {
                    retVal.szValue = this.storage.get(this, leftToken.tokenStr);
                }
                retVal.type = Token.BOOLEAN;
                if (retVal.szValue == null) {
                    errorWithContext("Bad identifier given. Usage: " + leftToken.tokenStr);
                }
                if(notSet)
                    return numeric.keywordNot(retVal);
                return retVal;
            } else {
                errorWithContext("Lone token MUST be a boolean! Given: " + leftToken.tokenStr);
            }
        }

        //there is more than just one token
        // Advance the cursor
        scanner.getNext();
        Token rightToken = scanner.currentToken;    // Solely for readability
        if (execute) {
            ResultValue resOp1 = new ResultValue(leftToken.tokenStr, leftToken.subClassif);
            ResultValue resOp2 = new ResultValue(rightToken.tokenStr, rightToken.subClassif);
            if((resOp1.szValue = this.storage.get(this, leftToken.tokenStr)) == null)
                resOp1.szValue = leftToken.tokenStr;
            if((resOp2.szValue = this.storage.get(this, rightToken.tokenStr)) == null)
                resOp2.szValue = rightToken.tokenStr;
            if(resOp1.type == Token.IDENTIFIER
                    && ((STIdentifiers) symbolTable.getSymbol(leftToken.tokenStr)).iParmType == Token.STRING) {
                resOp1.type = Token.STRING;
            }
            if (resOp2.type == Token.IDENTIFIER
                        && ((STIdentifiers) symbolTable.getSymbol(rightToken.tokenStr)).iParmType == Token.STRING) {
                resOp2.type = Token.STRING;
            }
            retVal = numeric.equalValue(resOp1, resOp2, comparison);

            scanner.getNext();

            ResultValue rightHS; // Just in case;
            switch (scanner.currentToken.tokenStr){
                case "and":
                    scanner.getNext();
                    rightHS = evaluateEquality(execute, scanner.currentToken, scanner.getNext());
                    retVal = numeric.keywordAnd(retVal, rightHS);
                    break;
                case "or":
                    scanner.getNext();
                    rightHS = evaluateEquality(execute, scanner.currentToken, scanner.getNext());
                    retVal = numeric.keywordOr(retVal, rightHS);
                    break;
                case ":":
                    break;
                default:
                    errorWithContext("Expected ':' not found at end of statement. Given: " + scanner.currentToken.tokenStr);
            }
        }
        if(notSet)
            return numeric.keywordNot(retVal);
        return retVal;
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

        //p("expressions: " + scanner.currentToken.tokenStr);
        ResultValue rt = null;
        //save off current token
        Token firstToken = scanner.currentToken;

        boolean firstIsNegative = false;
        int x = 0;
        float y = (float) 0.0;
        int iFirstTokenType;
        int iFirstTokenIntValue;
        float fFirstTokenFloatValue;

        //get all negative signs (Unary -)
        while ("U-".equals(scanner.currentToken.tokenStr)) {

            firstIsNegative = !firstIsNegative;
            scanner.getNext();
            firstToken = scanner.currentToken;

        }
        //p("subClassif = " + firstToken.subClassif);
        switch (firstToken.subClassif) {
            //LHS is an identifier
            
            case Token.IDENTIFIER:
                //set type for future evals
                //p(firstToken.tokenStr);
                iFirstTokenType = ((STIdentifiers) this.symbolTable.getSymbol(firstToken.tokenStr)).iParmType;
                
                //if referencing array value
                if (((STIdentifiers) this.symbolTable.getSymbol(firstToken.tokenStr)).iStruct == Token.ARRAY_UNBOUND 
                        || ((STIdentifiers) this.symbolTable.getSymbol(firstToken.tokenStr)).iStruct == Token.ARRAY_FIXED) {
                    
                    //arrays must be followed with [ or error
                    if (!"[".equals(scanner.getNext())) errorWithContext("Expected \"[\" when referencing arrays");
                    
                    scanner.getNext();
                    int indexOfArray = Integer.parseInt(expressions(execute).szValue);
                    if (!"]".equals(scanner.currentToken.tokenStr)) errorWithContext("Expected \"]\" when referencing arrays");
                    return new ResultValue(this.storage.getFromArray(firstToken.tokenStr, indexOfArray), Token.INTEGER);
                }
                    
                    
                    
                //means it was a simple assignment
                if (";".equals(scanner.getNext()) || ",".equals(scanner.currentToken.tokenStr) || ")".equals(scanner.currentToken.tokenStr) || "]".equals(scanner.currentToken.tokenStr)) {
                    if (firstIsNegative) {
                        switch (((STIdentifiers) symbolTable.getSymbol(firstToken.tokenStr)).iParmType) {
                            case Token.INTEGER:
                                return new ResultValue((Integer.parseInt(storage.get(this, firstToken.tokenStr)) * -1) + "", Token.INTEGER);
                            case Token.FLOAT:
                                return new ResultValue((Float.parseFloat(storage.get(this, firstToken.tokenStr)) * -1) + "", Token.FLOAT);
                        }
                    }

                    rt = new ResultValue(storage.get(this, firstToken.tokenStr), Token.SEPARATOR);

                    return rt;

                } else if ("+".equals(scanner.currentToken.tokenStr)) {

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {

                        case Token.IDENTIFIER:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    x = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        x = x * -1;
                                    }
                                    if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                        x = x + Integer.parseInt(expressions(execute).szValue);
                                        rt = new ResultValue(x + "", Token.INTEGER);
                                    } else {
                                        y = x + Float.parseFloat(expressions(execute).szValue);
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    }
                                    ///////////////////////// LEFT OFF HERE
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                        y = y + Integer.parseInt(expressions(execute).szValue);
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    } else {
                                        y = y + Float.parseFloat(expressions(execute).szValue);
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    }
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                        //RHS is an integer and LHS is IDENTIFIER
                        case Token.INTEGER:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    x = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        x = x * -1;
                                    }
                                    x = x + Integer.parseInt(expressions(execute).szValue);
                                    rt = new ResultValue(x + "", Token.INTEGER);
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = y + Float.parseFloat(expressions(execute).szValue);
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                        //LHS is identifier, RHS is float
                        case Token.FLOAT:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    y = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = y + Float.parseFloat(expressions(execute).szValue);
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = y + Float.parseFloat(expressions(execute).szValue);
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                    }

                    return rt;

                } else if ("-".equals(scanner.currentToken.tokenStr)) {

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {

                        case Token.IDENTIFIER:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    x = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        x = x * -1;
                                    }
                                    if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                        x = x - Integer.parseInt(expressions(execute).szValue);
                                        rt = new ResultValue(x + "", Token.INTEGER);
                                    } else {
                                        y = x - Float.parseFloat(expressions(execute).szValue);
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    }
                                    ///////////////////////// LEFT OFF HERE
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                        y = y - Integer.parseInt(expressions(execute).szValue);
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    } else {
                                        y = y - Float.parseFloat(expressions(execute).szValue);
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    }
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                        //RHS is an integer and LHS is IDENTIFIER
                        case Token.INTEGER:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    x = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        x = x * -1;
                                    }
                                    x = x - Integer.parseInt(expressions(execute).szValue);
                                    rt = new ResultValue(x + "", Token.INTEGER);
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = y - Float.parseFloat(expressions(execute).szValue);
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                        //LHS is identifier, RHS is float
                        case Token.FLOAT:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    y = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = y - Float.parseFloat(expressions(execute).szValue);
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = y - Float.parseFloat(expressions(execute).szValue);
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                    }

                    return rt;

                } else if ("*".equals(scanner.currentToken.tokenStr)) {

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {

                        case Token.IDENTIFIER:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    x = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        x = x * -1;
                                    }
                                    if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                        x = x * Integer.parseInt(expressions(execute).szValue);
                                        rt = new ResultValue(x + "", Token.INTEGER);
                                    } else {
                                        y = x * Float.parseFloat(expressions(execute).szValue);
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    }
                                    ///////////////////////// LEFT OFF HERE
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                        y = y * Integer.parseInt(expressions(execute).szValue);
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    } else {
                                        y = y * Float.parseFloat(expressions(execute).szValue);
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    }
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                        //RHS is an integer and LHS is IDENTIFIER
                        case Token.INTEGER:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    x = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        x = x * -1;
                                    }
                                    x = x * Integer.parseInt(expressions(execute).szValue);
                                    rt = new ResultValue(x + "", Token.INTEGER);
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = y * Float.parseFloat(expressions(execute).szValue);
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                        //LHS is identifier, RHS is float
                        case Token.FLOAT:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    y = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = y * Float.parseFloat(expressions(execute).szValue);
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = y * Float.parseFloat(expressions(execute).szValue);
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                    }

                    return rt;

                } else if ("/".equals(scanner.currentToken.tokenStr)) {

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {

                        case Token.IDENTIFIER:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    x = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        x = x * -1;
                                    }
                                    if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                        x = x / Integer.parseInt(expressions(execute).szValue);
                                        rt = new ResultValue(x + "", Token.INTEGER);
                                    } else {
                                        y = x / Float.parseFloat(expressions(execute).szValue);
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    }
                                    ///////////////////////// LEFT OFF HERE
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                        y = y / Integer.parseInt(expressions(execute).szValue);
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    } else {
                                        y = y / Float.parseFloat(expressions(execute).szValue);
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    }
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                        //RHS is an integer and LHS is IDENTIFIER
                        case Token.INTEGER:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    x = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        x = x * -1;
                                    }
                                    x = x / Integer.parseInt(expressions(execute).szValue);
                                    rt = new ResultValue(x + "", Token.INTEGER);
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = y / Float.parseFloat(expressions(execute).szValue);
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                        //LHS is identifier, RHS is float
                        case Token.FLOAT:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    y = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = y / Float.parseFloat(expressions(execute).szValue);
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = y / Float.parseFloat(expressions(execute).szValue);
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                    }

                    return rt;

                } else if ("^".equals(scanner.currentToken.tokenStr)) {

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {

                        case Token.IDENTIFIER:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    x = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        x = x * -1;
                                    }
                                    if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                        x = (int) Math.pow(x, Integer.parseInt(expressions(execute).szValue));
                                        rt = new ResultValue(x + "", Token.INTEGER);
                                    } else {
                                        y = (float) Math.pow(x, Float.parseFloat(expressions(execute).szValue));
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    }
                                    ///////////////////////// LEFT OFF HERE
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                        y = (float) Math.pow(y, Integer.parseInt(expressions(execute).szValue));
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    } else {
                                        y = (float) Math.pow(y, Float.parseFloat(expressions(execute).szValue));
                                        rt = new ResultValue(y + "", Token.FLOAT);
                                    }
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                        //RHS is an integer and LHS is IDENTIFIER
                        case Token.INTEGER:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    x = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        x = x * -1;
                                    }
                                    x = (int) Math.pow(x, Integer.parseInt(expressions(execute).szValue));
                                    rt = new ResultValue(x + "", Token.INTEGER);
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = (float) Math.pow(y, Float.parseFloat(expressions(execute).szValue));
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                        //LHS is identifier, RHS is float
                        case Token.FLOAT:
                            switch (iFirstTokenType) {
                                case Token.INTEGER:
                                    y = Integer.parseInt(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = (float) Math.pow(y, Float.parseFloat(expressions(execute).szValue));
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                case Token.FLOAT:
                                    y = Float.parseFloat(storage.get(this, firstToken.tokenStr));
                                    if (firstIsNegative) {
                                        y = y * -1;
                                    }
                                    y = (float) Math.pow(y, Float.parseFloat(expressions(execute).szValue));
                                    rt = new ResultValue(y + "", Token.FLOAT);
                                    break;
                                default:
                                    errorWithContext("improper number format, number must be float or int");
                            }
                            break;
                    }

                    return rt;
                } else // Check for equalities
                {
                    return rt = evaluateEquality(execute, firstToken, scanner.currentToken.tokenStr);
                }

            ///////end additions 
            // LHS is integer, RHS could be (Identifier, Int, or Float)
            case Token.INTEGER:
                //if negative, make negative
                //p(1802);
                if (firstIsNegative) {
                    firstToken.tokenStr = (Integer.parseInt(firstToken.tokenStr) * -1) + "";
                }

                //means it was a simple assignment
                if (";".equals(scanner.getNext()) || ",".equals(scanner.currentToken.tokenStr) || ")".equals(scanner.currentToken.tokenStr) || "]".equals(scanner.currentToken.tokenStr)) {
                    rt = new ResultValue(firstToken.tokenStr, Token.SEPARATOR);
                    //p(rt.szValue + " is the result");
                    return rt;

                } else if ("+".equals(scanner.currentToken.tokenStr)) {

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        //RHS is Identifier, LHS is Int
                        case Token.IDENTIFIER:

                            if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                x = Integer.parseInt(firstToken.tokenStr);
                                x = x + Integer.parseInt(expressions(execute).szValue);
                                rt = new ResultValue(x + "", Token.INTEGER);
                            } else {
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = y + Float.parseFloat(expressions(execute).szValue);
                                rt = new ResultValue(y + "", Token.FLOAT);
                            }

                            break;
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

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.IDENTIFIER:

                            if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                x = Integer.parseInt(firstToken.tokenStr);
                                x = x - Integer.parseInt(expressions(execute).szValue);
                                rt = new ResultValue(x + "", Token.INTEGER);
                            } else {
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = y - Float.parseFloat(expressions(execute).szValue);
                                rt = new ResultValue(y + "", Token.FLOAT);
                            }

                            break;
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

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.IDENTIFIER:

                            if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                x = Integer.parseInt(firstToken.tokenStr);
                                x = x * Integer.parseInt(expressions(execute).szValue);
                                rt = new ResultValue(x + "", Token.INTEGER);
                            } else {
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = y * Float.parseFloat(expressions(execute).szValue);
                                rt = new ResultValue(y + "", Token.FLOAT);
                            }

                            break;
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

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {

                        case Token.IDENTIFIER:

                            if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                x = Integer.parseInt(firstToken.tokenStr);
                                x = x / Integer.parseInt(expressions(execute).szValue);
                                rt = new ResultValue(x + "", Token.INTEGER);
                            } else {
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = y / Float.parseFloat(expressions(execute).szValue);
                                rt = new ResultValue(y + "", Token.FLOAT);
                            }

                            break;

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
                } else if ("^".equals(scanner.currentToken.tokenStr)) {

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.IDENTIFIER:

                            if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                x = Integer.parseInt(firstToken.tokenStr);
                                x = (int) Math.pow(x, Integer.parseInt(expressions(execute).szValue));
                                rt = new ResultValue(x + "", Token.INTEGER);
                            } else {
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = (float) Math.pow(y, Float.parseFloat(expressions(execute).szValue));
                                rt = new ResultValue(y + "", Token.FLOAT);
                            }

                            break;
                        case Token.INTEGER:
                            y = Float.parseFloat(firstToken.tokenStr);
                            //y = y / Integer.parseInt(expressions(execute).szValue);
                            Math.pow(y, Integer.parseInt(expressions(execute).szValue));
                            rt = new ResultValue(y + "", Token.INTEGER);
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            //y = (y / Float.parseFloat(expressions(execute).szValue));
                            Math.pow(y, Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "", Token.FLOAT);
                            break;
                    }
                    return rt;
                } else // Check for equalities
                {
                    return rt = evaluateEquality(execute, firstToken, scanner.currentToken.tokenStr);
                }
            case Token.FLOAT:
                //if negative, make negative
                if (firstIsNegative) {
                    firstToken.tokenStr = (Integer.parseInt(firstToken.tokenStr) * -1) + "";
                }

                //means it was a simple assignment
                if (";".equals(scanner.getNext()) || ",".equals(scanner.currentToken.tokenStr) || ")".equals(scanner.currentToken.tokenStr) || "]".equals(scanner.currentToken.tokenStr)) {
                    rt = new ResultValue(firstToken.tokenStr, Token.SEPARATOR);

                    return rt;

                } else if ("+".equals(scanner.currentToken.tokenStr)) {

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        //RHS is FLOAT, LHS is identifier
                        case Token.IDENTIFIER:

                            if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                //x = Integer.parseInt(firstToken.tokenStr);
                                //x = x + Integer.parseInt(expressions(execute).szValue);
                                //rt = new ResultValue(x + "", Token.INTEGER);
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = y + Integer.parseInt(expressions(execute).szValue);
                                rt = new ResultValue(y + "", Token.FLOAT);
                            } else {
                                //y = Float.parseFloat(firstToken.tokenStr);
                                //y = y + Float.parseFloat(expressions(execute).szValue);
                                //rt = new ResultValue(y + "", Token.FLOAT);
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = (y + Float.parseFloat(expressions(execute).szValue));
                                rt = new ResultValue(y + "", Token.FLOAT);
                            }

                            break;
                        case Token.INTEGER:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = y + Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(y + "", Token.FLOAT);
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = (y + Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "", Token.FLOAT);
                            break;
                    }

                    return rt;

                } else if ("-".equals(scanner.currentToken.tokenStr)) {

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.IDENTIFIER:

                            if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                //x = Integer.parseInt(firstToken.tokenStr);
                                //x = x + Integer.parseInt(expressions(execute).szValue);
                                //rt = new ResultValue(x + "", Token.INTEGER);
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = y - Integer.parseInt(expressions(execute).szValue);
                                rt = new ResultValue(y + "", Token.FLOAT);
                            } else {
                                //y = Float.parseFloat(firstToken.tokenStr);
                                //y = y + Float.parseFloat(expressions(execute).szValue);
                                //rt = new ResultValue(y + "", Token.FLOAT);
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = (y - Float.parseFloat(expressions(execute).szValue));
                                rt = new ResultValue(y + "", Token.FLOAT);
                            }

                            break;
                        case Token.INTEGER:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = y - Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(y + "", Token.FLOAT);
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = (y - Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "", Token.FLOAT);
                            break;
                    }

                    return rt;

                } else if ("*".equals(scanner.currentToken.tokenStr)) {

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.IDENTIFIER:

                            if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                //x = Integer.parseInt(firstToken.tokenStr);
                                //x = x + Integer.parseInt(expressions(execute).szValue);
                                //rt = new ResultValue(x + "", Token.INTEGER);
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = y * Integer.parseInt(expressions(execute).szValue);
                                rt = new ResultValue(y + "", Token.FLOAT);
                            } else {
                                //y = Float.parseFloat(firstToken.tokenStr);
                                //y = y + Float.parseFloat(expressions(execute).szValue);
                                //rt = new ResultValue(y + "", Token.FLOAT);
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = (y * Float.parseFloat(expressions(execute).szValue));
                                rt = new ResultValue(y + "", Token.FLOAT);
                            }

                            break;
                        case Token.INTEGER:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = y * Integer.parseInt(expressions(execute).szValue);
                            rt = new ResultValue(y + "", Token.FLOAT);
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            y = (y * Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "", Token.FLOAT);
                            break;
                    }

                    return rt;

                } else if ("/".equals(scanner.currentToken.tokenStr)) {

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.IDENTIFIER:

                            if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                //x = Integer.parseInt(firstToken.tokenStr);
                                //x = x + Integer.parseInt(expressions(execute).szValue);
                                //rt = new ResultValue(x + "", Token.INTEGER);
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = y / Integer.parseInt(expressions(execute).szValue);
                                rt = new ResultValue(y + "", Token.INTEGER);
                            } else {
                                //y = Float.parseFloat(firstToken.tokenStr);
                                //y = y + Float.parseFloat(expressions(execute).szValue);
                                //rt = new ResultValue(y + "", Token.FLOAT);
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = (y / Float.parseFloat(expressions(execute).szValue));
                                rt = new ResultValue(y + "", Token.FLOAT);
                            }

                            break;
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
                } else if ("^".equals(scanner.currentToken.tokenStr)) {

                    scanner.getNext();
                    switch (scanner.currentToken.subClassif) {
                        case Token.IDENTIFIER:

                            if (((STIdentifiers) symbolTable.getSymbol(scanner.currentToken.tokenStr)).iParmType == Token.INTEGER) {
                                //x = Integer.parseInt(firstToken.tokenStr);
                                //x = x + Integer.parseInt(expressions(execute).szValue);
                                //rt = new ResultValue(x + "", Token.INTEGER);
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = (float) Math.pow(y, Integer.parseInt(expressions(execute).szValue));
                                rt = new ResultValue(y + "", Token.FLOAT);
                            } else {
                                //y = Float.parseFloat(firstToken.tokenStr);
                                //y = y + Float.parseFloat(expressions(execute).szValue);
                                //rt = new ResultValue(y + "", Token.FLOAT);
                                y = Float.parseFloat(firstToken.tokenStr);
                                y = (float) Math.pow(y, Float.parseFloat(expressions(execute).szValue));
                                rt = new ResultValue(y + "", Token.FLOAT);
                            }

                            break;
                        case Token.INTEGER:
                            y = Float.parseFloat(firstToken.tokenStr);
                            //y = y / Integer.parseInt(expressions(execute).szValue);
                            y = (float) Math.pow(y, Integer.parseInt(expressions(execute).szValue));
                            rt = new ResultValue(y + "", Token.FLOAT);
                            break;
                        case Token.FLOAT:
                            y = Float.parseFloat(firstToken.tokenStr);
                            //y = (y / Float.parseFloat(expressions(execute).szValue));
                            y = (float) Math.pow(y, Float.parseFloat(expressions(execute).szValue));
                            rt = new ResultValue(y + "", Token.FLOAT);
                            break;
                    }
                    return rt;
                } else // Check for equalities
                {
                    return rt = evaluateEquality(execute, firstToken, scanner.currentToken.tokenStr);
                }
            case Token.BOOLEAN:
                //if negative, make negative
                if (firstIsNegative) {
                    firstToken.tokenStr = (Integer.parseInt(firstToken.tokenStr) * -1) + "";
                }
                //means it was a simple assignment
                if (";".equals(scanner.getNext())) {
                    rt = new ResultValue(firstToken.tokenStr, Token.SEPARATOR);
                    return rt;
                }
        }
        errorWithContext("Bad joo-joo found: " + scanner.currentToken.tokenStr);
        return rt;
    }

    /**
     * Debugging method made by Justin. Needs to be better, but by God it gets
     * the job done.
     *
     * @param s
     */
    private void p(String s) {

        System.out.println("OURDEBUGLINE::: " + s);

    }

    /**
     * Even more janky debug statement, but it's just as useful
     *
     * @param LineNumber Line number we're on
     */
    private void p(int LineNumber) {
        System.out.println("Line Number::: " + LineNumber);
    }

    /**
     * Returns the current token as a string. Useful for debugging/error messages
     * @return Current token string
     */
    private String ct(){ return scanner.currentToken.tokenStr;}

}
