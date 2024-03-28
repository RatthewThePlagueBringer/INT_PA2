import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

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

						// Check if the command is "meme"
						if (clientArgs[0].equals("Meme") || clientArgs[0].equals("meme")) {
							// Send the 10 memes in random order
							try {

								out.writeObject("memes");

								// Create an array with all of the memes
								ArrayList<File> images = new ArrayList<>();

								File image = new File("/memes/meme1.jpg");
								images.add(image);
								image = new File("/memes/meme2.jpg");
								images.add(image);
								image = new File("/memes/meme3.jpg");
								images.add(image);
								image = new File("/memes/meme4.jpg");
								images.add(image);
								image = new File("/memes/meme5.jpg");
								images.add(image);
								image = new File("/memes/meme6.jpg");
								images.add(image);
								image = new File("/memes/meme7.jpg");
								images.add(image);
								image = new File("/memes/meme8.jpg");
								images.add(image);
								image = new File("/memes/meme9.jpg");
								images.add(image);
								image = new File("/memes/meme10.jpg");
								images.add(image);

								// Randomize the array
								Collections.shuffle(images);

								for (int i = 0; i < 10; i++) {
									// Load the ith image
									FileInputStream fis = new FileInputStream(images.get(i));
									byte[] imageData = new byte[(int) images.get(i).length()];
									fis.read(imageData);
									fis.close();

									// Write ith image to stream
									out.write(imageData);
									System.out.println("Meme " + i + " sent to client");
								}

								out.flush();
								System.out.println("All memes sent to client");

							} catch (NumberFormatException | IOException e) {
								String msg = "Error reading meme image files";
								out.writeObject(msg);
								out.flush();
								System.out.println("Message sent to client: " + msg + "\n");
							}
						} else {
							// Invalid command format
							try {
								String msg = "Invalid command format";
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