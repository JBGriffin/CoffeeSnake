package havabol;

/**
 *
 */
public class Numeric
{
    int integerValue;
    double doubleValue;
    String strValue;    // display value
    int type;           // INTEGER, FLOAT

    // Error items
    Parser parser;
    String operatorGiven;
    String position;

    public Numeric(Parser parser, ResultValue resultValue, String operator, String operand) throws Exception
    {
        this.parser = parser;
        /*this.resultValue = resultValue;*/
        this.operatorGiven = operator;
        this.position = operand;
        this.type = resultValue.type;
        this.strValue = resultValue.szValue;
        /*this.resultValue = resultValue;*/

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

        if (operand1.type == Token.INTEGER && operand2.type == Token.INTEGER)
        {
            int intReturn = operand1.integerValue - operand2.integerValue;
            returnValue.type = Token.INTEGER;
            returnValue.szValue = intReturn + "";
        }
        else if (operand1.type == Token.FLOAT && operand2.type == Token.FLOAT)
        {
            double dblReturn = operand1.doubleValue - operand2.doubleValue;
            returnValue.type = Token.FLOAT;
            returnValue.szValue = dblReturn + "";
        }
        else if(operand1.type == Token.INTEGER && operand2.type == Token.FLOAT)
        {
            double dblReturn = operand1.integerValue - operand2.doubleValue;
            returnValue.type = Token.FLOAT;
            returnValue.szValue = dblReturn + "";
        }
        else
        {
            double dblReturn = operand1.doubleValue - operand2.integerValue;
            returnValue.type = Token.FLOAT;
            returnValue.szValue = dblReturn + "";
        }

        return returnValue;
    }

}
