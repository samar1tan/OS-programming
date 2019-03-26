// TODO: 1. read official tutorials about concurrency
// TODO: 2. implement FIFO
// TODO: 3. remove redundant synchronized mechanism

/**
 * TestSleepingBarber.java
 * test thread for the scheme to Sleeping Barbers Problem
 */
public class TestSleepingBarber implements Runnable {

    // Test Settings
    // Number of waiting chairs
    public static final int CHAIR_NUM           = 5;
    // Total number of arriving customers
    public static final int CUSTOMER_NUM        = 20;
    // Interval between two arrivals, in seconds
    public static final int MAX_INTERVAL        = 1;
    public static final int MIN_INTERVAL        = (int)(0.1 * MAX_INTERVAL);
    // Duration of haircutting, in seconds
    public static final int MAX_HAIRCUT_TIME    = 5;
    public static final int MIN_HAIRCUT_TIME    = (int)(0.1 * MAX_HAIRCUT_TIME);

    /**
     * Count the served customers
     */
    public class TestCounter {

        private int _cnt_served_customers = 0;

        public synchronized int increaseCounter() {
            return ++_cnt_served_customers;
        }

    }

    public static void main(String[] args) {
        new Thread(new TestSleepingBarber()).start();
    }

    @Override
    public void run() {
        TestCounter test_cnt = new TestCounter();
        BarberShop shop = new BarberShop(CHAIR_NUM);
        int customer_id = 1;
        while(customer_id <= CUSTOMER_NUM) {
            new Thread(new Customer(test_cnt, shop, customer_id)).start();
            customer_id++;
            waitForNextCustomer();
        }
    }

    /**
     * Pause testing thread to support repeated tests & simulate real-world intervals
     * util function for run()
     */
    private void waitForNextCustomer() {
        double real_time = Math.max(MAX_INTERVAL * Math.random(), MIN_INTERVAL);
        try {
            Thread.sleep((int)(real_time * 1000));
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

}
