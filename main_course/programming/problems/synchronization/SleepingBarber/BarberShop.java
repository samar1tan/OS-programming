/**
 * BarberShop.java
 * N waiting chairs and a barber as shared environment
 */
public class BarberShop {

    // states definitions
    enum ChairStates { FREE, OCCUPIED }
    enum BarberStates { SLEEPING, IDLE, BUSY }

    private int _chair_num; // number of waiting chairs
    private BarberStates _barber_state;
    private ChairStates[] _chair_state; // chairs "set", not ordered

    /**
     * Initialization
     * @param chair_num the number of waiting chairs
     */
    public BarberShop(int chair_num) {
        _chair_num = chair_num;
        _chair_state = new ChairStates[_chair_num];
        _barber_state = BarberStates.SLEEPING;
        for (int i = 0; i < _chair_num; ++i) {
            _chair_state[i] = ChairStates.FREE;
        }
    }

    /**
     * Switch the control from customer to barbershop, after customer's entry
     * @param customer_id customer identifier
     * @return Customer.CustomerStates SERVING/LEFT/WAITING
     */
    public Customer.CustomerStates enterShop(int customer_id) {
        if(_barber_state == BarberShop.BarberStates.SLEEPING) {
            if(invokeBarber(customer_id) != BarberShop.BarberStates.BUSY) { // sanity check
                System.err.println("ERROR: out of service");
                System.exit(1);
            }
        } else if(_barber_state == BarberStates.IDLE) {
            _barber_state = BarberStates.BUSY;
        } else { // barber's busy
            int test = findWaitingChair(customer_id);
            if(test == -1) {
                return Customer.CustomerStates.LEFT;
            } else {
                return Customer.CustomerStates.WAITING;
            }
        }

        return Customer.CustomerStates.SERVING; // barber's sleeping/idle before
    }

    /**
     * Get actually haircutting
     * @param customer_id prepare for error info
     */
    public void getService(int customer_id) {
        // serves
        System.out.println("[Barber]\t\t\t" + "Customer " + customer_id + " is getting haircut");
        simulateHaircut();

        // prepares for the next customer or sleep
        if(isAnyoneWaiting()){
            _barber_state = BarberStates.IDLE;
        } else {
            _barber_state = BarberStates.SLEEPING;
        }
    }

    /**
     * Free a waiting chair before serving
     * @return int the index of freed chair
     */
    public int freeChair() {
        int i;
        for(i = 0; i < _chair_num; ++i) {
            if(_chair_state[i] == ChairStates.OCCUPIED) {
                _chair_state[i] = ChairStates.FREE;
                break;
            }
        }

        if(i == _chair_num) { // sanity check
            System.err.println("[ERROR]\t\t\t\tbarber is serving a waiting ghost");
            System.exit(1);
        }

        return i;
    }

    /**
     * @return number of waiting customers
     */
    public int getWaitingNum() {
        int cnt = 0;
        for (ChairStates state:_chair_state) {
            if(state == ChairStates.OCCUPIED) {
                ++cnt;
            }
        }
        return cnt;
    }

    /**
     * Transfer barber's state from SLEEPING to IDLE
     * @param customer_id prepare for error info after failing sanity check
     * @return BarberShop.BarberStates barber's state after being invoked, IDLE normally; SLEEPING/BUSY abnormally
     */
    private BarberStates invokeBarber(int customer_id) {
        if(_barber_state != BarberStates.SLEEPING) { // sanity check
            System.err.println("[ERROR]\t\t\t\tbarber invoked from " + _barber_state.toString() + "by Customer " + customer_id);
            System.exit(1);
        }
        return _barber_state = BarberStates.BUSY;
    }

    /**
     * Try to allocate empty seat to new customer
     * @param customer_id prepare for error info after failing sanity check
     * @return int -1 for failing to find chairs (full); positive for chairs index
     */
    private int findWaitingChair(int customer_id) {
        if (_barber_state != BarberStates.BUSY) { // sanity check
            System.err.println("[ERROR]\t\t\t\tCustomer " + customer_id + " refused barber's service");
            System.exit(1);
        }

        int test = getFirstFreeChair();
        if (test == -1) {
            return test;
        } else {
            _chair_state[test] = ChairStates.OCCUPIED;
            return test + 1;
        }
    }

    /**
     * util method for findWaitingChair()
     * @return int -1 if no free chairs; Min index of free chair otherwise
     */
    private int getFirstFreeChair() {
        for(int i = 0; i < _chair_num; ++i) {
            if(_chair_state[i] == ChairStates.FREE) {
                return i;
            }
        }
        return -1;
    }

    /**
     * pause customer thread to simulate haircutting
     * max interval: MAX_HAIRCUT_TIME
     * min interval: 0.1 * MAX_HAIRCUT_TIME
     * util method for getService()
     */
    private void simulateHaircut() {
        double real_time = Math.max(TestSleepingBarber.MAX_HAIRCUT_TIME * Math.random(),
                TestSleepingBarber.MIN_HAIRCUT_TIME);
        try {
            Thread.sleep((int)(real_time * 1000));
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * util method for getService()
     * @return boolean True if any chair is OCCUPIED; False otherwise
     */
    private boolean isAnyoneWaiting() {
        for(int i = 0; i < _chair_num; ++i) {
            if(_chair_state[i] == ChairStates.OCCUPIED) {
                return true;
            }
        }
        return false;
    }

}
