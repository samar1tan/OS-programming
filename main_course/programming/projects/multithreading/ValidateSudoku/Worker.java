import java.util.concurrent.CountDownLatch;

public class Worker extends Thread {

    enum ScanIn {
        ROW, LINE, BLOCK, ALL_ROW, ALL_LINE, ALL_BLOCK, ALL
    }

    class Point {

        private int row_number;
        private int line_number;

        public int getRowNumber() {
            return row_number;
        }

        public int getLineNumber() {
            return line_number;
        }

        public void setRow(int row_number) {
            this.row_number = row_number;
        }

        public void setLine(int line_number) {
            this.line_number = line_number;
        }

        public void copyFrom (Point rval) {
            this.line_number = rval.getLineNumber();
            this.row_number  = rval.getRowNumber();
        }

        public void nextRow() {
            row_number++;
        }

        public void nextLine() {
            line_number++;
        }

        public void nextBlock() {
            if(line_number == 6) {
                row_number += 3;
                line_number = 0;
            } else {
                line_number += 3;
            }
        }

    }

    private Point start;
    private ScanIn scan_in;
    private int[][] sudoku;
    private Validation result;
    public CountDownLatch cdl;
    private int run_times;

    Worker(int start_row, int start_line, ScanIn field, int[][] sudoku, Validation result, CountDownLatch cdl, int run_times) {
        this.start = new Point();
        this.start.setRow(start_row);
        this.start.setLine(start_line);
        this.scan_in = field;
        this.sudoku = sudoku;
        this.result = new Validation();
        this.result = result;
        this.cdl = cdl;
        this.run_times = run_times;
    }

    private boolean checkRow(Point start) {
        boolean[] is_exist = { false, false, false, false, false, false, false, false, false };
        for(int i = 0; i < 9; i++) {
            int val = sudoku[start.getRowNumber()][i] - 1;
            if(is_exist[val]) {
                return false;
            } else {
                is_exist[val] = true;
            }
        }
        return true;
    }

    private boolean checkLine(Point start) {
        boolean[] is_exist = { false, false, false, false, false, false, false, false, false };
        for(int i = 0; i < 9; i++) {
            int val = sudoku[i][start.getLineNumber()] - 1;
            if(is_exist[val]) {
                return false;
            } else {
                is_exist[val] = true;
            }
        }
        return true;
    }

    private boolean checkBlock(Point start) {
        boolean[] is_exist = { false, false, false, false, false, false, false, false, false };
        for(int i = 0; i < 9; i++) {
            int val = sudoku[start.getRowNumber() + (i % 3)][start.getLineNumber() + (i / 3)] - 1;
            if(is_exist[val]) {
                return false;
            } else {
                is_exist[val] = true;
            }
        }
        return true;
    }

    private boolean validate() {
        boolean result = false;
        Point start_copy = new Point();
        start_copy.copyFrom(start);
        switch (this.scan_in) {
            case ROW:
                result = checkRow(start_copy);
                break;
            case LINE:
                result = checkLine(start_copy);
                break;
            case BLOCK:
                result = checkBlock(start_copy);
                break;
            case ALL_ROW:
                for (int i = 0; i < 9; i++) {
                    result = checkRow(start_copy);
                    if (!result) {
                        break;
                    } else {
                        start_copy.nextRow();
                    }
                }
                break;
            case ALL_LINE:
                for (int i = 0; i < 9; i++) {
                    result = checkLine(start_copy);
                    if (!result) {
                        break;
                    } else {
                        start_copy.nextLine();
                    }
                }
                break;
            case ALL_BLOCK:
                for (int i = 0; i < 9; i++) {
                    result = checkBlock(start_copy);
                    if (!result) {
                        break;
                    } else {
                        start_copy.nextBlock();
                    }
                }
                break;
            case ALL:
                for (int i = 0; i < 9; i++) {
                    result = checkRow(start_copy);
                    if (!result) {
                        break;
                    } else {
                        start_copy.nextRow();
                    }
                }
                start_copy.copyFrom(start);

                for (int i = 0; i < 9; i++) {
                    result = checkLine(start_copy);
                    if (!result) {
                        break;
                    } else {
                        start_copy.nextLine();
                    }
                }
                start_copy.copyFrom(start);

                for (int i = 0; i < 9; i++) {
                    result = checkBlock(start_copy);
                    if (!result) {
                        break;
                    } else {
                        start_copy.nextBlock();
                    }
                }

                break;
            default:
                System.err.println("Switch error");
                return false;
        }

        cdl.countDown();
        return result;
    }

    @Override
    public void run() {
        for(int i = 0; i < run_times; i++) {
            result.resetResult();
            result.storeResult(validate());
        }
        
    }

}
