
/**
 * UTA ID: 1001231367
 * NAME: MAHESH KAYARA
 * CLASS: CSE-5344-004
 **/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;

public class HTTPClient {
	static String enteredFilename;
	String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";
	static BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	static String destIP = "NOT SET";
	static String destPort = "NOT SET";

	public static void main(String[] args) {
		try {
			System.out.println("Enter Destination IP Address: ");
			destIP = bufferRead.readLine();
			System.out.println("Enter Destination Port: ");
			destPort = bufferRead.readLine();

			HTTPClient client = new HTTPClient();

			client.HttpRequest(destIP, destPort, "/REQUESTFILELIST");
			System.out.println("Enter something here : ");
			
			enteredFilename = bufferRead.readLine();
			
			client.HttpRequest(destIP, destPort, "/FILEREQUESTED/" + enteredFilename, enteredFilename);

		} catch (ArrayIndexOutOfBoundsException AIOBExp) {
			System.out.println("Invalid Execution! Please Enter the Arguments <ServerIP> <PortNo> <FileName>");
			System.out.println(AIOBExp.toString());
			AIOBExp.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

	public void HttpRequest(String serverIP, String serverPort, String requestType) {
		try {
			HttpClient httpClient = new HttpClient();

			/*
			 * String serverIP = "192.168.43.151"; String serverPort = "8080";
			 */
			String url = "http://" + serverIP + ":" + serverPort + requestType;// "/REQUESTFILELIST";

			GetMethod getMethod = new GetMethod(url);
			httpClient.executeMethod(getMethod);
			String string = getMethod.getResponseBodyAsString();
			// getMethod.getResponseBodyAsStream()
			String[] parts = string.split(",");
			for (int i = 0; i < parts.length; i++) {
				System.out.print(parts[i] + "\n");
			}

			getMethod.releaseConnection();
		} catch (java.net.ConnectException ConnExp) {
			System.out.println("Unable to Connect to Server/Server not Online");
			System.out.println(ConnExp.toString());
			ConnExp.printStackTrace();
		} catch (HttpException HttpExp) {
			System.out.println("Error Processing Request");
			System.out.println(HttpExp.toString());
			HttpExp.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException AIOBExp) {
			System.out.println("Invalid Execution! Please Enter the Arguments <ServerIP> <PortNo> <FileName>");
			System.out.println(AIOBExp.toString());
			AIOBExp.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}

	}

	// class overloading
	public void HttpRequest(String serverIP, String serverPort, String requestType, String s) {
		try {
			HttpClient httpClient = new HttpClient();

			/*
			 * String serverIP = "192.168.43.151"; String serverPort = "8080";
			 */
			String url = "http://" + serverIP + ":" + serverPort + requestType;// "/REQUESTFILELIST";

			GetMethod getMethod = new GetMethod(url);
			httpClient.executeMethod(getMethod);
			String stringdata = getMethod.getResponseBodyAsString();
			Thread.sleep(5000);
			File filename = new File(WEB_ROOT + "/" + enteredFilename);
			filename.setWritable(true);

			Writer writer = new BufferedWriter(new FileWriter(filename));
			writer.write(stringdata);
			writer.close();
			System.out.println("File Created. Please check the output folder.");

			getMethod.releaseConnection();
		} catch (java.net.ConnectException ConnExp) {
			System.out.println("Unable to Connect to Server/Server not Online");
			System.out.println(ConnExp.toString());
			ConnExp.printStackTrace();
		} catch (HttpException HttpExp) {
			System.out.println("Error Processing Request");
			System.out.println(HttpExp.toString());
			HttpExp.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException AIOBExp) {
			System.out.println("Invalid Execution! Please Enter the Arguments <ServerIP> <PortNo> <FileName>");
			System.out.println(AIOBExp.toString());
			AIOBExp.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}

	}

}

class SocketServerThread extends Thread {
	Socket clientSocket = null;
	ServerSocket socketServer;

	public SocketServerThread() throws IOException {
		runServer();
	}

	public void runServer() throws IOException {
		socketServer = new ServerSocket(80);
		clientSocket = socketServer.accept();

		InputStream inputStream = clientSocket.getInputStream();

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
		System.out.print("################## " + requestBuffer.toString());
		String string = requestBuffer.toString();
		String[] parts = string.split(",");
		for (i = 0; i < parts.length; i++) {
			System.out.print(parts[i] + "\n");
		}
	}

}