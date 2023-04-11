import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Server1 {
    private static final String SECRET_KEY = "mysecretpassword"; // replace with your own secret key
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Server started. Listening on port 1234...");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");
            Thread thread = new Thread(() -> {
                try {
                    // Receive file name from client
                    InputStream inputStream = clientSocket.getInputStream();
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead = inputStream.read(buffer);
                    String fileName = new String(buffer, 0, bytesRead);
                    System.out.println("Received file name: " + fileName);

                    // Read file from disk
                    byte[] fileBytes = Files.readAllBytes(Paths.get(fileName));

                    // Encrypt file using Camellia
                    byte[] encryptedBytes = CamelliaEncryption.encrypt(fileBytes, SECRET_KEY.getBytes());
                    System.out.println("File encrypted.");

                    // Compress file using LZW
                    byte[] compressedBytes = LZWCompression.compress(encryptedBytes);
                    System.out.println("File compressed.");

                    // Send compressed data to client
                    OutputStream outputStream = clientSocket.getOutputStream();
                    outputStream.write(compressedBytes);
                    System.out.println("File sent to client.");

                    clientSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }
}