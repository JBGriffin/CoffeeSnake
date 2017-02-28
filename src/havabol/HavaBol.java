/*
  This is a simple driver for the first programming assignment.
  Command Arguments:
      java HavaBol arg1
             arg1 is the havabol source file name.
  Output:
      Prints each token in a table.
  Notes:
      1. This creates a SymbolTable object which doesn't do anything
         for this first programming assignment.
      2. This uses the student's Scanner class to get each token from
         the input file.  It uses the getNext method until it returns
         an empty string.
      3. If the Scanner raises an exception, this driver prints 
         information about the exception and terminates.
      4. The token is printed using the Token::printToken() method.
 */
package havabol;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HavaBol 
{
    public static void main(String[] args) 
    {
        // Create the SymbolTable
        SymbolTable symbolTable = new SymbolTable();
        Scanner scan;
        Parser parser;
        try {
            System.out.printf("%-11s %-12s %s\n"
                    , "primClassif"
                    , "subClassif"
                    , "tokenStr");
            scan = new Scanner(args[0], symbolTable);
            parser = new Parser(symbolTable, scan);
            parser.parse();
        } catch (IOException ex) {
            Logger.getLogger(HavaBol.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(HavaBol.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        
        try
        {
            // Print a column heading 
            
            
            /*
            while (! scan.getNext().isEmpty())
            {
                //scan.currentToken.printToken();
                //parser.parse();
               
            }*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}