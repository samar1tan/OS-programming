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
        int runs = 100000;
        System.out.println("Validating time: " + runs + " runs\n");
        Validation result = new Validation();

        long tic = System.currentTimeMillis();
        System.out.println("Mode 1: single thread");
        for(int i = 10; i < runs; i++) {
            CountDownLatch cdl1 = new CountDownLatch(1);
            result.resetResult();

            Worker m1w1 = new Worker(0, 0, Worker.ScanIn.ALL, sudoku, result, cdl1);
            m1w1.run();
            try {
                m1w1.join();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                return;
            }
        }
        long toc = System.currentTimeMillis();
        System.out.println("testing result: " + result.getResult());
        System.out.println("time usage: " + (toc - tic) + " ms\n");

        System.out.println("Mode 2: 3 threads (all-line/all-row/all-block)");
        tic = System.currentTimeMillis();
        for(int i = 0; i < runs; i++) {
            CountDownLatch cdl2 = new CountDownLatch(3);
            result.resetResult();

            Worker m2w1 = new Worker(0, 0, Worker.ScanIn.ALL_ROW, sudoku, result, cdl2);
            m2w1.run();
            Worker m2w2 = new Worker(0, 0, Worker.ScanIn.ALL_LINE, sudoku, result, cdl2);
            m2w2.run();
            Worker m2w3 = new Worker(0, 0, Worker.ScanIn.ALL_BLOCK, sudoku, result, cdl2);
            m2w3.run();

            try {
                cdl2.await();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        toc = System.currentTimeMillis();
        System.out.println("testing result: " + result.getResult());
        System.out.println("time usage: " + (toc - tic) + " ms\n");

        System.out.println("Mode 3: 11 threads (all-line/all-row/each-block)");
        tic = System.currentTimeMillis();
        for(int i = 0; i < runs; i++) {
            CountDownLatch cdl3 = new CountDownLatch(11);
            result.resetResult();

            Worker m3w1 = new Worker(0, 0, Worker.ScanIn.ALL_ROW, sudoku, result, cdl3);
            m3w1.run();
            Worker m3w2 = new Worker(0, 0, Worker.ScanIn.ALL_LINE, sudoku, result, cdl3);
            m3w2.run();
            int row, line;
            row = line = 0;
            for(int j = 0; j < 9; j++) {
                Worker m3w3 = new Worker(row, line, Worker.ScanIn.BLOCK, sudoku, result, cdl3);
                m3w3.run();
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
        }
        toc = System.currentTimeMillis();
        System.out.println("testing result: " + result.getResult());
        System.out.println("time usage: " + (toc - tic) + " ms\n");

        System.out.println("Mode 4: 27 threads (each-line/each-row/each-block)");
        tic = System.currentTimeMillis();
        for(int i = 0; i < runs; i++) {
            CountDownLatch cdl4 = new CountDownLatch(27);
            result.resetResult();

            for(int j = 0; j < 9; j++) {
                Worker m4w1 = new Worker(j, 0, Worker.ScanIn.ROW, sudoku, result, cdl4);
                m4w1.run();
            }
            for(int j = 0; j < 9; j++) {
                Worker m4w2 = new Worker(0, j, Worker.ScanIn.LINE, sudoku, result, cdl4);
                m4w2.run();
            }
            int row, line;
            row = line = 0;
            for(int j = 0; j < 9; j++) {
                Worker m4w3 = new Worker(row, line, Worker.ScanIn.BLOCK, sudoku, result, cdl4);
                m4w3.run();
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
        }
        toc = System.currentTimeMillis();
        System.out.println("testing result: " + result.getResult());
        System.out.println("time usage: " + (toc - tic) + " ms\n");
    }

}
