import java.net.*;
import java.io.*;

public class Server_TCP {

	private static final int sPort = 8000;   // Server port number

	public static void main(String[] args) throws Exception {
		System.out.println("The server is running.");
		ServerSocket listener = new ServerSocket(sPort);
		Socket connection = listener.accept();
		System.out.println("Client has connected.");

		ObjectOutputStream out = null;
		ObjectInputStream in = null;

		try {
			// Initialize IO streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			try {
				while (true) {
					// Read command from the client
					String command = (String) in.readObject();

					if (command != null) {
						System.out.println("Received command: " + command + " from client.");
						// Split the command into args
						String[] clientArgs = command.split("\\s+");
						if (clientArgs.length == 1 && clientArgs[0].equals("bye")) {
							out.writeObject("disconnected");
							out.flush();
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
								out.writeObject(msg);
								out.flush();
								System.out.println("Joke content sent to client: " + msg);
							} catch (NumberFormatException | IOException e) {
								String msg = "Joke not found or error reading joke file.";
								out.writeObject(msg);
								out.flush();
								System.out.println("Message sent to client: " + msg + "\n");
							}
						} else {
							// Invalid command format
							try {
								String msg = "Invalid command format, please use 'Joke <number>'.";
								out.writeObject(msg);
								out.flush();
								System.out.println("Message sent to client: " + msg);
							} catch (IOException ioException) {
								// Debugging
								ioException.printStackTrace();
							}
						}
					}
				}
			} catch (ClassNotFoundException e) {
				System.err.println("Data received in unknown format.");
			}
		} catch (IOException ioException) {
			System.out.println("disconnected");
		} finally {
			// Close connections
			try {
				in.close();
				out.close();
				connection.close();
			} catch (IOException ioException) {
				System.out.println("disconnected");
			}
		}
	}
}