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
     * @param parser Parser passed in to utilize error method
     */
    public Date(Parser parser)
    {
        this.errParse = parser;
        this.day = this.month = this.year = 0;
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

    public ResultValue dateDiff(String date1, String date2) throws Exception
    {
        setDates(date1, date2);
        return null;
    }

    public ResultValue dateAdj(String date1, String date2) throws Exception
    {
        setDates(date1, date2);

        return null;
    }

    public ResultValue dateAge(String date1, String date2) throws Exception
    {
        setDates(date1, date2);

        return null;
    }

    /**
     * Ensures that the dates passed in through functions are valid.
     * @param date1 Start date to check
     * @param date2 End date to check
     * @throws Exception Kills the program if the dates are not valid
     */
    private void setDates(String date1, String date2) throws Exception
    {
        startDate = new ResultValue(date1, Token.DATE);
        endDate = new ResultValue(date2, Token.DATE);
        if(! validDate(startDate) || ! validDate(endDate))
            // Will never see this message
            errParse.errorWithContext("Invalid date provided.");

    }

    private void p(){ System.out.println("In date, Current Token: " + errParse.scanner.currentToken.tokenStr);}
}
