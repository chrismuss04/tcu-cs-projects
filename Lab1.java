import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class Lab1 extends JFrame implements ActionListener {
	static final long serialVersionUID = 1l;
	private JTextField assemblerInstruction;
	private JTextField binaryInstruction;
	private JTextField hexInstruction;
	private JLabel errorLabel;
	
	public Lab1() {
		setTitle("XDS Sigma 9");
		setBounds(100, 100, 400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
// * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// SET UP THE ASSEMBLY LANGUAGE TEXTFIELD AND BUTTON
		assemblerInstruction = new JTextField();
		assemblerInstruction.setBounds(25, 24, 134, 28);
		getContentPane().add(assemblerInstruction);

		JLabel lblAssemblyLanguage = new JLabel("Assembly Language");
		lblAssemblyLanguage.setBounds(30, 64, 160, 16);
		getContentPane().add(lblAssemblyLanguage);

		JButton btnEncode = new JButton("Encode");
		btnEncode.setBounds(200, 25, 117, 29);
		getContentPane().add(btnEncode);
		btnEncode.addActionListener(this);
// * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// SET UP THE BINARY INSTRUCTION TEXTFIELD AND BUTTON
		binaryInstruction = new JTextField();
		binaryInstruction.setBounds(25, 115, 330, 28);
		getContentPane().add(binaryInstruction);

		JLabel lblBinary = new JLabel("Binary Instruction");
		lblBinary.setBounds(30, 155, 190, 16);
		getContentPane().add(lblBinary);

		JButton btnDecode = new JButton("Decode Binary");
		btnDecode.setBounds(200, 150, 150, 29);
		getContentPane().add(btnDecode);
		btnDecode.addActionListener(this);
// * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// SET UP THE HEX INSTRUCTION TEXTFIELD AND BUTTON
		hexInstruction = new JTextField();
		hexInstruction.setBounds(25, 220, 134, 28);
		getContentPane().add(hexInstruction);

		JLabel lblHexEquivalent = new JLabel("Hex Instruction");
		lblHexEquivalent.setBounds(30, 260, 131, 16);
		getContentPane().add(lblHexEquivalent);

		JButton btnDecodeHex = new JButton("Decode Hex");
		btnDecodeHex.setBounds(200, 220, 150, 29);
		getContentPane().add(btnDecodeHex);
		btnDecodeHex.addActionListener(this);		
// * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// SET UP THE LABEL TO DISPLAY ERROR MESSAGES
		errorLabel = new JLabel("");
		errorLabel.setBounds(25, 320, 280, 16);
		getContentPane().add(errorLabel);
	}

	public void actionPerformed(ActionEvent evt) {
		errorLabel.setText("");
		if (evt.getActionCommand().equals("Encode")) {
			encode();
		} else if (evt.getActionCommand().equals("Decode Binary")) {
			decodeBin();
		} else if (evt.getActionCommand().equals("Decode Hex")) {
			decodeHex();
		}
	}

	public static void main(String[] args) {
		Lab1 window = new Lab1();
		window.setVisible(true);
	}

// USE THE FOLLOWING METHODS TO CREATE A STRING THAT IS THE
// BINARY OR HEX REPRESENTATION OF A SORT OR INT

// CONVERT AN INT TO 8 HEX DIGITS
	String displayIntAsHex(int x) {
		String ans="";
		for (int i=0; i<8; i++) {
			int hex = x & 15;
			char hexChar = "0123456789ABCDEF".charAt(hex);
			ans = hexChar + ans;
			x = (x >> 4);
		}
		return ans;
	}

// CONVERT AN INT TO 32 BINARY DIGITS
	String displayIntAsBinary(int x) {
		String ans="";
		for(int i=0; i<32; i++) {
			ans = (x & 1) + ans;
			x = (x >> 1);
		}
		return ans;
	}
// ENCODE ASSEMBLY INTO BINARY / HEXADECIMAL
	void encode() {
	String instruct = assemblerInstruction.getText();
	int  machine = 0;
	boolean error = false;
	
	if(instruct.indexOf(" ")<0) { //if there are no spaces at all in the input, error
		errorLabel.setText("Error - Incorrect Assembly Format");
		error=true;
	} 
	else if(instruct.indexOf(",")<0) { //if there are no commas at all in the input, error
		errorLabel.setText("Error - Incorrect Assembly Format");
		error=true;
	} 
	else if(instruct.indexOf(" ")<instruct.indexOf(",")) { // if there is a space before a comma, error -> would be LI ,5  as the input instead of LI,5
		errorLabel.setText("Error - Incorrect assembly format");
		error = true;
	} 
	else {
		String operation = instruct.substring(0,instruct.indexOf(",")); // cuts the input down to only from index 0 to the index of the first comma
		switch(operation) {
			case "LI": {
				machine = machine | (0b00100010<<24); // sets the leftmost 8 bits of the machine code
				break;
			}
			case "LW": {
				machine = machine | (0b00110010<<24);
				break;
			}
			case "AW": {
				machine = machine | (0b00110000<<24);
				break;
			}
			case "STW": {
				machine = machine | (0b00110101<<24);
				break;
			}
			default: {
				errorLabel.setText("Error - Invalid Mneonomic"); // if the operation code does not match one of the 4 possible, error
				error=true;
			}
		}
		if(!error) {
			String strR = instruct.substring(instruct.indexOf(",")+1,instruct.indexOf(" ")); // sets the string for R to be from 1 more than the index of the comma to the first space
			int register = 0;
			if(isValidInt(strR)) {
				register = (int)Long.parseLong(strR);  // parses the string r into its respective integer value
				if(register<=15&&register>=0) {
					machine = machine | (register<<20); // sets the respective bits for the R value
					
					String second = instruct.substring(instruct.indexOf(" ")).trim(); // sets the string for second part to be everything after the first space, uses trim method to get rid of any superfluous leading 0s
					if(operation.equals("LW")||operation.equals("AW")||operation.equals("STW")) { 
						if(second.startsWith("*")) {
							machine = machine | (0b10000000<<24); //sets high order bit to be a 1 if there is an asterisk in the correct place
							second = second.substring(1); // shortens the second part to be everything after the asterisk if it is present
						}
						String strD = second;   
						int d = 0;
						if(strD.indexOf(",")<0) { // if there is no comma present in the string D
							if(isValidInt(strD)) {
								d = (int)Long.parseLong(strD);  // parses the string D into its respective integer value
								if(d<=131071&&d>=0) {
									machine = machine | d; // sets the respective bits for D
								}
								else {
									errorLabel.setText("Error - Illegal Value for D"); // if D is out of its bounds, error
									error=true;
								}
							}
							else {
								errorLabel.setText("Error - Illegal Number"); // if D is not a valid integer, error
								error=true;
							}
						} else { // if there is a comma in the string D
							strD = strD.substring(0, strD.indexOf(","));  // sets the string D to be everything from the beginning up to the comma
							if(isValidInt(strD)) {
								d = (int)Long.parseLong(strD); // parses the string D into its respective integer value 
								if(d<=131071&&d>=0) {
									machine = machine | d; // sets the respective bits for D
									
									String strX = second.substring(second.indexOf(",")+1); // sets the string X to be everything after the comma
									int x = 0;
									if(isValidInt(strX)) {
										x = (int)Long.parseLong(strX); // parses the string X into its respective integer value
										if(x<=7&&x>=1) {
											machine = machine | (x<<17); // sets the respective bits for X
										}
										else {
											errorLabel.setText("Error - Illegal Value for X"); // if X is not within its bounds, error
											error=true;
										}
									}
									else {
										errorLabel.setText("Error - Illegal Number"); // if X is not a valid integer, error
										error=true;
									}
								}
								else {
									errorLabel.setText("Error - Illegal Value for D"); // if D is not within its bounds, error
									error=true;
								}
							}
							else {
								errorLabel.setText("Error - Illegal Number"); // if D is not a valid integer, error
								error=true;
							}
						}	
					}
					else if(operation.equals("LI")) { 
							String strV = second; // sets string Value to be everything after the spaces
							int v = 0;
							if(isValidInt(strV)) {
								v = (int)Long.parseLong(strV); // parses the string V into its respective integer value
								if(v>=-524288&&v<=524287) {
									v = v & 0x000FFFFF; // if V is negative then "v" is followed by leading 1s instead of 0s because of how parse works, so &ing it with 000FFFFF guarantees that only the correct bits are turned on
									machine = machine | v;	// sets the respective bits for V								
								}
								else {
									errorLabel.setText("Error - Illegal value for V"); // if V is not within its bounds, error
									error=true;
								}
							}
							else {
								errorLabel.setText("Error - Illegal Number"); // if V is not a valid integer, error
								error=true;
							}
						}
					}
					else {
						errorLabel.setText("Error - Illegal Value for R"); // if R is not within its bounds, error
						error=true;
					}
			}
			else {
				errorLabel.setText("Error - Illegal Number"); // if R is not a valid integer, error
				error=true;
			}			
		}	
	}
	if(error) { // if an error occurs, sets the hexInstruction and binaryInstruction JTextfields to the empty string
		hexInstruction.setText("");
		binaryInstruction.setText("");
	}
	else {
		hexInstruction.setText(displayIntAsHex(machine));
		binaryInstruction.setText(displayIntAsBinary(machine));
	}
	}
// DECODE BINARY 
	void decodeBin() {
		String getBin = binaryInstruction.getText().trim();
		boolean error=false;
			
		if(getBin.length()!=32) {
			errorLabel.setText("Error - Incorrect Length"); // if length post-trim is not correct, error
			error=true;
		}
		else if(getBin.matches("[0-1]+")==false) {
			errorLabel.setText("Error - Incorrect Format"); // if a character other than 0 or 1 is present post trim, error
			error=true;
		}
		else {
			int binInt = (int)Long.parseLong(getBin, 2); // parses the binary string into an integer to store its value
			int displayInt = binInt; // stores this binary int in displayInt
			
			String asterisk = "";
			if(getBin.substring(0,1).equals("1")) { // if the first bit is a 1, set asterisk to "*" and change the high order bit to a 0 so it can be matched against the operations
				asterisk = "*";
				binInt = binInt & (0x7FFFFFFF);
			
			}
			int binOp = binInt & (0xFF<<24); // sets the value for binary operation to have its respective value
			String operation = "";
			switch(binOp) {
				case 0b00100010<<24: { // if the first 8 bits of the input match the case, operation gets set as the matched op code
					operation = "LI";
					break; }
				case 0b00110010<<24: {
					operation = "LW";
					break; }
				case 0b00110000<<24: {
					operation = "AW";
					break; }
				case 0b00110101<<24: {
					operation = "STW";
					break; }
				default: {
					errorLabel.setText("Error - Invalid first eight bits"); // if first 8 bits don't match one of the 4 options, error
					error=true;
				}
			}
			
			if(asterisk.equals("*")&&(operation.equals("LI"))) { // if high order bit was a 1 and operation is LI, then there should be an error
				errorLabel.setText("ERROR - Illegal Number");
				error=true;
			}
			else {
				int reg=(binInt & (0x00F00000))>>20; // sets the value of int register to its respective bits
				if((reg<0)||(reg>15)) {
					errorLabel.setText("Error - R out of bounds"); // if R is not within its bounds, error
					error=true;
				}
				else {
					int value = 0;
					if(operation.equals("LI")) {
						value = binInt & (0x000FFFFF); // if LI, sets int value to its respective bits
						if((value<-524288)||(value>524287)) {
							errorLabel.setText("Error - V out of bounds"); // if V is not within its bounds, error
							error=true;
						}
					}
					else if((operation.equals("LW"))||(operation.equals("AW"))||(operation.equals("STW"))) {
						value = binInt & (0x0001FFFF); // if LW,AW,or STW value is really displacement with different bounds and # of bits
						if((value<-65536)||(value>65535)) {
							errorLabel.setText("Error - D out of bounds"); // if D is not within its bounds, error
							error=true;
						}
					}
					
					int x = 0;
					if((operation.equals("LW"))||(operation.equals("AW"))||(operation.equals("STW"))) { // if LW,AW,or STW, there can be an x register
						x = (binInt & (0x000E0000))>>17; // sets int for the x register to its respective bits
						if(x>7||x<0) {
							errorLabel.setText("Error - X out of bounds"); // if X is not within its bounds, error
							error=true;
						}
					}

					if((error==false)&&(operation.equals("LI"))) {
						hexInstruction.setText(displayIntAsHex(displayInt));
						assemblerInstruction.setText(operation + "," + reg + "  " + value); // if operation is LI, then assembly instructions are op,reg value and cannot have an asterisk or a X register
					}
					else if((error==false)&&(operation.equals("LW")||operation.equals("AW")||operation.equals("STW"))) {
						if(x==0) { // if x register is 0, no comma or x register is displayed
							hexInstruction.setText(displayIntAsHex(displayInt));
							assemblerInstruction.setText(operation+ "," + reg + "  " + asterisk + value);
						}
						else if(x>0) { // if there is an x register 1-7, then it is displayed with a comma following the displacement
							hexInstruction.setText(displayIntAsHex(displayInt));
							assemblerInstruction.setText(operation + "," + reg + "  " + asterisk + value + "," + x);
						}
					}
				}
			}
		}
		if(error == true) {
			hexInstruction.setText(""); // if there is an error, sets the hexInstruction and assemblerInstruction JTextfields to the empty string
			assemblerInstruction.setText("");
		}
	}
// DECODE HEXADECIMAL INTO ASSEMBLY
	void decodeHex() {
		String getHex = hexInstruction.getText().trim();
		boolean error=false;
		
		if((getHex.length())!=(8)) { // if the input is the incorrect length post-trim, error
		errorLabel.setText("Error - Incorrect Length");
		error=true;			
		}
		else if	(getHex.matches("[0-9A-F]+")==false) { // if there is a character not contained in 0123456789ABCDEF, error
			errorLabel.setText("Error - Incorrect Format");
			error=true;	
		}
		else {
			int hexInt = (int)Long.parseLong(getHex, 16); // parses the input string into int hexInt
			int displayInt = hexInt; // stores the value of hexInt in displayInt
			
			String asterisk = "";
			if((hexInt>>>31)==1) { // if high order bit is a 1, set asterisk to "*" and set the high order bit to a 0 so it can be compared to the op codes
				asterisk = "*";
				hexInt = hexInt & (0x7FFFFFFF);
			}
			int hexOp = hexInt & (0xFF<<24);
			String operation = "";	
			switch(hexOp) {
				case 0b00100010<<24: { // if the opcode matches,  set the integer value hexOp to have that value
					operation = "LI";
					break; }
				case 0b00110010<<24: {
					operation = "LW";
					break; }
				case 0b00110000<<24: {
					operation = "AW";
					break; }
				case 0b00110101<<24: {
					operation = "STW";
					break; }
				default: {
					errorLabel.setText("Error - Invalid first two digits");
					error=true;
				}
			}
			if((operation.equals("LI"))&&(asterisk.equals("*"))) { // if op code is LI and there is an asterisk, there will be an error
				errorLabel.setText("Error - Illegal number");
				error=true;
			}
			else {	
				int reg = (hexInt &(0x00F00000))>>20; // sets the integer value for the register to its respective bits
				if((reg<0)||(reg>15)) {
					errorLabel.setText("Error - R out of bounds"); // if R is not within its bounds, error
					error=true;
				}
				else {		
					int value =0;
					int xReg = 0;
					if(operation.equals("LI")) { // if op code is LI then int value has different bounds and # of bits
						value = hexInt & (0x000FFFFF); // sets V to its respective bits
						if((value<-524288)||(value>524287)) { // if V is not within its bounds, error
							errorLabel.setText("Error - V out of bounds");
							error=true;
						}
					} 
					else if((operation.equals("LW"))||(operation.equals("AW"))||(operation.equals("STW"))) {
						value = hexInt & (0x0001FFFF); // sets D to its respective bits
						if((value<-65536)||(value>65535)) {
							errorLabel.setText("Error - D out of bounds"); // if D is not within its bounds, error
							error=true;
						}
						xReg = (hexInt & (0x000E0000))>>17; // sets the X register to its respective bits
						if((xReg<0)||(xReg>7)) {
							errorLabel.setText("Error - X out of bounds"); // if X is not within its bounds, error -> x can be 0 here because it is checked later 
							error=true;
						}
						if((error==false)&&(operation.equals("LW")||operation.equals("AW")||operation.equals("STW"))) {
							if(xReg>=1) { // if x is within bounds and not 0, it is displayed with a comma following displacement
								binaryInstruction.setText(displayIntAsBinary(displayInt));
								assemblerInstruction.setText(operation + "," + reg + "  " + asterisk + value + "," + xReg);
							}
							else if(xReg==0) { // if x is 0, it is not displayed and neither is the comma
								binaryInstruction.setText(displayIntAsBinary(displayInt));
								assemblerInstruction.setText(operation + "," + reg + "  " + asterisk + value);
							}
						} 
						else if((error==false)&&(operation.equals("LI"))) {
							binaryInstruction.setText(displayIntAsBinary(displayInt));
							assemblerInstruction.setText(operation + "," + reg + "  " + value); // LI can neither have a comma following V nor an asterisk preceding V, so they are not included
						}
					}
				}
			}
		}	
		if(error==true) {
			assemblerInstruction.setText(""); // if there is an error anywhere, sets the assemblerInstruction and binaryInstruction JTextfields to the empty string
			binaryInstruction.setText("");
		}
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
