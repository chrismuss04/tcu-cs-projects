import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class Lab4Server {
	final static int PORT = 25413;
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println("Couldn't listen on port: " + PORT + ", " + e);
			System.exit(1);
		}
		while(true) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
			} catch(IOException e) {
				System.out.println("Accept failed: " + PORT + ", " + e);
				continue;
			}
			new Lab4ServerThread(clientSocket).start();
		}
	}
}


class Lab4ServerThread extends Thread{
		Socket socket = null;

		Lab4ServerThread(Socket socket) { //constructor of the ServerThread class
			this.socket = socket;
		}

		public void run() {
			try {
				BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter os = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
				String inputLine;
				while ((inputLine = is.readLine()) != null) {
					StringTokenizer tok = new StringTokenizer(inputLine, " "); //tokenizes the input line
					int scenario = tok.countTokens();
					String locomotive, seatType;
					if(scenario == 2 ) { //if 2 tokens, #adult and #child not included
						locomotive = tok.nextToken();
						seatType = tok.nextToken();
						if(locomotive.equals("Diesel")) {
							if(seatType.equals("Caboose")) {
								os.println(850);
							}
						}else if (locomotive.equals("Steam")) {
							if(seatType.equals("Caboose")) {
								os.println(950);
							}
						}
						os.flush();
					}
					else if(scenario == 4) { //if 4 tokens, #adult and #child are included
						locomotive = tok.nextToken();
						seatType = tok.nextToken();
						int numAdult = Integer.parseInt(tok.nextToken());
						int numChild = Integer.parseInt(tok.nextToken());
						
						if(locomotive.equals("Diesel")) {
							if(seatType.equals("Presidential")) {
								os.println((77*numAdult) + (57*numChild));
							}
							else if(seatType.equals("FirstClass")) {
								os.println((57*numAdult) + (37*numChild));
							}
							else if(seatType.equals("OpenAir")) {
								os.println((27*numAdult) + (22*numChild));	
							}	
						}else if (locomotive.equals("Steam")) {
							if(seatType.equals("Presidential")) {
								os.println((87*numAdult) + (67*numChild));
							}
							else if(seatType.equals("FirstClass")) {
								os.println((67*numAdult) + (47*numChild));
							}
							else if(seatType.equals("OpenAir")) {
								os.println((37*numAdult) + (32*numChild));
							}
						}
					}
					os.flush(); 
				}
				os.close();
				is.close();
				socket.close();
			} catch (IOException e) {
				System.out.println("I/O error: " + e);
			}
		}
}
