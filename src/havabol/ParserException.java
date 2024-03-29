package havabol;

/**
 * Exception class provided by Larry.
 */
public class ParserException extends Exception
{
    private int iLineNr;
    private String diagnostic;

    // constructor
    public ParserException(int iLineNr, String diagnostic)
    {
        this.iLineNr = iLineNr;
        this.diagnostic = diagnostic;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    }

    // Exceptions are required to provide tosString()
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Line ");
        sb.append(Integer.toString(iLineNr));
        sb.append(": ");
        sb.append(diagnostic);
        return sb.toString();
    }
}