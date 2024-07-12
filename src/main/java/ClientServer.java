import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientServer implements Runnable{

    Socket clientSocket = null;

    public ClientServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8);
            System.out.println("accepted new connection");
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            List<String> request = new ArrayList<>();
            String buffer;
            while ((buffer = reader.readLine()) != null && !buffer.isEmpty())
                request.add(new String(buffer));
            System.out.println(request);
            String line = request.get(0);
            String path = line.split(" ")[1];
            String[] paths = path.split("/");
            System.out.println(Arrays.toString(paths));
            if (path != null && path.equalsIgnoreCase("/"))
                writer.write("HTTP/1.1 200 OK\r\n\r\n");
            else if (paths.length > 2 && path.split("/")[1].equalsIgnoreCase("echo")) {
                String res = path.split("/")[2];
                writer.write("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + res.length() + "\r\n\r\n" + res);
            } else if (paths.length > 1 && path.split("/")[1].equalsIgnoreCase("user-agent")) {
                String res = request.get(2).split("/r/n")[0].split(" ")[1];
                writer.write("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + res.length() + "\r\n\r\n" + res);
            } else if (paths.length > 2 && path.split("/")[1].equalsIgnoreCase("files")) {
                String filename = path.split("/")[2];
                String filePathStr  = "./" + filename;
                Path filePath = Paths.get(filePathStr);
                System.out.println(filePath);
                System.out.println(Files.exists(filePath));
                if(Files.exists(filePath)){
                FileInputStream fileInputStream = new FileInputStream(filePathStr);
                reader = new BufferedReader(new InputStreamReader(fileInputStream));
                String fileText = reader.readLine();
                writer.write("HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + fileText.length() + "\r\n\r\n" + fileText);
                } else {
                writer.write("HTTP/1.1 404 Not Found\r\n\r\n");
                }
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
