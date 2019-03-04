class ThreadSum extends Thread {

    private int upper_bound;
    private ShareVal sum;

    public ThreadSum (int upper_bound, ShareVal sum) {
        this.upper_bound = upper_bound;
        this.sum = sum;
    }

    @Override
    public void run() {
        int temp = 0;
        for(int i = 1; i <= upper_bound; i++) {
            temp += i;
        }
        sum.setSum(temp);
    }

}
