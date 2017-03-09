package havabol;

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
                    doubleValue = Double.parseDouble(strValue);
                }
                catch (NullPointerException e)
                {
                    try
                    {
                        integerValue = Integer.parseInt(strValue);
                    }
                    catch (NullPointerException i)
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
     * Sets the value of the given result type.
     * @param resultValue Value to be set
     * @param type Determines whether resultValue is set to Token.INTEGER or Token.FLOAT
     * @param szValue Display value of the result
     * @return The result value with type and display set
     */
    private ResultValue setValue(ResultValue resultValue, int type, String szValue)
    {
        resultValue.type = type;
        resultValue.szValue = szValue;
        return resultValue;
    }

    /**
     * Takes in two operands and returns operand1 subtracted from operand2. If mixed operands,
     * then the result value will take the typing of the leftmost type.
     * @param operand1 Operand containing integer or double
     * @param operand2 Operand containing integer or double
     * @return Returns a ResultValue containing new type and string value.
     */
    public ResultValue subtract(Numeric operand1, Numeric operand2)
    {
        ResultValue returnValue = new ResultValue("",0);
        int intReturn;
        double dblReturn;

        if (operand1.type == Token.INTEGER && operand2.type == Token.INTEGER)
        {
            intReturn = operand1.integerValue - operand2.integerValue;
            returnValue = setValue(returnValue, Token.INTEGER, intReturn + "");

        }
        else if (operand1.type == Token.FLOAT && operand2.type == Token.FLOAT)
        {
            dblReturn = operand1.doubleValue - operand2.doubleValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }
        else if(operand1.type == Token.INTEGER && operand2.type == Token.FLOAT)
        {
            intReturn = (int)(operand1.integerValue - operand2.doubleValue);
            returnValue = setValue(returnValue, Token.INTEGER, intReturn + "");
        }
        else
        {
            dblReturn = operand1.doubleValue - operand2.integerValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }

        return returnValue;
    }

    /**
     * Takes in two operands and returns operand1 added to operand2. If either operand
     * is a double, the result value type will be set to double.
     * @param operand1 Operand containing integer or double
     * @param operand2 Operand containing integer or double
     * @return Returns a ResultValue containing new type and string value.
     */
    public ResultValue add(Numeric operand1, Numeric operand2)
    {
        ResultValue returnValue = new ResultValue("", 0);
        int intReturn;
        double dblReturn;

        if (operand1.type == Token.INTEGER && operand2.type == Token.INTEGER)
        {
            intReturn = operand1.integerValue + operand2.integerValue;
            returnValue = setValue(returnValue, Token.INTEGER, intReturn + "");

        }
        else if (operand1.type == Token.FLOAT && operand2.type == Token.FLOAT)
        {
            dblReturn = operand1.doubleValue + operand2.doubleValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }
        else if(operand1.type == Token.INTEGER && operand2.type == Token.FLOAT)
        {
            intReturn = (int)(operand1.integerValue + operand2.doubleValue);
            returnValue = setValue(returnValue, Token.INTEGER, intReturn + "");
        }
        else
        {
            dblReturn = operand1.doubleValue + operand2.integerValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }
        return returnValue;
    }

    /**
     * Takes in two operands and returns operand1 multiplied with operand2. If either operand
     * is a double, the result value type will be set to double.
     * @param operand1 Operand containing integer or double
     * @param operand2 Operand containing integer or double
     * @return Returns a ResultValue containing new type and string value.
     */
    public ResultValue multiply(Numeric operand1, Numeric operand2)
    {
        ResultValue returnValue = new ResultValue("", 0);
        int intReturn;
        double dblReturn;

        if (operand1.type == Token.INTEGER && operand2.type == Token.INTEGER)
        {
            intReturn = operand1.integerValue * operand2.integerValue;
            returnValue = setValue(returnValue, Token.INTEGER, intReturn + "");

        }
        else if (operand1.type == Token.FLOAT && operand2.type == Token.FLOAT)
        {
            dblReturn = operand1.doubleValue * operand2.doubleValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }
        else if(operand1.type == Token.INTEGER && operand2.type == Token.FLOAT)
        {
            intReturn = (int)(operand1.integerValue * operand2.doubleValue);
            returnValue = setValue(returnValue, Token.INTEGER, intReturn + "");
        }
        else
        {
            dblReturn = operand1.doubleValue * operand2.integerValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }

        return returnValue;
    }

    /**
     * Takes in two operands and returns operand1 divided by operand2. If either operand
     * is a double, the result value type will be set to double.
     * @param divisor Operand containing integer or double
     * @param dividend Operand containing integer or double
     * @return Returns a ResultValue containing new type and string value.
     * @throws Exception Will throw divide by zero exception. Because in java dividing by zero with floats is valid,
     * will only be thrown for integer division by zero.
     */
    public ResultValue divide(Numeric divisor, Numeric dividend) throws Exception
    {
        ResultValue returnValue = new ResultValue("", 0);
        double dblReturn;
        int intReturn = 0;

        if (divisor.type == Token.INTEGER && dividend.type == Token.INTEGER)
        {
            if(dividend.integerValue != 0)
                intReturn = divisor.integerValue / dividend.integerValue;
            else
                parser.errorWithContext("Can not divide by zero! Operands given: " + divisor.integerValue + " and "
                        + dividend.integerValue);

            returnValue = setValue(returnValue, Token.INTEGER, intReturn + "");

        }
        else if (divisor.type == Token.FLOAT && dividend.type == Token.FLOAT)
        {
            dblReturn = divisor.doubleValue / dividend.doubleValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }
        else if(divisor.type == Token.INTEGER && dividend.type == Token.FLOAT)
        {
            if(dividend.doubleValue != 0)
                intReturn = (int)(divisor.integerValue / dividend.doubleValue);
            else
                parser.errorWithContext("Can not divide by zero! Operands given: " + divisor.integerValue + " and "
                        + dividend.integerValue);
            returnValue = setValue(returnValue, Token.INTEGER, intReturn + "");
        }
        else
        {
            dblReturn = divisor.doubleValue / dividend.integerValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }

        return returnValue;
    }
    
    /**
     * 
     * operand1 raised to the power of operand2
     * 
     * @param baseOfExp Base of the operation, can be integer or double
     * @param powerOfExp What the base will be raised to the power of, can be integer or double
     * @return Returns a ResultValue containing new type and string value.
     */
    public ResultValue power(Numeric baseOfExp, Numeric powerOfExp) throws Exception
    {
        ResultValue returnValue = new ResultValue("", 0);
        double dblReturn;
        int intReturn;

        
        //2^3 op1 = 2 and op2 = 3
        if (baseOfExp.type == Token.INTEGER && powerOfExp.type == Token.INTEGER)
        {
            intReturn = (int) Math.pow(baseOfExp.integerValue, powerOfExp.integerValue);
            returnValue = setValue(returnValue, Token.INTEGER, intReturn + "");

        }
        else if (baseOfExp.type == Token.FLOAT && powerOfExp.type == Token.FLOAT)
        {
            dblReturn = Math.pow(baseOfExp.integerValue, powerOfExp.integerValue);
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }
        else if(baseOfExp.type == Token.INTEGER && powerOfExp.type == Token.FLOAT)
        {
            intReturn = (int)(Math.pow(baseOfExp.integerValue , powerOfExp.doubleValue));
            returnValue = setValue(returnValue, Token.INTEGER, intReturn + "");
        }
        else
        {
            dblReturn = Math.pow(baseOfExp.doubleValue, powerOfExp.integerValue);
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }

        return returnValue;
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
