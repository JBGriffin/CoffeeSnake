/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package havabol;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Justin Hooge
 */
public class Expressions {

    Parser parser;

    ArrayList<String> TokensM = new ArrayList();

    Expressions(Parser parser) {

        this.parser = parser;

    }

    /**
     *
     *
     *
     * @param execute
     * @return
     * @throws java.lang.Exception
     */
    public ResultValue workExpressions(boolean execute) throws Exception {

        int iCountParen = 0;

        int iCountBracket = 0;

        ResultValue rt;

        Token firstTokenEncountered = parser.scanner.currentToken;
        boolean firstIsNegative = false;

        while (!";".equals(parser.scanner.currentToken.tokenStr)) {
            //System.out.println(parser.scanner.currentToken.tokenStr);
            if (parser.scanner.currentToken.primClassif == Token.FUNCTION) {
                //System.out.println(parser.scanner.currentToken.tokenStr);
                this.TokensM.add(parser.function(execute).szValue);
                continue;

            }
            switch (parser.scanner.currentToken.tokenStr) {
                case "[":
                case "(":
                    parser.scanner.getNext();
                    rt = workExpressions(execute);
                    this.TokensM.add(rt.szValue);
                    continue;
                case ")":
                    parser.scanner.getNext();
                    return this.evalExpression(TokensM);
                case "]":
                    parser.scanner.getNext();
                    return this.evalExpression(TokensM);
                case ",":
                    return this.evalExpression(TokensM);
                case "to":
                case "by":
                case ":": case">": case"<": case "==": case "!=": case ">=": case "<=":
                    return this.evalExpression(TokensM);
                default:
                    String saveString = parser.scanner.currentToken.tokenStr;
                    if (parser.scanner.currentToken.subClassif == Token.IDENTIFIER) {
                        if (((STIdentifiers) parser.symbolTable.getSymbol(saveString)).iStruct == Token.ARRAY_FIXED
                                || ((STIdentifiers) parser.symbolTable.getSymbol(saveString)).iStruct == Token.ARRAY_UNBOUND) {

                            //handle array
                            String arrayName = saveString;
                            parser.scanner.getNext();
                            parser.scanner.getNext();
                            int index = (int) Float.parseFloat(workExpressions(true).szValue);;
                            //if (":".equals(parser.scanner.currentToken.tokenStr)) break;
                            saveString = this.parser.storage.getFromArray(arrayName, index);
                            //System.out.println("getting from array " + saveString);
                            //System.out.println("returned with " + parser.scanner.currentToken.tokenStr);
                        } else {
                            //need to get array if it is array
                            saveString = this.parser.storage.get(parser, saveString);

                        }
                    }
                    //System.out.println("Here about to save " + saveString);
                    this.TokensM.add(saveString);
                    break;
            }
            if (";".equals(parser.scanner.currentToken.tokenStr)) break;
            parser.scanner.getNext();
        }
        return this.evalExpression(TokensM);

    }

    /**
     * 
     * This is passed an arraylist of token strings from a given collection.
     * It will evaluate in infix order and return value to caller.
     * 
     * @param TokensM
     * @return double value of expression given
     */
    public ResultValue evalExpression(ArrayList<String> TokensM) {

        Stack<String> operatorStack = new Stack();
        Stack<Double> valueStack = new Stack();
        Double dValue = 0.0;
        while (!TokensM.isEmpty()) {
            //get first token
            String token = TokensM.remove(0);

            //if token is an operator
            if (!isOperator(token)) {
                dValue = Double.parseDouble(token);
                valueStack.push(dValue);

            } else if (isOperator(token)) {
                while (operatorStack.isEmpty() == false && (getPrio(operatorStack.peek()) >= getPrio(token))) {
                    String operator = operatorStack.pop();
                    double val1 = valueStack.pop();
                    double val2 = valueStack.pop();
                    dValue = evaluate(operator, val1, val2);
                    valueStack.push(dValue);

                }
                operatorStack.push(token);
            }

        }

        while (!operatorStack.isEmpty()) {

            String operator = operatorStack.pop();
            double val1 = valueStack.pop();
            double val2 = valueStack.pop();
            dValue = evaluate(operator, val1, val2);
            valueStack.push(dValue);

        }
        if (!valueStack.empty()) {
            dValue = valueStack.pop();
        }
        return new ResultValue(dValue + "", Token.FLOAT);

    }

    /**
     * evaluate takes to values and an operator to evaluate.
     * It can handle +-/^* and returns value as a double
     * 
     * @param operator
     * @param val1
     * @param val2
     * @return 
     */
    public double evaluate(String operator, double val1, double val2) {
        double result = 0.0;
        if (operator.equals("^")) {
            result = Math.pow(val2, val1);
        }
        if (operator.equals("+")) {
            result = val1 + val2;
        }
        if (operator.equals("-")) {
            result = val2 - val1;
        }
        if (operator.equals("*")) {
            result = val1 * val2;
        }
        if (operator.equals("/")) {
            result = val2 / val1;
        }
        return result;
    }

    
    /**
     * Takes a String ch and returns if the character is an operator
     * @param ch
     * @return 
     */
    public boolean isOperator(String ch) {
        if (ch.equals("+") || ch.equals("-") || ch.equals("*") || ch.equals("/") || ch.equals("^")) {
            return true;
        }
        return false;
    }

    /**
     * getPrio is called to return operator priority
     * @param ch
     * @return 
     */
    public int getPrio(String ch) {
        int iPriority = 0;

        if (ch.equals("^")) {
            iPriority = 3;
        } else if (ch.equals("*") || ch.equals("/")) {
            iPriority = 2;
        } else if (ch.equals("+") || ch.equals("-")) {
            iPriority = 1;
        } else if (ch.equals("(") || ch.equals(")")) {
            iPriority = 0;
        }

        return iPriority;
    }

    
    /**
     * stringExpressions evaluates strings until ;
     * handles concats, simples, and more
     * @param execute
     * @return value of the string
     * @throws Exception 
     */
    public ResultValue stringExpressions(boolean execute) throws Exception {
        StringBuilder sb = new StringBuilder();
        Token firstToken = parser.scanner.currentToken;

        while (!";".equals(parser.scanner.currentToken.tokenStr)) {
            String stringToAppend = "";
            if ("#".equals(parser.scanner.currentToken.tokenStr) || "+".equals(parser.scanner.currentToken.tokenStr)) {

                if (";".equals(parser.scanner.getNext())) {
                    parser.errorWithContext("Missing string to concat");
                }

                continue;

            } else if (parser.scanner.currentToken.subClassif == Token.STRING) {

                stringToAppend = parser.scanner.currentToken.tokenStr;

            } else if (parser.scanner.currentToken.subClassif == Token.IDENTIFIER) {

                if (((STIdentifiers) this.parser.symbolTable.getSymbol(parser.scanner.currentToken.tokenStr)).iStruct == Token.STRING) {
                    
                    if (parser.scanner.nextToken.tokenStr.equals("[")) {
                        String stringName = parser.scanner.currentToken.tokenStr;
                        parser.scanner.getNext();
                        parser.scanner.getNext();
                        int startIndex = (int) Float.parseFloat(this.workExpressions(execute).szValue);
                        stringToAppend = this.parser.storage.getCharsFromString(parser, stringName, startIndex, startIndex);
                    } else {
                        stringToAppend = this.parser.storage.get(parser, parser.scanner.currentToken.tokenStr);
                    }
                                      
                } else {
                    //it is an array (well it should be lol)
                    String arrayName = parser.scanner.currentToken.tokenStr;
                    parser.scanner.getNext();
                    parser.scanner.getNext();
                    int index = (int) Float.parseFloat(this.workExpressions(true).szValue);
                    stringToAppend = this.parser.storage.getFromArray(arrayName, index);
                    sb.append(stringToAppend);
                    continue;

                }

            } else if (",".equals(parser.scanner.currentToken.tokenStr) 
                    || ")".equals(parser.scanner.currentToken.tokenStr)) {
                break;
            }

            sb.append(stringToAppend);
            if (parser.scanner.currentToken.tokenStr.equals(";")) break;
            parser.scanner.getNext();

        }
        return new ResultValue(sb.toString(), Token.STRING);

    }

}
