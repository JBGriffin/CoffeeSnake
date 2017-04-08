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
     * @return
     */
    public ResultValue workExpressions() throws Exception {

        int iCountParen = 0;

        int iCountBracket = 0;

        ResultValue rt;

        Token firstTokenEncountered = parser.scanner.currentToken;

        System.out.println(parser.scanner.currentToken.tokenStr);
        /*
        String sHold = firstTokenEncountered.tokenStr;
        if (firstTokenEncountered.subClassif == Token.IDENTIFIER) {
            sHold = this.parser.storage.get(parser, sHold);
        }
        this.TokensM.add(sHold);*/
        while (!";".equals(parser.scanner.currentToken.tokenStr)) {

            if ("(".equals(parser.scanner.currentToken.tokenStr)) {
                parser.scanner.getNext();
                rt = workExpressions();
                this.TokensM.add(rt.szValue);
                parser.scanner.getNext();
                continue;
            } else if (")".equals(parser.scanner.currentToken.tokenStr)) {
                return this.evalExpression(TokensM);
            } else if ("]".equals(parser.scanner.currentToken.tokenStr)) {
                return this.evalExpression(TokensM);
            } else {
                String saveString = parser.scanner.currentToken.tokenStr;
                if (parser.scanner.currentToken.subClassif == Token.IDENTIFIER) {
                    saveString = this.parser.storage.get(parser, saveString);
                }
                this.TokensM.add(saveString);
            }
            parser.scanner.getNext();
        }
        return this.evalExpression(TokensM);

        /*
            
            
            
            HOLD SLOT
            
            
            
            
         */
        //if value or identifer is first, go until ;
        /*
        if (firstTokenEncountered.primClassif == Token.OPERAND) {
            String s1 = firstTokenEncountered.tokenStr;
            if (firstTokenEncountered.subClassif == Token.IDENTIFIER) {
                s1 = this.parser.storage.get(parser, s1);
            }
            this.TokensM.add(s1);
            while (!";".equals(parser.scanner.getNext())) {

                if (parser.scanner.currentToken.primClassif == Token.SEPARATOR) {

                    rt = workExpressions();

                    this.TokensM.add(rt.szValue);

                    continue;

                }
                String saveString = parser.scanner.currentToken.tokenStr;
                if (parser.scanner.currentToken.subClassif == Token.IDENTIFIER) {
                    saveString = this.parser.storage.get(parser, saveString);
                }

                this.TokensM.add(saveString);

            }
            System.out.println(parser.scanner.currentToken.tokenStr);

            return this.evalExpression(TokensM);

        } //if ( go until close of that ) //// or [ go to ]
        else if (firstTokenEncountered.primClassif == Token.SEPARATOR) {
            System.out.println(firstTokenEncountered.tokenStr);
            parser.scanner.getNext();
            switch (firstTokenEncountered.tokenStr) {

                case "(":
                    firstTokenEncountered = parser.scanner.currentToken;
                    String s = firstTokenEncountered.tokenStr;
                    if (firstTokenEncountered.subClassif == Token.IDENTIFIER) {
                        s = this.parser.storage.get(parser, s);
                    }
                    this.TokensM.add(s);
                    while (!")".equals(parser.scanner.getNext())) {

                        if (parser.scanner.currentToken.primClassif == Token.SEPARATOR) {

                            if ("(".equals(parser.scanner.currentToken.tokenStr) || "[".equals(parser.scanner.currentToken.tokenStr)) {

                                rt = workExpressions();

                                this.TokensM.add(rt.szValue);

                                continue;

                            }

                        }
                        String saveString = parser.scanner.currentToken.tokenStr;
                        if (parser.scanner.currentToken.subClassif == Token.IDENTIFIER) {
                            saveString = this.parser.storage.get(parser, saveString);
                        }

                        this.TokensM.add(saveString);

                    }
                    System.out.println(parser.scanner.currentToken.tokenStr);

                    return this.evalExpression(TokensM);

                case "[":

                    break;
                default:
                    parser.errorWithContext("Unexpected Separator Found");

            }

        }

        return null;
         */
    }

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
        dValue = valueStack.pop();

        return new ResultValue(dValue + "", Token.FLOAT);

    }

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

    public boolean isOperator(String ch) {
        if (ch.equals("+") || ch.equals("-") || ch.equals("*") || ch.equals("/") || ch.equals("^")) {
            return true;
        }
        return false;
    }

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

}
