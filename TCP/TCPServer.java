import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class TCPServer {

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
								System.out.println("appending images");

								File image = new File("meme1.jpg");
								images.add(image);
								image = new File("meme2.jpg");
								images.add(image);
								image = new File("meme3.jpg");
								images.add(image);
								image = new File("meme4.jpg");
								images.add(image);
								image = new File("meme5.jpg");
								images.add(image);
								image = new File("meme6.jpg");
								images.add(image);
								image = new File("meme7.jpg");
								images.add(image);
								image = new File("meme8.jpg");
								images.add(image);
								image = new File("meme9.jpg");
								images.add(image);
								image = new File("meme10.jpg");
								images.add(image);
								System.out.println("images successfully appended to array");

								// Randomize the array
								Collections.shuffle(images);
								System.out.println("images shuffled, sending memes");
								System.out.println(images.size());

								for (File imageFile : images) {
									out.writeObject("next");
									out.flush();

									System.out.println("creating file input stream");
									FileInputStream fis = new FileInputStream(imageFile);

									System.out.println("creating byte array");
									byte[] imageData = new byte[(int) imageFile.length()];

									System.out.println("pushing array to file input stream");
									fis.read(imageData);

									System.out.println("closing file input stream");
									fis.close();

									// Write image to stream
									System.out.println("writing image to output stream");
									out.write(imageData);
									out.flush();
									out.reset();

									// Wait for confirmation from the client
									String confirmation = (String) in.readObject();
									System.out.println("Received confirmation: " + confirmation);
								}
								out.flush();
								System.out.println("All memes sent to client");

							} catch (NumberFormatException | IOException e) {
								String msg = "Error reading meme image files";
								out.writeObject(msg);
								out.flush();
								System.out.println("Message sent to client: " + e + "\n");
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