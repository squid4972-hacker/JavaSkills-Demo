/**
 * This class handles serialization and deserialization of RDT 3.0 packets
 * Network header (44 bytes) + Transport segment (10 bytes) = 54 bytes total
 * 
 * @author Sai Laborn
 * @date 18 July 2026
 */

import java.io.Serializable;
import java.util.Arrays;

public class Packet implements Serializable {
    // Network header fields (44 bytes total)
    private String srcIP;           // 16 bytes
    private String srcPort;         // 6 bytes
    private String destIP;          // 16 bytes
    private String destPort;        // 6 bytes
    
    // Transport segment fields (10 bytes)
    private byte seqNum;            // 1 byte: 0 or 1
    private byte ackFlag;           // 1 byte: 0 or 1
    private byte corruptTag;        // 1 byte: 0 = clean, 1 = corrupt
    private byte[] payload;         // 7 bytes: actual data
    
    // Constructor for creating packets with network header
    public Packet(String srcIP, String srcPort, String destIP, String destPort,
                  byte seqNum, byte ackFlag, byte[] payload) {
        this.srcIP = padString(srcIP, 16);
        this.srcPort = padString(srcPort, 6);
        this.destIP = padString(destIP, 16);
        this.destPort = padString(destPort, 6);
        this.seqNum = seqNum;
        this.ackFlag = ackFlag;
        this.corruptTag = 0;  // Initially clean
        this.payload = new byte[7];
        
        // Copy payload, padding with zeros if necessary
        if (payload != null) {
            System.arraycopy(payload, 0, this.payload, 0, Math.min(payload.length, 7));
        }
    }
    
    // Serialize packet to 54-byte array
    public byte[] toByteArray() {
        byte[] result = new byte[54];
        int index = 0;
        
        // Network header
        byte[] srcIPBytes = srcIP.getBytes();
        System.arraycopy(srcIPBytes, 0, result, index, 16);
        index += 16;
        
        byte[] srcPortBytes = srcPort.getBytes();
        System.arraycopy(srcPortBytes, 0, result, index, 6);
        index += 6;
        
        byte[] destIPBytes = destIP.getBytes();
        System.arraycopy(destIPBytes, 0, result, index, 16);
        index += 16;
        
        byte[] destPortBytes = destPort.getBytes();
        System.arraycopy(destPortBytes, 0, result, index, 6);
        index += 6;
        
        // Transport segment
        result[index++] = seqNum;
        result[index++] = ackFlag;
        result[index++] = corruptTag;
        System.arraycopy(payload, 0, result, index, 7);
        
        return result;
    }
    
    // Deserialize packet from 54-byte array
    public static Packet fromByteArray(byte[] data) {
        if (data.length < 54) {
            throw new IllegalArgumentException("Packet must be 54 bytes");
        }
        
        int index = 0;
        
        // Extract network header
        String srcIP = new String(Arrays.copyOfRange(data, index, index + 16)).trim();
        index += 16;
        
        String srcPort = new String(Arrays.copyOfRange(data, index, index + 6)).trim();
        index += 6;
        
        String destIP = new String(Arrays.copyOfRange(data, index, index + 16)).trim();
        index += 16;
        
        String destPort = new String(Arrays.copyOfRange(data, index, index + 6)).trim();
        index += 6;
        
        // Extract transport segment
        byte seqNum = data[index++];
        byte ackFlag = data[index++];
        byte corruptTag = data[index++];
        
        byte[] payload = Arrays.copyOfRange(data, index, index + 7);
        
        // Create packet and set corruption flag
        Packet packet = new Packet(srcIP, srcPort, destIP, destPort, seqNum, ackFlag, payload);
        packet.corruptTag = corruptTag;
        
        return packet;
    }
    
    // Helper: pad string to exact length
    private static String padString(String str, int length) {
        if (str == null) str = "";
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.append("\0");
        }
        return sb.toString();
    }
    
    // Getters
    public String getDestPort() { 
        return destPort.trim().replaceAll("\0", "");
    }

    public String getSrcPort() { 
        return srcPort.trim().replaceAll("\0", "");
    }

    public String getDestIP() { 
        return destIP.trim().replaceAll("\0", "");
    }

    public String getSrcIP() { 
        return srcIP.trim().replaceAll("\0", "");
    }
    public byte getSeqNum() { return seqNum; }
    public byte getAckFlag() { return ackFlag; }
    public byte getCorruptTag() { return corruptTag; }
    public byte[] getPayload() { return payload; }
    
    // Setters
    public void setCorruptTag(byte tag) { this.corruptTag = tag; }
    
    // Check if this is an ACK packet
    public boolean isAck() {
        return ackFlag == 1;
    }
    
    // Get payload as string (removes null padding)
    public String getPayloadAsString() {
        int len = 0;
        for (int i = 0; i < payload.length; i++) {
            if (payload[i] == 0) break;
            len++;
        }
        return new String(Arrays.copyOfRange(payload, 0, len));
    }
    
    @Override
    public String toString() {
        return String.format("Packet[src=%s:%s, dest=%s:%s, seq=%d, ack=%d, corrupt=%d, payload=%s]",
                getSrcIP(), getSrcPort(), getDestIP(), getDestPort(), 
                seqNum, ackFlag, corruptTag, getPayloadAsString());
    }
}
