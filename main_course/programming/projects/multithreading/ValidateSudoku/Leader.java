import java.util.concurrent.CountDownLatch;

public class Leader {

    public static void main(String[] args) {
        int[][] sudoku = {
                {1, 9, 6, 3, 5, 8, 4, 7, 2},
                {7, 2, 5, 4, 6, 9, 8, 1, 3},
                {8, 4, 3, 1, 2, 7, 5, 9, 6},
                {6, 5, 7, 8, 1, 2, 9, 3, 4},
                {3, 8, 2, 5, 9, 4, 1, 6, 7},
                {9, 1, 4, 6, 7, 3, 2, 5, 8},
                {5, 3, 9, 2, 4, 6, 7, 8, 1},
                {2, 6, 1, 7, 8, 5, 3, 4, 9},
                {4, 7, 8, 9, 3, 1, 6, 2, 5}
        };
        boolean target = true;
        boolean[] results = {false, false, false, false};
        int runs = 100000; // can be customed
        long[] time_costs = {0, 0, 0, 0};
        /* reference (example true solution):
                {1, 9, 6, 3, 5, 8, 4, 7, 2},
                {7, 2, 5, 4, 6, 9, 8, 1, 3},
                {8, 4, 3, 1, 2, 7, 5, 9, 6},
                {6, 5, 7, 8, 1, 2, 9, 3, 4},
                {3, 8, 2, 5, 9, 4, 1, 6, 7},
                {9, 1, 4, 6, 7, 3, 2, 5, 8},
                {5, 3, 9, 2, 4, 6, 7, 8, 1},
                {2, 6, 1, 7, 8, 5, 3, 4, 9},
                {4, 7, 8, 9, 3, 1, 6, 2, 5} */

        System.out.println("Testing different amounts of worker threads");
        System.out.println("Validating time: " + runs + " runs\n");
        Validation result = new Validation();

        long tic = System.currentTimeMillis();
        System.out.println("Mode 1: single thread");
        CountDownLatch cdl1 = new CountDownLatch(1 * runs);
        Worker m1w1 = new Worker(0, 0, Worker.ScanIn.ALL, sudoku, result, cdl1, runs);
        m1w1.start();
        try {
            m1w1.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            return;
        }
        long toc = System.currentTimeMillis();
        System.out.println("testing result: " + (results[0] = result.getResult()));
        System.out.println("time usage: " + (time_costs[0] = toc - tic) + " ms\n");

        System.out.println("Mode 2: 3 threads (all-line/all-row/all-block)");
        tic = System.currentTimeMillis();
        CountDownLatch cdl2 = new CountDownLatch(3 * runs);
        Worker m2w1 = new Worker(0, 0, Worker.ScanIn.ALL_ROW, sudoku, result, cdl2, runs);
        m2w1.start();
        Worker m2w2 = new Worker(0, 0, Worker.ScanIn.ALL_LINE, sudoku, result, cdl2, runs);
        m2w2.start();
        Worker m2w3 = new Worker(0, 0, Worker.ScanIn.ALL_BLOCK, sudoku, result, cdl2, runs);
        m2w3.start();
        try {
            cdl2.await();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        toc = System.currentTimeMillis();
        System.out.println("testing result: " + (results[1] = result.getResult()));
        System.out.println("time usage: " + (time_costs[1] = toc - tic) + " ms\n");

        System.out.println("Mode 3: 11 threads (all-line/all-row/each-block)");
        tic = System.currentTimeMillis();
        CountDownLatch cdl3 = new CountDownLatch(11 * runs);
        Worker m3w1 = new Worker(0, 0, Worker.ScanIn.ALL_ROW, sudoku, result, cdl3, runs);
        m3w1.start();
        Worker m3w2 = new Worker(0, 0, Worker.ScanIn.ALL_LINE, sudoku, result, cdl3, runs);
        m3w2.start();
        int row, line;
        row = line = 0;
        for(int i = 0; i < 9; i++) {
                Worker m3w3 = new Worker(row, line, Worker.ScanIn.BLOCK, sudoku, result, cdl3, runs);
                m3w3.start();
                if(line == 6) {
                    row += 3;
                    line = 0;
                } else {
                    line += 3;
                }
        }
        try {
            cdl3.await();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        toc = System.currentTimeMillis();
        System.out.println("testing result: " + (results[2] = result.getResult()));
        System.out.println("time usage: " + (time_costs[2] = toc - tic) + " ms\n");

        System.out.println("Mode 4: 27 threads (each-line/each-row/each-block)");
        tic = System.currentTimeMillis();
        CountDownLatch cdl4 = new CountDownLatch(27 * runs);
        for(int i = 0; i < 9; i++) {
            Worker m4w1 = new Worker(i, 0, Worker.ScanIn.ROW, sudoku, result, cdl4, runs);
            m4w1.start();
        }
        for(int i = 0; i < 9; i++) {
            Worker m4w2 = new Worker(0, i, Worker.ScanIn.LINE, sudoku, result, cdl4, runs);
            m4w2.start();
        }
        row = line = 0;
        for(int i = 0; i < 9; i++) {
            Worker m4w3 = new Worker(row, line, Worker.ScanIn.BLOCK, sudoku, result, cdl4, runs);
            m4w3.start();
            if(line == 6) {
                row += 3;
                line = 0;
            } else {
                line += 3;
            }
        }
        try {
            cdl4.await();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        toc = System.currentTimeMillis();
        System.out.println("testing result: " + (results[3] = result.getResult()));
        System.out.println("time usage: " + (time_costs[3] = toc - tic) + " ms\n");

        System.out.println("\nSummary:");
        if(target == results[0] && results[0] == results[1] && results[1] == results[2] && results[2] == results[3]) {
            System.out.println("Validation processes are all verified.");
        } else {
            System.out.println("Validation processes didn't pass the verification.");
            return;
        }
        int mode_num = 1;
        for(long i:time_costs) {
            System.out.println("Mode " + mode_num + ": " + i + " ms");
            mode_num++;
        }
    }

}
