public class Validation {

    private Boolean is_valid;

    Validation() {
        is_valid = true;
    }

    public void storeResult(boolean result) {
        is_valid &= result;
    }

    public boolean getResult() {
        return is_valid;
    }

    public void resetResult() {
        is_valid = true;
    }

}
