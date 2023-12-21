import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Lab2 extends JFrame implements ActionListener {
	JButton open = new JButton("Next Program");
	JTextArea result = new JTextArea(20,40);
	JLabel errors = new JLabel();
	JScrollPane scroller = new JScrollPane();
	
	public Lab2() {
		setLayout(new java.awt.FlowLayout());
		setSize(500,430);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(open); open.addActionListener(this);
		scroller.getViewport().add(result);
		add(scroller);
		add(errors);
	}
	
	public void actionPerformed(ActionEvent evt) {
		result.setText("");	//clear TextArea for next program
		errors.setText("");
		processProgram();
	}
	
	public static void main(String[] args) {
		Lab2 display = new Lab2();
		display.setVisible(true);
	}
	
	String getFileName() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile().getPath();
		else
			return null;
	}
		
	void processProgram() {
		String fileName = getFileName();
		ArrayList<String> code = new ArrayList<String>();
		ArrayList<String> variable = new ArrayList<String>();
		ArrayList<Double> value = new ArrayList<Double>();
		if(fileName != null) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(fileName));
				String line;
				while((line=in.readLine()) != null) {
					StringTokenizer tok = new StringTokenizer(line, "\n");
					while(tok.hasMoreTokens()) { 
						String nextLine = tok.nextToken(); //builds the list "code" to contain each line in the file, whereas line 1 is at index 0, line 2 is at index 1, etc
						code.add(nextLine);
					}
				}
				boolean error = false;
				for(int i=0; i<code.size(); i++) { //loops from index 0 to last index in the "code" list, simulates going line by line in the program
					String compile = code.get(i);
					if(!error) { //if an error at any line is detected, error will be true and the next line will not be checked to prevent any further errors
						if(compile.indexOf("IF")>=0) {
							String vari = compile.substring(compile.indexOf("IF")+2, compile.indexOf("IS")).trim(); //breaks the line up into a variable, value, and a command
							String dVal = compile.substring(compile.indexOf("IS")+2, compile.indexOf("THEN")).trim();   	
							String command = compile.substring(compile.indexOf("N")+1).trim();
							if(isValidDouble(dVal)) { //checks if there is a legitimate double value to get checked
								int var = variable.indexOf(vari);
								if(var>=0) { //if the variable you are comparing actually exists in variable list
									if((Double.parseDouble(dVal))==(value.get(var))) { //if the value corresponding to the variable matches the given in the conditional statement
										if(command.indexOf("=")>=0) {
											String vars = command.substring(0,command.indexOf("=")).trim(); //extracts variable name from the command
											if(variable.indexOf(vars)<0) { // if variable does not already contain the variable, add it
												variable.add(vars);
												value.add(0.0);
											}
											String expression = command.substring(command.indexOf("=")+1).trim();
											StringTokenizer tokExp = new StringTokenizer(expression, " "); //breaks up the expression into numbers and operators
											ArrayList<String> exp = new ArrayList<String>();
											while(tokExp.hasMoreTokens()) {
												exp.add(tokExp.nextToken()); //builds the list of numbers and operators
											}
											for(int m=0; m<variable.size(); m++) { //cross references the variable list and list of components in the expression, and uses their values for arithmetic
												for(int n=0; n<exp.size(); n++) {
													if(variable.get(m).equals(exp.get(n))) {
														double temp = value.get(m);
														exp.set(n, (temp + ""));
													}
												}
											}
											double val=0;
											boolean opActive = false; //boolean value for whether or not there is a operator active
											for(int j =0; j<exp.size(); j++) { 
												if(opActive) { 
													if(exp.get(j).equals("+")) { //if there was just an operator and now there is another, error
														error=true;
														errors.setText("Error - Consecutive operators in simple expression");
													}else if(exp.get(j).equals("-")) {
														error=true;
														errors.setText("Error - Consecutive operators in simple expression");
													}else if(exp.get(j).equals("*")) {
														error=true;
														errors.setText("Error - Consecutive operators in simple expression");
													}else if(exp.get(j).equals("/")) {
														error=true;
														errors.setText("Error - Consecutive operators in simple expression");
													}else {
														switch(exp.get(j-1)) { //if one before was +-/*, do the operation
															case "+": {
																if(isValidDouble(exp.get(j))) {
																	val = val + Double.parseDouble(exp.get(j));
																	opActive = false;
																	break;
																} 
																else {
																	error=true;
																	errors.setText("Error - Invalid value in simple expression");
																	break;
																}	
															}
															case "-": {
																if(isValidDouble(exp.get(j))) {
																	val = val - Double.parseDouble(exp.get(j));
																	opActive = false;
																	break;
																} 
																else {
																	error=true;
																	errors.setText("Error - Invalid value in simple expression");
																	break;
																}	
															}
															case "/": {
																if(isValidDouble(exp.get(j))) {
																	if(Double.parseDouble(exp.get(j))!= 0) {
																		val = val / Double.parseDouble(exp.get(j));
																		opActive = false;
																		break;
																	}
																	else {
																		error=true;
																		errors.setText("Error - Divide by 0");
																		break;
																	}
																} 
																else {
																	error=true;
																	errors.setText("Error - Invalid value in simple expression");
																	break;
																}	
															}
															case "*": {
																if(isValidDouble(exp.get(j))) {
																	val = val * Double.parseDouble(exp.get(j));
																	opActive = false;
																	break;
																}
																else {
																	error=true;
																	errors.setText("Error - Invalid value in simple expression");
																	break;
																}
															}
															default: { //if one before was not one of these and opActive was true, error
																error=true;
																errors.setText("Error - Invalid simple expression");
															}
														}
													}
												}
												else if(opActive==false){
													switch(exp.get(j)) { //if current one is +-/*, set opActive to true
														case "+": {
															opActive = true;
															break;
														}case "-": {
															opActive = true;
															break;
														}case "/": {
															opActive = true;
															break;
														}case "*": {
															opActive = true;
															break;
														}default: {
															if(isValidDouble(exp.get(j))) {
															val = Double.parseDouble(exp.get(j)); //if no active op and it is not an operator, set value to this for later on arithmetic
															}
															else {
																error=true;
																errors.setText("Error - Invalid component in simple expression");
															}
														}
													}
												}
											}
											value.set(variable.indexOf(vars), val);						
										}										
										else if(command.indexOf("GOTO")>=0) {
		
											StringTokenizer tokGo = new StringTokenizer(command, "GOTO ");
											String lineNum = tokGo.nextToken();
											if(isValidInt(lineNum)) {
												int index = Integer.parseInt(lineNum); 
												if((index>0)&&(index<=code.size())){ //if index is within its bounds, change the increment variable so line # changes
													i = index - 2;
												} else {
													error=true;
													errors.setText("Error - Invalid line number");
												}
											}
											else {
												error=true;
												errors.setText("Error - Invalid line number");
											}
										}
										else if(command.indexOf("PRINT")>=0) {
											StringTokenizer tokPrin = new StringTokenizer(command, "PRINT ");
											String printVar = tokPrin.nextToken();
											if(variable.indexOf(printVar)>=0) {
												result.append(String.format("%.2f", value.get(variable.indexOf(vari))) + "\n");
											}
											else {
												error=true;
												errors.setText("Error - Printed variable not defined");
											}
										}
										else {
											error=true;
											errors.setText("Error - Invalid conditional statement");
										}
									}

								}
								else {
									error = true;
									errors.setText("Error - Invalid variable in Conditional");
								}
							}
							else {
								error=true;
								errors.setText("Error - Invalid value in Conditional");
							}
						} else if(compile.indexOf("IF")<0) {						
							if(compile.indexOf("=")>=0) {
								//Needs to be able to take left and right side of = as two separate array list of variables and values
								String var = compile.substring(0,compile.indexOf("=")).trim();
								if(variable.indexOf(var)<0) { // if variable does not already contain var, add it
									variable.add(var);
									value.add(0.0);
								}
								String expression = compile.substring(compile.indexOf("=")+1).trim();
								StringTokenizer tokExp = new StringTokenizer(expression, " "); //breaks up the expression into numbers and operators
								ArrayList<String> exp = new ArrayList<String>();
								while(tokExp.hasMoreTokens()) {
									exp.add(tokExp.nextToken()); //builds the list of numbers and operators
								}
							
								//needs screening to see if anything present in variable list is here in the expression, if it is then swap the double value of the variable in here for arithmetic

								for(int m=0; m<variable.size(); m++) {
									for(int n=0; n<exp.size(); n++) {
										if(variable.get(m).equals(exp.get(n))) {
											double temp = value.get(m);
											exp.set(n, (temp + ""));
										}
									}
								}
								double val=0;
								boolean opActive = false; //boolean value for whether or not there is a operator active
								for(int j =0; j<exp.size(); j++) {
									String component = exp.get(j);
									if(opActive) { 
										if(component.equals("+")) { //if there was just an operator and now there is another, error
											error=true;
											errors.setText("Error - Consecutive operators in simple expression");
										}else if(component.equals("-")) {
											error=true;
											errors.setText("Error - Consecutive operators in simple expression");
										}else if(component.equals("*")) {
											error=true;
											errors.setText("Error - Consecutive operators in simple expression");
										}else if(component.equals("/")) {
											error=true;
											errors.setText("Error - Consecutive operators in simple expression");
										}else {
											switch(exp.get(j-1)) { //if one before was +-/*, do the operation
												case "+": {
													if(isValidDouble(component)) {
														val = val + Double.parseDouble(component);
														opActive = false;
														break;
													} 
													else {
														error=true;
														errors.setText("Error - Invalid value in simple expression");
														break;
													}	
												}
												case "-": {
													if(isValidDouble(component)) {
														val = val - Double.parseDouble(component);
														opActive = false;
														break;
													} 
													else {
														error=true;
														errors.setText("Error - Invalid value in simple expression");
														break;
													}	
												}
												case "/": {
													if(isValidDouble(component)) {
														if(Double.parseDouble(component)!=0) {
															val = val / Double.parseDouble(component);
															opActive = false;
															break;
														}
														else {
															error=true;
															errors.setText("Error - Divide by 0");
															break;
														}
													} 
													else {
														error=true;
														errors.setText("Error - Invalid value in simple expression");
														break;
													}	
												}
												case "*": {
													if(isValidDouble(component)) {
														val = val * Double.parseDouble(component);
														opActive = false;
														break;
													} 
													else {
														error=true;
														errors.setText("Error - Invalid value in simple expression");
														break;
													}
												}
												default: { //if one before was not one of these and opActive was true, error
													error=true;
													errors.setText("Error - Invalid simple expression");
												}
											}
										}
									}
									else if(opActive==false){
										switch(component) { //if current one is +-/*, set opActive to true
											case "+": {
												opActive = true;
												break;
											}case "-": {
												opActive = true;
												break;
											}case "/": {
												opActive = true;
												break;
											}case "*": {
												opActive = true;
												break;
											}default: {
												if(isValidDouble(component)) {
												val = Double.parseDouble(component); //if no active op
												}
												else {
													error=true;
													errors.setText("Error - Invalid component in simple expression");
												}
											}
										}
									}
								}
								value.set(variable.indexOf(var), val);						
							}
							else if(compile.indexOf("END")>=0) {
								if(compile.equals(code.get(code.size()-1))) { //if END is on the last line
									i=code.size();
								}
								else {
									error=true;
									errors.setText("Error - END not on last line");
								}
							}
							else if(compile.indexOf("GOTO")>=0) {
								StringTokenizer tokGo = new StringTokenizer(compile, "GOTO ");
								String lineNum = tokGo.nextToken();
								if(isValidInt(lineNum)) {
									int index = Integer.parseInt(lineNum);
									if((index>0)&&(index<=code.size())){
										i = index - 2;
									} else {
										error=true;
										errors.setText("Error - Invalid line number");
									}
								} 
								else {
									error=true;
									errors.setText("Error - Invalid line number");
								}
							}
							else if(compile.indexOf("PRINT")>=0) {
								StringTokenizer tokPrin = new StringTokenizer(compile, "PRINT ");
								String vari = tokPrin.nextToken();
								if(variable.indexOf(vari)>=0) {
									result.append(String.format("%.2f", value.get(variable.indexOf(vari))) + "\n");
								}
								else {
									error=true;
									errors.setText("Error - Printed variable not defined");
								}
							}
						}
					}
					if(error) {
						result.setText("");
						i=code.size();
					}
					in.close();
				}
			} catch(IOException e ) {
				errors.setText("ERROR" + e);
			}
		}
	}
	boolean isValidDouble(String s) {
		boolean valid = true;
		if(s.equals("")||s.equals(null)) { // if the parameter is the empty string or null it is capable of parsing to a double but is not a valid number, so return false
			valid = false;
		} else {
			try {
			Double.parseDouble(s);  // try to parse the double, if it works great if it doesn't catch the exception and return a false value
			}
			catch (NumberFormatException e) {
			valid = false;			
			}
		}
		return valid;	
	}
	boolean isValidInt(String s) { // checks if the parameter string is capable of parsing to an integer and returns a T/F value
		boolean valid = true;
		if(s.equals("")||s.equals(null)) { // if the parameter is the empty string or null it is capable of parsing to an integer but is not a valid number, so return false
			valid = false;
		} else {
			try {
			Integer.parseInt(s);  // try to parse the integer, if it works great if it doesn't catch the exception and return a false value
			}
			catch (NumberFormatException e) {
			valid = false;			
			}
		}
		return valid;	
	}
}
