# RDT 3.0 (Alternating Bit Protocol) Implementation

## Project Overview
This project implements a reliable data transfer protocol (RDT 3.0) using Java and UDP. The implementation consists of three programs:
- **Sender**: Reads user input, segments data, and transmits with sequence numbers
- **Receiver**: Receives segments, detects duplicates, and assembles messages
- **Network**: Simulates network transport with loss, corruption, and delay

## Files Included
1. `Packet.java` - Packet serialization/deserialization class
2. `Sender.java` - RDT 3.0 Sender implementation
3. `Receiver.java` - RDT 3.0 Receiver implementation
4. `Network.java` - Network simulator
5. `TestingGuide.md` - Protocol documentation
6. `README.md` - This file

## Compilation

Compile all files together:
```bash
javac *.java
```

Or compile individually:
```bash
javac Packet.java
javac Sender.java
javac Receiver.java
javac Network.java
```

**Note**: No compilation errors or warnings should occur (hopefully).

## Running the Programs

The three programs must be run in separate terminals on the same machine or across different machines.

### 1. Start the Network (acts as message router)
```bash
java Network <port> <lostPercent> <delayedPercent> <errorPercent>
```

Example (5% packet loss, 10% delay, 3% corruption):
```bash
java Network 60050 5 10 3
```

Example (no loss, no delay, no corruption):
```bash
java Network 60050 0 0 0
```

### 2. Start the Receiver
```bash
java Receiver <port>
```

Example:
```bash
java Receiver 60051
```

### 3. Start the Sender
```bash
java Sender <port> <networkHost> <networkPort>
```

Example (running on same machine):
```bash
java Sender 60052 127.0.0.1 60050
```

Example (network on different machine):
```bash
java Sender 60052 192.168.1.100 60050
```

## Testing

### Test 1: Basic Functionality (No Loss/Delay/Corruption)
```bash
# Terminal 1: Network with no failures
java Network 60050 0 0 0

# Terminal 2: Receiver
java Receiver 60051

# Terminal 3: Sender
java Sender 60052 127.0.0.1 60050

# In Terminal 3, send test messages:
# Enter: "Hello World"
# Enter: "RDT 3.0 Protocol Test"
# Enter: quit
```

### Test 2: With Packet Loss
```bash
# Terminal 1: Network with 20% packet loss
java Network 60050 20 0 0

# Terminal 2: Receiver
java Receiver 60051

# Terminal 3: Sender
java Sender 60052 127.0.0.1 60050

# In Terminal 3, send a message (should auto-retransmit on timeout)
```

### Test 3: With Corruption
```bash
# Terminal 1: Network with 15% corruption
java Network 60050 0 0 15

# Terminal 2: Receiver
java Receiver 60051

# Terminal 3: Sender
java Sender 60052 127.0.0.1 60050

# Send a message (corrupted segments should be dropped, retransmitted)
```

### Test 4: With Delay
```bash
# Terminal 1: Network with 30% delay
java Network 60050 0 30 0

# Terminal 2: Receiver
java Receiver 60051

# Terminal 3: Sender
java Sender 60052 127.0.0.1 60050

# Send a message (sender waits longer for ACK due to delays)
```

### Test 5: Stress Test (Long Message)
```bash
# Terminal 1: Network
java Network 60050 0 0 0

# Terminal 2: Receiver
java Receiver 60051

# Terminal 3: Sender
java Sender 60052 127.0.0.1 60050

# Send a long message (50+ bytes will require multiple segments)
```

### Test 6: All Failures
```bash
# Terminal 1: Network with all failures
java Network 60050 10 15 5

# Terminal 2: Receiver
java Receiver 60051

# Terminal 3: Sender
java Sender 60052 127.0.0.1 60050

# Send multiple messages
```

## Port Ranges
- **Sender**: Use port in range 60000-60099 (e.g., 60052)
- **Receiver**: Use port in range 60000-60099 (e.g., 60051)
- **Network**: Use port in range 60000-60099 (e.g., 60050)

All three programs can run on different machines if network access permits.

## Expected Output

### Sender Output
```
Sender initialized
Sender host: 127.0.0.1
Sender port: 60052
Network host: 127.0.0.1
Network port: 60050

=== Sending message: Hello World
Message length: 11 bytes
Segments needed: 2
Segment 1/2 sent (seq=0, data="Hello Wor")
ACK received (seq=0)
Segment 2/2 sent (seq=1, data="ld")
ACK received (seq=1)
=== Message transmission complete
```

### Receiver Output
```
Receiver initialized
Receiver host: 127.0.0.1
Receiver port: 60051
Waiting for messages...

--- Received packet from 127.0.0.1:60052
Segment received (seq=0, data="Hello Wor")
Sequence number matches expected (0)
ACK sent (seq=0)

--- Received packet from 127.0.0.1:60052
Segment received (seq=1, data="ld")
Sequence number matches expected (1)
ACK sent (seq=1)
```

### Network Output
```
Network initialized
Network host: 127.0.0.1
Network port: 60050
Lost packet probability: 0%
Delayed packet probability: 0%
Error packet probability: 0%

Listening for packets...

[Network] Received packet from 127.0.0.1:60052
[Network] Forwarding to 127.0.0.1:60051 (seq=0, ack=0)
[Network] Sent packet to 127.0.0.1:60051

========== TRAFFIC STATISTICS ==========
Packets received: 2
Packets sent: 2
Packets dropped: 0
Packets delayed: 0
Packets corrupted: 0
=========================================
```

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

## Troubleshooting

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
- All programs should be terminated with Ctrl+C

## Testing on SSH Server
1. Compile: `javac *.java`
2. Open three SSH terminals (or use screen/tmux)
3. Run programs as described in Testing section
