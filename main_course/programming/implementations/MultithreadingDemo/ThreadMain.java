import java.util.Scanner;

public class ThreadMain {

    public static void main(String[] args) { // init/run 1st thread
        int upper_bound = 0;
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            upper_bound = Integer.parseInt(scanner.nextLine());
        }

        if (upper_bound < 0) {
            System.err.println(upper_bound + " must be not negative.");
        } else {
            ShareVal sum = new ShareVal(); // inter-process communication: shared object (in OOP)
            ThreadSum thrd_sum = new ThreadSum(upper_bound, sum); // init 2nd thread
            thrd_sum.start(); // run 2nd thread
            try { // qualification
                thrd_sum.join(); // wait for the ending of 2nd thread
                System.out.println("Sum: " + sum.getSum());
            } catch (InterruptedException ie) {
                System.err.println("Interruption Exception: " + ie);
            }
        }
    }

}
