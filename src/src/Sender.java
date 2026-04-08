import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class Sender {

    private static final String SECRET_KEY = "mysecretkey";
    private static final String MESSAGE = "Hello, this is a secure message!";
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
        String hmacDigest = generateHMAC(MESSAGE);

        System.out.println("=== SENDER ===");
        System.out.println("Message:    " + MESSAGE);
        System.out.println("SHA (HMAC): " + hmacDigest);

        Socket socket = new Socket("localhost", PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Send message and SHA on separate lines
        out.println(MESSAGE);
        out.println(hmacDigest);

        System.out.println("\nPayload sent successfully.");

        socket.close();
    }
}