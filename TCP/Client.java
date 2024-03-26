import java.net.*;
import java.io.*;

public class Client {
	private static final int cPort = 8000; // Client port number
	public static void main(String args[]) {
		Socket requestSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		String joke;

		try {
			// Create a socket to connect to the server
			requestSocket = new Socket("localhost", cPort);
			System.out.println("Connected to localhost in port " + cPort);
			System.out.println("Hello!");

			// Initialize IO streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				System.out.print("Input a command: ");
				// Read in a command
				String msg = bufferedReader.readLine();
				// Send the command to the server
				try {
					// Write the message to output stream
					out.writeObject(msg);
					out.flush();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
				// Receive the response from the server
				try {
					joke = (String) in.readObject();
					// If received "disconnected" message from server, terminate client and print "exit"
					if ("disconnected".equals(joke)) {
						System.out.println("exit");
						break;
					}
					// Show the joke content to the user
					System.out.println(joke);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				} catch (ClassNotFoundException classNotFoundException) {
					classNotFoundException.printStackTrace();
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
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				if (requestSocket != null) {
					requestSocket.close();
				}
			} catch (IOException ioException) {
				// Debugging
				ioException.printStackTrace();
			}
		}
	}
}