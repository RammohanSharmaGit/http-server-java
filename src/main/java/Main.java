import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
     ServerSocket serverSocket = null;
     Socket clientSocket = null;
     String directory = "";
     if(args.length>1)
         directory = args[1];

     try {
       serverSocket = new ServerSocket(4221);
       serverSocket.setReuseAddress(true);

       while(true) {

           clientSocket = serverSocket.accept(); // Wait for connection from client.
           new Thread(new ClientServer(clientSocket,directory)).start();
       }

     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}
