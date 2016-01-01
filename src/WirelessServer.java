import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class WirelessServer {
	public static ServerSocket socketServer;
	public static int port = 8080;
	static String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

	public static void main(String args[]) throws Exception {

		try {
			socketServer = new ServerSocket(port);
			System.out.println("Initiating Wireless Server....");
			Thread.sleep(5000);
			System.out.println("Wireless Server Initiated at Port No: " + socketServer.getLocalPort());
			System.out.println("########################################################################");
			while (true) {
				Socket socketRequest = socketServer.accept();
				Wireless request = new Wireless(socketRequest);
				Thread thread = new Thread(request);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}

final class Wireless implements Runnable {
	String file_list_tobe_sent_to_server;
	long startTime;
	long endTime;
	String CRLF = "\r\n";
	int BUFFER_SIZE = 1024;
	Socket socket;
	String requestedFileName;
	String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

	@SuppressWarnings("static-access")
	public Wireless(Socket socket) throws Exception {
		this.socket = socket;
		System.out.println("Client IP: " + socket.getLocalAddress());
		System.out.println("Client Port: " + socket.getPort());
	}

	public void run() {
		try {
			byte[] mybytearray = new byte[1024]; // create byte array to buffer
													// the file
			//
			InputStream inputStream = socket.getInputStream();
			StringBuffer requestBuffer = new StringBuffer(2048);
			int i = 0;
			byte[] buffer = new byte[1024];
			try {
				i = inputStream.read(buffer);
			} catch (SocketException sockExp) {
				System.out.println("Socket Timed-Out. Please Retry..");
				System.out.println(sockExp.toString());
			} catch (IOException e) {
				e.printStackTrace();
				i = -1;
			}
			for (int j = 0; j < i; j++) {
				requestBuffer.append((char) buffer[j]);
			}
			
			String request = readRequest(requestBuffer.toString());
			if (request.trim().contains("/REQUESTFILELIST")) {
				System.out.print("Request Line: " + requestBuffer.toString());
				file_list_tobe_sent_to_server = getFileList(WEB_ROOT);
				String file_list_tobe_sent_to_server1 = file_list_tobe_sent_to_server.toString().substring(1);
				String file_list_tobe_sent_to_server2 = file_list_tobe_sent_to_server1.toString().substring(0,file_list_tobe_sent_to_server1.length()-1);
				System.out.println(file_list_tobe_sent_to_server2);
				System.out.println("$$$$$$$$$$$$" + file_list_tobe_sent_to_server2);

				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				pw.write("HTTP/1.1 200 OK \r\n" + "Content-Type: FILE" + "\r\n" + "Content-Length: "
						+ file_list_tobe_sent_to_server2.length() + "\r\n" + "\r\n" 
						+ file_list_tobe_sent_to_server2); // Test message!
				pw.flush();
				// Send off the data
				pw.close();
			} else if (request.trim().contains("/FILEREQUESTED/")) {
				System.out.print("Request Line: " + requestBuffer.toString());
				String[] filename = request.split("/FILEREQUESTED/");
				System.out.println("%%%%%%%%%%%5" + filename.toString());
				sendFileToServer(filename[1], socket);
				// Toast.makeText(ConnectServerWithDetails.this,filename[1] +
				// "is Sent!!!",Toast.LENGTH_SHORT);
			} else {// if(request.trim().contains("HEADER")){
				File filename = new File(WEB_ROOT + "/UploadedFile.txt");
				Writer writer = new BufferedWriter(new FileWriter(filename));				
				writer.write(requestBuffer.toString());
				writer.flush();
				writer.close();
				System.out.println("File Created. Please check the output folder.");				
			}

			socket.close();

			System.out.println("########################################################################");
		} catch (SocketException sockExp) {
			System.out.println("Socket Timed-Out. Please Retry..");
			System.out.println(sockExp.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendFileToServer(String filename,Socket socket) {

        int BUFFER_SIZE = 1024;
        Socket client;
        FileInputStream fileInputStream;
        BufferedInputStream bufferedInputStream;
        OutputStream outputStream;
        PrintWriter printwriter;
        List<String> path = null;
        try {
            String filepath = WEB_ROOT+"/"+filename;

            byte[] bytes = new byte[BUFFER_SIZE];
            FileInputStream fis = null;
                File file = new File(filepath);

            String headerString = "HTTP/1.1 200 OK \r\n" + "Content-Type: FILE" + "\r\n" +
                    "Content-Length: "+file.length() + "\r\n" + "\r\n" + "\r\n";
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, BUFFER_SIZE);

            OutputStream output = socket.getOutputStream();
                output.write(headerString.getBytes());
                while (ch != -1) {
                    output.write(bytes, 0, ch);
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                }

            output.flush();
            output.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

	public String readRequest(String requestString) {
		int i = requestString.indexOf(' ');
		if (i != -1) {
			int j = requestString.indexOf(' ', i + 1);
			if (j > i)
				return requestString.substring(i + 1, j);
		}
		return null;
	}

	private static String getFileList(String WEB_ROOT) {

		List<String> item = null;
		List<String> path = null;
		String root = WEB_ROOT;
		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(root);
		File[] files = f.listFiles();
		if (!root.equals(root)) {
			item.add(root);
			path.add(root);
			item.add("../");
			path.add(f.getParent());
		}
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			path.add(file.getPath());
			if (file.isDirectory())
				item.add(file.getName() + "/");
			else
				item.add(file.getName());
		}
				return (item.toString());

	}

}
