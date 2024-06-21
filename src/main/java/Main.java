import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
  public static void main(String[] args) {
     ServerSocket serverSocket = null;
     Socket clientSocket = null;

     try {
       serverSocket = new ServerSocket(4221);
       serverSocket.setReuseAddress(true);
       clientSocket = serverSocket.accept(); // Wait for connection from client.
       OutputStreamWriter writer = new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8);
       System.out.println("accepted new connection");
       writer.write("HTTP/1.1 200 OK\r\n\r\n");
       writer.flush();
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}
