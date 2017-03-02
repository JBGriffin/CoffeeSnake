package havabol;

/**
 * Converts given result value strings into a numeric.
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
            default:
                parser.errorWithContext("Expression must be a numeric. Item " + resultValue.szValue + " given as " + operand);
        }
    }

    private ResultValue setValue(ResultValue resultValue, int type, String szValue)
    {
        resultValue.type = type;
        resultValue.szValue = szValue;
        return resultValue;
    }

    /**
     * Takes in two operands and returns operand1 subtracted from operand2. If either operand
     * is a double, the result value type will be set to double.
     * @param operand1 Operand containing integer or double
     * @param operand2 Operand containing integer or double
     * @return Returns a ResultValue containing new type and string value.
     */
    public ResultValue subtract(Numeric operand1, Numeric operand2)
    {
        ResultValue returnValue = new ResultValue("");
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
            dblReturn = operand1.integerValue - operand2.doubleValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
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
        ResultValue returnValue = new ResultValue("");
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
            dblReturn = operand1.integerValue + operand2.doubleValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
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
        ResultValue returnValue = new ResultValue("");
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
            dblReturn = operand1.integerValue * operand2.doubleValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
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
     * @param operand1 Operand containing integer or double
     * @param operand2 Operand containing integer or double
     * @return Returns a ResultValue containing new type and string value.
     * @throws Exception Will throw divide by zero exception. Because in java dividing by zero with floats is valid,
     * will only be thrown for integer division by zero.
     */
    public ResultValue divide(Numeric operand1, Numeric operand2) throws Exception
    {
        ResultValue returnValue = new ResultValue("");
        double dblReturn;
        int intReturn = 0;

        if (operand1.type == Token.INTEGER && operand2.type == Token.INTEGER)
        {
            if(operand2.integerValue != 0)
                intReturn = operand1.integerValue / operand2.integerValue;
            else
                parser.errorWithContext("Can not divide by zero! Operands given: " + operand1.integerValue + " and "
                        + operand2.integerValue);

            returnValue = setValue(returnValue, Token.INTEGER, intReturn + "");

        }
        else if (operand1.type == Token.FLOAT && operand2.type == Token.FLOAT)
        {
            dblReturn = operand1.doubleValue / operand2.doubleValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }
        else if(operand1.type == Token.INTEGER && operand2.type == Token.FLOAT)
        {
            dblReturn = operand1.integerValue / operand2.doubleValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }
        else
        {
            dblReturn = operand1.doubleValue / operand2.integerValue;
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }

        return returnValue;
    }
    
    /**
     * 
     * operand1 raised to the power of operand2
     * 
     * @param operand1 "2" 2^3
     * @param operand2 "3" 2^3
     * @return
     * @throws Exception 
     */
    public ResultValue power(Numeric operand1, Numeric operand2) throws Exception
    {
        ResultValue returnValue = new ResultValue("");
        double dblReturn;
        int intReturn = 0;

        
        //2^3 op1 = 2 and op2 = 3
        if (operand1.type == Token.INTEGER && operand2.type == Token.INTEGER)
        {
            //if(operand2.integerValue != 0)
            intReturn = (int) Math.pow(operand1.integerValue, operand2.integerValue); 
                //operand1.integerValue / operand2.integerValue;
            //else
            //    parser.errorWithContext("Can not divide by zero! Operands given: " + operand1.integerValue + " and "
             //           + operand2.integerValue);

            returnValue = setValue(returnValue, Token.INTEGER, intReturn + "");

        }
        else if (operand1.type == Token.FLOAT && operand2.type == Token.FLOAT)
        {
            dblReturn = Math.pow(operand1.integerValue, operand2.integerValue);
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }
        else if(operand1.type == Token.INTEGER && operand2.type == Token.FLOAT)
        {
            //dblReturn = operand1.integerValue / operand2.doubleValue;
            dblReturn = Math.pow(operand1.integerValue, operand2.doubleValue);
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }
        else
        {
            //dblReturn = operand1.doubleValue / operand2.integerValue;
            dblReturn = Math.pow(operand1.doubleValue, operand2.integerValue);
            returnValue = setValue(returnValue, Token.FLOAT, dblReturn + "");
        }

        return returnValue;
    }
    
    /**
     * 
     * Get priority of operator
     * 
     * @param szOperator +, -, *, ^, /, (, )
     * @return 
     */
    public int getPriority(String szOperator){
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
