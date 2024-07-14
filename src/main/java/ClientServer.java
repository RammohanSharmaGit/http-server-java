import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ClientServer implements Runnable{

    Socket clientSocket = null;

    String directory = "";

    public ClientServer(Socket clientSocket, String directory) {
        this.clientSocket = clientSocket;
        this.directory = directory;
    }

    @Override
    public void run() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8);
            System.out.println("accepted new connection");
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String requestLine = reader.readLine();
            String httpMethod = requestLine.split(" ")[0];
            String resourcePath = requestLine.split(" ")[1];
            String[] paths = resourcePath.split("/");
            System.out.println(Arrays.toString(paths));


            Map<String,String> headers = new HashMap<>();
            String buffer;
            while ((buffer = reader.readLine()) != null && !buffer.isEmpty()) {
                headers.put(buffer.split(":")[0].trim(),buffer.split(":")[1].trim());
            }
            System.out.println(headers);


            if (resourcePath != null && resourcePath.equalsIgnoreCase("/"))
                writer.write("HTTP/1.1 200 OK\r\n\r\n");
            else if (paths.length > 2 && resourcePath.split("/")[1].equalsIgnoreCase("echo")) {
                String res = resourcePath.split("/")[2];
                writer.write("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + res.length() + "\r\n\r\n" + res);
            } else if (paths.length > 1 && resourcePath.split("/")[1].equalsIgnoreCase("user-agent")) {
                String res = headers.get("user-agent");
                writer.write("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + res.length() + "\r\n\r\n" + res);
            } else if (httpMethod.equalsIgnoreCase("get") && paths.length > 2 && resourcePath.split("/")[1].equalsIgnoreCase("files")) {
                String filename = resourcePath.split("/")[2];
                Path filePath = Paths.get(directory,filename);
                if(Files.exists(filePath)){
                String fileText = Files.readString(filePath);
                writer.write("HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + fileText.length() + "\r\n\r\n" + fileText);
                } else {
                writer.write("HTTP/1.1 404 Not Found\r\n\r\n");
                }
            } else if (httpMethod.equalsIgnoreCase("post") && paths.length > 2 && resourcePath.split("/")[1].equalsIgnoreCase("files")) {
                String filename = resourcePath.split("/")[2];
                Path filePath = Paths.get(directory,filename);

                String fileText = "";
                while(reader.ready()) {
                    fileText += (char)reader.read();
                }
                System.out.println(fileText);
                Files.writeString(filePath,fileText);
                writer.write("HTTP/1.1 201 Created\r\n\r\n");

            } else {
                writer.write("HTTP/1.1 404 Not Found\r\n\r\n");
            }
            writer.flush();
            clientSocket.close();
            } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
