# RDT 3.0 Protocol Documentation

## Overview
This document describes the Reliable Data Transfer (RDT 3.0) protocol implementation, also known as the Alternating Bit Protocol. RDT 3.0 is a transport layer protocol that provides reliable delivery of data over an unreliable network channel.

## Protocol Characteristics
- **Sequence Numbers**: Binary alternating (0 or 1)
- **Acknowledgments**: Explicit ACK packets with sequence numbers
- **Timeout-Based Retransmission**: Sender uses timer to detect lost/delayed packets
- **Duplicate Detection**: Receiver detects and discards duplicate packets
- **Error Detection**: Corruption tag in packet indicates if segment is damaged
- **Maximum Segment Size**: 7 bytes of payload per segment

## Transport Layer Segment Format

```
Byte  0: Sequence Number (0 or 1)
Byte  1: ACK Flag (0 = data packet, 1 = ACK packet)
Byte  2: Corruption Tag (0 = clean, 1 = corrupted)
Bytes 3-9: Payload (7 bytes of actual message data)

Total: 10 bytes per segment
```

## Network Header Format

The Network header is prepended to each segment to support routing through the Network simulator:

```
Bytes  0-15: Source IP Address (16 bytes, padded with null)
Bytes 16-21: Source Port (6 bytes, padded with null)
Bytes 22-37: Destination IP Address (16 bytes, padded with null)
Bytes 38-43: Destination Port (6 bytes, padded with null)

Total: 44 bytes
```

## Complete Packet Structure

```
[Network Header: 44 bytes] + [Transport Segment: 10 bytes] = 54 bytes total

Byte Layout:
0         10        20        30        40        44        50    53
|---------|---------|---------|---------|---------|---------|------|
Network Header (44 bytes)           Segment (10 bytes)
```

## Sender Algorithm

### Sender State Machine

```
                   ┌──────────────────────────┐
                   │  WAITING_FOR_DATA        │
                   │  (seq = current)         │
                   └──────────────────────────┘
                            │
                            │ (data ready)
                            ↓
                   ┌──────────────────────────┐
                   │  CREATE SEGMENT          │
                   │  - Sequence number       │
                   │  - 7-byte payload        │
                   │  - Send packet           │
                   │  - Start timeout         │
                   └──────────────────────────┘
                            │
                            ↓
                   ┌──────────────────────────┐
                   │  WAITING_FOR_ACK         │
                   │  (seq = current)         │
                   └──────────────────────────┘
                        │        │
              (timeout) │        │ (ACK received, seq=current)
                        │        │
         ┌──────────────┘        └──────────────┐
         │                                       │
         ↓                                       ↓
    Retransmit             ┌──────────────────────────┐
    (up to 10 times)       │  Toggle Sequence Number  │
         │                  │  seq = 1 - seq           │
         │                  └──────────────────────────┘
         │                            │
         └───────────────┬────────────┘
                         │
                         ↓
                   ┌──────────────────────────┐
                   │  WAITING_FOR_DATA        │
                   │  (for next segment)      │
                   └──────────────────────────┘
```

### Sender Pseudocode

```
SENDER ALGORITHM:
1. Prompt user for message
2. Break message into 7-byte segments
3. For each segment:
   a. Set seqNum = current sequence number (0 or 1)
   b. Create packet with:
      - seqNum
      - ackFlag = 0 (data packet)
      - payload = 7 bytes of data
   c. Send packet to Network
   d. Start timer (3 second timeout)
   e. Wait for ACK or timeout
   f. If timeout:
      - Retransmit packet
      - Restart timer
      - Repeat up to 10 times
   g. If ACK received with correct seqNum:
      - Cancel timer
      - Toggle seqNum: seqNum = 1 - seqNum
      - Proceed to next segment
   h. If ACK received with wrong seqNum:
      - Ignore (duplicate ACK)
      - Continue waiting for correct ACK

TIMEOUT: 3000 milliseconds
MAX_RETRIES: 10
```

### Key Sender Features
- **Segmentation**: Breaks messages into 7-byte chunks
- **Sequencing**: Alternates sequence number 0 and 1 per segment
- **Timeout Management**: Uses socket timeout to detect lost ACKs
- **Retransmission**: Resends on timeout or duplicate ACK
- **Duplicate Detection**: Ignores ACKs with wrong sequence number

## Receiver Algorithm

### Receiver State Machine

```
                   ┌──────────────────────────┐
                   │  WAITING_FOR_SEGMENT     │
                   │  (expected_seq = 0 or 1) │
                   └──────────────────────────┘
                            │
                            │ (packet received)
                            ↓
                   ┌──────────────────────────┐
                   │  CHECK SEQUENCE NUMBER   │
                   └──────────────────────────┘
                        │        │
        (seq != expected)│        │ (seq = expected)
                        │        │
         ┌──────────────┘        └──────────────┐
         │                                       │
         ↓                                       ↓
    DUPLICATE            ┌──────────────────────────┐
    (Ignore data,        │  DELIVER PAYLOAD         │
     Send ACK for        │  Assemble into message   │
     expected)           └──────────────────────────┘
         │                            │
         │                   ┌────────┴────────┐
         │                   │                 │
         │            (no corruption)    (corrupted)
         │                   │                 │
         │                   ↓                 ↓
         │            STORE IN BUFFER      DISCARD
         │                   │
         │                   ↓
         │        ┌──────────────────────────┐
         │        │  TOGGLE EXPECTED_SEQ     │
         │        │  expected_seq = 1-seq    │
         │        └──────────────────────────┘
         │                   │
         └───────────────┬───┘
                         │
                         ↓
             ┌──────────────────────────┐
             │  SEND ACK                │
             │  - ackFlag = 1           │
             │  - seqNum = last_seq     │
             └──────────────────────────┘
                         │
                         ↓
             ┌──────────────────────────┐
             │  WAITING_FOR_SEGMENT     │
             └──────────────────────────┘
```

### Receiver Pseudocode

```
RECEIVER ALGORITHM:
1. Initialize expected_seq = 0
2. Listen for incoming packets
3. Upon packet reception:
   a. Parse packet header
   b. Check corruption tag:
      - If corrupted:
        * Discard packet
        * Send ACK for next expected (1 - expected_seq)
        * Return to step 2
   c. Check sequence number:
      - If seq = expected_seq:
        * Deliver payload to assembly buffer
        * Toggle expected_seq: expected_seq = 1 - expected_seq
      - Else (seq != expected_seq):
        * Duplicate detected
        * Discard payload (don't deliver)
        * Send ACK for previous (1 - seq)
   d. Send ACK packet with:
      - ackFlag = 1
      - seqNum = (1 - expected_seq)
   e. Return to step 2

INITIAL_EXPECTED_SEQ: 0
```

### Key Receiver Features
- **Sequence Number Checking**: Validates segment against expected sequence
- **Duplicate Detection**: Recognizes duplicate segments by sequence number
- **Corruption Detection**: Checks corruption tag and discards if needed
- **ACK Generation**: Always sends ACK (for expected or duplicate)
- **Message Assembly**: Concatenates payloads in correct order

## Network Simulator Algorithm

### Network Responsibilities
1. **Routing**: Direct packets from Sender to Receiver and vice versa
2. **Packet Loss Simulation**: Randomly drop packets
3. **Corruption Simulation**: Randomly mark packets as corrupted
4. **Delay Simulation**: Randomly delay packets using threads
5. **Statistics Tracking**: Record packet counts and statistics

### Packet Loss
```
If random(0-100) < lostPercent:
    DROP packet (do not forward)
    Increment dropped count
Else:
    Continue processing
```

### Packet Corruption
```
If random(0-100) < errorPercent:
    Set corruption tag = 1
    Increment corrupted count
Else:
    Leave corruption tag = 0
```

### Packet Delay
```
If random(0-100) < delayedPercent:
    Increment delayed count
    Create new thread:
        Sleep for (1500 + random(0-500)) milliseconds
        Send packet in background
Else:
    Send packet immediately
```

### Network Pseudocode

```
NETWORK ALGORITHM:
1. Initialize statistics counters
2. Bind socket to port
3. Listen for packets
4. Upon packet reception:
   a. Increment packets_received
   b. Parse network header for destination
   c. Check if packet should be dropped:
      - If yes: increment packets_dropped, go to step 5
   d. Check if packet should be corrupted:
      - If yes: set corruption flag, increment packets_corrupted
   e. Check if packet should be delayed:
      - If yes:
        * Increment packets_delayed
        * Create thread to send with delay
        * Jump to step 5
      - Else:
        * Send immediately
   f. Increment packets_sent
5. If (packets_received % STATS_INTERVAL == 0):
   a. Print traffic statistics
6. Return to step 3

STATS_INTERVAL: 2 packets
MIN_DELAY: 1500 ms (1.5 seconds)
MAX_DELAY: 2000 ms (2 seconds)
```

### Statistics Output
The Network program prints traffic statistics showing:
- Total packets received
- Total packets sent (forwarded)
- Total packets dropped
- Total packets delayed
- Total packets corrupted

## Message Flow Example

### Scenario: Send "Hello" (5 bytes, 1 segment)

```
SENDER                  NETWORK                 RECEIVER
  │                        │                        │
  │  SEND SEGMENT (seq=0)   │                        │
  ├───────────────────────>─│                        │
  │                         │  FORWARD (seq=0)      │
  │                         ├───────────────────────>│
  │                         │                   RECV │
  │                         │              SEND ACK  │
  │                         │<───────ACK (seq=0)────┤
  │                    RECV ACK │                    │
  │<───────────────────────ACK──┤                    │
  │                         │                        │
  ✓ MESSAGE COMPLETE       │                        │
```

### Scenario: Lost Packet (with retransmission)

```
SENDER                  NETWORK                 RECEIVER
  │                        │                        │
  │  SEND SEGMENT (seq=0)   │                        │
  ├───────────────────────>─│                        │
  │                    DROP │                        │
  │               (packet lost)                      │
  │                         │                        │
  │ (TIMEOUT - 3 seconds)   │                        │
  │  RETRANSMIT (seq=0)     │                        │
  ├───────────────────────>─│                        │
  │                         │  FORWARD (seq=0)      │
  │                         ├───────────────────────>│
  │                         │                   RECV │
  │                         │              SEND ACK  │
  │                         │<───────ACK (seq=0)────┤
  │                    RECV ACK │                    │
  │<───────────────────────ACK──┤                    │
  │                         │                        │
  ✓ MESSAGE COMPLETE       │                        │
```

## Error Handling

### Corruption Detection
- Network randomly sets corruption tag on packets
- Receiver detects corruption and discards packet
- Receiver still sends ACK (with previous sequence)
- Sender interprets missing/delayed ACK as loss, retransmits

### Loss Detection
- Sender uses 3-second timeout
- If ACK not received within timeout, retransmit
- Maximum 10 retransmission attempts per segment

### Delay Tolerance
- Network delay is 1.5-2 seconds (less than sender timeout)
- Sender's 3-second timeout accommodates network delays

## Protocol Guarantees

✓ **In-Order Delivery**: Receiver assembles segments in correct sequence
✓ **Reliability**: Sender retransmits lost or corrupted packets
✓ **Duplicate Elimination**: Receiver discards duplicate segments
✓ **No Data Loss**: (assuming max 10 retries sufficient)

## Limitations

- **Alternating bit protocol**: Only works with 1-bit sequence numbers
- **Unidirectional data**: Data flows Sender→Receiver; ACKs flow Receiver→Sender
- **No flow control**: Sender can send segments faster than receiver can process
- **No congestion control**: No adaptation to network conditions
