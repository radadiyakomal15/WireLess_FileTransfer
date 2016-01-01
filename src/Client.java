import java.io.*;
import java.net.*;

public class Client {
    public static void main(String argv[]) throws Exception {
    	
    	String serverIPAddress = argv[0].trim();
    	int portNumber = Integer.parseInt(argv[1].trim());
    	//String fileName = argv[2].trim();
    	
    	//This can be used to define default IPAddress,filename and port no
//    	  String serverIPAddress = "localhost";
//    	int portNumber = Integer.parseInt("4853");
//    	String fileName = "sample.txt"; 
    	
        String sentence;
        String modifiedSentence;
        String Client_Name = "Mahesh:Server";
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
              Socket clientSocket = new Socket(serverIPAddress, portNumber);
        ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream outstream = clientSocket .getOutputStream(); 
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
	    for (int j=0; j<i; j++) {
	    	requestBuffer.append((char) buffer[j]);
	    }
	    System.out.print("################## "+requestBuffer.toString());
	    String string = requestBuffer.toString();
	    String[] parts = string.split(",");
	    for(i = 0; i<parts.length; i++){
	    System.out.print(parts[i]+"\n");
	    }
	    String requestMessage = "RequestFileList";
//	    OutputStream outToServer1 = clientSocket.getOutputStream();
//        DataOutputStream out = new DataOutputStream(outToServer1);
//        out.writeUTF("RequestFileList");
        ObjectOutputStream outToServer11 = new ObjectOutputStream(clientSocket.getOutputStream());      
        outToServer11.writeObject(requestMessage + '\n');    // Sending client request to server
		outToServer11.flush();
		System.out.println("Request sEN=T!!");

        clientSocket.close();
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
}