/*	Program:		 Lab 5
 *	
 * 	Student Name: 	 Robert Hannaford and Chris Musselman
 *	Semester: 		 Spring 2023
 *	Class Section: 	 COSC 20203
 *	Instructor: 	 Dr. Rinewalt
 * 
 */

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

public class Lab5Client extends JFrame implements ActionListener {
	static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		Lab5Client scc = new Lab5Client();
		scc.setVisible(true);
	}
	JRadioButton dieselButton;
	JRadioButton steamButton;
	ButtonGroup locomotiveGroup;

	JRadioButton cabooseButton;
	JRadioButton presidentialButton;
	JRadioButton firstClassButton;
	JRadioButton openAirButton;
	ButtonGroup seatingGroup;

	JLabel adultLabel;
	JTextField adultTF;
	JLabel childrenLabel;
	JTextField childrenTF;

	JButton calcButton;
	JLabel ansLabel;
	JTextField ansTF;
	
	JLabel errorLabel;

	public Lab5Client() {
		setTitle("Price Calculator");
		setBounds(100, 100, 320, 280);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		dieselButton = new JRadioButton("Diesel");
		dieselButton.setBounds(25, 24, 100, 16);
		dieselButton.setSelected(true);
		getContentPane().add(dieselButton);

		steamButton = new JRadioButton("Steam");
		steamButton.setBounds(25, 64, 100, 16);
		getContentPane().add(steamButton);

		locomotiveGroup = new ButtonGroup();
		locomotiveGroup.add(dieselButton);
		locomotiveGroup.add(steamButton);

		cabooseButton = new JRadioButton("Caboose");
		cabooseButton.setBounds(170, 14, 100, 16);
		cabooseButton.setSelected(true);
		getContentPane().add(cabooseButton);

		presidentialButton = new JRadioButton("Presidential");
		presidentialButton.setBounds(170, 34, 140, 16);
		getContentPane().add(presidentialButton);

		firstClassButton = new JRadioButton("First Class");
		firstClassButton.setBounds(170, 54, 140, 16);
		getContentPane().add(firstClassButton);

		openAirButton = new JRadioButton("Open Air");
		openAirButton.setBounds(170, 74, 140, 16);
		getContentPane().add(openAirButton);

		seatingGroup = new ButtonGroup();
		seatingGroup.add(cabooseButton);
		seatingGroup.add(presidentialButton);
		seatingGroup.add(firstClassButton);
		seatingGroup.add(openAirButton);

		adultLabel = new JLabel("Adults");
		adultLabel.setBounds(30, 104, 50, 16);
		getContentPane().add(adultLabel);
		adultLabel.setVisible(false);

		adultTF = new JTextField();
		adultTF.setBounds(80, 104, 50, 16);
		getContentPane().add(adultTF);
		adultTF.setVisible(false);

		childrenLabel = new JLabel("Children");
		childrenLabel.setBounds(160, 104, 60, 16);
		getContentPane().add(childrenLabel);
		childrenLabel.setVisible(false);

		childrenTF = new JTextField();
		childrenTF.setBounds(225, 104, 50, 16);
		getContentPane().add(childrenTF);
		childrenTF.setVisible(false);

		calcButton = new JButton("Calculate");
		calcButton.setBounds(100, 144, 80, 16);
		getContentPane().add(calcButton);

		ansLabel = new JLabel("Answer");
		ansLabel.setBounds(80, 184, 50, 16);
		getContentPane().add(ansLabel);

		ansTF = new JTextField();
		ansTF.setBounds(150, 184, 75, 16);
		getContentPane().add(ansTF);
		ansTF.setEditable(false);
		
		errorLabel = new JLabel("");
		errorLabel.setBounds(40, 220, 200, 16);
		getContentPane().add(errorLabel);

		cabooseButton.addActionListener(this);
		presidentialButton.addActionListener(this);
		firstClassButton.addActionListener(this);
		openAirButton.addActionListener(this);
		calcButton.addActionListener(this);
		
		connectToServer();
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("Diesel") || action.equals("Steam")) {
			ansTF.setText(""); 
			errorLabel.setText("");
		}
		else if (action.equals("Caboose")) {
			ansTF.setText("");
			errorLabel.setText("");
			adultLabel.setVisible(false);
			adultTF.setVisible(false);
			childrenLabel.setVisible(false);
			childrenTF.setVisible(false);
		} else if (action.equals("Presidential") || action.equals("First Class") || action.equals("Open Air")) {
			ansTF.setText("");
			errorLabel.setText("");
			adultLabel.setVisible(true);
			adultTF.setVisible(true);
			childrenLabel.setVisible(true);
			childrenTF.setVisible(true);
		} else if (action.equals("Calculate")) {
			calculate();
		} 
	}

// ========================================================================
	
// Do not change anything above this line
// Two global variables have been defined here and you will need to add more
	final static String server = "127.0.0.1";
	final static int port = 25413;
	Socket socket = null;
	String seatType;
	String locomotive;
	String adults;
	String children;

	
// Then implement the following methods

// The following method connects to the ShapeCalcultorServer
	void connectToServer() {

		try {
			socket = new Socket(server, port);
		} catch (UnknownHostException e) {
			System.out.println("Can't find the server");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Server not responding");
		}
		
	}

// The following method sends an appropriate command to the server
// Then reads the result and displays it in the answer text field
	void calculate() {
		errorLabel.setText("");
		
		try {
			
			
			BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter os = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
			String resultLine;
			
			
		//check locomotive type
			if(steamButton.isSelected()){
				locomotive = steamButton.getText();
			}else
				locomotive = dieselButton.getText();
			
		//check seat type	
			if(cabooseButton.isSelected()) {
				seatType = cabooseButton.getText();
			}else if(presidentialButton.isSelected()) {
				seatType = presidentialButton.getText();
			}else if(firstClassButton.isSelected()) {
				seatType = "FirstClass";
			}else 
				seatType = "OpenAir";
			
		
		//get adults and children text
			adults = adultTF.getText();
			children = childrenTF.getText();
			
			if(adultTF.getText().equals("")) {
				adults = "0";
			}
			if(childrenTF.getText().equals("")) {
				children = "0";
			}
			
		//send output
			if(seatType.equals("Caboose")) {
			os.println(locomotive + " " + seatType);
			os.flush();
			
			resultLine = is.readLine();
			System.out.println("Server response: " + resultLine);
			ansTF.setText("$" + resultLine);
			}else
				try {
					
                int adultInt = Integer.parseInt(adults);
                int childrenInt = Integer.parseInt(children);
                
                	if(adultInt < 0 || childrenInt < 0) {
                	
                		errorLabel.setText("Input invald");
                		ansTF.setText("");
                		adultTF.setText("");
                        childrenTF.setText("");
                    
                	} else {
                	
                		os.println(locomotive + " " + seatType + " " + adults + " " + children);
                		os.flush();
    			
                		resultLine = is.readLine();
                		System.out.println("Server response: " + resultLine);
                		ansTF.setText("$" + resultLine);
    			
                	}
                } catch (NumberFormatException e) {
                	
                    errorLabel.setText("Input invald");
                    ansTF.setText("");
                    adultTF.setText("");
                    childrenTF.setText("");
                    
                }
		} catch (IOException e) {
			
			System.out.println("client");
			System.out.println("I/O error: " + e);
			
		}
		
	}

}