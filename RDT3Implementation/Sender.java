/**
 * This class implements RDT 3.0 Sender protocol
 * Reads input from user, segments data, sends with sequence numbers and timeouts
 * 
 * @author Sai Laborn
 * @date 6 July 2026
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Sender {
    private DatagramSocket socket;
    private String myHost;
    private int myPort;
    private String receiverHost;
    private int receiverPort;
    private String networkHost;
    private int networkPort;
    private byte currentSeqNum;
    private static final int TIMEOUT_MS = 3000;  // 3 second timeout
    private static final int PAYLOAD_SIZE = 7;   // 7 bytes of actual data per segment
    
    public Sender(int port, String receiverHost, int receiverPort, String networkHost, int networkPort) {
        this.myPort = port;
        this.myHost = "127.0.0.1";
        this.receiverHost = receiverHost;
        this.receiverPort = receiverPort;
        this.networkHost = networkHost;
        this.networkPort = networkPort;
        this.currentSeqNum = 0;
    }
    
    // Initializes socket
    public void initialize() throws Exception {
        socket = new DatagramSocket(myPort);
        socket.setSoTimeout(TIMEOUT_MS);
        System.out.println("Sender initialized");
        System.out.println("Sender host: " + myHost);
        System.out.println("Sender port: " + myPort);
        System.out.println("Receiver host: " + receiverHost);
        System.out.println("Receiver port: " + receiverPort);
        System.out.println("Network host: " + networkHost);
        System.out.println("Network port: " + networkPort);
    }
    
    // the primary send method
    public void sendMessage(String message) {
        System.out.println("\n=== Sending message: " + message);
        System.out.println("Message length: " + message.length() + " bytes");
        
        byte[] messageBytes = message.getBytes();
        int segmentCount = (int) Math.ceil((double) messageBytes.length / PAYLOAD_SIZE);
        System.out.println("Segments needed: " + segmentCount);
        
        // Sends each segment
        for (int i = 0; i < segmentCount; i++) {
            // Extracts 7 bytes of payload
            int start = i * PAYLOAD_SIZE;
            int end = Math.min(start + PAYLOAD_SIZE, messageBytes.length);
            byte[] payload = new byte[PAYLOAD_SIZE];
            System.arraycopy(messageBytes, start, payload, 0, end - start);
            
            // Sends segment with retries
            boolean ackReceived = false;
            int retries = 0;
            int maxRetries = 10;
            
            while (!ackReceived && retries < maxRetries) {
                try {
                    // Creates and sends packet with Receiver as destination
                    Packet sendPacket = new Packet(myHost, String.valueOf(myPort),
                                                   receiverHost, String.valueOf(receiverPort),
                                                   currentSeqNum, (byte) 0, payload);
                    
                    byte[] sendData = sendPacket.toByteArray();
                    // Send to Network (which will route to Receiver based on packet header)
                    DatagramPacket dgPacket = new DatagramPacket(sendData, sendData.length,
                                                                  InetAddress.getByName(networkHost),
                                                                  networkPort);
                    socket.send(dgPacket);
                    System.out.println("Segment " + (i + 1) + "/" + segmentCount +
                                     " sent (seq=" + currentSeqNum + ", data=\"" +
                                     new String(payload).trim() + "\")");
                    
                    // Waits for ACK
                    byte[] recvData = new byte[54];
                    DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
                    
                    try {
                        socket.receive(recvPacket);
                        Packet ackPacket = Packet.fromByteArray(recvData);
                        
                        // Checks if it's an ACK with correct sequence number
                        if (ackPacket.isAck() && ackPacket.getSeqNum() == currentSeqNum) {
                            System.out.println("ACK received (seq=" + ackPacket.getSeqNum() + ")");
                            ackReceived = true;
                        } else {
                            System.out.println("Received non-matching ACK (seq=" + 
                                             ackPacket.getSeqNum() + "), ignoring");
                        }
                    } catch (SocketTimeoutException e) {
                        retries++;
                        System.out.println("Timeout! Retransmitting segment " + (i + 1) +
                                         " (attempt " + (retries + 1) + ")");
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error sending segment: " + e.getMessage());
                    retries++;
                }
            }
            
            if (!ackReceived) {
                System.err.println("Failed to send segment " + (i + 1) + " after " +
                                 maxRetries + " retries");
            } else {
                // Toggle sequence number for next segment
                currentSeqNum = (byte) (1 - currentSeqNum);
            }
        }
        
        System.out.println("Message transmission complete\n");
    }
    
    // Prompts user for input and send
    public void promptAndSend() {
        Scanner scanner = new Scanner(System.in);
        String input;
        
        while (true) {
            System.out.print("Enter message to send (or 'quit' to exit): ");
            input = scanner.nextLine();
            
            if (input.equalsIgnoreCase("quit")) {
                break;
            }
            
            if (!input.isEmpty()) {
                sendMessage(input);
            }
        }
        
        scanner.close();
    }
    
    // Get local hostname
    private String getLocalHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "localhost";
        }
    }
    
    // Cleanup
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
    
    // Main method
    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Usage: java Sender <port> <receiverHost> <receiverPort> <networkHost> <networkPort>");
            System.exit(1);
        }
        
        int port = Integer.parseInt(args[0]);
        String receiverHost = args[1];
        int receiverPort = Integer.parseInt(args[2]);
        String networkHost = args[3];
        int networkPort = Integer.parseInt(args[4]);
        
        Sender sender = new Sender(port, receiverHost, receiverPort, networkHost, networkPort);
        
        try {
            sender.initialize();
            sender.promptAndSend();
        } catch (Exception e) {
            System.err.println("Sender error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sender.close();
        }
    }
}
