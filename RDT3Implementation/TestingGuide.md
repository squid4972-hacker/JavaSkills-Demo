# RDT 3.0 Testing Guide

This document provides comprehensive test cases to verify the RDT 3.0 implementation is working correctly.

## Pre-Testing Checklist

- [ ] All provided .java files compile without errors
- [ ] No compilation warnings
- [ ] Have three terminals available
- [ ] Ports 60050-60052 are available on your system
- [ ] Java is installed and working

## Test Execution Template

For each test below, follow this pattern:

```
Terminal 1: java Network 60050 [lossPercent] [delayPercent] [errorPercent]
Terminal 2: java Receiver 60051 127.0.0.1 60050
Terminal 3: java Sender 60052 127.0.0.1 60051 127.0.0.1 60050

Wait for all programs to print their initialization messages.
Then proceed with test steps.
```

---

## Test 1: Basic Functionality (No Failures)

**Objective**: Verify that Sender, Receiver, and Network can communicate without errors.

**Setup**:
```bash
Terminal 1: java Network 60050 0 0 0
Terminal 2: java Receiver 60051 127.0.0.1 60050
Terminal 3: java Sender 60052 127.0.0.1 60051 127.0.0.1 60050
```

**Test Steps**:
1. In Terminal 3, type: `Hello World`
2. Observe Terminal 3 shows segment transmission
3. Observe Terminal 2 receives segments
4. In Terminal 3, type: `quit`

**Expected Results**:
- Sender shows "Segment 1/2 sent" and "ACK received"
- Sender shows "Segment 2/2 sent" and "ACK received"
- Receiver shows "Segment received" twice
- Receiver shows "Sequence number matches expected"
- Network shows traffic forwarded successfully
- No timeouts or retransmissions

**Pass/Fail**: _______

---

## Test 2: Long Message (Multiple Segments)

**Objective**: Verify segmentation works for messages longer than 7 bytes.

**Setup**:
```bash
Terminal 1: java Network 60050 0 0 0
Terminal 2: java Receiver 60051 127.0.0.1 60050
Terminal 3: java Sender 60052 127.0.0.1 60051 127.0.0.1 60050
```

**Test Steps**:
1. In Terminal 3, type: `This is a longer test message that will need multiple segments`
2. Count segments transmitted
3. Verify all segments arrive
4. In Terminal 3, type: `quit`

**Expected Results**:
- Message length shown (60 bytes)
- Segments needed shown (9 segments for 60 bytes with 7-byte payload)
- Each segment sent and ACK received
- No duplicate messages
- No timeouts

**Pass/Fail**: Fail needed 10 segments

---

## Test 3: Packet Loss Detection and Retransmission

**Objective**: Verify Sender retransmits when packets are lost.

**Setup**:
```bash
Terminal 1: java Network 60050 50 0 0
Terminal 2: java Receiver 60051 127.0.0.1 60050
Terminal 3: java Sender 60052 127.0.0.1 60051 127.0.0.1 60050
```

**Note**: 50% loss means roughly half the packets will be dropped.

**Test Steps**:
1. In Terminal 3, type: `Test Loss`
2. Observe Terminal 1 showing dropped packets
3. Observe Terminal 3 showing retransmissions and timeouts
4. Verify message still arrives correctly
5. In Terminal 3, type: `quit`

**Expected Results**:
- Network shows "*** PACKET DROPPED ***" message
- Sender shows "Timeout! Retransmitting"
- Sender retransmits multiple times
- Segments eventually succeed
- Message assembles correctly at receiver
- No retransmit count exceeds 10

**Pass/Fail**: _____

---

## Test 4: Packet Corruption Detection

**Objective**: Verify Receiver detects and discards corrupted packets, Sender retransmits.

**Setup**:
```bash
Terminal 1: java Network 60050 0 0 30
Terminal 2: java Receiver 60051 127.0.0.1 60050
Terminal 3: java Sender 60052 127.0.0.1 60051 127.0.0.1 60050
```

**Note**: 30% corruption rate means roughly 1 in 3 packets will be marked corrupt.

**Test Steps**:
1. In Terminal 3, type: `Corrupt Test`
2. Observe Terminal 1 showing corrupted packets
3. Observe Terminal 2 discarding corrupted segments
4. Verify Sender retransmits without corruption
5. In Terminal 3, type: `quit`

**Expected Results**:
- Network shows "*** PACKET CORRUPTED ***" message
- Receiver shows "Segment is corrupt, discarding"
- Sender times out and retransmits
- Retransmitted packets reach the receiver successfully
- Message assembles with only clean data

**Pass/Fail**: ____

---

## Test 5: Packet Delay Simulation

**Objective**: Verify Network delays packets appropriately and Sender waits for delayed ACKs.

**Setup**:
```bash
Terminal 1: java Network 60050 0 30 0
Terminal 2: java Receiver 60051 127.0.0.1 60050
Terminal 3: java Sender 60052 127.0.0.1 60051 127.0.0.1 60050
```

**Note**: 30% delay means roughly 1 in 3 packets will be delayed 1.5-2 seconds.

**Test Steps**:
1. In Terminal 3, type: `Delay Test`
2. Observe Terminal 1 showing delayed packets
3. Note the time delay before ACKs arrive
4. Observe Sender waiting but NOT timing out
5. In Terminal 3, type: `quit`

**Expected Results**:
- Network shows "*** PACKET DELAYED ***" message
- Network shows "sent delayed packet" after delay
- Sender receives delayed ACKs without timeout
- 3-second timeout is sufficient for 1.5-2 second delays
- Message transmits successfully
- No unnecessary retransmissions

**Pass/Fail**: ____

---

## Test 6: Duplicate Packet Handling

**Objective**: Verify Receiver correctly identifies and handles duplicate segments.

**Setup**:
```bash
Terminal 1: java Network 60050 0 50 0
Terminal 2: java Receiver 60051 127.0.0.1 60050
Terminal 3: java Sender 60052 127.0.0.1 60051 127.0.0.1 60050
```

**Test Steps**:
1. In Terminal 3, type: `Duplicate`
2. With 50% delay, some ACKs will be delayed
3. Sender may retransmit (creating duplicate at network)
4. Observe Receiver handling duplicates gracefully
5. In Terminal 3, type: `quit`

**Expected Results**:
- Receiver shows "Duplicate segment (seq=X), expected seq=Y"
- Duplicate data is NOT added to message twice
- Receiver still sends ACK for duplicate
- Message is correct at end (no duplication)

**Pass/Fail**: ____

---

## Test 7: Combined Failure Modes

**Objective**: Verify protocol is robust under realistic conditions with multiple failures.

**Setup**:
```bash
Terminal 1: java Network 60050 10 15 5
Terminal 2: java Receiver 60051 127.0.0.1 60050
Terminal 3: java Sender 60052 127.0.0.1 60051 127.0.0.1 60050
```

**Note**: 10% loss, 15% delay, 5% corruption = realistic network conditions

**Test Steps**:
1. Send first message: `Test 1 - Multiple failures`
2. Wait for completion
3. Send second message: `Test 2 - Message two`
4. Wait for completion
5. In Terminal 3, type: `quit`

**Expected Results**:
- Both messages transmit successfully
- Network shows mix of normal, dropped, delayed, and corrupted packets
- Sender retransmits as needed
- No message corruption at receiver
- Statistics show reasonable counts

**Pass/Fail**: _____

---

## Test 8: Traffic Statistics

**Objective**: Verify Network correctly records and displays statistics.

**Setup**:
```bash
Terminal 1: java Network 60050 5 10 3
Terminal 2: java Receiver 60051 127.0.0.1 60050
Terminal 3: java Sender 60052 127.0.0.1 60051 127.0.0.1 60050
```

**Test Steps**:
1. Send a message with several segments
2. Observe Terminal 1 for statistics output
3. Count packets received in Terminal 1
4. Verify statistics print every 2 packets received
5. In Terminal 3, type: `quit`

**Expected Results**:
- Statistics print after every 2 packets received
- Packets received count matches observed packets
- Packets sent >= packets received (some may be dropped)
- Dropped count + sent count approximately = received count
- Statistics format is clear and readable

**Pass/Fail**: _____

---

## Test 9: Sequence Number Alternation

**Objective**: Verify sequence numbers properly alternate between 0 and 1.

**Setup**:
```bash
Terminal 1: java Network 60050 0 0 0
Terminal 2: java Receiver 60051 127.0.0.1 60050
Terminal 3: java Sender 60052 127.0.0.1 60051 127.0.0.1 60050
```

**Test Steps**:
1. In Terminal 3, type: `Sequence Test - More than seven bytes`
2. Carefully observe each segment's sequence number
3. Note the pattern in all three terminals
4. In Terminal 3, type: `quit`

**Expected Results**:
- Segment 1 shows seq=0
- Segment 2 shows seq=1
- Segment 3 shows seq=0
- Pattern continues: 0, 1, 0, 1, 0, 1...
- ACKs match with corresponding segments

**Pass/Fail**: _____

---

## Test 10: No Data Loss End-to-End

**Objective**: Final verification that no data is lost or corrupted through the entire pipeline.

**Setup**:
```bash
Terminal 1: java Network 60050 20 25 10
Terminal 2: java Receiver 60051 127.0.0.1 60050
Terminal 3: java Sender 60052 127.0.0.1 60051 127.0.0.1 60050
```

**Test Steps**:
1. Send a test message: `The quick brown fox jumps over the lazy dog`
2. Let it transmit completely
3. Manually verify the message matches exactly
4. Test another message: `ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789`
5. In Terminal 3, type: `quit`

**Expected Results**:
- Message received at receiver matches exactly
- No extra characters
- No missing characters
- Correct order
- Both test messages identical at receiver

**Pass/Fail**: ____

---

## Test Summary

| Test # | Description | Pass | Fail |
|--------|-------------|------|------|
| 1 | Basic Functionality | ___ | ___ |
| 2 | Long Message | ___ | ___ |
| 3 | Packet Loss | ___ | ___ |
| 4 | Packet Corruption | ___ | ___ |
| 5 | Packet Delay | ___ | ___ |
| 6 | Duplicate Handling | ___ | ___ |
| 7 | Combined Failures | ___ | ___ |
| 8 | Statistics | ___ | ___ |
| 9 | Sequence Numbers | ___ | ___ |
| 10 | Data Integrity | ___ | ___ |

Total Tests Passed: __ / 10

---

## Debugging Tips

### If Test Fails: "Connection refused"
- Check Network program is running
- Verify correct hostnames and ports
- Ensure ports 60050-60052 are not in use by other programs

### If Test Fails: "Timeout"
- Check that all three programs are running
- Verify Network is actually forwarding packets
- Try without delays/losses first

### If Test Fails: "Message doesn't match"
- Check Receiver terminal for corruption messages
- Verify Sender completed all segments (check sender output)
- Count bytes in message - may need more segments

### If Test Fails: "Infinite loop"
- May be stuck waiting for packet
- Use Ctrl+C to stop programs
- Check port assignments

### To Verify Network Routing
- Run Network first, it should print initialization
- Run Receiver, it should print initialization and "Waiting"
- Run Sender, if successful, should see "Sender initialized"
- If Sender cannot connect, you'll see immediate error

## Performance Notes

- First transmission may be slower (DNS resolution)
- Subsequent transmissions should be faster
- Network delays (1.5-2 seconds) are intentional
- Retransmissions may make total time longer
- All programs should complete without hanging

## Next Steps if All Tests Pass

1. Test on SSH server (required for grading)
2. Try different port numbers in valid range
3. Test with extreme percentages (0%, 100%)
4. Test with very long messages (100+ bytes)
5. Verify code is well-commented and documented
