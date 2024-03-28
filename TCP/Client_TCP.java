import java.net.*;
import java.io.*;

public class Client_TCP {
	private static final int cPort = 8000; // Client port number
	public static void main(String args[]) {
		Socket requestSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		String memeStr;
		int counter;

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
					memeStr = (String) in.readObject();
					// If the first object sent is a string that says "memes", receive 10 image files
					if ("memes".equals(memeStr)) {

						// Read image data from socket
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte[] buffer = new byte[1024];
						int bytesRead;

						// Loop 10 times
						for (int i = 1; i < 11; i++) {
							while ((bytesRead = in.read(buffer)) != -1) {
								baos.write(buffer, 0, bytesRead);
								if (bytesRead < buffer.length) {
									break;
								}
							}

							// Convert byte array to image and save it
							byte[] imageData = baos.toByteArray();
							String fileName = "received_image" + i + ".jpg";
							FileOutputStream fos = new FileOutputStream(fileName);
							fos.write(imageData);

							fos.close();
							baos.close();
							System.out.println("Image " + i + " received and saved!");
						}
						
						System.out.println("All memes saved!");
					}
					
					else {
						System.out.println("exit");
						break;
					}
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