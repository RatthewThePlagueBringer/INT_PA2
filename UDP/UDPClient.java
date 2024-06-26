import java.net.*;
import java.util.Arrays;
import java.io.*;

public class UDPClient {
	private static final int cPort = 8000; // Client port number
	public static void main(String args[]) {
		DatagramSocket ds = null;

		long startTime;
		long endTime;

		double[] RTTs = new double[10];
		
		try {
			startTime = System.nanoTime();
			ds = new DatagramSocket();
			ds.setSoTimeout(1000);
			InetAddress ip = InetAddress.getLocalHost(); 
			endTime = System.nanoTime();
			double setupTime = (endTime - startTime) / 1e6;
			System.out.println("TCP Setup Time: " + setupTime + " ms");

			// Create a socket to connect to the server
			System.out.println("Connected to localhost in port " + cPort);
			System.out.println("Hello!");

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
					byte[] memeStr = new byte[1024];
					DatagramPacket receive = new DatagramPacket(memeStr, memeStr.length);
					ds.receive(receive);
					String confirm = new String(receive.getData(), 0, receive.getLength());
					InetAddress serverAddress = receive.getAddress();

					// If the datagram sent is "memes", receive 10 image files
					if (confirm.equals("memes")) {
						int imageIndex = 1;
						while (imageIndex < 11) {

							memeStr = new byte[1024];
							receive = new DatagramPacket(memeStr, memeStr.length);
							ds.receive(receive);
							confirm = new String(receive.getData(), 0, receive.getLength());

							if (confirm.equals("next")) {
								try {
									byte[] imageData = new byte[1024];
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									startTime = System.nanoTime();
									// Collect fragments and write them to a byte stream
									while (true) {
										try {
											DatagramPacket receivePacket = new DatagramPacket(imageData, imageData.length);
											ds.receive(receivePacket);
											baos.write(imageData, 0, receivePacket.getLength());
											if (receivePacket.getLength() < 1024) {
												break;
											}
										} catch (SocketTimeoutException e) {
											break;
										}
									}
									
									// Write byte stream to image file
									imageData = baos.toByteArray();
									String fileName = "received_image" + imageIndex + ".jpg";
									FileOutputStream fos = new FileOutputStream(fileName);
									fos.write(imageData);

									endTime = System.nanoTime();
									double RTT = (endTime - startTime) / 1e6;
									RTTs[imageIndex - 1] = RTT;
									System.out.println("RTT: " + RTT + " ms");

									fos.close();

									System.out.println("Image " + imageIndex + " received and saved!");
									imageIndex++;
									
									msg = "confirmation";
									byte[] msgData = msg.getBytes();
									DatagramPacket confirmation = new DatagramPacket(msgData, msgData.length, serverAddress, cPort);
									ds.send(confirmation);

								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							else {
								break;
							}
						}
							
					}
					System.out.println();
					System.out.println("RTT Statistics");
					printStats(RTTs);
					
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