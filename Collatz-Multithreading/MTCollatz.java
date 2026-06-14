import java.lang.Thread;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;
/**
 * @author enayalaborn
 * @version 1.1.1
 * @date 12 June 2026
 * 
 * MTCollatz - Multi-threaded Collatz Stopping Time Generator
 * 
 * This class implements the Collatz Conjecture to experiment with the application of thread 
 * synchronization and explore the use of threads in a computational setting. 
 * 
 * Command-line Arguments:
 *   N - The maximum number to test (range is 1 to N)
 *   T - The number of worker threads to create
 * 
 * Example: java MTCollatz 1000000 0
 *   Computes Collatz stopping times for numbers 1-1,000,000 using 8 threads
 * 
 * Output:
 *   Histogram of stopping times (stopping_time, frequency)
 *   Timing data (N, T, elapsed_time_in_seconds)

 */
import java.time.Instant;

public class MTCollatz {
    static int counter = 1;
    static int[] histogram = new int[20001];
    static Object lock = new Object();
    
    public static void main(String[] args) throws InterruptedException {
        
        if (args.length < 2) {
            System.out.println("Usage: java MTCollatz <N> <T>");
            System.out.println("Example: java MTCollatz 1000000 8");
            return;
        }
        
        int N = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);
        
        System.out.println("Starting computation with N=" + N + ", T=" + T);
        
        Instant start = Instant.now();
        
        Thread[] threads = new Thread[T];
        for (int i = 0; i < T; i++) {
            threads[i] = new Thread(() -> {
                while (true) {
                    int num;
                    synchronized(lock) {
                        if (counter > N) break;
                        num = counter;
                        counter++;
                    }
                    
                    int steps = 0;
                    long n = num;
                    while (n != 1) {
                        if (n % 2 == 0) {
                            n = n / 2;
                        } else {
                            n = 3 * n + 1;
                        }
                        steps++;
                    }
                    
                    histogram[steps]++;
                }
            });
            threads[i].start();
        }
        
        for (int i = 0; i < T; i++) {
            threads[i].join();
        }
        
        Instant end = Instant.now();
        long elapsedMs = end.toEpochMilli() - start.toEpochMilli();
        double elapsedSec = elapsedMs / 1000.0;
        
        System.out.println("\nHISTOGRAM");
        for (int i = 1; i <= 20000; i++) {
            if (histogram[i] > 0) {
                System.out.println(i + "," + histogram[i]);
            }
        }
        
        System.out.println("\nTIMING");
        System.out.println(N + "," + T + "," + elapsedSec);
    }
}
