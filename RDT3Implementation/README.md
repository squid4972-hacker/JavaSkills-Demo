# RDT 3.0 (Alternating Bit Protocol) Implementation

## Project Overview
This project implements a reliable data transfer protocol (RDT 3.0) using Java and UDP. The implementation consists of three programs:
- **Sender**: Reads user input, segments data, and transmits with sequence numbers
- **Receiver**: Receives segments, detects duplicates, and assembles messages
- **Network**: Simulates network transport with loss, corruption, and delay

**Note**: No compilation errors or warnings should occur (hopefully).
**How to use me**
- Step 1: Compile All Files
- Step 2: Open Three Terminals
- Step 3: Start in This Order
        Terminal 1:
        java Network 60050 0 0 0
        Terminal 2:
        java Receiver 60051 127.0.0.1 60050
        Terminal 3:
        java Sender 60052 127.0.0.1 60051 127.0.0.1 60050
        Step 4: Send Test Message
        In Terminal 3, type: “Hello World”
        Then “quit”

## Key Implementation Details

### Packet Structure
- **Network Header** (44 bytes): Source IP, Source Port, Dest IP, Dest Port
- **Transport Segment** (10 bytes):
  - Sequence Number (1 byte): 0 or 1
  - ACK Flag (1 byte): 0 for data, 1 for ACK
  - Corruption Tag (1 byte): 0 for clean, 1 for corrupt
  - Payload (7 bytes): Actual message data

### Segment Size
- Maximum payload per segment: 7 bytes
- A 70-byte message requires 10 segments

### Timeout Behavior
- Sender timeout: 3 seconds
- If ACK not received, segment is retransmitted
- Maximum retries: 10 attempts per segment

### Protocol Features
- Alternating bit (sequence numbers 0 and 1)
- Duplicate detection at receiver
- Automatic retransmission on timeout
- Corruption detection and retransmission
- Network simulation with configurable failure rates

## Troubleshooting errors

### "Port already in use" error
- Change the port number to a different value in 60000-60099 range
- Wait a minute and try again (OS may not have released port immediately)

### "Connection refused" error
- Ensure Network program is running before starting Sender/Receiver
- Verify correct 127.0.0.1 and port in command line arguments

### Sender stuck waiting for ACK
- Check that Receiver is running
- Verify Network is forwarding packets (check Network output)
- Increase timeout tolerance if network is slow

### Messages not assembling correctly
- Verify receiver output shows segments received in order
- Check that sender completes all segments (check sender output)
- Test without loss/delay/corruption first

## Notes
- All three programs use UDP and must bind to different ports
- The Network program routes all packets between Sender and Receiver
- Traffic statistics print every 2 packets received by Network
- Corrupt packets are discarded but ACKs are still sent

