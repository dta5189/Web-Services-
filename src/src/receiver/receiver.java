package receiver;

import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class receiver {

    private static final String SECRET_KEY = "mysecretkey";
    private static final int PORT = 9999;

    public static String generateHMAC(String message) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
        mac.init(keySpec);
        byte[] hmacBytes = mac.doFinal(message.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hmacBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== RECEIVER: Waiting for connection... ===");

        ServerSocket serverSocket = new ServerSocket(PORT);
        Socket socket = serverSocket.accept();

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String receivedMessage = in.readLine();
        String receivedSHA = in.readLine();

        // Generate HMAC on receiver side
        String generatedSHA = generateHMAC(receivedMessage);

        System.out.println("\nMessage Received:  " + receivedMessage);
        System.out.println("SHA Received:      " + receivedSHA);
        System.out.println("SHA Generated:     " + generatedSHA);

        if (receivedSHA.equals(generatedSHA)) {
            System.out.println("\n Integrity check PASSED — SHAs match!");
        } else {
            System.out.println("\n Integrity check FAILED — SHAs do NOT match!");
        }

        socket.close();
        serverSocket.close();
    }
}