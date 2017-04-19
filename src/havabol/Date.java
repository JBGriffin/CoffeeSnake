package havabol;

/**
 * Date class to be implemented in Havabol
 */
public class Date {
    // Parser class to utilize error method
    private Parser errParse;
    ResultValue startDate;
    ResultValue endDate;

    // Static array containing all months.
    // Starts at index 1 for readability
    private static int daysPerMonth[] =
            { 0, 31, 29, 31
               , 30, 31, 30
               , 31, 31, 30
               , 31, 30, 31 };

    private int day;
    private int month;
    private int year;


    /**
     * Construtor for Date class. Sets the parser class for error trapping purposes
     * @param parser
     */
    public Date(Parser parser)
    {
        this.errParse = parser;
        this.startDate = new ResultValue("", Token.DATE);
        this.endDate = new ResultValue("", Token.DATE);
    }

    /**
     * Sets the day, month, and year for the date for easier comparisons
     * @param dateValue Target value to convert
     */
    private void setNumerics(ResultValue dateValue)
    {
        this.year = Integer.parseInt(dateValue.szValue.substring(0, 4));
        this.month = Integer.parseInt(dateValue.szValue.substring(5, 7));
        this.day = Integer.parseInt(dateValue.szValue.substring(8));
    }

    /**
     * Validates the date and returns a boolean
     *
     * Notes:
     * <p>
     *     1. The length must be 10 characters
     * <p>
     *     2. The date must be in the form "yyyy-mm-dd"
     * <p>
     *     3. The month must be from 1-12
     * <p>
     *     4. The day must be between 1 and the max for each month,
     *     as seen in the array daysPerMonth
     * <p>
     *     If Feb 29 is specified, validate that the year is a leap year
     * </p>
     * @param dateCheck Date to be checked
     * @return True if the date is valid, false otherwise
     */
    public boolean validDate(ResultValue dateCheck) throws Exception
    {

        if(dateCheck.szValue.length() != 10)
            errParse.errorWithContext("Invalid format due to length. Usage: "
                    + dateCheck.szValue);
        // String is the correct length. Grab out the day, month, and year
        setNumerics(dateCheck);

        // validate month
        if(month < 1 || month > 12)
            errParse.errorWithContext("Invalid month given. Usage: " + month);

        // validate day
        if(day < 1 || day > daysPerMonth[month])
            errParse.errorWithContext("Invalid day given. Usage: Day " + day + " used with Month " + month);

        // check for leap year
        if(day == 29 && month ==2)
        {
            if(year % 4 == 0 && (year %100 != 0 || year % 400 == 0))
                return true;
            else
                errParse.errorWithContext("Invalid attempt of leap year. Usage: "
                + "Day = " + day + " Month = " + month + " Year = " + year);
        }

        // If it makes it this far, it's a valid date
        return true;
    }

    public ResultValue dateDiff(ResultValue date1, ResultValue date2) throws Exception
    {
        if(! validDate(date1) || ! validDate(date2))
            // Will never see this message
            errParse.errorWithContext("Invalid date provided.");


        return null;
    }

    public ResultValue dateAdj(ResultValue date1, ResultValue date2) throws Exception
    {
        if(! validDate(date1) || ! validDate(date2))
            // Will never see this message
            errParse.errorWithContext("Invalid date provided.");


        return null;
    }

    public ResultValue dateAge(ResultValue date1, ResultValue date2) throws Exception
    {
        if(! validDate(date1) || ! validDate(date2))
            // Will never see this message
            errParse.errorWithContext("Invalid date provided.");

        return null;
    }

    private void p(){ System.out.println("In date, Current Token: " + errParse.scanner.currentToken.tokenStr);}
}
