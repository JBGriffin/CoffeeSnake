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

        ArrayList<String> TokensM = new ArrayList();

        int iCountParen = 0;

        int iCountBracket = 0;

        ResultValue rt;

        Token firstTokenEncountered = parser.scanner.currentToken;
        boolean firstIsNegative = false;
        while (!";".equals(parser.scanner.currentToken.tokenStr)) {

            Token firstToken = parser.scanner.currentToken;

            firstIsNegative = false;

            while ("U-".equals(parser.scanner.currentToken.tokenStr)) {
                firstIsNegative = !firstIsNegative;
                parser.scanner.getNext();
                firstToken = parser.scanner.currentToken;

            }

            if (parser.scanner.currentToken.primClassif == Token.FUNCTION) {
                //System.out.println(parser.scanner.currentToken.tokenStr);
                TokensM.add(parser.function(execute).szValue);
                continue;

            }
            switch (parser.scanner.currentToken.tokenStr) {
                case "[":
                case "(":
                    parser.scanner.getNext();
                    rt = workExpressions(execute);
                    TokensM.add(rt.szValue);
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
                case ":":
                case ">":
                case "<":
                case "==":
                case "!=":
                case ">=":
                case "<=":
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
                    
                    if (firstIsNegative) {
                        double dHold = -1 * Float.parseFloat(saveString);
                        saveString = dHold + "";
                    }
                    TokensM.add(saveString);
                    break;
            }
            if (";".equals(parser.scanner.currentToken.tokenStr)) {
                break;
            }
            parser.scanner.getNext();
        }
        return this.evalExpression(TokensM);

    }

    /**
     *
     * This is passed an arraylist of token strings from a given collection. It
     * will evaluate in infix order and return value to caller.
     *
     * @param LocalTokensM
     * @return double value of expression given
     */
    public ResultValue evalExpression(ArrayList<String> LocalTokensM) {

        Stack<String> operatorStack = new Stack();
        Stack<Double> valueStack = new Stack();
        Double dValue = 0.0;
        while (!LocalTokensM.isEmpty()) {
            //get first token
            String token = LocalTokensM.remove(0);

            //if token is an operator
            if (!isOperator(token)) {
                if (token.isEmpty()) {
                    break;
                }
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
        //System.out.println(dValue);
        return new ResultValue(dValue + "", Token.FLOAT);

    }

    /**
     * evaluate takes to values and an operator to evaluate. It can handle +-/^*
     * and returns value as a double
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
     *
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
     *
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
     * stringExpressions evaluates strings until ; handles concats, simples, and
     * more
     *
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
            if (parser.scanner.currentToken.tokenStr.equals(";")) {
                break;
            }
            parser.scanner.getNext();

        }
        return new ResultValue(sb.toString(), Token.STRING);

    }

    public String[] arrayExpressions(boolean execute, String targetArrayName) throws Exception {

        int iNewArraySize = 0;

        int iMaxSize = 0;

        String[] tempM = this.parser.storage.getArray(targetArrayName);
        iMaxSize = tempM.length;
        /*
        for(String s : tempM) {
            if (s == null) break;
            iNewArraySize++;
        }*/

        iNewArraySize = tempM.length;

        String[] srcArrayM = new String[iNewArraySize];

        if ("=".equals(parser.scanner.currentToken.tokenStr)) {
            parser.scanner.getNext();
        }

        Token srcArrayToken = parser.scanner.currentToken;

        int i = 0;
        String workingString;

        //handle string
        if (srcArrayToken.subClassif == Token.STRING) {
            workingString = srcArrayToken.tokenStr;
            if (workingString.length() <= iMaxSize) {
                for (char c : workingString.toCharArray()) {
                    tempM[i++] = c + "";
                }
            } else {

                //workingString size is great than iMaxSize
                //go while i < maxSize
                for (char c : workingString.toCharArray()) {
                    if (i >= iMaxSize) {
                        break;
                    }
                    tempM[i++] = c + "";
                }

            }

            return tempM;

        } else if (((STIdentifiers) this.parser.symbolTable.getSymbol(srcArrayToken.tokenStr)).iStruct == Token.STRING) {
            //handle string identifier
            workingString = srcArrayToken.tokenStr;
            workingString = parser.storage.get(parser, workingString);
            if (workingString.length() <= iMaxSize) {
                for (char c : workingString.toCharArray()) {
                    tempM[i++] = c + "";
                }
            } else {

                //workingString size is great than iMaxSize
                //go while i < maxSize
                for (char c : workingString.toCharArray()) {
                    if (i >= iMaxSize) {
                        break;
                    }
                    tempM[i++] = c + "";
                }

            }

            return tempM;

        } else {
            //handle array copy

            workingString = srcArrayToken.tokenStr;
            srcArrayM = parser.storage.getArray(workingString);
            if (srcArrayM.length <= iMaxSize) {
                for (String c : srcArrayM) {
                    tempM[i++] = c;
                }
            } else {

                //workingString size is great than iMaxSize
                //go while i < maxSize
                for (String c : srcArrayM) {
                    if (i >= iMaxSize) {
                        break;
                    }
                    tempM[i++] = c;
                }

            }

            return tempM;

        }

    }

}
