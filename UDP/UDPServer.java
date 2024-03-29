import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class UDPServer {

	private static final int sPort = 8000;   // Server port number

	public static void main(String[] args) throws Exception {
		System.out.println("The server is running.");
		DatagramSocket ds = null;

		long startTime;
		long endTime;

		double[] localAccesses = new double[10];

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
						// Check if the command is "meme"
						if (clientArgs[0].equals("Meme") || clientArgs[0].equals("meme")) {

							String msg = "memes";
							byte[] msgData = msg.getBytes();
							DatagramPacket confirmation = new DatagramPacket(msgData, msgData.length, clientAddress, clientPort);
							ds.send(confirmation);

							// Send the 10 memes in random order
							try {

								// Create an array with all of the memes
								ArrayList<File> images = new ArrayList<>();

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
									
									msg = "next";
									msgData = msg.getBytes();
									confirmation = new DatagramPacket(msgData, msgData.length, clientAddress, clientPort);
									ds.send(confirmation);

									FileInputStream fis = new FileInputStream(imageFile);
									byte[] imageData = new byte[(int) imageFile.length()];
									fis.close();

									// Split byte array into chunks and send them
									int numChunks = (int) Math.ceil((double) imageData.length / 1024);
									for (int i = 0; i < numChunks; i++) {
										int offset = i * 1024;
										int length = Math.min(2024, imageData.length - offset);
										byte[] chunkData = Arrays.copyOfRange(imageData, offset, offset + length);

										DatagramPacket sendPacket = new DatagramPacket(chunkData, chunkData.length, clientAddress, clientPort);
										ds.send(sendPacket);
									}
									
									byte[] memeStr = new byte[1024];
									DatagramPacket receive = new DatagramPacket(memeStr, memeStr.length);
									ds.receive(receive);
									String confirm = new String(receive.getData(), 0, receive.getLength());

								}
								System.out.println("All images sent!");

							} catch (NumberFormatException | IOException e) {
								msg = "Error reading meme image files";
								msgData = msg.getBytes();
								DatagramPacket sendPacket = new DatagramPacket(msgData, msgData.length, clientAddress, clientPort);
								ds.send(sendPacket);
								System.out.println("Message sent to client: " + msg + "\n");
							}
						} else {
							// Invalid command format
							try {
								String msg = "Invalid command format";
								byte[] msgData = msg.getBytes();
								DatagramPacket sendPacket = new DatagramPacket(msgData, msgData.length, clientAddress, clientPort);
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