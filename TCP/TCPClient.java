import java.net.*;
import java.io.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.ArrayList;

public class TCPClient {
	private static final int cPort = 8000; // Client port number
	public static void main(String args[]) {
		Socket requestSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		String memeStr;

		long startTime;
		long endTime;

		double[] RTTs = new double[10];

		try {
			// Create a socket to connect to the server
			startTime = System.nanoTime();
			requestSocket = new Socket("localhost", cPort);
			endTime = System.nanoTime();
			double setupTime = (endTime - startTime) / 1e6;
			System.out.println("TCP Setup Time: " + setupTime + " ms");

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
					memeStr = (String) in.readObject();
					// If the first object sent is a string that says "memes", receive 10 image files
					if ("memes".equals(memeStr)) {
						int imageIndex = 1;
						while (true) {
							memeStr = (String) in.readObject();
							if (memeStr.equals("next")) {
								byte[] imageData = null;
								try {
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									byte[] buffer = new byte[1024];
									int bytesRead;
									startTime = System.nanoTime();
									// Read image data into byte array
									while ((bytesRead = in.read(buffer)) != -1) {
										baos.write(buffer, 0, bytesRead);
										if (bytesRead < buffer.length) {
											break;
										}
									}

									imageData = baos.toByteArray();
									String fileName = "received_image" + imageIndex + ".jpg";
									FileOutputStream fos = new FileOutputStream(fileName);
									fos.write(imageData);
									endTime = System.nanoTime();
									double RTT = (endTime - startTime) / 1e6;
									RTTs[imageIndex - 1] = RTT;
									System.out.println("Image " + imageIndex + " received and saved!");
									System.out.println("RTT: " + RTT + " ms");

									fos.close();
									baos.close();


									imageIndex++;
									out.writeObject("Confirmation");
									out.flush();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							else if (memeStr.equals("done")) {
								break;
							}
						}

					}
					else if ("disconnected".equals(memeStr)){
						System.out.println("exit");
						break;
					}
					System.out.println();
					System.out.println("RTT Statistics");
					printStats(RTTs);
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