
import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;
import java.lang.Math;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// adds servlet mapping annotation
import javax.servlet.annotation.WebServlet;
@WebServlet( name = "assignment9", urlPatterns = {"/assignment9"} )


public class myServlet extends HttpServlet{
	static enum Data {LOGICALOPERATION,DISPLAY};
	
	// Location of servlet.
	static String Domain  = "";
	static String Path    = "";
	static String Servlet = "assignment9";
	
	// Button labels
	static String OperationSubmit = "Submit";
	
	static String Style ="https://www.cs.gmu.edu/~gterziys/public_html/style.css";
	
	/** *****************************************************
	 *  Overrides HttpServlet's doPost().
	 *  Converts the values in the form, performs the operation
	 *  indicated by the submit button, and sends the results
	 *  back to the client.
	********************************************************* */
	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response)
	   throws ServletException, IOException
	{
		//THINGS NEED TO BE DONE
		//COMPETION OF TRUTH TABLE
		//CHECK FOR VALID INPUT PREDICATE AND PRINT MESSAGE TO USER
		
		
		//get vars
		String logicalOperation = request.getParameter(Data.LOGICALOPERATION.name()); //"A & B"
		String displaySelection = request.getParameter("display"); //"TRUE/FALSE"
		ArrayList displayOptions = new ArrayList();
		if (displaySelection != null ) {
			displayOptions = new ArrayList(Arrays.asList(displaySelection.split("/"))); //split by /
		}
		//Parse it into a structure that separates boolean variables and logical operators
		 ArrayList legalOps = new ArrayList(Arrays.asList("&&", "AND", "&","*", "^", "+", "||", "|", "OR", "V", "~", "NOT", "!", "==", "=", "EQUAL"));
	     ArrayList arrayEq = new ArrayList(Arrays.asList(logicalOperation.split(" "))); //split by space "A & B -> [A,&,B]"
	     ArrayList arrayOps = new ArrayList();
	     
	     ArrayList<EquationVars> variableArray = new ArrayList<>();
	 	 ArrayList<Object> equationArray = new ArrayList<>();
	 	 String[][] Table = null;
	 	boolean makeTable = true;
	     
	     //loops through the equation and stores all variables in a variable array.
	     for(int i = 0; i< arrayEq.size(); i++){
	    	 if(!(legalOps.contains(arrayEq.get(i)))){
	    		 boolean alreadyExists = false; //Keeps track of duplicate variables
	    		 EquationVars temp = new EquationVars((String)arrayEq.get(i),true);
	    		 
	    		//checks for duplicate variables and doesn't add them to the array twice
				for (EquationVars v : variableArray){
					if (v.getName()==temp.getName()){
						alreadyExists = true;
						temp = v;
					}
				}
				if (!alreadyExists){
					variableArray.add(temp); 
				}
				//stores the variable objects that are created in an equation array as well
				equationArray.add(temp);
	    	 }else {
	    		 // Operators get stored in an equation array
	    		 equationArray.add(arrayEq.get(i));
	    	 }
	     }
	     
	     //Creates an instance of the truth table with the proper parameters
	     if (variableArray.size() > 0){
	    	 Table = TruthTable(variableArray, equationArray);
	    	 // check if Final input has error
	    	 if (Table[Table.length - 1][Table[Table.length-1].length - 1] == "E") {
	    		 makeTable = false;makeTable = false;
	    	 }
	    	 if (logicalOperation.length() <= 1 || logicalOperation == null) {
	    		 makeTable = false;
	    	 }
	     }
		
		//remove nulls from arrayEq
		arrayEq.removeAll(Collections.singleton(null)); //now we have two array lists {A, B} and {&}
		
		//print the predicate they entered
		//Print a complete truth table for the predicate, including a column with the result for each row
		//for loop for table size look online
		
		// Change Display Feature
		String t = "1"; // Default setting
		String f = "0";
	    String[][] temp = Table;
	    if (displayOptions.size() > 0) {
	    	t = (String)displayOptions.get(0);
	    	f = (String)displayOptions.get(1);
	    }
	    
		//Echo the predicate to the user
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		PrintHead(writer);
 		PrintResponseBody(writer);
		writer.append("<!DOCTYPE html>")
			.append("<html>")
			.append("	<center>Display selected: " + displaySelection + "</center>")
			.append("	<center>You typed: " + logicalOperation + "</center>")
			.append("</html>");
		
		// Print Table
		if (makeTable) {
			changeDisplay(t, f, temp);
			writer.append("<center>");
			writer.append("<table border=2 cellpadding=0 cellspacing=0>");
			for (int i = 0; i < Table.length; i++) {	
				writer.append("<tr>");
				for (int j = 0; j < Table[i].length; j++) {
					writer.append("<td>" + Table[i][j] + "</td>");
				}
				writer.append("</tr>");
			}
			writer.append("</table> </center>");
		}
		else{
			writer.append("<center> INVALID EQUATION!!! </center>");
		}	
	  }
	


	/**
	 * The method that constructs the truth table
	 * It goes through every possible binary combination for the variables, and calls parseEquation()
	 * for each one. If parseEquation returns false, the program stops executing
	 */
	private static String[][] TruthTable(ArrayList<EquationVars> variables, ArrayList<Object> equation) {
		int width = variables.size();
		int length = (int) Math.pow(2, width);
		String[][] table = new String[length+1][width+2];
		table[0][0] = "Rows";
		
		//prints out the top row of the truth table
		for (int i = 0; i < variables.size(); i++){
			table[0][i+1] = variables.get(i).getName();
			System.out.print(" | " + variables.get(i).getName());
		}
		table[0][width+1] = "Result";
		
		for (int i=1; i<length+1; i++) {
			String value = "";
			String row = i + ".";
			table[i][0] = row;
			for (int j=width-1; j>=0; j--) {
				int v = (i-1)/(int) Math.pow(2, j)%2;
				if (v == 1){
					variables.get(j).setState(true);
					table[i][j+1] = "1";
	            }
				else {
					variables.get(j).setState(false);
					table[i][j+1] = "0";
				}
				//writer.append(row);
			}
			// Calculate current state of equation
			value = calculateBoolean(equation);
			table[i][width+1] = value;		
		}
		return table;
	}
	
	private static String calculateBoolean(ArrayList<Object> equation) {
		ArrayList AND = new ArrayList(Arrays.asList("&&", "AND", "&", "*"));
		ArrayList OR = new ArrayList(Arrays.asList("+", "^", "||", "|", "OR", "V"));
		ArrayList NOT = new ArrayList(Arrays.asList("~", "NOT", "!"));
		ArrayList<Object> temp = new ArrayList<>(equation);
		String result = "E";
		// change boolean values to ints
		for (int j = 0; j < temp.size();j++){
			if (temp.get(j).getClass().equals(EquationVars.class)){
				temp.set(j, ((EquationVars)temp.get(j)).getState() ? 1 : 0);
			}
		}
		for (int i = 0; i < temp.size(); i++){
			if (temp.get(i).getClass().equals(String.class)){
				// check is eligible for NOT operation
				if ((NOT.contains(temp.get(i))) && (temp.get(i+1).getClass().equals(Integer.class))){
					invertVal(i+1, temp);
					//System.out.println("NOT check:" + temp);
				}else if ((!(i == 0) && !(i == temp.size()-1)) && (temp.get(i-1).getClass().equals(Integer.class))){
					if ((temp.get(i+1).getClass().equals(Integer.class))){
						if (OR.contains(temp.get(i))){
							orValues(i-1, i+1, temp);
							//System.out.println("OR check:" + temp);
						}else if (AND.contains(temp.get(i))){
							andValues(i-1, i+1, temp);
							//System.out.println("AND check:" + temp);
						}
					}else if (NOT.contains(temp.get(i+1)) && (temp.get(i+2).getClass().equals(Integer.class))){
						invertVal(i+2,temp);
						if (OR.contains(temp.get(i))){
							orValues(i-1, i+2, temp);
						}else if (AND.contains(temp.get(i))){
							andValues(i-1, i+2, temp);
						}
						temp.set(i+1, "");
					}else{
						System.out.println("Invalid String");
						return result;
					}
				}else {
					System.out.println("Invalid String");
					return result;
				}
			//If two integers are next to each other, end the program for an improper equation
			}else if (temp.get(i).getClass().equals(Integer.class) && i < temp.size()-1 && temp.get(i+1).getClass().equals(Integer.class)){
				return result;
			}
		}
		result = temp.get(temp.size()-1).toString();
		return result;
	}
	
	public static void invertVal(int pos, ArrayList<Object> temp){
		if ((Integer)temp.get(pos)==0){
			temp.set(pos, 1);
		}else{
			temp.set(pos, 0);
		}
	}
	
	public static void orValues(int leftPos, int rightPos,  ArrayList<Object> temp){
		if ((Integer)temp.get(leftPos) == 1 || (Integer) temp.get(rightPos) == 1){
			temp.set(rightPos, 1);
		}else{
			temp.set(rightPos, 0);
		}
	}
	
	public static void andValues(int leftPos, int rightPos,  ArrayList<Object> temp){
		if ((Integer) temp.get(leftPos) == 1 && ((Integer) temp.get(rightPos)) == 1){
			temp.set(rightPos, 1);
		}else{
			temp.set(rightPos, 0);
		}
	}
	
	public static void changeDisplay(String t, String f, String[][] table) {
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				if (table[i][j].equals("1")) {
					table[i][j] = t;
				}
				else if(table[i][j].equals("0"))
				{ 
					table[i][j] = f;
				}
			}
		}
	}
	
	public static void printTable(String[][] table) {
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				System.out.print(table[i][j] + "|");
			}
			System.out.println();
		}
	}


	/** *****************************************************
	 *  Overrides HttpServlet's doGet().
	 *  Prints an HTML page with a blank form.
	********************************************************* */
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response)
	       throws ServletException, IOException
	{
	   response.setContentType("text/html");
	   PrintWriter out = response.getWriter();
	   request.setAttribute("writer", out);
	   PrintHead(out);
	   PrintBody(out, request, response);
	   PrintTail(out);
	} // End doGet
	
	/** *****************************************************
	 *  Prints the <head> of the HTML page, no <body>.
	********************************************************* */
	private void PrintHead (PrintWriter out)
	{
	   out.println("<html>");
	   out.println("");
	   out.println("<head>");
	   out.println("<meta charset=\"utf-8\" />");
	   out.println("<title>Logical Predicates</title>");
	   out.println(" <link rel=\"stylesheet\" type=\"text/css\" href=\"" + Style + "\">");
	   out.println("</head>");
	   out.println("");
	} // End PrintHead 

	
	/** *****************************************************
	 *  Prints the <BODY> of the HTML page with the form data
	 *  values from the parameters.
	 * @throws IOException 
	 * @throws ServletException 
	********************************************************* */
	private void PrintBody (PrintWriter out,String lhs, String rhs, String rslt, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		out.println("<body>");
		out.println("    <h1><center>Predicate Logic Calculator</center></h1>");
		out.println("    <h2><center>Sonal Kumar * Angela Gentile * George Terziysky * SWE-432-001</center>  </h2>");
		out.println("	 <h3><center>Formatting/Syntax Instructions:</center></h3>");
		out.println("    <h4><center>In order to calculate the final value of the logical operation, type in the predicate with the following constraints:</center></h4>");
		out.println("");
		out.println("    <ul>");
		out.println("        <li>The entry should be typed in the format of (Variable A)(LOGICAL OPERATOR)(Variable B)</li>");
		out.println("        <li>");
		out.println("            The entry can also be an extention of the format described above. There can be multiple Variables with logical operations in between them. For example, you can enter");
		out.println("            \"Apple OR Orange\",which would give you its Truth Table Values.");
		out.println("            Variables can be any letter or Name. Each Variable and Operand must be separated by a space.");
		out.println("        </li>");
		out.println("    </ul>");
		out.println("    <h4><center>Options for supported logical symbols:</center></h4>");
		out.println("    <ul>");
		out.println("        <li>Supported symbols for AND: \"&&\", \"AND\", \"&\", \"^\", \"*\"</li>");
		out.println("        <li>Supported symbols for OR: \"||\", \"|\", OR, \"V\", \"+\" </li>");
		out.println("        <li>Supported symbols for NOT: \"~\", \"NOT\", \"!\"</li>");
		out.println("        <li>Supported symbols for EQUAL: \"==\", \"=\", \"EQUAL\"</li>");
		out.println("    </ul>");
		out.println("    <br />    <br />    <br />");
		out.println("");
		out.println("    <form method=\"post\" action=\"\\assignment9\">");
		out.println("        <center>");
		out.println("			<select name=\"display\">");
		out.println("		  	<option value=\"1/0\" selected=selected>1/0</option>");
		out.println("			<option value=\"T/F\">T/F</option>");
		out.println("			<option value=\"t/f\">t/f</option>");
		out.println("			<option value=\"X/O\">X/O</option>");
		out.println("			<option value=\"TRUE/FALSE\">TRUE/FALSE</option>");
		out.println("		</select>");
		out.println("		</center>");
		out.println("    <br />");
		out.println("        <center>");
		out.println("            <label for=\"logicalOperation\">Enter Logical Operation:</label>");
		out.println("            <input type=\"text\" id=\"logicalOperation\" name=\"LOGICALOPERATION\"><br><br>");
		out.println("            <input type=\"submit\" value=\"Submit\" style=\"background-color: #80ced6\">");
		out.println("        </center>");
		out.println("    </form>");
		
		   RequestDispatcher dispatcher=request.getRequestDispatcher("/compute");          
		   dispatcher.include(request, response); 
		   
		out.println("<p><center>Collaboration Summary: All group members worked on different parts of the assignment and brought the pieces together in the end. Sonal worked on fixing up the previous html page used for assignment 3 and recreated the page to fulfill the requirements for the assignment. Angela worked on grabbing the input passed through from the user and using the doGet and doPost methods to manipulate the outcome. George calculated the outcome of the input text and created a table to visualize the results. George also made sure to include error checking to ensure that the user was only entering values that could be evaluated using logical predicates, and then dynamically implemented the truth table. </center></p>");
		out.println("</body>");
		out.println("");
		
	}
	
	/** *****************************************************
	 *  Overloads PrintBody (out,lhs,rhs,rslt) to print a page
	 *  with blanks in the form fields.
	 * @throws IOException 
	 * @throws ServletException 
	********************************************************* */
	private void PrintBody (PrintWriter out,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
	   PrintBody(out,"", "", "", request, response);
	}
	
	/** *****************************************************
	 *  Prints the bottom of the HTML page.
	********************************************************* */
	private void PrintTail (PrintWriter out)
	{
	   out.println("");
	   out.println("</html>");
	} // End PrintTail
	
	
	
	
	private void PrintResponseBody (PrintWriter out)
	{
		out.println("<body>");
		out.println("    <h1><center>Predicate Logic Calculator</center></h1>");
		out.println("    <h2><center>Sonal Kumar * Angela Gentile * George Terziysky * SWE-432-001</center>  </h2>");
		out.println("	 <h3><center>Formatting/Syntax Instructions:</center></h3>");
		out.println("    <h4><center>In order to calculate the final value of the logical operation, type in the predicate with the following constraints:</center></h4>");
		out.println("");
		out.println("    <ul>");
		out.println("        <li>The entry should be typed in the format of (Variable A)(LOGICAL OPERATOR)(Variable B)</li>");
		out.println("        <li>");
		out.println("            The entry can also be an extention of the format described above. There can be multiple Variables with logical operations in between them. For example, you can enter");
		out.println("            \"Apple OR Orange\",which would give you its Truth Table Values.");
		out.println("            Variables can be any letter or Name. Each Variable and Operand must be separated by a space.");
		out.println("        </li>");
		out.println("    </ul>");
		out.println("    <h4><center>Options for supported logical symbols:</center></h4>");
		out.println("    <ul>");
		out.println("        <li>Supported symbols for AND: \"&&\", \"AND\", \"&\", \"^\", \"*\"</li>");
		out.println("        <li>Supported symbols for OR: \"||\", \"|\", OR, \"V\", \"+\" </li>");
		out.println("        <li>Supported symbols for NOT: \"~\", \"NOT\", \"!\"</li>");
		out.println("        <li>Supported symbols for EQUAL: \"==\", \"=\", \"EQUAL\"</li>");
		out.println("    </ul>");
		out.println("    <br />    <br />    <br />");
		out.println("");
		out.println("    <form method=\"post\" action=\"\\assignment9\">");
		out.println("        <center>");
		out.println("			<select name=\"display\">");
		out.println("		  	<option value=\"1/0\" selected=selected>1/0</option>");
		out.println("			<option value=\"T/F\">T/F</option>");
		out.println("			<option value=\"t/f\">t/f</option>");
		out.println("			<option value=\"X/O\">X/O</option>");
		out.println("			<option value=\"TRUE/FALSE\">TRUE/FALSE</option>");
		out.println("		</select>");
		out.println("		</center>");
		out.println("    <br />");
		out.println("        <center>");
		out.println("            <label for=\"logicalOperation\">Enter Logical Operation:</label>");
		out.println("            <input type=\"text\" id=\"logicalOperation\" name=\"LOGICALOPERATION\"><br><br>");
		out.println("            <input type=\"submit\" value=\"Submit\" style=\"background-color: #80ced6\">");
		out.println("        </center>");
		out.println("    </form>");
		out.println("<p><center>Collaboration Summary: All group members worked on different parts of the assignment and brought the pieces together in the end. Sonal worked on fixing up the previous html page used for assignment 3 and recreated the page to fulfill the requirements for the assignment. Angela worked on grabbing the input passed through from the user and using the doGet and doPost methods to manipulate the outcome. George calculated the outcome of the input text and created a table to visualize the results. George also made sure to include error checking to ensure that the user was only entering values that could be evaluated using logical predicates, and then dynamically implemented the truth table. </center></p>");
		
	}
	
	
	private void PrintResponseBodyEnd (PrintWriter out){
		
		out.println("</body>");
		out.println("");
		
	}

}

class EquationVars {
	
	private boolean state;
	private String name;
	
	/**
	 * Stores variables with a name and a binary state (1 or 0)
	 */
	public EquationVars(String theName, boolean theState){
		name = theName;
		state = theState;
	}
	
	// Returns the binary state 
	public boolean getState(){
		return state;
	}
	
	// Returns the name 
	public String getName(){
		return name;
	}

	public void setState(boolean b){
		state = b;	
	}
	
	@Override
	public String toString() {
		return ("Variable: " + this.getName()+ " Value: " + this.getState());
	}
		
}