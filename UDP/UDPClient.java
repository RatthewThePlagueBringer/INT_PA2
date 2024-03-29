import java.net.*;
import java.io.*;

public class UDPClient {
	private static final int cPort = 8000; // Client port number
	public static void main(String args[]) {
		DatagramSocket ds = null;
		
		try {
			ds = new DatagramSocket();
			ds.setSoTimeout(1000);
			InetAddress ip = InetAddress.getLocalHost(); 
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
						while (true) {

							memeStr = new byte[1024];
							receive = new DatagramPacket(memeStr, memeStr.length);
							ds.receive(receive);
							confirm = new String(receive.getData(), 0, receive.getLength());

							if (confirm.equals("next")) {
								try {
									System.out.println("creating byte stream");
									byte[] imageData = new byte[1024];
									ByteArrayOutputStream baos = new ByteArrayOutputStream();

									System.out.println("collecting fragments");
									// Collect fragments and write them to a byte stream
									while (true) {
										try {
											DatagramPacket receivePacket = new DatagramPacket(imageData, imageData.length);
											ds.receive(receivePacket);
											baos.write(imageData, 0, receivePacket.getLength());
											if (receivePacket.getLength() < 1024) {
												System.out.println("     limit reached, breaking");
												break;
											}
										} catch (SocketTimeoutException e) {
											System.err.println("Timeout occurred, skipping packet");
											break;
										}
									}
									
									System.out.println("writing byte stream to file");
									// Write byte stream to image file
									imageData = baos.toByteArray();
									String fileName = "received_image" + imageIndex + ".jpg";
									FileOutputStream fos = new FileOutputStream(fileName);
									fos.write(imageData);
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