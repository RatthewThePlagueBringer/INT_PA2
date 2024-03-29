import java.net.*;
import java.io.*;

public class Client_TCP {
	private static final int cPort = 8000; // Client port number
	public static void main(String args[]) {
		Socket requestSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		String memeStr;

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
						int imageIndex = 1;
						while (true) {
							memeStr = (String) in.readObject();
							if (memeStr.equals("next")) {
								byte[] imageData = null;
								try {

									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									byte[] buffer = new byte[1024];
									int bytesRead;
									// Read image data into byte array
									while ((bytesRead = in.read(buffer)) != -1) {
										baos.write(buffer, 0, bytesRead);
										if (bytesRead < buffer.length) {
											System.out.println("buffer limit reached, breaking while loop");
											break;
										}
									}

									imageData = baos.toByteArray();
									System.out.println("creating image object");
									String fileName = "received_image" + imageIndex + ".jpg";
									FileOutputStream fos = new FileOutputStream(fileName);
									System.out.println("writing byte array to image file stream");
									fos.write(imageData);

									System.out.println("closing streams");
									fos.close();
									baos.close();
									imageIndex++;
									System.out.println("Image " + imageIndex + " received and saved!");
									out.writeObject("Confirmation");
									out.flush();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							else {
								break;
							}
						}
					}

					else if ("disconnected".equals(memeStr)){
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

	private static boolean isMarker(byte[] data) {
		byte[] marker = { -1, -1, -1, -1 }; // Example marker byte array
		return data.length == marker.length && java.util.Arrays.equals(data, marker);
	}
}