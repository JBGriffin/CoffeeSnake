package havabol;

import javax.xml.transform.Result;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;

import static java.lang.Math.pow;

/**
 * Converts given result value strings into a numeric. Operand type will be set as the leftmost given
 * operand. I.E., if given x += 1.0, x will be set as float.
 */
public class Numeric
{
    private int integerValue;
    private double doubleValue;
    private String strValue;    // display value
    private int type;           // INTEGER, FLOAT

    // Error items
    Parser parser;
    String operatorGiven;
    String position;

    /**
     * New constructor. Much better.
     * @param parser Parser instance for errors
     */
    public Numeric(Parser parser)
    {
        this.parser = parser;
    }

    /**
     * Old constructor I'm afraid to delete
     * @param parser Parser instance for errors
     * @param resultValue Value to create numeric
     * @param operator Operator to compare to
     * @param operand operand to compare to
     * @throws Exception raises exception if created incorrectly
     */
    public Numeric(Parser parser, ResultValue resultValue, String operator, String operand) throws Exception
    {
        this.parser = parser;
        this.operatorGiven = operator;
        this.position = operand;
        this.type = resultValue.type;
        this.strValue = resultValue.szValue;

        switch(this.type)
        {
            case Token.INTEGER:
                integerValue = Integer.parseInt(strValue);
                break;
            case Token.FLOAT:
                doubleValue = Float.parseFloat(strValue);
                break;
            /* Dirty!! Is there a better way to do this??
             */
            case Token.IDENTIFIER:
                try
                {
                    integerValue = Integer.parseInt(strValue);
                    type = Token.INTEGER;
                }
                catch (NumberFormatException e)
                {
                    try
                    {

                        doubleValue = Double.parseDouble(strValue);
                        type = Token.FLOAT;
                    }
                    catch (NumberFormatException i)
                    {
                        parser.errorWithContext("Expression must be a numeric. Item " + resultValue.szValue
                            + " given as " + resultValue.type);
                    }
                }
                break;
            default:
                parser.errorWithContext("Expression must be a numeric. Item " + resultValue.szValue + " given as " + resultValue.type);
        }
    }

    /**
     * Converts the given string into a numeric.
     * @param sConvert String to be converted
     * @return Result with type set to Integer or Float, with converted value
     * @throws Exception If not able to be converted, error is thrown.
     */
    private ResultValue strToNum(ResultValue sConvert) throws Exception{
        ResultValue returnVal = null;

        System.out.println("sConvert == " + sConvert.szValue + " of type: " + Token.strSubClassifM[sConvert.type]);
        try{
            returnVal.type = Token.INTEGER;
            int iTemp = Integer.parseInt(sConvert.szValue);
            System.out.println("iTemp == " + iTemp);
            returnVal.szValue = iTemp + "";
        } catch (Exception e){}
        try{
            returnVal.type = Token.FLOAT;
            double dTemp = Double.parseDouble(sConvert.szValue);
            returnVal.szValue = dTemp + "";
        } catch (Exception e)
        {
            parser.errorWithContext("Could not convert " + sConvert + " into a Numeric.");
        }

        return returnVal;
    }

    /**
     * Subtracts two values together. Returned type is based on left hand side
     * @param leftVal Typing of the return value, as well as containing a numeric. If it
     *                is a string, will attempt to convert it to a string
     * @param rightVal Right hand value of the addition
     * @return The subtraction of the leftVal and rightVal
     * @throws Exception If the numbers cannot be subtracted or converted, exception is thrown.
     */
    public ResultValue subtract(ResultValue leftVal, ResultValue rightVal) throws Exception
    {
        ResultValue returnVal = new ResultValue("", leftVal.type);
        switch(leftVal.type)
        {
            case Token.INTEGER:
                int iLeft = Integer.parseInt(leftVal.szValue);
                int iRight = Integer.parseInt(rightVal.szValue);
                returnVal.szValue = (iLeft - iRight) + "";
                break;
            case Token.FLOAT:
                double dLeft = Double.parseDouble(leftVal.szValue);
                double dRight = Double.parseDouble(rightVal.szValue);
                returnVal.szValue = (dLeft - dRight) + "";
                break;
            case Token.STRING:
                returnVal = strToNum(leftVal);
                return add(returnVal, rightVal);
            default:
                parser.errorWithContext("Could not subtract " + leftVal.szValue);
        }
        return returnVal;
    }

    /**
     * Adds two values together. Returned type is based on left hand side
     * @param leftVal Typing of the return value, as well as containing a numeric. If it
     *                is a string, will attempt to convert it to a string
     * @param rightVal Right hand value of the addition
     * @return The addition of the leftVal and rightVal
     * @throws Exception If the numbers cannot be added or converted, exception is thrown.
     */
    public ResultValue add(ResultValue leftVal, ResultValue rightVal) throws Exception
    {
        ResultValue returnVal = new ResultValue("", leftVal.type);
        switch(leftVal.type)
        {
            case Token.INTEGER:
                int iLeft = (int) Float.parseFloat(leftVal.szValue);
                int iRight = (int) Float.parseFloat(rightVal.szValue);
                returnVal.szValue = (iLeft + iRight) + "";
                break;
            case Token.FLOAT:
                double dLeft = Double.parseDouble(leftVal.szValue);
                double dRight = Double.parseDouble(rightVal.szValue);
                returnVal.szValue = (dLeft + dRight) + "";
                break;
            case Token.STRING:
                returnVal = strToNum(leftVal);
                return add(returnVal, rightVal);
            default:
                parser.errorWithContext("Could not add " + leftVal.szValue);
        }
        return returnVal;
    }

    /**
     * Multiplies two values together. Returned type is based on left hand side
     * @param leftVal Typing of the return value, as well as containing a numeric. If it
     *                is a string, will attempt to convert it to a string
     * @param rightVal Right hand value of the addition
     * @return The product of the leftVal and rightVal
     * @throws Exception If the numbers cannot be multiplied or converted, exception is thrown.
     */
    public ResultValue multiply(ResultValue leftVal, ResultValue rightVal) throws Exception
    {
        ResultValue returnVal = new ResultValue("", leftVal.type);
        switch(leftVal.type)
        {
            case Token.INTEGER:
                int iLeft = Integer.parseInt(leftVal.szValue);
                int iRight = Integer.parseInt(rightVal.szValue);
                returnVal.szValue = (iLeft * iRight) + "";
                break;
            case Token.FLOAT:
                double dLeft = Double.parseDouble(leftVal.szValue);
                double dRight = Double.parseDouble(rightVal.szValue);
                returnVal.szValue = (dLeft * dRight) + "";
                break;
            case Token.STRING:
                returnVal = strToNum(leftVal);
                return add(returnVal, rightVal);
            default:
                parser.errorWithContext("Could not multiply " + leftVal.szValue);
        }
        return returnVal;
    }

    /**
     * Divides two values together. Returned type is based on left hand side
     * @param leftVal Typing of the return value, as well as containing a numeric. If it
     *                is a string, will attempt to convert it to a string
     * @param rightVal Right hand value of the addition
     * @return The division of the leftVal and rightVal
     * @throws Exception If the numbers cannot be divided or converted, exception is thrown.
     */
    public ResultValue divide(ResultValue leftVal, ResultValue rightVal) throws Exception
    {
        ResultValue returnVal = new ResultValue("", leftVal.type);
        switch(leftVal.type)
        {
            case Token.INTEGER:
                int iLeft = Integer.parseInt(leftVal.szValue);
                int iRight = Integer.parseInt(rightVal.szValue);
                returnVal.szValue = (iLeft / iRight) + "";
                break;
            case Token.FLOAT:
                double dLeft = Double.parseDouble(leftVal.szValue);
                double dRight = Double.parseDouble(rightVal.szValue);
                returnVal.szValue = (dLeft / dRight) + "";
                break;
            case Token.STRING:
                returnVal = strToNum(leftVal);
                return add(returnVal, rightVal);
            default:
                parser.errorWithContext("Could not divide " + leftVal.szValue);
        }
        return returnVal;
    }

    /**
     * Raises the left operand to the power of the right. Returned type is based on left hand side
     * @param leftVal Typing of the return value, as well as containing a numeric. If it
     *                is a string, will attempt to convert it to a string
     * @param rightVal Right hand value of the addition
     * @return The product of the leftVal and rightVal
     * @throws Exception If the numbers cannot be exponentiated or converted, exception is thrown.
     */
    public ResultValue power(ResultValue leftVal, ResultValue rightVal) throws Exception
    {
        ResultValue returnVal = new ResultValue("", leftVal.type);
        switch(leftVal.type)
        {
            case Token.INTEGER:
                int iLeft = Integer.parseInt(leftVal.szValue);
                int iRight = Integer.parseInt(rightVal.szValue);
                returnVal.szValue = (pow(iLeft, iRight)) + "";
                break;
            case Token.FLOAT:
                double dLeft = Double.parseDouble(leftVal.szValue);
                double dRight = Double.parseDouble(rightVal.szValue);
                returnVal.szValue = pow(dLeft, dRight) + "";
                break;
            case Token.STRING:
                returnVal = strToNum(leftVal);
                return add(returnVal, rightVal);
            default:
                parser.errorWithContext("Could not exponentiate " + leftVal.szValue);
        }
        return returnVal;
    }

    /**
 * Converts a target value into an int
 * @param target Item to be changed
 * @return Result value with new values set
 * @throws Exception If could not be converted, error is thrown
 */
public ResultValue toInt(ResultValue target) throws Exception{
    ResultValue resultValue = new ResultValue("", Token.INTEGER);
    try {
        resultValue.szValue = Integer.parseInt(target.szValue) + "";
        return resultValue;
    } catch (Exception e){}
    try{
        resultValue.szValue = ((int) Double.parseDouble(target.szValue)) + "";
        return resultValue;
    } catch (Exception e){
        parser.errorWithContext("Could not convert " + target.szValue + " to an Int");
    }
    return null;
}

    /**
     * Converts a target value into a float
     * @param target Item to be changed
     * @return Result value with new values set
     * @throws Exception If could not be converted, error is thrown
     */
    private ResultValue toFloat(ResultValue target) throws Exception{
        ResultValue resultValue = new ResultValue("", Token.FLOAT);
        try {
            resultValue.szValue = Double.parseDouble(target.szValue) + "";
            return resultValue;
        } catch (Exception e){
            parser.errorWithContext("Could not convert " + target.szValue + " to an Int");
        }
        return null;
    }

    /**
     * Assignment method which bases return value on left hand side
     * @param assignTo Item to be assigned into
     * @param valAssign Value to be assigned
     * @return New value of the assignment statement
     * @throws Exception If assignment can not occur, error is thrown.
     */
    public ResultValue assignment(ResultValue assignTo, ResultValue valAssign) throws Exception{
        ResultValue returnValue = null;

        switch(assignTo.type)
        {
            case Token.INTEGER:
                returnValue = toInt(valAssign);
                break;
            case Token.FLOAT:
                returnValue = toFloat(valAssign);
                break;
            case Token.STRING:
                returnValue.szValue = valAssign.szValue;
                returnValue.type = Token.STRING;
                break;
            case Token.BOOLEAN:
                returnValue.szValue = valAssign.szValue;
                returnValue.type = Token.BOOLEAN;
                break;
            default:
                parser.errorWithContext("Not a valid assignment. " + valAssign.szValue
                        + " can't be assigned to " + assignTo.szValue);
        }
        return returnValue;
    }

    /**
     * Combines strings
     * @param beginning First half of string
     * @param end Second half of string
     * @return Combined string
     */
    public ResultValue combineStr(ResultValue beginning, ResultValue end){
        ResultValue returnValue = new ResultValue("", Token.STRING);
        StringBuilder stringBuilder = new StringBuilder(beginning.szValue);
        stringBuilder.append(end.szValue);
        returnValue.szValue = stringBuilder.toString();
        return returnValue;
    }


    private ResultValue convertIdentifier(ResultValue value){
        ResultValue resultValue = new ResultValue("", 0);
        resultValue.type = Token.STRING;
        resultValue.szValue = value.szValue;
        try{
            resultValue.type = Token.INTEGER;
            resultValue.szValue = Integer.parseInt(value.szValue) + "";
            return resultValue;
        } catch (Exception e) {}
        try{
            resultValue.type = Token.FLOAT;
            resultValue.szValue = Double.parseDouble(value.szValue) + "";
            return resultValue;
        } catch (Exception e) {}
        finally {
            /*resultValue.type = Token.STRING;
            resultValue.szValue = value.szValue;*/
            return resultValue;
        }
    }

    /**
     * Returns a truth value based on the two numerics given
     * @param leftOp Left hand side of the equality
     * @param rightOp Right hand side of the equality
     * @param comparison Operator given: >, <, ==, etc.
     * @return True or False based on the equality
     * @throws Exception Parser exception if given bad equality statement
     */
    public ResultValue equalValue(ResultValue leftOp, ResultValue rightOp, String comparison) throws Exception
    {
        ResultValue returnValue = new ResultValue("", Token.BOOLEAN);
        int iOp1, iOp2, strCmp;
        double dOp1, dOp2;

        switch(comparison)
        {
            case ">":
                switch(leftOp.type) {
                    case Token.INTEGER:
                        rightOp = toInt(rightOp);
                        iOp1 = Integer.parseInt(leftOp.szValue);
                        assert rightOp != null;
                        iOp2 = Integer.parseInt(rightOp.szValue);
                        returnValue.szValue = (iOp1 > iOp2) ? "T" : "F";
                        break;
                    case Token.FLOAT:
                        rightOp = toFloat(rightOp);
                        dOp1 = Double.parseDouble(leftOp.szValue);
                        assert rightOp != null;
                        dOp2 = Double.parseDouble(rightOp.szValue);
                        returnValue.szValue = (dOp1 > dOp2) ? "T" : "F";
                        break;
                    case Token.STRING:
                        strCmp = leftOp.szValue.compareTo(rightOp.szValue);
                        returnValue.szValue = (strCmp > 0) ? "T" : "F";
                        break;
                    case Token.IDENTIFIER:
                        returnValue = convertIdentifier(leftOp);
                        return equalValue(returnValue, rightOp, comparison);

                    default:
                        parser.errorWithContext("Bad equalities given. LHS: " + leftOp.szValue + " and RHS: " + rightOp.szValue);
                }
                break;
            case ">=":
                switch(leftOp.type) {
                    case Token.INTEGER:
                        rightOp = toInt(rightOp);
                        iOp1 = Integer.parseInt(leftOp.szValue);
                        assert rightOp != null;
                        iOp2 = Integer.parseInt(rightOp.szValue);
                        returnValue.szValue = (iOp1 >= iOp2) ? "T" : "F";
                        break;
                    case Token.FLOAT:
                        rightOp = toFloat(rightOp);
                        dOp1 = Double.parseDouble(leftOp.szValue);
                        assert rightOp != null;
                        dOp2 = Double.parseDouble(rightOp.szValue);
                        returnValue.szValue = (dOp1 >= dOp2) ? "T" : "F";
                        break;
                    case Token.STRING:
                        strCmp = leftOp.szValue.compareTo(rightOp.szValue);
                        returnValue.szValue = (strCmp >= 0) ? "T" : "F";
                        break;
                    case Token.IDENTIFIER:
                        returnValue = convertIdentifier(leftOp);
                        return equalValue(returnValue, rightOp, comparison);
                    default:
                        parser.errorWithContext("Bad equalities given. LHS: " + leftOp.szValue + " and RHS: " + rightOp.szValue);
                }
                break;
            case "<":
                switch(leftOp.type) {
                    case Token.INTEGER:
                        rightOp = toInt(rightOp);
                        iOp1 = (int) Float.parseFloat(leftOp.szValue);
                        assert rightOp != null;
                        iOp2 = (int) Float.parseFloat(rightOp.szValue);
                        returnValue.szValue = (iOp1 < iOp2) ? "T" : "F";
                        break;
                    case Token.FLOAT:
                        rightOp = toFloat(rightOp);
                        dOp1 = Double.parseDouble(leftOp.szValue);
                        assert rightOp != null;
                        dOp2 = Double.parseDouble(rightOp.szValue);
                        returnValue.szValue = (dOp1 < dOp2) ? "T" : "F";
                        break;
                    case Token.STRING:
                        strCmp = leftOp.szValue.compareTo(rightOp.szValue);
                        returnValue.szValue = (strCmp < 0) ? "T" : "F";
                        break;
                    case Token.IDENTIFIER:
                        returnValue = convertIdentifier(leftOp);
                        return equalValue(returnValue, rightOp, comparison);
                    default:
                        parser.errorWithContext("Bad equalities given. LHS: " + leftOp.szValue + " and RHS: " + rightOp.szValue);
                }
                break;
            case "<=":
                switch(leftOp.type) {
                    case Token.INTEGER:
                        rightOp = toInt(rightOp);
                        iOp1 = Integer.parseInt(leftOp.szValue);
                        assert rightOp != null;
                        iOp2 = Integer.parseInt(rightOp.szValue);
                        returnValue.szValue = (iOp1 <= iOp2) ? "T" : "F";
                        break;
                    case Token.FLOAT:
                        rightOp = toFloat(rightOp);
                        dOp1 = Double.parseDouble(leftOp.szValue);
                        assert rightOp != null;
                        dOp2 = Double.parseDouble(rightOp.szValue);
                        returnValue.szValue = (dOp1 <= dOp2) ? "T" : "F";
                        break;
                    case Token.STRING:
                        strCmp = leftOp.szValue.compareTo(rightOp.szValue);
                        returnValue.szValue = (strCmp <= 0) ? "T" : "F";
                        break;
                    case Token.IDENTIFIER:
                        returnValue = convertIdentifier(leftOp);
                        return equalValue(returnValue, rightOp, comparison);
                    default:
                        parser.errorWithContext("Bad equalities given. LHS: " + leftOp.szValue + " and RHS: " + rightOp.szValue);
                }
                break;
            case "==":switch(leftOp.type) {
                case Token.INTEGER:
                    rightOp = toInt(rightOp);
                    iOp1 = Integer.parseInt(leftOp.szValue);
                    assert rightOp != null;
                    iOp2 = Integer.parseInt(rightOp.szValue);
                    returnValue.szValue = (iOp1 == iOp2) ? "T" : "F";
                    break;
                case Token.FLOAT:
                    rightOp = toFloat(rightOp);
                    dOp1 = Double.parseDouble(leftOp.szValue);
                    assert rightOp != null;
                    dOp2 = Double.parseDouble(rightOp.szValue);
                    returnValue.szValue = (dOp1 == dOp2) ? "T" : "F";
                    break;
                case Token.STRING:
                    strCmp = leftOp.szValue.compareTo(rightOp.szValue);
                    returnValue.szValue = (strCmp == 0) ? "T" : "F";
                    break;
                case Token.IDENTIFIER:
                    returnValue = convertIdentifier(leftOp);
                    return equalValue(returnValue, rightOp, comparison);
                default:
                    parser.errorWithContext("Bad equalities given. LHS: " + leftOp.szValue + " and RHS: " + rightOp.szValue);
            }
                break;
            case "!=":
                switch(leftOp.type) {
                    case Token.INTEGER:
                        rightOp = toInt(rightOp);
                        iOp1 = Integer.parseInt(leftOp.szValue);
                        assert rightOp != null;
                        iOp2 = Integer.parseInt(rightOp.szValue);
                        returnValue.szValue = (iOp1 != iOp2) ? "T" : "F";
                        break;
                    case Token.FLOAT:
                        rightOp = toFloat(rightOp);
                        dOp1 = Double.parseDouble(leftOp.szValue);
                        assert rightOp != null;
                        dOp2 = Double.parseDouble(rightOp.szValue);
                        returnValue.szValue = (dOp1 != dOp2) ? "T" : "F";
                        break;
                    case Token.STRING:
                        strCmp = leftOp.szValue.compareTo(rightOp.szValue);
                        returnValue.szValue = (strCmp != 0) ? "T" : "F";
                        break;
                    case Token.IDENTIFIER:
                        returnValue = convertIdentifier(leftOp);
                        return equalValue(returnValue, rightOp, comparison);
                    default:
                        parser.errorWithContext("Bad equalities given. LHS: " + leftOp.szValue + " and RHS: " + rightOp.szValue);
                }
                break;
            case "#":
                if(leftOp.szValue == null)
                    leftOp.szValue = "";
                else if(rightOp.szValue == null)
                    rightOp.szValue = "";
                return combineStr(leftOp, rightOp);
            case "and":
                return keywordAnd(leftOp, rightOp);
            case "or":
                return keywordOr(leftOp, rightOp);

        }

        return returnValue;
    }

    /**
     * Checks to see if the given items are both equal to true
     * @param leftOp Left value
     * @param rightOp Right value
     * @return True or false, depending on equality
     */
    public ResultValue keywordAnd(ResultValue leftOp, ResultValue rightOp){
        ResultValue returnValue = new ResultValue("", Token.BOOLEAN);
        returnValue.szValue =  (leftOp.szValue.equals("T") && rightOp.szValue.equals("T")) ? "T" : "F";
        return returnValue;
    }

    /**
     * Checks to see if either of the given items are true
     * @param leftOp Left Value
     * @param rightOp Right Value
     * @return True or false, depending on equality
     */
    public ResultValue keywordOr(ResultValue leftOp, ResultValue rightOp){
        ResultValue returnValue = new ResultValue("", Token.BOOLEAN);
        returnValue.szValue = (leftOp.szValue.equals("T") || rightOp.szValue.equals("T")) ? "T" : "F";
        return returnValue;
    }

    /**
     * Negates given result value
     * @param resultValue Result value to be flipped
     * @return The negation of the given result value
     */
    public ResultValue keywordNot(ResultValue resultValue){
        resultValue.szValue = resultValue.szValue.equals("T") ? "F" : "T";
        return resultValue;
    }

    /**
     * 
     * Get priority of operator
     * 
     * @param szOperator +, -, *, ^, /, (, )
     * @return Priority of the operator
     */
    public static int getPriority(String szOperator){
		int iPriority = 0;
		
        switch (szOperator) {
            case "^":
                iPriority = 3;
                break;
            case "*":
            case "/":
                iPriority = 2;
                break;
            case "+":
            case "-":
                iPriority = 1;
                break;
            case "(":
            case ")":
                iPriority = 0;
                break;
            default:
                break;
        }
		
		return iPriority;
    
    }

}
