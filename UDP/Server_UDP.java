import java.net.*;
import java.io.*;

public class Server_UDP {

	private static final int sPort = 8000;   // Server port number

	public static void main(String[] args) throws Exception {
		System.out.println("The server is running.");
		DatagramSocket ds = null;

		try {
			// Initialize IO streams
			ds = new DatagramSocket(sPort);
			try {
				while (true) {
					// Read command from the client
					byte[] receiveData = new byte[1024];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					ds.receive(receivePacket);
					//make recieved data a string
					String command = new String(receivePacket.getData(), 0, receivePacket.getLength());
					InetAddress clientAddress = receivePacket.getAddress();
					int clientPort = receivePacket.getPort();

					if (command != null) {
						System.out.println("Received command: " + command + " from client.");
						// Split the command into args
						String[] clientArgs = command.split("\\s+");
						if (clientArgs.length == 1 && clientArgs[0].equals("bye")) {
							String msg = "disconnected";
							byte[] msgBytes = msg.getBytes();
							DatagramPacket sendPacket = new DatagramPacket(msgBytes, msgBytes.length, clientAddress, clientPort);
							ds.send(sendPacket);
							System.out.println("disconnected");
							break;
						}
						// Check if args has length 2 and if the first element is "Joke"
						if (clientArgs.length == 2 && clientArgs[0].equals("Joke")) {
							// Send the requested joke
							try {
								int jokeNum = Integer.parseInt(clientArgs[1]);
								String jokeFileName = "joke" + jokeNum + ".txt";
								BufferedReader fileReader = new BufferedReader(new FileReader(jokeFileName));
								StringBuilder jokeContent = new StringBuilder();
								String line;

								// Read through file
								while ((line = fileReader.readLine()) != null) {
									jokeContent.append(line).append("\n");
								}
								fileReader.close();

								String msg = jokeContent.toString();
								byte[] msgBytes = msg.getBytes();
								DatagramPacket sendPacket = new DatagramPacket(msgBytes, msgBytes.length, clientAddress, clientPort);
								ds.send(sendPacket);
								System.out.println("Joke content sent to client: " + msg);
							} catch (NumberFormatException | IOException e) {
								String msg = "Joke not found or error reading joke file.";
								byte[] msgBytes = msg.getBytes();
								DatagramPacket sendPacket = new DatagramPacket(msgBytes, msgBytes.length, clientAddress, clientPort);
								ds.send(sendPacket);
								System.out.println("Message sent to client: " + msg + "\n");
							}
						} else {
							// Invalid command format
							try {
								String msg = "Invalid command format, please use 'Joke <number>'.";
								byte[] msgBytes = msg.getBytes();
								DatagramPacket sendPacket = new DatagramPacket(msgBytes, msgBytes.length, clientAddress, clientPort);
								ds.send(sendPacket);
							} catch (IOException ioException) {
								// Debugging
								ioException.printStackTrace();
							}
						}
					}
				}
			} catch (IOException e) {
				System.err.println("Data received in unknown format.");
			}
		} catch (IOException ioException) {
			System.out.println("disconnected");
		} finally {
			ds.close();
			//System.out.println("disconnected");
		}
	}
}