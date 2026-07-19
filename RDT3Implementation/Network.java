/**
 * This class simulates network transport between Sender and Receiver
 * Implements packet loss, corruption, and delay simulation
 * Records and prints traffic statistics
 * 
 * @author Sai Laborn
 * @date 18 July 2026
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class Network {
    private DatagramSocket socket;
    private String myHost;
    private int myPort;
    private int lostPercent;
    private int delayedPercent;
    private int errorPercent;
    private Random random;
    
    // Traffic statistics
    private int packetsReceived = 0;
    private int packetsSent = 0;
    private int packetsDropped = 0;
    private int packetsDelayed = 0;
    private int packetsCorrupted = 0;
    private static final int STATS_INTERVAL = 2;  // Print stats every 2 packets received
    
    // Timing for delay simulation
    private static final int MIN_DELAY_MS = 1500;   // 1.5 seconds
    private static final int MAX_DELAY_MS = 2000;   // 2 seconds
    
    public Network(int port, int lostPercent, int delayedPercent, int errorPercent) {
        this.myPort = port;
        this.myHost = "127.0.0.1";
        this.lostPercent = lostPercent;
        this.delayedPercent = delayedPercent;
        this.errorPercent = errorPercent;
        this.random = new Random();
    }
    
    // Initialize socket
    public void initialize() throws Exception {
        socket = new DatagramSocket(myPort);
        System.out.println("Network initialized");
        System.out.println("Network host: " + myHost);
        System.out.println("Network port: " + myPort);
        System.out.println("Lost packet probability: " + lostPercent + "%");
        System.out.println("Delayed packet probability: " + delayedPercent + "%");
        System.out.println("Error packet probability: " + errorPercent + "%");
        System.out.println("\nListening for packets...\n");
    }
    
    // Simulate packet loss
    private boolean shouldDropPacket() {
        return random.nextInt(100) < lostPercent;
    }
    
    // Simulate packet delay
    private boolean shouldDelayPacket() {
        return random.nextInt(100) < delayedPercent;
    }
    
    // Simulate packet corruption
    private boolean shouldCorruptPacket() {
        return random.nextInt(100) < errorPercent;
    }
    
    // Get random delay in milliseconds
    private long getRandomDelay() {
        return MIN_DELAY_MS + random.nextInt(MAX_DELAY_MS - MIN_DELAY_MS + 1);
    }
    
    // Send packet with delay in separate thread if needed
    private void sendPacket(byte[] data, String destHost, int destPort, boolean isDelayed) {
        if (isDelayed) {
            // Create thread to send with delay
            new Thread(() -> {
                try {
                    long delay = getRandomDelay();
                    Thread.sleep(delay);
                    
                    DatagramPacket dgPacket = new DatagramPacket(data, data.length,
                                                                 InetAddress.getByName(destHost),
                                                                 destPort);
                    socket.send(dgPacket);
                    
                    synchronized (this) {
                        packetsSent++;
                        System.out.println("[Network] Sent delayed packet to " + destHost +
                                         ":" + destPort + " (delayed " + delay + "ms)");
                    }
                } catch (Exception e) {
                    System.err.println("[Network] Error sending delayed packet: " + e.getMessage());
                }
            }).start();
        } else {
            try {
                DatagramPacket dgPacket = new DatagramPacket(data, data.length,
                                                             InetAddress.getByName(destHost),
                                                             destPort);
                socket.send(dgPacket);
                
                synchronized (this) {
                    packetsSent++;
                }
            } catch (Exception e) {
                System.err.println("[Network] Error sending packet: " + e.getMessage());
            }
        }
    }
    
    // Print traffic statistics
    private void printStatistics() {
        System.out.println("\n========== TRAFFIC STATISTICS ==========");
        System.out.println("Packets received: " + packetsReceived);
        System.out.println("Packets sent: " + packetsSent);
        System.out.println("Packets dropped: " + packetsDropped);
        System.out.println("Packets delayed: " + packetsDelayed);
        System.out.println("Packets corrupted: " + packetsCorrupted);
        System.out.println("=========================================\n");
    }
    
    // Main receive and forward loop
    public void forwardPackets() {
        while (true) {
            try {
                byte[] recvData = new byte[54];
                DatagramPacket dgPacket = new DatagramPacket(recvData, recvData.length);
                socket.receive(dgPacket);
                
                synchronized (this) {
                    packetsReceived++;
                }
                
                // Parse packet to get destination
                Packet packet = Packet.fromByteArray(recvData);
                String destHost = packet.getDestIP();
                int destPort = Integer.parseInt(packet.getDestPort());
                
                String srcHost = dgPacket.getAddress().getHostName();
                int srcPort = dgPacket.getPort();
                
                System.out.println("[Network] Received packet from " + srcHost + ":" + srcPort);
                System.out.println("[Network] Forwarding to " + destHost + ":" + destPort +
                                 " (seq=" + packet.getSeqNum() + ", ack=" + packet.getAckFlag() + ")");
                
                // Check if packet should be dropped
                if (shouldDropPacket()) {
                    synchronized (this) {
                        packetsDropped++;
                    }
                    System.out.println("[Network] *** PACKET DROPPED ***");
                } else {
                    // Check if packet should be corrupted
                    boolean isCorrupted = false;
                    if (shouldCorruptPacket()) {
                        isCorrupted = true;
                        packet.setCorruptTag((byte) 1);
                        recvData = packet.toByteArray();
                        synchronized (this) {
                            packetsCorrupted++;
                        }
                        System.out.println("[Network] *** PACKET CORRUPTED ***");
                    }
                    
                    // Check if packet should be delayed
                    boolean isDelayed = false;
                    if (shouldDelayPacket()) {
                        isDelayed = true;
                        synchronized (this) {
                            packetsDelayed++;
                        }
                        System.out.println("[Network] *** PACKET DELAYED ***");
                    }
                    
                    // Send packet
                    sendPacket(recvData, destHost, destPort, isDelayed);
                }
                
                // Print statistics every STATS_INTERVAL packets received
                synchronized (this) {
                    if (packetsReceived % STATS_INTERVAL == 0) {
                        printStatistics();
                    }
                }
                
            } catch (Exception e) {
                System.err.println("[Network] Error: " + e.getMessage());
            }
        }
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
        if (args.length != 4) {
            System.err.println("Usage: java Network <port> <lostPercent> <delayedPercent> <errorPercent>");
            System.exit(1);
        }
        
        int port = Integer.parseInt(args[0]);
        int lostPercent = Integer.parseInt(args[1]);
        int delayedPercent = Integer.parseInt(args[2]);
        int errorPercent = Integer.parseInt(args[3]);
        
        // Validate percentages
        if (lostPercent < 0 || lostPercent > 100 ||
            delayedPercent < 0 || delayedPercent > 100 ||
            errorPercent < 0 || errorPercent > 100) {
            System.err.println("Percentages must be between 0 and 100");
            System.exit(1);
        }
        
        Network network = new Network(port, lostPercent, delayedPercent, errorPercent);
        
        try {
            network.initialize();
            network.forwardPackets();
        } catch (Exception e) {
            System.err.println("Network error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            network.close();
        }
    }
}
