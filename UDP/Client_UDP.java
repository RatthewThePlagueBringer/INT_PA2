import java.net.*;
import java.io.*;
import java.io.IOException; 
import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.net.InetAddress;

public class Client_UDP {
	private static final int cPort = 8000; // Client port number
	public static void main(String args[]) {
		DatagramSocket ds = null;
		
		try {
			ds = new DatagramSocket();
			InetAddress ip = InetAddress.getLocalHost(); 
			// Create a socket to connect to the server
			System.out.println("Connected to localhost in port " + cPort);
			System.out.println("Hello!");

			// Initialize IO streams
			//out = new ObjectOutputStream(requestSocket.getOutputStream());
			//out.flush();
			//in = new ObjectInputStream(requestSocket.getInputStream());

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				System.out.print("Input a command: ");
				// Read in a command
				String msg = bufferedReader.readLine();
				byte[] msgBytes = msg.getBytes();
				DatagramPacket DpSend = new DatagramPacket(msgBytes, msgBytes.length, ip, cPort);

				// Send the command to the server
				try {
					// Write the message to output stream
					ds.send(DpSend);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
				// Receive the response from the server
				try {
					byte[] receiveData = new byte[1024];
                	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					ds.receive(receivePacket);
					String joke = new String(receivePacket.getData(), 0, receivePacket.getLength());
					// If received "disconnected" message from server, terminate client and print "exit"
					if ("disconnected".equals(joke)) {
						System.out.println("exit");
						break;
					}
					// Show the joke content to the user
					System.out.println(joke);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		} catch (ConnectException e) {
			System.err.println("Connection refused, initiate a server first.");
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host.");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// Close connections
			if (ds != null){
				ds.close();
			}
		}
	}
}