package Banks.BankManagerComponent;

/**
 * Created by dima on 02.06.16.
 */
public enum TransactionState {

    READY("ready"),
    FAILED("failed"),
    COMMITED("commited"),
    ROLLBACK("rollback");

    private String val;

    TransactionState(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
