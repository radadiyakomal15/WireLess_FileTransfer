import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Calendar;

public final class MultiThreadedServer {
	
	public static ServerSocket socketServer;
	public static int port = 8080;
	
	public static void main(String args[]) throws Exception {
		try {
			socketServer = new ServerSocket(port);
			System.out.println("Initiating Server....");
			Thread.sleep(5000);
			System.out.println("Server Initiated at Port No: " + socketServer.getLocalPort());
			System.out.println("########################################################################");
			while (true) {
				Socket socketRequest = socketServer.accept();
				HTTPServer request = new HTTPServer(socketRequest);
				Thread thread = new Thread(request);
				thread.start();
			}
		} catch (IOException e) {
		    e.printStackTrace();
		    System.exit(1);
		}
	}
}

final class HTTPServer implements Runnable {
	
	public static long startTime;
	public static long endTime;
	public static String CRLF = "\r\n";
	public static int BUFFER_SIZE = 1024;
	public static Socket socket;
	public static String requestedFileName;
	public static String WEB_ROOT = System.getProperty("user.dir") + File.separator  + "webroot";

	@SuppressWarnings("static-access")
	public HTTPServer(Socket socket) throws Exception {
		this.socket = socket;
		System.out.println("Client IP: "+socket.getLocalAddress());
		System.out.println("Client Port: "+socket.getPort());
	}

	public void run() {
		try {
			processRequest();
			processResponse();
			System.out.println("########################################################################");
		} catch (SocketException sockExp) {
			System.out.println("Socket Timed-Out. Please Retry..");
			System.out.println(sockExp.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void processRequest() throws Exception {
		try {
			System.out.println("******************************REQUEST**********************************");
			System.out.println(Calendar.getInstance().getTime()+"|Request Recieved....");
			InputStream input = socket.getInputStream();
		    StringBuffer requestBuffer = new StringBuffer(2048);
		    int i = 0;
		    byte[] buffer = new byte[BUFFER_SIZE];
		    try {
		      i = input.read(buffer);
		    } catch (SocketException sockExp) {
				System.out.println("Socket Timed-Out. Please Retry..");
				System.out.println(sockExp.toString());
			} catch (IOException e) {
		      e.printStackTrace();
		      i = -1;
		    }
		    for (int j=0; j<i; j++) {
		    	requestBuffer.append((char) buffer[j]);
		    }
		    System.out.println("Request Type: GET");
		    System.out.println("HTTP Version: HTTP/1.1");
		    System.out.print("Request Line: "+requestBuffer.toString());
		    requestedFileName = readRequest(requestBuffer.toString());
		    System.out.println("Requested File: "+requestedFileName);
		} catch (SocketException sockExp) {
			System.out.println("Socket Timed-Out. Please Retry..");
			System.out.println(sockExp.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String readRequest(String requestString) {
	    int i = requestString.indexOf(' ');	
	    if (i != -1) {
	      int j = requestString.indexOf(' ', i + 1);
	      if (j > i)
	        return requestString.substring(i + 1, j);
	    }
	    return null;
	  }

	  public static String getRequestedFileName() {
	    return requestedFileName;
	  }
	  
	  public static void processResponse() throws Exception {
		  System.out.println("******************************RESPONSE**********************************");
		  System.out.println(Calendar.getInstance().getTime()+"|Creating & Sending Response....");
		  OutputStream output = socket.getOutputStream();
		  BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
		  
		  byte[] bytes = new byte[BUFFER_SIZE];
		    FileInputStream fis = null;
		    try {
		      File file = new File(WEB_ROOT, getRequestedFileName());
		      if (file.exists()) {
		    	  fis = new FileInputStream(file);
		    	  int ch = fis.read(bytes, 0, BUFFER_SIZE);
		    	  String headerString = "HTTP/1.1 200 OK \r\n" +
				          "Content-Type: "+contentType(getRequestedFileName())+"\r\n" +
				          "Content-Length: "+ch+"\r\n" + "\r\n";
		    	  output.write(headerString.getBytes());
		    	  while (ch!=-1) {
		    		  output.write(bytes, 0, ch);
		    		  ch = fis.read(bytes, 0, BUFFER_SIZE);
		    	  }
		      } else {
		        String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
		          "Content-Type: "+contentType(getRequestedFileName())+"\r\n" +
		          "Content-Length: 99\r\n" +
		          "\r\n" +
		          "<h1>HTTP:404 File Not Found</h1>";
		        output.write(errorMessage.getBytes());
		      }
		      System.out.println(Calendar.getInstance().getTime()+"|----Response Sent----");
		    } catch (SocketException sockExp) {
				System.out.println("Socket Timed-Out. Please Retry..");
				System.out.println(sockExp.toString());
				String errorMessage = "HTTP/1.1 500 Server Error\r\n" +
				          "Content-Type: "+contentType(getRequestedFileName())+"\r\n" +
				          "Content-Length: 99\r\n" +
				          "\r\n" +
				          "<h1>HTTP:500 Server Error</h1>";
		    output.write(errorMessage.getBytes());
			} catch (FileNotFoundException FNFExp) {
		    	String errorMessage = "HTTP/1.1 400 Bad Request\r\n" +
				          "Content-Type: "+contentType(getRequestedFileName())+"\r\n" +
				          "Content-Length: 99\r\n" +
				          "\r\n" +
				          "<h1>400 Bad Request</h1>";
		    	output.write(errorMessage.getBytes());
		    	//FNFExp.printStackTrace();
		    } catch (NullPointerException nullExp) {
		    	//output.write(nullExp.getMessage().getBytes());
		    	System.out.println(nullExp.toString());
		    	//nullExp.printStackTrace();
		    } catch (Exception e) {
		    	output.write(e.getMessage().getBytes());
		    }
		    finally {
		      if (fis!=null)
		        fis.close();
		      output.flush();
		      output.close();
		    }
	  }
	
	private static String contentType(String fileName) {
		if (fileName.toLowerCase().endsWith(".htm") || fileName.toLowerCase().endsWith(".html")) {
			return "text/html";
		} else if (fileName.toLowerCase().endsWith(".gif")) {
			return "image/gif";
		} else if (fileName.toLowerCase().endsWith(".jpg")) {
			return "image/jpeg";
		} else if (fileName.toLowerCase().endsWith(".txt")) {
			return "text/plaintext";
		} else {
			return "application/octet-stream";
		}
	}
}