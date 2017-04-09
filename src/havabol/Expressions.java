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
        boolean firstIsNegative = false;
        
        while (!";".equals(parser.scanner.currentToken.tokenStr)) {
            //System.out.println(parser.scanner.currentToken.tokenStr);
            switch (parser.scanner.currentToken.tokenStr) {
                case "(":
                    parser.scanner.getNext();
                    rt = workExpressions();
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
                case ":":
                    return this.evalExpression(TokensM);
                default:
                    String saveString = parser.scanner.currentToken.tokenStr;
                    if (parser.scanner.currentToken.subClassif == Token.IDENTIFIER) {
                        saveString = this.parser.storage.get(parser, saveString);
                    }
                    this.TokensM.add(saveString);
                    break;
            }
            parser.scanner.getNext();
        }
        return this.evalExpression(TokensM);

        
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
        if (!valueStack.empty())
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
