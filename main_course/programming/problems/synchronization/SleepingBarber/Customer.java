/**
 * Customer.java
 * customer thread surrounded by shared BarberShop environment
 */
public class Customer implements Runnable {

    private TestSleepingBarber.TestCounter _test_cnt; // count served customers, for testing
    private BarberShop _shop; // environment
    private int _customer_id; // identifier
    enum CustomerStates { SERVING, LEFT, WAITING }

    /**
     * Initialization
     * @param test_cnt TestSleepingBarber.TestCounter
     * @param shop BarberShop instance as shared environment
     * @param customer_id customer's identifier
     */
    public Customer(TestSleepingBarber.TestCounter test_cnt, BarberShop shop, int customer_id) {
        _test_cnt = test_cnt;
        _shop = shop;
        _customer_id = customer_id;
    }

    /**
     * customer thread tries to get a haircut
     */
    @Override
    public void run() {
        System.out.println("[Customer " + _customer_id + "]\t\t" + "Entering shop");
        switch (_shop.enterShop(_customer_id)) {
            case LEFT:
                System.out.println("[WARNING]\t\t\tbarbershop's full, Customer " + _customer_id + " left");
                break;
            case SERVING:
                synchronized (_shop) {
                    getHaircut();
                }
                break;
            case WAITING:
                System.out.println("[Customer " + _customer_id + "]\t\t" + "waiting with " +
                        (_shop.getWaitingNum() - 1) + " other customers");
                synchronized (_shop) {
                    while (_shop.enterShop(_customer_id) == CustomerStates.WAITING) { // safety
                        try {
                            _shop.wait();
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    }
                    _shop.freeChair();
                    getHaircut();
                }
        }
    }

    private void getHaircut() {
        _shop.getService(_customer_id);
        System.out.println("[Customer " + _customer_id + "]\t\t" + "haircut finished. " +
                _shop.getWaitingNum() + " customers waiting");
        System.out.println("[COUNTER]\t\t\tbarber has served " + _test_cnt.increaseCounter() + " customers");
        _shop.notify();
    }

}
