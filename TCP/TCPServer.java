import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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

		long startTime;
		long endTime;

		double[] localAccesses = new double[10];


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

								for (int i = 1; i <= 10; i++) {
									startTime = System.nanoTime();
									File image = new File("meme" + i + ".jpg");
									endTime = System.nanoTime();
									double localAccess = (endTime - startTime) / 1e6;
									localAccesses[i - 1] = localAccess;
									System.out.println("Local Access Time " + i + ": " + localAccess + "ms");
									images.add(image);
								}
								System.out.println();
								System.out.println("Local Access Times Statistics");
								printStats(localAccesses);

								// Randomize the array
								Collections.shuffle(images);

								for (File imageFile : images) {
									out.writeObject("next");
									out.flush();

									FileInputStream fis = new FileInputStream(imageFile);

									byte[] imageData = new byte[(int) imageFile.length()];

									fis.read(imageData);

									fis.close();

									// Write image to stream
									out.write(imageData);
									out.flush();
									out.reset();

									out.writeObject("confirmation");
									out.flush();
									out.reset();

									// Wait for confirmation from the client
									String confirmation = (String) in.readObject();
								}
								out.flush();
								System.out.println("All memes sent to client");
								out.writeObject("done");
								out.flush();

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

	public static void printStats(double[] data) {
		// Calculate minimum, maximum, and median
		Arrays.sort(data);
		double min = data[0];
		double max = data[data.length - 1];
		double median;
		if (data.length % 2 == 0) {
			median = (data[data.length / 2] + data[data.length / 2 - 1]) / 2.0;
		} else {
			median = data[data.length / 2];
		}

		// Mean
		double sum = 0;
		for (double value : data) {
			sum += value;
		}
		double mean = sum / data.length;

		// Calculate variance and standard deviation
		double variance = 0;
		for (double value : data) {
			variance += Math.pow(value - mean, 2);
		}
		variance /= data.length;
		double stddev = Math.sqrt(variance);

		// Output results
		System.out.println("Minimum: " + min);
		System.out.println("Maximum: " + max);
		System.out.println("Median: " + median);
		System.out.println("Standard Deviation: " + stddev);
	}
}