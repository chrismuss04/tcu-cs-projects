import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.prefs.*;
import java.awt.FileDialog;

public class Lab3 extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JButton addButton;
	private JLabel addressLabel;
	private JPanel addressPanel;
	private JTextField addressTextField;
	private JPanel buttonPanel;
	private JLabel cityLabel;
	private JPanel cityStatePanel;
	private JTextField cityTextField;
	private JButton deleteButton;
	private JButton findButton;
	private JButton firstButton;
	private JLabel givenNameLabel;
	private JPanel givenNamePanel;
	private JTextField givenNameTextField;
	private JButton lastButton;
	private JButton nextButton;
	private JButton previousButton;
	private JLabel stateLabel;
	private JTextField stateTextField;
	private JLabel surnameLabel;
	private JPanel surnamePanel;
	private JTextField surnameTextField;
	private JButton updateButton;
	
	String bookFile = null;
	String indexFile = null;
	
	RandomAccessFile index; 
	RandomAccessFile book;

	public Lab3() {
		setTitle("Address Book");
		setBounds(100, 100, 704, 239);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new java.awt.GridLayout(5, 0));

		surnamePanel = new JPanel();
		surnameLabel = new JLabel();
		surnameTextField = new JTextField();
		givenNamePanel = new JPanel();
		givenNameLabel = new JLabel();
		givenNameTextField = new JTextField();
		addressPanel = new JPanel();
		addressLabel = new JLabel();
		addressTextField = new JTextField();
		cityStatePanel = new JPanel();
		cityLabel = new JLabel();
		cityTextField = new JTextField();
		stateLabel = new JLabel();
		stateTextField = new JTextField();
		buttonPanel = new JPanel();
		firstButton = new JButton();
		nextButton = new JButton();
		previousButton = new JButton();
		lastButton = new JButton();
		findButton = new JButton();
		addButton = new JButton();
		deleteButton = new JButton();
		updateButton = new JButton();

		surnamePanel.setName("surnamePanel");

		surnameLabel.setText("Surname");
		surnameLabel.setName("surnameLabel");
		surnamePanel.add(surnameLabel);

		surnameTextField.setColumns(45);
		surnameTextField.setText("");
		surnameTextField.setName("surnameTextField");
		surnamePanel.add(surnameTextField);

		getContentPane().add(surnamePanel);

		givenNamePanel.setName("givenNamePanel");

		givenNameLabel.setText("Given Names");
		givenNameLabel.setName("givenNameLabel");
		givenNamePanel.add(givenNameLabel);

		givenNameTextField.setColumns(45);
		givenNameTextField.setText("");
		givenNameTextField.setName("givenNameTextField");
		givenNamePanel.add(givenNameTextField);

		getContentPane().add(givenNamePanel);

		addressPanel.setName("addressPanel");

		addressLabel.setText("Street Address");
		addressLabel.setName("addressLabel");
		addressPanel.add(addressLabel);

		addressTextField.setColumns(45);
		addressTextField.setText("");
		addressTextField.setName("addressTextField");
		addressPanel.add(addressTextField);

		getContentPane().add(addressPanel);

		cityStatePanel.setName("cityStatePanel");

		cityLabel.setText("City");
		cityLabel.setName("cityLabel");
		cityStatePanel.add(cityLabel);

		cityTextField.setColumns(30);
		cityTextField.setText("");
		cityTextField.setName("cityTextField");
		cityStatePanel.add(cityTextField);

		stateLabel.setText("State");
		stateLabel.setName("stateLabel");
		cityStatePanel.add(stateLabel);

		stateTextField.setColumns(5);
		stateTextField.setText("");
		stateTextField.setName("stateTextField");
		cityStatePanel.add(stateTextField);

		getContentPane().add(cityStatePanel);

		buttonPanel.setName("buttonPanel");

		firstButton.setText("First");
		firstButton.setName("firstButton");
		firstButton.addActionListener(this);
		buttonPanel.add(firstButton);

		nextButton.setText("Next");
		nextButton.setName("nextButton");
		nextButton.addActionListener(this);
		buttonPanel.add(nextButton);

		previousButton.setText("Previous");
		previousButton.setName("previousButton");
		previousButton.addActionListener(this);
		buttonPanel.add(previousButton);

		lastButton.setText("Last");
		lastButton.setName("lastButton");
		lastButton.addActionListener(this);
		buttonPanel.add(lastButton);

		findButton.setText("Find");
		findButton.setName("findButton");
		findButton.addActionListener(this);
		buttonPanel.add(findButton);

		addButton.setText("Add");
		addButton.setEnabled(false);
		addButton.setName("addButton");
		addButton.addActionListener(this);
		buttonPanel.add(addButton);

		deleteButton.setText("Delete");
		deleteButton.setEnabled(false);
		deleteButton.setName("deleteButton");
		deleteButton.addActionListener(this);
		buttonPanel.add(deleteButton);

		updateButton.setText("Update");
		updateButton.setEnabled(false);
		updateButton.setName("updateButton");
		updateButton.addActionListener(this);
		buttonPanel.add(updateButton);

		getContentPane().add(buttonPanel);

		getFiles();
		
		try {
			index = new RandomAccessFile(indexFile, "r");
			book = new RandomAccessFile(bookFile, "r");
		} catch(IOException ioe) {
			System.out.println(ioe);
			System.exit(0);
		}

	}
	
	void getFiles() {
			FileDialog fd = new FileDialog(this, "Select the Address Book", FileDialog.LOAD);
			fd.setVisible(true);
			String filename = fd.getFile();
			if (filename == null)
				System.exit(0);
			bookFile = fd.getDirectory() + filename;
			fd = new FileDialog(this, "Select the Index File", FileDialog.LOAD);
			fd.setVisible(true);
			filename = fd.getFile();
			if (filename == null)
				System.exit(0);
			indexFile = fd.getDirectory() + filename;
	}


	public static void main(String[] args) {
		Lab3 window = new Lab3();
		window.setVisible(true);
	}

/***************************************************************/

	public void actionPerformed(ActionEvent evt) {
		if(evt.getActionCommand().equals("First")) {
			first();
		}
		else if(evt.getActionCommand().equals("Next")) {
			next();

		}
		else if(evt.getActionCommand().equals("Previous")) {
			previous();

		}
		else if(evt.getActionCommand().equals("Last")) {
			last();

		}
		else if(evt.getActionCommand().equals("Find")) {
			find();

		}
	}
	void first() {
		try {
			index.seek(0); //seeks first long in the index.dat file
			book.seek(index.readLong()); //seeks the addressBook.dat file to the offset given by the long in the index.dat file
			surnameTextField.setText(book.readUTF());
			givenNameTextField.setText(book.readUTF());
			addressTextField.setText(book.readUTF());
			cityTextField.setText(book.readUTF());
			stateTextField.setText(book.readUTF());
		}
		catch(IOException e) {
			
		}
	}
	void next() {
		try {
			book.seek(index.readLong()); //seeks the addressBook.dat file to the offset given by the long in the index.dat file
			surnameTextField.setText(book.readUTF());
			givenNameTextField.setText(book.readUTF());
			addressTextField.setText(book.readUTF());
			cityTextField.setText(book.readUTF());
			stateTextField.setText(book.readUTF());
		} 
		catch(IOException e) {
			
		}
	
	}
	void previous() {
		try {
		index.seek(index.getFilePointer()-16); //file pointer is at the end of the byte, so -8 would repeat the same byte over and over
		book.seek(index.readLong()); //seeks the addressBook.dat file to the offset given by the long in the index.dat file
		surnameTextField.setText(book.readUTF());
		givenNameTextField.setText(book.readUTF());
		addressTextField.setText(book.readUTF());
		cityTextField.setText(book.readUTF());
		stateTextField.setText(book.readUTF());
		}
		catch(IOException e) {
			
		}
	}
	void last() {
		try {
			index.seek(index.length()-8); //seeks the last offset value in the index.dat file
			book.seek(index.readLong()); //seeks the addressBook.dat file to the offset given by the long in the index.dat file
			surnameTextField.setText(book.readUTF());
			givenNameTextField.setText(book.readUTF());
			addressTextField.setText(book.readUTF());
			cityTextField.setText(book.readUTF());
			stateTextField.setText(book.readUTF());
			
		}
		catch(IOException e) {
		
		}
	}
	void find() {
		try {
			String sur = surnameTextField.getText().toLowerCase();
			String giv = givenNameTextField.getText().toLowerCase();
			long location = binarySearch(sur, giv, 0, index.length()-8);		
			index.seek(location);
			book.seek(index.readLong());
			surnameTextField.setText(book.readUTF());
			givenNameTextField.setText(book.readUTF());
			addressTextField.setText(book.readUTF());
			cityTextField.setText(book.readUTF());
			stateTextField.setText(book.readUTF());
		}
		catch (IOException e) {
			
		}
	}
	long binarySearch(String surname, String givenName, long lower, long upper){  //needs to be modified to take surname and given name, and check for both, if givenName is "" then just dont check for it
		long mid = (lower + upper) / 2;
		mid = divisibleByEight(mid);//check if mid is divisible by 8 -> if its not, its not at the start of a long and will not give the correct offset when you do the seeks
		try {
		if(mid==lower) { //if mid is equal to lower or upper that means that there is only one index left and it will be checked over and over because of the isDivisible method
			if(mid==0) { //if mid is 0 then it is at first index, so return mid to get the first 
			return mid;
			}
			else if(mid==index.length()-8) {//if mid is at the last index, return mid to get the correct place
			return mid;
			}
			else return mid+8;	//else return mid+8 to get to the correct location
		}
			index.seek(mid);
			book.seek(index.readLong());
			String surCheck = book.readUTF().toLowerCase();
			String givenCheck = book.readUTF().toLowerCase();
			int surComp = surCheck.compareTo(surname);
			int givComp = givenCheck.compareTo(givenName);
			if (surComp==0) {//if the surname is correct, check the givenName
				if(givComp>0) {
					return binarySearch(surname, givenName, lower, mid);
				}
				else if(givComp<0) {
					return binarySearch(surname, givenName, mid, upper);
				}
				else { //if both are correct, return mid
					return mid;
				}
			}
			else if (surComp>0) {
				return binarySearch(surname, givenName, lower, mid); // if comparator is greater then value comes before the one at that index, so search top 1/2
			}
			else
				return binarySearch(surname, givenName, mid, upper); // if comparator is less then value comes after the one at that index so search bottom 1/2
		} catch(IOException e) {
			return -1;
		}
	}
	long divisibleByEight(long num) {
		if(num%8==0) { //if num is divisible by 8, return num
			return num;
		}else {
			long remainder = num%8;
			if(remainder<=4) { // if num is not divisble by 8 but the remainder is less than 4
				return num-remainder;  //returns the closest smaller number that is divisble by 8
			}
			else {
				return num + (8-remainder); //if it is not less than 4 returns the closest larger number that is divisible by 8
			}
		}
		
	}
}
