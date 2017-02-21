package havabol;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Reads in the source file from the user, as well as the symbol table (which will not be
 * used in assignment 1). Opens the file and begins to parse the given file. Sets all lines of the file into 
 * an array list. Will create two tokens, because of how it will be utilized later on in the project. 
 * <p>
 * Note: If an error is encountered, will throw an exception to main to kill the program immediately.
 * <p>
 * @author Garrett Griffin, Myka Hancevic, and Justin Hooge
 *
 */
public class Scanner {

	public Token currentToken;
	public Token nextToken;
	private final static String delimiters = " \t,;:()\'\"=!<>+-*/[]#^\n"; // terminate a token
	
	private String currentLine;
	private String sourceFileName;
	private ArrayList<String> sourceFileM;
	private int iColPos;
	private int iSourceLineNr;
	private char[] textCharM;

	/**
	 * Constructor for the class. Verifies that the source file is viable, and creates two new tokens
	 * for future use. Calls advanceLine() to set the first line.
	 * <p>
	 * @param sourceFileNm Name of the file user uploads
	 * @param symbolTable Symbol table to be generated at a later date
	 * @throws IOException Exception to be thrown if bad file is read in
	 */
	public Scanner(String sourceFileNm, SymbolTable symbolTable) throws IOException
	{
		this.sourceFileName = sourceFileNm;
		// Init a new array list for the lines
		sourceFileM = new ArrayList<>();
		
		// Open file and read first line into an array list of strings.
		BufferedReader fileRead = new BufferedReader(new FileReader(sourceFileNm));
		
		while((currentLine = fileRead.readLine()) != null)
		{
			sourceFileM.add(currentLine);
		}
		
		// Initialize all items to be used in getNext();
		iSourceLineNr = 0;	
		
		// Initialize currentToken and nextToken to new objects so the parser
		// won't have to check for null later.
		currentToken = new Token();
		nextToken = new Token();

		advanceLine();
	}

	/**
	 * Advances through the source code, resetting the column position, and setting
	 * currentLine to the next line of the code. Once the file size is met, will break to return EoF.
	 * <p>
	 */
	private void advanceLine()
	{
		iSourceLineNr += 1;
		iColPos = 0;
		// Check if we're at the end of the file
		// If we are, head home.
		if(iSourceLineNr > sourceFileM.size()) 
		{
           currentLine = "";
           nextToken = new Token();
           nextToken.primClassif = Token.EOF;
           nextToken.tokenStr = "";
           return;
		}
		
		currentLine = sourceFileM.get(iSourceLineNr - 1);
		System.out.println("  " + iSourceLineNr + " " + currentLine);
		

		if(currentLine.isEmpty())
		{
			advanceLine();
		}
		
		// reset column position
		iColPos = 0;
	}
	
	/**
	 * Initializes the nextToken to the next part of textCharM. Will advance the 
	 * file when necessary. Automatically skips whitespace. Will only reach the end
	 * of the method if it is the end of the file. 
	 * <p>
	 * @throws Exception Throws an exception if it is an invalid token
	 */
	private void setNextToken() throws Exception
	{
		textCharM = currentLine.toCharArray();
		String nextTokStr = "";
		
		for(int index = iColPos; index < currentLine.length(); index++)
		{
			// Check for delimiters. If the build string is not empty, build a new token
			// And come back to see if we need to use it again.
			if(delimiters.indexOf(textCharM[index]) != -1)
			{
				// If the build string is not empty, create the token and 
				// advance the column position
                // Check if it's a special typing or not
				if(! nextTokStr.isEmpty())
				{
					// Construct a new token
					constructToken(index, nextTokStr);

					// Check to see if it's a special operator
					switch(nextTokStr)
					{
                        // Set operators: 'and', 'or', 'not', etc.
						case "and":case "not":case "in":case "or":case "notin":
							// subClassIf set to 0 to indicate no sub class
							setAllClassIf(Token.OPERATOR, 0);
							break;
                        // Flow control keywords: 'if', 'endif', 'while', etc.
                        case "if":case "def":case "for":case "while":
							setAllClassIf(Token.CONTROL, Token.FLOW);
                        	break;
                        case "endwhile":case "endif":case "endfor":
						case "else":case "enddef":
							setAllClassIf(Token.CONTROL, Token.END);
                            break;
                        // Declaration constants(Int, Bool, etc)
                        case "Int":case "Float":case "Bool":case "String":
							setAllClassIf(Token.CONTROL, Token.DECLARE);
                        	break;
						case "T": case "F":
							setAllClassIf(Token.OPERAND, Token.BOOLEAN);
							return;
						// Functions:
						case "LENGTH":case "MAXLENGTH":case "SPACES":
						case "ELEM":case "MAXELEM":
							setAllClassIf(Token.FUNCTION, Token.BUILTIN);
							break;
						case "Date":
							setAllClassIf(Token.CONTROL, Token.DATE);
							break;
						case "Void":
							setAllClassIf(Token.CONTROL, Token.VOID);
                            break;
						case "print":
							setAllClassIf(Token.FUNCTION, Token.BUILTIN);
							break;
                        default:
                            // For now, we know it's an operand and
							// need to find its subclassif. Later on,
							// for user functions, we'll have to revisit this
                            nextToken.primClassif = Token.OPERAND;
                            setSubClass();
					}
					return;
				}

				switch(textCharM[index])
				{
				// Whitespace
				case ' ': 
				case '\t':
				case '\n':
					if(! nextTokStr.isEmpty()) // At white space, but I still have things to print
					{					
						constructToken(index + 1, nextTokStr);
						return;
					}
					iColPos = index + 1;

					if(iColPos == currentLine.length())
					{
						getNext();
						return;
					}
					break;
				// Operator tokens: + - * / < > = ! 
				case '+': case '-':	case '*': case '/':case '^':
				case '!': case '=': case '<': case '>':
					nextTokStr = currentLine.substring(iColPos, index + 1);
					// Check to see if we need to combine operators
                    // If it is, add it to the token string and increment position
					if(textCharM[iColPos + 1] == '/' && textCharM[iColPos] == '/')
					{
						advanceLine();
						getNext();
						return;
					}
					if(textCharM[iColPos + 1] == '=')
					{
					    iColPos += 1;
					    index += 1;
                        nextTokStr += textCharM[iColPos];
                    }
					constructToken(index + 1, nextTokStr);
					nextToken.primClassif = Token.OPERATOR;
					return;
				// Seperator tokens: ( ) , : ; [ ]
				case '(': case ')': case ',': case ':': 
				case ';': case '[': case ']':
					nextTokStr = currentLine.substring(iColPos, index + 1);
					constructToken(index + 1, nextTokStr);
					nextToken.primClassif = Token.SEPARATOR;
					return;
				// String literal tokens
				case '\"':
				case '\'':
					buildStringLiteral(textCharM[index], index + 1);
					return;
				// Must be an operand. Check for sub classification
				default:
					nextTokStr = currentLine.substring(iColPos, index + 1);
					constructToken(index + 1, nextTokStr);
					nextToken.primClassif = Token.OPERAND;
					setSubClass();
					return;
				}
			}
			// No delimiters hit, continue building the string
			else
			{
				nextTokStr = currentLine.substring(iColPos, index + 1);
			}
		}

		// Check if we missed a token to create.
		if(! nextTokStr.isEmpty())
		{
			nextTokStr = currentLine.substring(iColPos, textCharM.length);
			constructToken(textCharM.length, nextTokStr);
			nextToken.primClassif = Token.OPERAND;
			setSubClass();
			return;
		}
		// Check to see if we still have anything in the file
		if(iSourceLineNr < sourceFileM.size())
		{
			advanceLine();
			return;        // If we hit this statement, we missed tokens
		}

		// If we reach this point, we're at the end of the file.
		nextToken = new Token();
		nextToken.primClassif = Token.EOF;
	}

	/**
	 * Sets prime and sub classifications for next token
	 * @param primClassif Prime Classification to be set for next token
	 * @param subClassif Sub Classification to be set for next token
	 */
	private void setAllClassIf(int primClassif, int subClassif)
	{
		nextToken.primClassif = primClassif;
		nextToken.subClassif = subClassif;
	}
	
	/**
	 * Sets the subclass of the next token. Will only be run on operands.
	 * Checks the first digit to see if it is a number or not. If it isn't a digit,
	 * it must be an identifier. If it is, checks to see if it is a valid integer
	 * or float.
	 * <p>
	 * Note: If the string literal is not valid, it will call the error method which
	 * will display the error message, and throw an exception.
     * <p>
     * @throws Exception Throws an exception if the subclass is not valid
	 */
	private void setSubClass() throws Exception {
		// Variables to check number string
		boolean validFloat = false;
		char[] checkDigits = nextToken.tokenStr.toCharArray();
		
		// Checks if the first digit is a digit. If it is, it has to be an
		// Integer. Error tap for non digit characters, and whether or not it
		// Contains one or more periods
		if(Character.isLetter(checkDigits[0]))
		{
			nextToken.subClassif = Token.IDENTIFIER;
		}
		// First character is a digit. Check if it's a valid integer or float
		else if(Character.isDigit(checkDigits[0]))
		{
			// We know the first index is a digit, so start at index 1.
			// Iterate through array to verify validity of number string
			// If one period is encountered, it's valid. 
			// If two or more is encountered, throw an error
			for(int i = 1; i < checkDigits.length; i++)
			{
				if(! Character.isDigit(checkDigits[i]) && checkDigits[i] != '.')
				{
					String err = "Numeric constants may contain only ONE period," +
							" or can only contain digits. Usage: " + nextToken.tokenStr;
					error(err);
				}
				// Found first period. Set the boolean to true and continue
				else if(checkDigits[i] == '.' && ! validFloat)
				{
					validFloat = true;
				}
				// Found a second period. Kill it!
				else if(checkDigits[i] == '.' && validFloat)
				{
					String err = "Numeric float constants must contain only ONE period," +
							" and can only contain digits. Usage: " + nextToken.tokenStr;
					error(err);
				}
			}
			// Know that it is either an int or float at this point
			if(validFloat)
				nextToken.subClassif = Token.FLOAT;
			else
				nextToken.subClassif = Token.INTEGER;
		}
		// Does not start with an digit or a character, so it must be invalid
		else
		{
			String err = "Numeric constants must start with a digit. Usage: " + nextToken.tokenStr;
			error(err);
		}
	}
	
	/**
	 * Builds a new string token based off of the quote, single or double, given.
	 * Will break and create the string only if it encounters the same 
	 * quote again, and the previous character is not an escape character.
	 * <p>
	 * Note: If the string literal is not valid, it will call the error method which
	 * will display the error message, and throw an exception.
	 * <p>
	 * @param quote Quote to build the string on. Will either be a single or double quote.
	 * @param strStart Starting position of the string. Has been pre-incremented to throw away
	 * the first quote.
	 */
	private void buildStringLiteral(char quote, int strStart) throws Exception {
		String newTokStr = "";
		int strEnd = strStart;
		boolean quoteOpen = true;
		
		for(int i = strStart; i < currentLine.length(); i++)
		{
			if(textCharM[i] == quote && textCharM[i - 1] != '\\')
			{
				quoteOpen = false;
				strEnd = i;
				break;
			}

			// Check for special characters, i.e. \t, \n.
			if(textCharM[i] == '\\')
            {
                if(textCharM[i + 1] == 'a')
                    textCharM[i + 1] = 0x07;
                if(textCharM[i + 1] == 't')
                    textCharM[i +1] = 0x09;
                if(textCharM[i + 1] == 'n')
                    textCharM[i + 1] = 0x0A;
                i += 1; // Increment index to skip escaped char
            }
			newTokStr += Character.toString(textCharM[i]);
		}
		
		if(quoteOpen)
		{
			String err = "Unclosed quote found. Last quote opened at position " + strStart;
			error(err);
		}
		constructToken(strEnd + 1, newTokStr);
		nextToken.primClassif = Token.OPERAND;
		nextToken.subClassif = Token.STRING;
	}
	
	/**
	 * Creates a new token with given position and token name. Classifications
	 * to be handled outside of this method.
	 * <p>
	 * @param setPos New position for iColPos.
	 * @param tokName Name of the token.
	 */
	private void constructToken(int setPos, String tokName)
	{
		nextToken = new Token(tokName);
		nextToken.iColPos = iColPos;
		nextToken.iSourceLineNr = iSourceLineNr;
		iColPos = setPos;
	}

	/**
	 * Grabs the next token from the file. If at the end of the current
	 * line, will advance to the next line in the file. Sets the current
	 * token to the next token, and returns a string to the current token.
	 * <p>
	 * @return Functionally returns name of the next token. 
	 * @throws Exception Throws an exception to main if found in one of the called methods
	 */
	String getNext() throws Exception
	{
    	if(iColPos == currentLine.length())
			advanceLine();
		if(currentToken.primClassif == Token.EOF)
		{
			currentToken = nextToken;
			return "";
		}

        setNextToken();
        currentToken = nextToken;

        return currentToken.tokenStr;
	}

	/**
	 * Exits program and gives meaningful information at where and why the program
	 * ended.   --
	 * </p>
	 * @param diagnosticTxt Text for the user to be able to debug program
	 * @throws Exception Will show stack trace of where, when, and why the program ended.
	 */
	private void error(String diagnosticTxt) throws Exception
	{
		throw new ParserException(iSourceLineNr, diagnosticTxt, sourceFileName);
	}
}
