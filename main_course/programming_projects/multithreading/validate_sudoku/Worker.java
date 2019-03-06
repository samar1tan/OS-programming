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

    Worker(int start_row, int start_line, ScanIn field, int[][] sudoku, Validation result, CountDownLatch cdl) {
        this.start = new Point();
        this.start.setRow(start_row);
        this.start.setLine(start_line);
        this.scan_in = field;
        this.sudoku = sudoku;
        this.result = new Validation();
        this.result = result;
        this.cdl = cdl;
    }

    private boolean checkRow() {
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

    private boolean checkLine() {
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

    private boolean checkBlock() {
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
        switch (this.scan_in) {
            case ROW:
                result = checkRow();
                break;
            case LINE:
                result = checkLine();
                break;
            case BLOCK:
                result = checkBlock();
                break;
            case ALL_ROW:
                for (int i = 0; i < 9; i++) {
                    result = checkRow();
                    if (!result) {
                        break;
                    } else {
                        start.nextRow();
                    }
                }
                break;
            case ALL_LINE:
                for (int i = 0; i < 9; i++) {
                    result = checkLine();
                    if (!result) {
                        break;
                    } else {
                        start.nextLine();
                    }
                }
                break;
            case ALL_BLOCK:
                for (int i = 0; i < 9; i++) {
                    result = checkBlock();
                    if (!result) {
                        break;
                    } else {
                        start.nextBlock();
                    }
                }
                break;
            case ALL:
                Point copy = new Point();
                copy.copyFrom(start);

                for (int i = 0; i < 9; i++) {
                    result = checkRow();
                    if (!result) {
                        break;
                    } else {
                        start.nextRow();
                    }
                }
                start.copyFrom(copy);

                for (int i = 0; i < 9; i++) {
                    result = checkLine();
                    if (!result) {
                        break;
                    } else {
                        start.nextLine();
                    }
                }
                start.copyFrom(copy);

                for (int i = 0; i < 9; i++) {
                    result = checkBlock();
                    if (!result) {
                        break;
                    } else {
                        start.nextBlock();
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
        result.storeResult(validate());
    }

}
