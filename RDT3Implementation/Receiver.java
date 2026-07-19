/**
 * This class implements RDT 3.0 Receiver protocol
 * Receives segments, assembles message, detects duplicates
 * 
 * @author Sai Laborn
 * @date 6 July 2026
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
 
public class Receiver {
    private DatagramSocket socket;
    private String myHost;
    private int myPort;
    private String networkHost;
    private int networkPort;
    private byte expectedSeqNum;
    private StringBuilder receivedMessage;
    private boolean messageComplete;
    
    public Receiver(int port, String networkHost, int networkPort) {
        this.myPort = port;
        this.myHost = "127.0.0.1";
        this.networkHost = networkHost;
        this.networkPort = networkPort;
        this.expectedSeqNum = 0;
        this.receivedMessage = new StringBuilder();
        this.messageComplete = false;
    }
    
    // Initialize socket
    public void initialize() throws SocketException {
        socket = new DatagramSocket(myPort);
        System.out.println("Receiver initialized");
        System.out.println("Receiver host: " + myHost);
        System.out.println("Receiver port: " + myPort);
        System.out.println("Network host: " + networkHost);
        System.out.println("Network port: " + networkPort);
        System.out.println("Waiting for messages...");
    }
    
    // Send ACK packet (through Network, which routes to Sender)
    private void sendAck(String senderHost, int senderPort, byte seqNum) {
        try {
            byte[] ackPayload = new byte[7];  // Empty payload for ACK
            // ACK packet has Sender as destination (so Network knows where to route it)
            Packet ackPacket = new Packet(myHost, String.valueOf(myPort),
                                         senderHost, String.valueOf(senderPort),
                                         seqNum, (byte) 1, ackPayload);  // ackFlag = 1
            
            byte[] ackData = ackPacket.toByteArray();
            // Send ACK to Network (not directly to Sender)
            DatagramPacket dgPacket = new DatagramPacket(ackData, ackData.length,
                                                        InetAddress.getByName(networkHost),
                                                        networkPort);
            socket.send(dgPacket);
            System.out.println("ACK sent (seq=" + seqNum + ")");
        } catch (Exception e) {
            System.err.println("Error sending ACK: " + e.getMessage());
        }
    }
    
    // Process received packet
    private void processPacket(Packet packet) {
        // Extract sender's address from packet header (not UDP source)
        String senderHost = packet.getSrcIP();
        int senderPort = Integer.parseInt(packet.getSrcPort());
        
        byte seqNum = packet.getSeqNum();
        String payload = packet.getPayloadAsString();
        
        System.out.println("Segment received (seq=" + seqNum + ", data=\"" + payload + "\")");
        
        if (packet.getCorruptTag() == 1) {
            System.out.println("Segment is corrupt, discarding");
            // Send ACK for expected sequence anyway (receiver state doesn't change)
            sendAck(senderHost, senderPort, expectedSeqNum);
            return;
        }
        
        // Check if this is the expected sequence number
        if (seqNum == expectedSeqNum) {
            System.out.println("Sequence number matches expected (" + expectedSeqNum + ")");
            receivedMessage.append(payload);
            
            // Toggle expected sequence number for next segment
            expectedSeqNum = (byte) (1 - expectedSeqNum);
        } else {
            System.out.println("Duplicate segment (seq=" + seqNum + 
                             "), expected seq=" + expectedSeqNum);
        }
        
        // Always send ACK (for the sequence we just processed or duplicate)
        sendAck(senderHost, senderPort, (byte) (1 - expectedSeqNum));
    }
    
    // Main receive loop
    public void receiveMessages() {
        while (true) {
            try {
                byte[] recvData = new byte[54];
                DatagramPacket dgPacket = new DatagramPacket(recvData, recvData.length);
                socket.receive(dgPacket);
                
                // Network's address (source of this UDP packet)
                String networkActualHost = dgPacket.getAddress().getHostName();
                int networkActualPort = dgPacket.getPort();
                
                // Parse packet
                Packet packet = Packet.fromByteArray(recvData);
                
                // Extract original sender from packet header
                String senderHost = packet.getSrcIP();
                
                System.out.println("\n--- Received packet from " + networkActualHost + ":" + networkActualPort);
                System.out.println("    Original sender: " + senderHost);
                
                // Process the packet
                processPacket(packet);
                
            } catch (Exception e) {
                System.err.println("Error receiving packet: " + e.getMessage());
            }
        }
    }
    
    // Get received message
    public String getReceivedMessage() {
        return receivedMessage.toString();
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
        if (args.length != 3) {
            System.err.println("Usage: java Receiver <port> <networkHost> <networkPort>");
            System.exit(1);
        }
        
        int port = Integer.parseInt(args[0]);
        String networkHost = args[1];
        int networkPort = Integer.parseInt(args[2]);
        
        Receiver receiver = new Receiver(port, networkHost, networkPort);
        
        try {
            receiver.initialize();
            receiver.receiveMessages();
        } catch (Exception e) {
            System.err.println("Receiver error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            receiver.close();
        }
    }
}
